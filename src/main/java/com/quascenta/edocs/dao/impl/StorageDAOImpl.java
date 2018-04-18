package com.quascenta.edocs.dao.impl;

import com.quascenta.edocs.dao.StorageDAO;
import com.quascenta.edocs.entities.StorageDetails;
import com.quascenta.edocs.exception.GenericExceptionHandler;
import com.quascenta.edocs.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import static com.quascenta.edocs.utils.Constants.USER_ID;

/**
 * Created by Software_Development on 1/24/2018.
 */
@Component
@Transactional
@Repository
public class StorageDAOImpl implements StorageDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private HttpServletRequest request;


    @Override
    public StorageDetails getStorageDetails(int storageId) {

        StorageDetails storageDetails = new StorageDetails();
        try {
            System.out.println(QueryConstants.FETCH_STORAGE_DETAILS + " -- " +storageDetails.getTenantId() );
            storageDetails = jdbcTemplate.queryForObject(QueryConstants.FETCH_STORAGE_DETAILS, new Object[]{getTenantId()}, (resultSet, i) -> RowMappers.storageDetailsObjectMapper(resultSet));
            storageDetails.setTotalFreeStorage(storageDetails.getTotalAllowedStorage() - storageDetails.getTotalUsedStorage());

            storageDetails.setTotalFreeStorageInPercentage(storageDetails.getTotalFreeStorage() * 100 / storageDetails.getTotalAllowedStorage());
            storageDetails.setTotalUsedStorageInPercentage(storageDetails.getTotalUsedStorage() * 100 / storageDetails.getTotalAllowedStorage());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return storageDetails;
    }

    @Override
    public void add(StorageDetails storageDetails) {
        try {
            SimpleJdbcInsert addStorage = new SimpleJdbcInsert(jdbcTemplate.getDataSource()).withTableName(TableNames.STORAGE).
                    usingGeneratedKeyColumns(SqlColumn.STORAGE_ID);
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(SqlColumn.TENANT_ID, storageDetails.getTenantId())
                    .addValue(SqlColumn.ALLOWED_STORAGE, storageDetails.getTotalAllowedStorage());

            addStorage.executeAndReturnKey(parameters);

        } catch (DuplicateKeyException exception) {
            throw new GenericExceptionHandler("Storage with this id already exists!", HttpStatus.CONFLICT);
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void save(StorageDetails storageDetails) {
        try{
            jdbcTemplate.update(QueryConstants.UPDATE_STORAGE,storageDetails.getTotalAllowedStorage(),storageDetails.getTenantId());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void delete(int tenantId) {
        try{
            jdbcTemplate.update(QueryConstants.DELETE_STORAGE_BY_TENANT_ID,new Object[]{tenantId});
        }catch (Exception ex){
            ex.printStackTrace();
        }
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
}
