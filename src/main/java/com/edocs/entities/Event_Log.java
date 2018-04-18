package com.edocs.entities;

import java.util.Date;

/**
 * Created by Software_Development on 12/17/2017.
 */
public class Event_Log {

    private int id;

    private String user_Id;

    private String username;

    private String activity;

    private String ip_Address;

    private int type;

    private int tenant_Id;

    private Date created_Date;

    private int component_Id;

    private String componentName;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(String user_Id) {
        this.user_Id = user_Id;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getIp_Address() {
        return ip_Address;
    }

    public void setIp_Address(String ip_Address) {
        this.ip_Address = ip_Address;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTenant_Id() {
        return tenant_Id;
    }

    public void setTenant_Id(int tenant_Id) {
        this.tenant_Id = tenant_Id;
    }

    public Date getCreated_Date() {
        return created_Date;
    }

    public void setCreated_Date(Date created_Date) {
        this.created_Date = created_Date;
    }

    public int getComponent_Id() {
        return component_Id;
    }

    public void setComponent_Id(int component_Id) {
        this.component_Id = component_Id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
}
