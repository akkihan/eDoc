package com.quascenta.edocs.entities;

/**
 * Created by ak on 3/5/2018.
 */
public class TenantConfiguration {

    private int tenantConfiguration;

    private int tenantId;

    private boolean autoVersioning;


    public int getTenantConfiguration() {
        return tenantConfiguration;
    }

    public void setTenantConfiguration(int tenantConfiguration) {
        this.tenantConfiguration = tenantConfiguration;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public boolean isAutoVersioning() {
        return autoVersioning;
    }

    public void setAutoVersioning(boolean autoVersioning) {
        this.autoVersioning = autoVersioning;
    }
}
