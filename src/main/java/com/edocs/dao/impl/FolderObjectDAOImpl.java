package com.edocs.dao.impl;

import com.edocs.dao.FolderObjectDAO;
import com.edocs.entities.Event_Log;
import com.edocs.entities.FolderObject;
import com.edocs.entities.Module;
import com.edocs.exception.GenericExceptionHandler;
import com.edocs.service.BucketAccesService;
import com.edocs.service.Event_LogService;
import com.edocs.utils.*;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Software_Development on 12/7/2017.
 */
@Component
@Repository
@Transactional
public class FolderObjectDAOImpl implements FolderObjectDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BucketAccesService bucketAccesService;

    @Autowired
    Event_LogService eventLogService;

    List<String> sortColumns = Arrays.asList("folderName","datecreated");

    @Autowired
    private HttpServletRequest request;

    @Override
    public FolderObject getFolderObjectById(int folderID) {
        FolderObject folderObject = new FolderObject();
        try {
            folderObject = jdbcTemplate.queryForObject(QueryConstants.FETCH_FOLDEROBJECT_BY_ID, new Object[] {folderID,getGetTenantId()}, (resultSet, i) -> RowMappers.folderObjectMapper(resultSet) );
            if(!folderObject.getDeleted()) {
                List<Module> modules = jdbcTemplate.query(QueryConstants.GET_FOLDER_MODULES, new Object[]{folderID}, (resultSet, i) -> RowMappers.moduleMapper(resultSet));
                if (!modules.isEmpty()) {
                    folderObject.setModules(modules);
                }
            }
            return folderObject;
        }catch (EmptyResultDataAccessException exception){
            throw new GenericExceptionHandler("No such folder id = " + folderID + " exists!",HttpStatus.NOT_FOUND);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            return null; }
    }
    @Override
    public int addFolderObject(FolderObject folderObject) {
        String folderPath ="";
        Date now = new Date();


        int tenantId = getGetTenantId();
        //check if name is duplicate or parent folder exists

        int duplicateName = jdbcTemplate.queryForObject(QueryConstants.FOLDER_WITH_SAME_NAME_EXISTS, new Object[]{folderObject.getFolderName(),folderObject.getParentFolderId(),tenantId},Integer.class);

        if(duplicateName > 0)
            throw new GenericExceptionHandler("Folder with this name already exists!", HttpStatus.CONFLICT);
        int count = 0;
        if(folderObject.getParentFolderId() == -1) {
            folderPath = folderObject.getFolderName() + "_" + tenantId + "/";
            count = 1;
        }
        else {
            count = jdbcTemplate.queryForObject(QueryConstants.PARENT_FOLDER_WITH_ID, new Object[]{folderObject.getParentFolderId(),tenantId}, Integer.class);
        }

        if(count == 1) {
            if(folderObject.getParentFolderId() > 0) {
                String parentFolderPath = jdbcTemplate.queryForObject(QueryConstants.GET_PARENT_FOLDER_PATH, new Object[]{folderObject.getParentFolderId(),tenantId}, String.class);
                if (folderObject.getParentFolderId() > 0)
                    folderPath = parentFolderPath + folderObject.getFolderName() + "_" + tenantId + "/";
            }
            SimpleJdbcInsert addFolder = new SimpleJdbcInsert(jdbcTemplate.getDataSource()).withTableName(TableNames.FOLDEROBJECT).
                    usingGeneratedKeyColumns(SqlColumn.FOLDER_ID);
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(SqlColumn.FOLDER_NAME, folderObject.getFolderName())
                    .addValue(SqlColumn.PARENT_FOLDER_ID, folderObject.getParentFolderId())
                    .addValue(SqlColumn.PATH,folderPath)
                    .addValue(SqlColumn.DATE_CREATED, now)
                    .addValue(SqlColumn.IS_DELETED,false)
                    .addValue(SqlColumn.DATE_DELETED, folderObject.getDateDeleted())
                    .addValue(SqlColumn.TENANT_ID,tenantId);

            int moduleCount;

            for (Module module: folderObject.getModules()) {
                moduleCount = jdbcTemplate.queryForObject(QueryConstants.GIVEN_MODULE_EXISTS, new Object[]{module.getModuleId(),tenantId}, Integer.class);
                if(moduleCount < 1)
                    throw new GenericExceptionHandler("Given module does not exists!",HttpStatus.NOT_FOUND);
            }
            int realFolderID = addFolder.executeAndReturnKey(parameters).intValue();
            this.insertBatch(realFolderID, folderObject.getModules());
            this.updateChangeControl(realFolderID, folderObject.getChangeControlNumber());
            bucketAccesService.createFolder(folderPath);

            saveEventLog(realFolderID, Constants.FOLDER_CREATED,tenantId);
            return realFolderID;
        }else{
            throw new GenericExceptionHandler("No such parent folder exists!",HttpStatus.NOT_FOUND);
        }
    }

    private void updateChangeControl(int folderId, String changeControlNumber) {
        jdbcTemplate.update(QueryConstants.INSERT_CHANGE_CONTROL_FOLDER_ID,new Object[]{folderId,changeControlNumber});
    }


    private void saveEventLog(int folderObjectID, String activity,int tenantId) {
        Event_Log evenLog = new Event_Log();

        Principal principal = request.getUserPrincipal();
        String username = principal.getName();

        evenLog.setUser_Id(Constants.USER_ID);
        evenLog.setUsername(username);
        evenLog.setActivity(activity);
        evenLog.setIp_Address(getClientIp());
        evenLog.setType(Constants.EVENT_LOG_TYPE_FOLDER);
        evenLog.setTenant_Id(tenantId);
        evenLog.setCreated_Date(new Date());
        evenLog.setComponent_Id(folderObjectID);

        eventLogService.save(evenLog);

        System.out.println("Event log saved " + activity);
    }

    private String getClientIp() {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");

            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }
    @Override
    public int deleteFolder(int folderId,String comments, String changeControlNumber) {


        FolderObject folderObject = getFolderObjectById(folderId);
        System.out.println("path is " + folderObject.getPath());
        Date now = new Date();
        //check if parent object exists
        String folderPath ="";
        int count = 0;
        Map<String,Object> map = new HashMap<>();

        count = jdbcTemplate.queryForObject(QueryConstants.PARENT_FOLDER_WITH_ID, new Object[]{folderId,getGetTenantId()}, Integer.class);


        if(count == 1) {
            map = jdbcTemplate.queryForMap(QueryConstants.DELETE_BY_FOLDER_ID,new Object[]{folderId,comments,changeControlNumber});
        }else{
            throw new GenericExceptionHandler("No such folder exists!",HttpStatus.NOT_FOUND);
        }



        int success = (Integer)map.get("success");
        if(success == 1){
            bucketAccesService.deleteFolder(folderObject.getPath());
            saveEventLog(folderObject.getFolderID(),Constants.FOLDER_DELETED, folderObject.getTenantID());
        }else if(success == 2){
            throw new GenericExceptionHandler("This folder contains child folders! Delete them first!",HttpStatus.CONFLICT);
        }else if(success == 3){
            throw new GenericExceptionHandler("This folder contains child files! Delete them first!",HttpStatus.CONFLICT);
        }else if(success == 4){
            throw new GenericExceptionHandler("This folder is deleted",HttpStatus.NOT_FOUND);
        }
        return success;
    }

    @Override
    public List<FolderObject> getChildFoldersByFolderId(int parentFolderId, String sortBy) {
        String sql = QueryConstants.FETCH_CHILDFOLDERS_BY_PARENTFOLDER_ID;
        System.out.println(" fetching by id  = " + parentFolderId);
        System.out.println("Sort by " + sortBy);

        if(sortColumns.contains(sortBy)){
            sql += " ORDER BY " + sortBy + ",folderName";
        }else{
            sql +=  " ORDER BY folderName";
        }

        List<FolderObject> childFolders  = jdbcTemplate.query(sql,new Object[]{parentFolderId,getGetTenantId()}, ((resultSet, i) -> RowMappers.folderObjectMapper(resultSet)));

        List<Module> modules = new ArrayList<Module>();
        for (FolderObject folderObject : childFolders) {
            modules = jdbcTemplate.query(QueryConstants.GET_FOLDER_MODULES,new Object[] {folderObject.getFolderID()},(resultSet, i) -> RowMappers.moduleMapper(resultSet));
            if(!modules.isEmpty()) {
                folderObject.setModules(modules);
            }
        }
        return childFolders;
    }

    @Override
    public void insertBatch(int folderID, final List<Module> modules) {
        String sql = QueryConstants.INSERT_FOLDER_MODULE_RELATION;


        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Module module = modules.get(i);
                ps.setInt(1, folderID);
                ps.setInt(2, module.getModuleId());
            }

            @Override
            public int getBatchSize() {
                return modules.size();
            }
        });

    }

    private int getGetTenantId(){
        int tenantId;
        Principal principal = request.getUserPrincipal();
        String username = principal.getName();
        if(username.equals("user1"))
            tenantId = Constants.USER1_TENANT_ID;
        else
            tenantId = Constants.USER2_TENANT_ID;

        return tenantId;
    }

}
