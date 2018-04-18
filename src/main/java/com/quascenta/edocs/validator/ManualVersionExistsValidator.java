package com.quascenta.edocs.validator;

import com.quascenta.edocs.entities.TenantConfiguration;
import com.quascenta.edocs.utils.QueryConstants;
import com.quascenta.edocs.utils.RowMappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.quascenta.edocs.utils.UtilMethods.getTenantId;

/**
 * Created by ak on 3/5/2018.
 */
public class ManualVersionExistsValidator implements ConstraintValidator<ManualVersionExists,String> {

    @Autowired
    JdbcTemplate jdbcTemplate;

    boolean manualValidation = false;
    TenantConfiguration tenantConfiguration;

    @Override
    public void initialize(ManualVersionExists manualVersionExists) {
        tenantConfiguration = jdbcTemplate.queryForObject(QueryConstants.FETCH_TENANT_CONFIGURATION_DETAILS, new Object[]{getTenantId()}, (resultSet, i) -> RowMappers.tenantConfigurationObjectMapper(resultSet));
        manualValidation = !tenantConfiguration.isAutoVersioning();
    }

    @Override
    public boolean isValid(String versionNumber, ConstraintValidatorContext constraintValidatorContext) {
        if(manualValidation){
            if(versionNumber==null || versionNumber.isEmpty())
                return false;
        }
        return true;
    }
}
