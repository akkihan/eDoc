package com.quascenta.edocs.service.impl;

import com.quascenta.edocs.dao.TenantConfigurationDAO;
import com.quascenta.edocs.entities.TenantConfiguration;
import com.quascenta.edocs.service.TenantConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by ak on 3/5/2018.
 */
@Component
@Service
public class TenantConfigurationImpl implements TenantConfigurationService {

    @Autowired
    TenantConfigurationDAO tenantConfigurationDAO;

    @Override
    public TenantConfiguration getTenantConfiguration() {
        return tenantConfigurationDAO.getTenantConfiguration();
    }

    @Override
    public void add(TenantConfiguration tenantConfiguration) {
        tenantConfigurationDAO.add(tenantConfiguration);
    }

    @Override
    public void save(TenantConfiguration tenantConfiguration) {
        tenantConfigurationDAO.save(tenantConfiguration);

    }
}
