package com.edocs.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Software_Development on 12/7/2017.
 */
public class FolderObject {

    private int folderID;

    private int tenantID;

    @NotEmpty(message = "Must specify a folder name")
    @Pattern(regexp = "[^/]*", message = "/ character in name is not allowed")
    private String folderName;

    @NotNull
    @Min(value = -1, message = "Parent folder ID must be greated than or equal to -1")
    private int parentFolderId;

    @JsonIgnore
    private String path;

    private Date dateCreated;

    @JsonIgnore
    private Date dateDeleted;

    private Boolean isDeleted;

    @NotNull
    @Size(min = 1, message = "At least on module must be selected")
    private List<Module> modules = new ArrayList<Module>();

    @NotEmpty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String changeControlNumber;

    public int getFolderID() {
        return folderID;
    }

    public void setFolderID(int folderID) {
        this.folderID = folderID;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateDeleted() {
        return dateDeleted;
    }

    public void setDateDeleted(Date dateDeleted) {
        this.dateDeleted = dateDeleted;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public void addModule(Module module){
        this.modules.add(module);
    }

    public int getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(int parentFolderId) {
        this.parentFolderId = parentFolderId;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public String getChangeControlNumber() {
        return changeControlNumber;
    }

    public void setChangeControlNumber(String changeControlNumber) {
        this.changeControlNumber = changeControlNumber;
    }

    public int getTenantID() {
        return tenantID;
    }

    public void setTenantID(int tenantID) {
        this.tenantID = tenantID;
    }
}
