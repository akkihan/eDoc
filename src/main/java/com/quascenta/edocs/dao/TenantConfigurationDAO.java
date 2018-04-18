package com.quascenta.edocs.dao;

import com.quascenta.edocs.entities.TenantConfiguration;

/**
 * Created by ak on 3/5/2018.
 */
public interface TenantConfigurationDAO {

    TenantConfiguration getTenantConfiguration();
    void add(TenantConfiguration tenantConfiguration);

    void save(TenantConfiguration tenantConfiguration);
}
