package com.quascenta.edocs.dao.impl;

import com.quascenta.edocs.entities.*;
import com.quascenta.edocs.exception.GenericExceptionHandler;
import com.quascenta.edocs.service.BucketAccesService;
import com.quascenta.edocs.service.Event_LogService;
import com.quascenta.edocs.utils.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.quascenta.edocs.dao.DocumentObjectDAO;
import com.quascenta.edocs.entities.FileNameObject;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static com.quascenta.edocs.utils.Constants.USER_ID;

@Component
@Repository
@Transactional
public class DocumentObjectDAOImpl implements DocumentObjectDAO{

	
	@Autowired
	private JdbcTemplate jdbcTemplate;

    @Autowired
    private BucketAccesService bucketAccesService;

    @Autowired
    private Event_LogService eventLogService;

    @Autowired
    private HttpServletRequest request;

    List<String> sortColumns = Arrays.asList("documentId","documentName","documentType", "datecreated", "dateModified", "versionNumber");

    @Override
    public void delete(int id, String comments, String changeControlNumber) {
        System.out.println("Deleting file " + id);
        DocumentObject documentObject = getDocumentObjectById(id);
        if(documentObject.isLocked())
            throw new GenericExceptionHandler("This file is locked!",HttpStatus.NOT_ACCEPTABLE);
        List<DocumentVersion> versionsList = getVersions(documentObject.getObjectId(),false);
        boolean deleted = false;
        String path = "";

        for (DocumentVersion documentVersion: versionsList) {
            path = documentObject.getPath() + "_" + documentVersion.getDocumentVersionNumber();
            deleted = bucketAccesService.delete(path);
        }

        if (deleted) {
            Date now = new Date();
            System.out.println(comments + "-- " + now + " --" + USER_ID + " --" + changeControlNumber);
            jdbcTemplate.update(QueryConstants.DELETE_BY_DOCUMENT_ID,new Object[]{id, getTenantId(),comments,now,USER_ID,changeControlNumber});
            saveEventLog(documentObject,Constants.FILE_DELETED);
        }
    }

    @Override
    public DocumentObject getDocumentObjectById(int objectId) {
        int tenantId = getTenantId();
        try {
            DocumentObject documentObject = new DocumentObject();

            documentObject = jdbcTemplate.queryForObject(QueryConstants.FETCH_DOCUMENTOBJECT_BY_ID, new Object[] {objectId, tenantId}, (resultSet, i) -> RowMappers.documentObjectMapper(resultSet) );
            if(!documentObject.isDeleted()) {
                List<Module> modules = jdbcTemplate.query(QueryConstants.GET_DOCUMENT_MODULES, new Object[]{objectId, tenantId}, (resultSet, i) -> RowMappers.moduleMapper(resultSet));
                if (!modules.isEmpty()) {
                    documentObject.setModules(modules);
                }
            }else{
                throw new GenericExceptionHandler("Document with id = " + objectId + " not found!",HttpStatus.NOT_FOUND);
            }
            return documentObject;
        }
        catch (EmptyResultDataAccessException exception){
            throw new GenericExceptionHandler("Document with id = " + objectId + " not found!",HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public DocumentObject getDocumentObjectByIdAndVersionNumber(int objectId, String versionNumber) {
        DocumentObject documentObject = new DocumentObject();
        try {
            System.out.println(QueryConstants.FETCH_DOCUMENTOBJECT_BY_ID_AND_VERSIONNUMBER + objectId + " " + versionNumber + " " + getTenantId());
            documentObject = jdbcTemplate.queryForObject(QueryConstants.FETCH_DOCUMENTOBJECT_BY_ID_AND_VERSIONNUMBER, new Object[]{objectId,versionNumber, getTenantId()},(resultSet, i) -> RowMappers.documentObjectMapper(resultSet));
             return documentObject;
        }catch(EmptyResultDataAccessException exception){
            throw new GenericExceptionHandler("No such document with this versionNumber!", HttpStatus.NOT_FOUND);

        }catch(Exception ex) {
            ex.printStackTrace();
            return null; }
    }


    @Override
    public int addDocumentObject(DocumentObject documentObject) {
        Date now = new Date();
        String filePath ="";
        int tenantId = getTenantId();
        int duplicateName = jdbcTemplate.queryForObject(QueryConstants.FILE_WITH_SAME_NAME_EXISTS, new Object[]{documentObject.getDocumentName(),documentObject.getParentFolderId(),tenantId},Integer.class);

        if(duplicateName > 0)
            throw new GenericExceptionHandler("Document with same name already exists",HttpStatus.CONFLICT);

        int duplicateId = jdbcTemplate.queryForObject(QueryConstants.FILE_WITH_SAME_ID_EXISTS, new Object[]{documentObject.getDocumentId(),documentObject.getParentFolderId(),tenantId},Integer.class);
        if(duplicateId > 0)
            throw new GenericExceptionHandler("Document with same id already exists",HttpStatus.CONFLICT);

        if(documentObject.getParentFolderId() == -1){
            filePath = documentObject.getDocumentName() + "_" + documentObject.getDocumentId() + "_" + tenantId;
        }else{
            try {
                String parentFolderPath = jdbcTemplate.queryForObject(QueryConstants.GET_PARENT_FOLDER_PATH, new Object[]{documentObject.getParentFolderId(),tenantId}, String.class);
                filePath = parentFolderPath + documentObject.getDocumentName() + "_" + documentObject.getDocumentId() + "_" + tenantId;
            }catch (EmptyResultDataAccessException exception){
                throw new GenericExceptionHandler("Given parent folder id= " + documentObject.getParentFolderId() + " does not exist!",HttpStatus.NOT_FOUND);
            }
        }

        Number objectId;
        try {
            boolean hasLinkedFiles;
            if(documentObject.getLinkedFileId().size() >0)
                hasLinkedFiles = true;
            else
                hasLinkedFiles = false;

            SimpleJdbcInsert addDocument = new SimpleJdbcInsert(jdbcTemplate.getDataSource()).withTableName(TableNames.DOCUMENTOBJECT).
                    usingGeneratedKeyColumns(SqlColumn.OBJECT_ID);
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(SqlColumn.DOCUMENT_NAME, documentObject.getDocumentName())
                    .addValue(SqlColumn.DOCUMENT_ID, documentObject.getDocumentId())
                    .addValue(SqlColumn.DOCUMENT_TYPE, documentObject.getDocumentType())
                    .addValue(SqlColumn.PATH, filePath)
                    .addValue(SqlColumn.PARENT_OBJECT_ID, documentObject.getParentFolderId())
                    .addValue(SqlColumn.DATE_CREATED, now)
                    .addValue(SqlColumn.DATE_DELETED, documentObject.getDateDeleted())
                    .addValue(SqlColumn.IS_DELETED,Boolean.FALSE)
                    .addValue(SqlColumn.HAS_LINKED_FILES, hasLinkedFiles)
                    .addValue(SqlColumn.IS_FILE, Boolean.TRUE)
                    .addValue(SqlColumn.TENANT_ID,tenantId);

           objectId = addDocument.executeAndReturnKey(parameters);
            int moduleCount;

            for (Module module: documentObject.getModules()) {
                moduleCount = jdbcTemplate.queryForObject(QueryConstants.GIVEN_MODULE_EXISTS, new Object[]{module.getModuleId(),tenantId}, Integer.class);
                if(moduleCount < 1)
                    throw new GenericExceptionHandler("Given module does not exists!",HttpStatus.NOT_FOUND);
            }

            this.insertBatch(objectId.intValue(), documentObject.getModules(),tenantId);
            System.out.println("Version number is " + documentObject.getVersionNumber());
            this.updateVersion(objectId.intValue(), documentObject.getVersionNumber(),documentObject.getVersionStartingNumber(), now);
            this.updateChangeControl(objectId.intValue(),documentObject.getChangeControlNumber(),documentObject.getVersionNumber());
            saveEventLog(documentObject,Constants.CREATE_FILE);
            if(hasLinkedFiles)
                this.saveLinkedFiles(objectId.intValue(),documentObject.getLinkedFileId());
        }catch(DuplicateKeyException exception){
            throw new GenericExceptionHandler("Document with this id already exists!",HttpStatus.CONFLICT);
        }catch (Exception ex){
            throw new GenericExceptionHandler("Some error occured. Contact Administrator",HttpStatus.BAD_REQUEST);
        }
        return objectId.intValue();
    }

    private void saveEventLog(DocumentObject documentObject,String activity) {
        Event_Log evenLog = new Event_Log();

        Principal principal = request.getUserPrincipal();
        String username = principal.getName();


        evenLog.setUser_Id(Constants.USER_ID);
        evenLog.setUsername(username);
        evenLog.setActivity(activity);
        evenLog.setIp_Address(getClientIp());
        evenLog.setType(Constants.EVENT_LOG_TYPE_DOCUMENT);
        evenLog.setTenant_Id(getTenantId());
        evenLog.setCreated_Date(new Date());
        evenLog.setComponent_Id(documentObject.getObjectId());

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
    public List<DocumentVersion> getVersions(int objectId, boolean log) {

        DocumentObject documentObject = getDocumentObjectById(objectId);
        if(documentObject.isLocked())
            throw new GenericExceptionHandler("File is locked",HttpStatus.CONFLICT);

        List<DocumentVersion> documentVersions = jdbcTemplate.query(QueryConstants.GET_DOCUMENT_VERSIONS,new Object[] {objectId},(resultSet, i) -> RowMappers.documentVersion(resultSet));
        if(log)
            saveEventLog(documentObject,Constants.FILE_VERSIONS_VIEWED);
        return documentVersions;
    }



    @Override
    public boolean checkInFile(DocumentObject documentObject, RequestBodyObject requestBodyObject) {
        boolean selfLocked = false;
        TenantConfiguration tenantConfiguration;
       int tenantId = getTenantId();
        String versionNumber;

        if(documentObject.isLocked() == Boolean.TRUE && !getSelfLocked(documentObject)) {
            throw new GenericExceptionHandler("File is locked!", HttpStatus.CONFLICT);
        }else {
            if(requestBodyObject.getVersionNumber() == null || requestBodyObject.getVersionNumber().isEmpty() || requestBodyObject.getChangeControlNumber() == null ||
                        requestBodyObject.getChangeControlNumber().isEmpty())
                    throw new GenericExceptionHandler("Version number and/or change control number are required!",HttpStatus.NOT_ACCEPTABLE);
                versionNumber = requestBodyObject.getVersionNumber();
            }

            Date now = new Date();
            System.out.println("new version will be " + versionNumber);
            int versionId = this.updateVersion(documentObject.getObjectId(),versionNumber, requestBodyObject.getVersionStartingNumber(), now);
            if(versionId >0 ) {
                updateChangeControl(documentObject.getObjectId(), requestBodyObject.getChangeControlNumber(), versionId + "");
                documentObject.setLocked(false);
                save(documentObject);

                saveEventLog(documentObject, Constants.FILE_NEW_VERSION);

                return true;
            }else
                return false;
    }

    private boolean getSelfLocked(DocumentObject documentObject){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        String whoLockedName = jdbcTemplate.queryForObject(QueryConstants.GET_WHO_LOCKED_DOCUMENT_ID,new Object[]{documentObject.getObjectId()},String.class);
        return name.equals(whoLockedName);

    }
    @Override
    public void update(String id, RequestBodyObject requestBodyObject) {

    }

    @Override
    public boolean checkOutFile(DocumentObject documentObject, RequestBodyObject requestBodyObject) {
        if(documentObject.isLocked() == Boolean.TRUE)
            throw new GenericExceptionHandler("File is locked!", HttpStatus.NOT_ACCEPTABLE);
        try{
            updateChangeControlWithoutVersion(documentObject.getObjectId(),requestBodyObject.getChangeControlNumber());
            addComments(documentObject,requestBodyObject.getComments());
            documentObject.setLocked(true);
            save(documentObject);
            saveEventLog(documentObject,Constants.FILE_CHECK_OUT);
            return true;
        }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        return false;
    }

    @Override
    public void cancelCheckout(DocumentObject documentObject){

        if(documentObject.isLocked() == Boolean.TRUE) {
            boolean selfLocked = false;
            selfLocked = getSelfLocked(documentObject);
            if(selfLocked){
                documentObject.setLocked(false);
                save(documentObject);
                saveEventLog(documentObject,Constants.CANCEL_CHECKOUT);
            }else {
                throw new GenericExceptionHandler("File is locked!", HttpStatus.CONFLICT);
            }
        }else{
            throw new GenericExceptionHandler("File is not locked!", HttpStatus.NOT_ACCEPTABLE);
        }
    }


    @Override
    public List<DocumentObject> getAllDocumentsByParentFolderId(int parentFolderId,String sortBy) {

        int tenantId = getTenantId();

        String sql=QueryConstants.FETCH_CHILDFILES_BY_PARENTFOLDER_ID;
        if(sortColumns.contains(sortBy)) {
            sql += " ORDER BY " + sortBy + ",documentId";
        }else{
            sql += " ORDER BY documentId";
        }

        List<DocumentObject> childFiles  = jdbcTemplate.query(sql,new Object[]{parentFolderId,tenantId},((resultSet, i) -> RowMappers.documentObjectMapper(resultSet)));
        for (DocumentObject documentObject : childFiles) {
            List<Module> modules = jdbcTemplate.query(QueryConstants.GET_DOCUMENT_MODULES,new Object[] {documentObject.getObjectId(),tenantId},(resultSet, i) -> RowMappers.moduleMapper(resultSet));
            if(!modules.isEmpty()) {
                documentObject.setModules(modules);
            }
        }
        return childFiles;
    }

    @Override
    public List<DocumentObject> getAllDocumentsAndFoldersByParentFolderId(int parentFolderId,String sortBy, int limit, int offset) {
        int tenantId = getTenantId();
        String sql=QueryConstants.FETCH_FILEANDFOLDERS_BY_PARENTFOLDERID;
        if(sortColumns.contains(sortBy)) {
            sql += " , " + sortBy + ",documentId";

        }else{
            sql += " ,documentId";
        }

        if(limit > 0 && offset >= 0){
            sql += " LIMIT " + limit + " OFFSET " + offset;
        }


        List<DocumentObject> childFiles  = jdbcTemplate.query(sql,new Object[]{parentFolderId,tenantId},((resultSet, i) -> RowMappers.documentObjectMapper(resultSet)));
        List<Module> modules = new ArrayList<Module>();
        for (DocumentObject documentObject : childFiles) {
            if(documentObject.isFile())
                modules = jdbcTemplate.query(QueryConstants.GET_DOCUMENT_MODULES,new Object[] {documentObject.getObjectId(),tenantId},(resultSet, i) -> RowMappers.moduleMapper(resultSet));
            else
                modules = jdbcTemplate.query(QueryConstants.GET_FOLDER_MODULES,new Object[] {documentObject.getDocumentId()},(resultSet, i) -> RowMappers.moduleMapper(resultSet));
            if(!modules.isEmpty()) {
                documentObject.setModules(modules);
            }
        }
        return childFiles;
    }

    @Override
    public Page<DocumentObject> getAllDocumentsAndFoldersByParentFolderIdPaginated(int parentFolderId,String sortBy, int pageSize, int offset) {
        Page<DocumentObject> page = new Page<DocumentObject>();
        int previousPage = -1;
        int nextPage = -1;
        int firstPage = -1;
        int rowCount = 0;
        int lastPage = -1;

        int tenantId = getTenantId();
        String sql=QueryConstants.FETCH_FILEANDFOLDERS_BY_PARENTFOLDERID;

        if(sortColumns.contains(sortBy)) {
            sql += " , " + sortBy + ",documentId";

        }else{
            sql += " ,documentId";
        }

        rowCount = jdbcTemplate.queryForObject(QueryConstants.FETCH_FILEANDFOLDERS_BY_PARENTFOLDERID_ROW_COUNT,new Object[]{parentFolderId,tenantId},Integer.class);

        page.setTotalRecordCount(rowCount);

        if(pageSize > 0 && offset >= 0){
            sql += " LIMIT " + pageSize + " OFFSET " + offset;

            //to setup previous page
            if((offset-pageSize)>=0) {
                previousPage = offset - pageSize;
                firstPage = 0;

            }

            //to set up next page
            if((offset+pageSize) < rowCount){
                nextPage = offset + pageSize;
                lastPage = ((rowCount-1)/pageSize)*pageSize;

            }

        }


        List<DocumentObject> childFiles  = jdbcTemplate.query(sql,new Object[]{parentFolderId,tenantId},((resultSet, i) -> RowMappers.documentObjectMapper(resultSet)));
        List<Module> modules = new ArrayList<Module>();
        for (DocumentObject documentObject : childFiles) {
            if(documentObject.isFile()) {
                modules = jdbcTemplate.query(QueryConstants.GET_DOCUMENT_MODULES, new Object[]{documentObject.getObjectId(),tenantId}, (resultSet, i) -> RowMappers.moduleMapper(resultSet));

            }
            else {
                modules = jdbcTemplate.query(QueryConstants.GET_FOLDER_MODULES, new Object[]{documentObject.getDocumentId()}, (resultSet, i) -> RowMappers.moduleMapper(resultSet));
            }
            if(!modules.isEmpty()) {
                documentObject.setModules(modules);
            }
        }
        page.setPageItems(childFiles);
        page.setPreviousPage(previousPage);
        page.setNextPage(nextPage);
        page.setFirstPage(firstPage);
        page.setLastPage(lastPage);
        return page;
    }

    @Override
    public List<FileNameObject> getAllDocumentsByName(String documentName) {
        List<FileNameObject> documentObjects = null;
        String sql = QueryConstants.GET_DOCUMENTS_BY_NAME + documentName + "%'";

        try{
            documentObjects  = jdbcTemplate.query(sql, new Object[]{getTenantId()},((resultSet, i) -> RowMappers.linkedFileObject(resultSet)));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return documentObjects;
    }

    public int updateFileDetails(int objectId, String versionNumber, String mimeType, String fileName, String fileExtension, long fileSize ) {
        //jdbcTemplate.update(QueryConstants.UPDATE_UPLOADED_TO_S3,true,mimeType,fileName,fileExtension,fileSize,objectId,versionNumber,getTenantId());

        Map<String,Object> map = new HashMap<>();

        map = jdbcTemplate.queryForMap(QueryConstants.UPDATE_FILE_DETAILS,new Object[]{getTenantId(),objectId,versionNumber,mimeType,fileName,fileExtension,fileSize});

        int result = (Integer)map.get("result");

        return result;

    }

    /*@Override
    public String getFileExtension(int documentId, String versionNumber) {

        try {
            return jdbcTemplate.queryForObject(QueryConstants.FETCH_FILE_EXTENSION, new Object[]{documentId, versionNumber},String.class);
        }catch(EmptyResultDataAccessException ex){
            throw new GenericExceptionHandler(documentId,versionNumber);
        }

    }
*/
    @Override
    public void saveToS3Bucket(DocumentObject documentObject, MultipartFile filepayload){

            boolean contains = Arrays.asList(Constants.contentTypesAllowed).contains(filepayload.getContentType());
            if(contains){
            try {

                String fileName = FilenameUtils.removeExtension(filepayload.getOriginalFilename());
                String fileExtension = FilenameUtils.getExtension(filepayload.getOriginalFilename());
                int result = updateFileDetails(documentObject.getObjectId(),documentObject.getVersionNumber(),filepayload.getContentType(),fileName,fileExtension,filepayload.getSize());
                if(result != 1)
                    throw new GenericExceptionHandler("Storgae limit exceeded!", HttpStatus.INSUFFICIENT_STORAGE);
                else
                    bucketAccesService.saveFile(documentObject,filepayload);
            }catch(IOException ioEx){
                ioEx.printStackTrace();
            }
            }else{
                System.out.println("File type " + filepayload.getContentType() + " is not allowed!");
                throw new GenericExceptionHandler("This file type is not allowed",HttpStatus.NOT_ACCEPTABLE);
            }
    }

    @Override
    public void getFromS3Bucket(DocumentObject documentObject, HttpServletResponse response, String download) {

        if(documentObject.isLocked())
            throw new GenericExceptionHandler("This file is locked!", HttpStatus.NOT_ACCEPTABLE);

        try {
            if(download.equalsIgnoreCase("true"))
                saveEventLog(documentObject,Constants.FILE_CONTENT_DOWNLOADED);
            else
                saveEventLog(documentObject,Constants.FILE_CONTENT_VIEWED);

            bucketAccesService.readFromS3Bucket(documentObject,response, download);

        }catch(IOException ioEx){
            ioEx.printStackTrace();
        }
    }

    @Override
    public void getSpecificVersionFromS3(DocumentObject documentObject, HttpServletResponse response, String download, String versionId) {

        if(documentObject.isLocked())
            throw new GenericExceptionHandler("This file is locked",HttpStatus.NOT_ACCEPTABLE);

        documentObject.setVersionNumber(versionId);
        try{
            if(download.equalsIgnoreCase("true"))
                saveEventLog(documentObject,Constants.FILE_CONTENT_DOWNLOADED);
            else
                saveEventLog(documentObject,Constants.FILE_CONTENT_VIEWED);
            bucketAccesService.readVersions(documentObject,response,download);
        }catch(IOException ioEx){
            ioEx.printStackTrace();
        }


    }

    @Override
    public void addComments(DocumentObject documentObject, String comments) {
        jdbcTemplate.update(QueryConstants.INSERT_COMMENTS,new Object[]{documentObject.getObjectId(),documentObject.getVersionNumber(), comments,new Date(),USER_ID, getTenantId()});
        saveEventLog(getDocumentObjectById(documentObject.getObjectId()),Constants.FILE_COMMENTS_ADDED);
    }

    @Override
    public List<Comments> getCommentsByObjectId(int objectId) {
        List<Comments> comments = null;
        try {
             comments = jdbcTemplate.query(QueryConstants.FETCH_DOCUMENT_COMMENTS, new Object[]{objectId, getTenantId()}, (resultSet, i) -> RowMappers.commentsObjectMapper(resultSet));
        }catch(EmptyResultDataAccessException ex){
            throw new GenericExceptionHandler("No comments available", HttpStatus.NOT_FOUND);
        }
        saveEventLog(getDocumentObjectById(objectId),Constants.FILE_COMMENTS_VIEWED);
        return comments;
    }

    private void updateChangeControl(int objectId, String changeControlNumber,String versionId) {
        jdbcTemplate.update(QueryConstants.INSERT_CHANGE_CONTROL_DOCUMENT_ID,new Object[]{objectId,changeControlNumber,versionId});
    }

    private void updateChangeControlWithoutVersion(int objectId, String changeControlNumber) {
        jdbcTemplate.update(QueryConstants.INSERT_CHANGE_CONTROL_DOCUMENT_ID,new Object[]{objectId,changeControlNumber,null});
    }

    @Override
	public int save(DocumentObject documentObject) {
			jdbcTemplate.update(QueryConstants.PARTIAL_UPDATE_DOCUMENT_ID,documentObject.getParentFolderId(),
                        documentObject.isLocked(),documentObject.getObjectId());
		return documentObject.getObjectId();
	}

    @Override
    public Integer moveFile(DocumentObject documentObject, int parentFolderId, String skipassociation) {
        List<DocumentVersion> versionsList;
        int tenantId = getTenantId();
        try {
            int count = jdbcTemplate.queryForObject(QueryConstants.PARENT_FOLDER_WITH_ID,new Object[]{parentFolderId,tenantId},Integer.class);
            if(count < 1 && parentFolderId != -1)
                throw new GenericExceptionHandler("No such parent folder wit id = " + parentFolderId + " exists!", HttpStatus.NOT_FOUND);
            int duplicateName = jdbcTemplate.queryForObject(QueryConstants.FILE_WITH_SAME_NAME_EXISTS, new Object[]{documentObject.getDocumentName(),parentFolderId,tenantId},Integer.class);
            if(duplicateName > 0)
                throw new GenericExceptionHandler("Document with same name already exists",HttpStatus.CONFLICT);

            int duplicateId = jdbcTemplate.queryForObject(QueryConstants.FILE_WITH_SAME_ID_EXISTS, new Object[]{documentObject.getDocumentId(),parentFolderId,tenantId},Integer.class);
            if(duplicateId > 0)
                throw new GenericExceptionHandler("Document with same id already exists",HttpStatus.CONFLICT);

            boolean sameassociation;
            if(skipassociation.equalsIgnoreCase("true") || parentFolderId == -1)
                sameassociation = true;
            else
                sameassociation = checkmodulesAssociation(documentObject,parentFolderId);

            if(sameassociation) {
                System.out.println("Skipped assosiation!");
                String newPath ="";
                String oldPath = "";
                String parentPath="";
                String documentPath="";

                if(parentFolderId != -1) {

                        parentPath = jdbcTemplate.queryForObject(QueryConstants.GET_PARENT_FOLDER_PATH, new Object[]{documentObject.getParentFolderId(),tenantId}, String.class);

                    documentPath  = parentPath + documentObject.getDocumentName() + "_" + documentObject.getDocumentId() + "_" + tenantId;
                }
                else{
                    documentPath = documentObject.getDocumentName() + "_" + documentObject.getDocumentId() + "_" + tenantId;
                }
                oldPath = documentObject.getPath();

                versionsList = getVersions(documentObject.getObjectId(),false);

                jdbcTemplate.update(QueryConstants.MOVE_DOCUMENT, documentObject.getParentFolderId(), documentPath, documentObject.getObjectId());
                for (DocumentVersion documentVersion: versionsList) {
                    oldPath = documentObject.getPath();
                    if (documentObject.getParentFolderId() != -1) {
                        newPath = parentPath + documentObject.getDocumentName() + "_" + documentObject.getDocumentId() + "_" + tenantId + "_" + documentVersion.getDocumentVersionNumber();
                    }else{
                        newPath = documentObject.getDocumentName() + "_" + documentObject.getDocumentId() + "_" + tenantId + "-" + documentVersion.getDocumentVersionNumber();
                    }
                    documentObject.setVersionNumber(documentVersion.getDocumentVersionNumber());
                    oldPath += "_" +documentVersion.getDocumentVersionNumber();

                    System.out.println(newPath);
                    System.out.println(oldPath);
                    bucketAccesService.moveFile(oldPath,newPath);
                }
                saveEventLog(documentObject,Constants.FILE_MOVED);
                return 1;
            }
            else{
                return -1;
            }
        }catch (EmptyResultDataAccessException exception){
            throw new GenericExceptionHandler("No such parent folder exists!",HttpStatus.NOT_FOUND);
        }

    }


    public boolean checkmodulesAssociation(DocumentObject documentObject,int parentFolderId){
        List<Module> fileModules = documentObject.getModules();
        List<Module> folderModules = jdbcTemplate.query(QueryConstants.GET_FOLDER_MODULES,new Object[] {parentFolderId},(resultSet, i) -> RowMappers.moduleMapper(resultSet));
        ArrayList<String> fileModulesIds = new ArrayList<String>();
        ArrayList<String> folderModulesId = new ArrayList<String>();


        for (Module module:fileModules) {
            System.out.println(module.getModuleId() + " " + module.getModuleName());
            fileModulesIds.add(module.getModuleName());

        }

        for (Module modules: folderModules) {

            System.out.println(modules.getModuleId() + " " + modules.getModuleName());
            folderModulesId.add(modules.getModuleName());
        }


        return equalLists(fileModulesIds,folderModulesId);

    }

    public  boolean equalLists(List<String> one, List<String> two){
        if (one == null && two == null){
            return true;
        }

        if((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()){
            return false;
        }


        one = new ArrayList<String>(one);
        two = new ArrayList<String>(two);

        Collections.sort(one);
        Collections.sort(two);
        return one.equals(two);
    }

    private int updateVersion(int objectId, String versionNumber, String versionStartingNumber, Date dateCreated) {

        boolean autoVersioning = false;
        TenantConfiguration tenantConfiguration;
        String sql = QueryConstants.INSERT_DOCUMENT_VERSION_NUMBER;

        try {
            tenantConfiguration = jdbcTemplate.queryForObject(QueryConstants.FETCH_TENANT_CONFIGURATION_DETAILS, new Object[]{getTenantId()}, (resultSet, i) -> RowMappers.tenantConfigurationObjectMapper(resultSet));
            System.out.println("starting number " + versionStartingNumber);
            if(tenantConfiguration.isAutoVersioning()){
                int leastPossibleVersionNumber = jdbcTemplate.queryForObject(QueryConstants.GET_LEAST_POSSIBLE_VERSION_NUMBER,new Object[]{objectId},Integer.class);
                versionNumber = String.valueOf(leastPossibleVersionNumber) + ".0";
                if(versionStartingNumber!=null && !versionStartingNumber.isEmpty() && Integer.parseInt(versionStartingNumber) > 0 ) {
                    if(Integer.parseInt(versionStartingNumber) < leastPossibleVersionNumber){
                        throw new GenericExceptionHandler("Version starting number can not be smaller than " + leastPossibleVersionNumber,HttpStatus.BAD_REQUEST);
                    }
                    versionNumber = versionStartingNumber + ".0";
                }
            }
            System.out.println("version number is " + versionNumber);
        SimpleJdbcInsert addDocument = new SimpleJdbcInsert(jdbcTemplate.getDataSource()).withTableName(TableNames.DOCUMENTVERSION).
                usingGeneratedKeyColumns(SqlColumn.VERSION_ID);

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(SqlColumn.OBJECT_ID, objectId)
                .addValue(SqlColumn.VERSION_NUMBER, versionNumber)
                .addValue(SqlColumn.DATE_CREATED, dateCreated)
                .addValue(SqlColumn.USER_ID,Constants.USER_ID);


            return addDocument.executeAndReturnKey(parameters).intValue();

        }catch(DuplicateKeyException exception){
            throw new GenericExceptionHandler("Document with version number = " + versionNumber + " already exists!",HttpStatus.CONFLICT);
        }catch (NumberFormatException  ex){
            throw new GenericExceptionHandler("Starting version number must be integer",HttpStatus.BAD_REQUEST);
        }
    }

    public void insertBatch(int objectId, final List<Module> modules,int tenatId) {
        String sql = QueryConstants.INSERT_DOCUMENT_MODULE_RELATION;

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Module module = modules.get(i);
                ps.setInt(1, objectId);
                ps.setInt(2, module.getModuleId());
                ps.setInt(3,tenatId);
            }

            @Override
            public int getBatchSize() {
                return modules.size();
            }
        });

    }

    @Override
    public void saveLinkedFiles(int objectId, List<Integer> linkedFiles) {
        int tenantId = getTenantId();
        String sql = "";
        for (Integer i: linkedFiles) {
            sql += "insert into linkedfiles(sourceobjectId,targetobjectId) values(" + objectId + ", " + i + ", " + tenantId +  ")";
        }

        DocumentObject documentObject = getDocumentObjectById(objectId);
        if(documentObject.isLocked())
            throw new GenericExceptionHandler("File is locked",HttpStatus.CONFLICT);

        this.updateBatchLinkedFiles(QueryConstants.INSERT_LINKED_FILES,objectId,linkedFiles,tenantId);
        saveEventLog(getDocumentObjectById(objectId),Constants.FILE_LINKS_ADDED);
        System.out.println(sql);
    }

    @Override
    public void removeLinkedFiles(int objectId, List<Integer> unlinkFiles) {
        int tenantId = getTenantId();
        DocumentObject documentObject = getDocumentObjectById(objectId);
        if(documentObject.isLocked())
            throw new GenericExceptionHandler("File is locked",HttpStatus.CONFLICT);

        this.updateBatchLinkedFiles(QueryConstants.DELETE_LINKED_FILES,objectId,unlinkFiles,tenantId);
        saveEventLog(getDocumentObjectById(objectId),Constants.FILE_LINKS_REMOVED);
    }

    @Override
    public List<FileNameObject> getLinkedFilesByDocumentId(DocumentObject documentObject) {

        if(documentObject.isLocked())
            throw new GenericExceptionHandler("File is locked!",HttpStatus.CONFLICT);

        List<FileNameObject> linkedFiles = jdbcTemplate.query(QueryConstants.GET_LINKED_FILES,new Object[] {documentObject.getObjectId()},(resultSet, i) -> RowMappers.linkedFileObject(resultSet));

        System.out.println(QueryConstants.GET_LINKED_FILES);
        saveEventLog(documentObject,Constants.FILE_LINKS_VIEWED);
        return linkedFiles;

    }

    private void updateBatchLinkedFiles(String sql,int objectId, List<Integer> linkedFiles, int tenantId) {

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, objectId);
                ps.setInt(2, linkedFiles.get(i));
                ps.setInt(3,tenantId);
            }

            public int getBatchSize() {
                return linkedFiles.size();
            }
        });
    }

    private int getTenantId(){
        int tenantId;
        //Principal principal = request.getUserPrincipal();
        String currentUserName = ""; //= principal.getName();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }
        if(currentUserName.equals("user1"))
            tenantId = Constants.USER1_TENANT_ID;
        else
            tenantId = Constants.USER2_TENANT_ID;

        return tenantId;
    }

    private int getUserId(){
        int userId;
        Principal principal = request.getUserPrincipal();
        return Integer.parseInt(Constants.USER_ID);

    }

}
