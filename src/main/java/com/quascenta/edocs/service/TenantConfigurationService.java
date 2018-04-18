package com.quascenta.edocs.service;

import com.quascenta.edocs.entities.TenantConfiguration;
import org.springframework.stereotype.Service;

/**
 * Created by ak on 3/5/2018.
 */
@Service
public interface TenantConfigurationService {

    TenantConfiguration getTenantConfiguration();

    void add(TenantConfiguration tenantConfiguration);

    void save(TenantConfiguration tenantConfiguration);
}
