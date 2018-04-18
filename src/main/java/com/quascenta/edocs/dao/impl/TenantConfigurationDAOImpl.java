package com.quascenta.edocs.dao.impl;

import com.quascenta.edocs.dao.TenantConfigurationDAO;
import com.quascenta.edocs.entities.TenantConfiguration;
import com.quascenta.edocs.exception.GenericExceptionHandler;
import com.quascenta.edocs.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
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

/**
 * Created by ak on 3/5/2018.
 */
@Component
@Transactional
@Repository
public class TenantConfigurationDAOImpl implements TenantConfigurationDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;


    @Override
    public TenantConfiguration getTenantConfiguration() {
        TenantConfiguration tenantConfiguration = new TenantConfiguration();

        try {
            tenantConfiguration = jdbcTemplate.queryForObject(QueryConstants.FETCH_TENANT_CONFIGURATION_DETAILS, new Object[]{getTenantId()}, (resultSet, i) -> RowMappers.tenantConfigurationObjectMapper(resultSet));
        } catch (EmptyResultDataAccessException exception){
            throw new GenericExceptionHandler("No configuration found for tenantId = " + getTenantId() + "!", HttpStatus.NOT_FOUND);
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return tenantConfiguration;
    }

    @Override
    public void add(TenantConfiguration tenantConfiguration) {
        try {
            SimpleJdbcInsert addStorage = new SimpleJdbcInsert(jdbcTemplate.getDataSource()).withTableName(TableNames.TENANT_CONFIGURATION).
                    usingGeneratedKeyColumns(SqlColumn.TENANT_CONFIGURATION_ID);
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(SqlColumn.TENANT_ID, getTenantId())
                    .addValue(SqlColumn.AUTO_VERSIONING, tenantConfiguration.isAutoVersioning());

            addStorage.executeAndReturnKey(parameters);

        } catch (DuplicateKeyException exception) {
            throw new GenericExceptionHandler("This tenant already created, please use PUT to update configuration", HttpStatus.CONFLICT);
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void save(TenantConfiguration tenantConfiguration) {
        try{
            jdbcTemplate.update(QueryConstants.UPDATE_TENANT_CONFIGURATION,tenantConfiguration.isAutoVersioning(),getTenantId());
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
