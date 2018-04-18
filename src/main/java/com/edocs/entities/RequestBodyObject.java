package com.edocs.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Software_Development on 12/6/2017.
 */
@XmlRootElement
public class RequestBodyObject {

    @XmlElement
    private String comments;

    @XmlElement
    private String changeControlNumber;

    @XmlElement
    private int parentFolderId;

    @XmlElement
    private List<Integer> linkedFileId;

    @XmlElement
    private Boolean locked;

    @XmlElement
    private String versionNumber;

    @XmlElement
    @Pattern(regexp = "(?<=^| )\\d+",message = "Version starting number can only be positive integer number")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String versionStartingNumber;


    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getChangeControlNumber() {
        return changeControlNumber;
    }

    public void setChangeControlNumber(String changeControlNumber) {
        this.changeControlNumber = changeControlNumber;
    }

    public int getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(int parentFolderId) {
        this.parentFolderId = parentFolderId;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }


    public List<Integer> getLinkedFileId() {
        return linkedFileId;
    }

    public void setLinkedFileId(List<Integer> linkedFileId) {
        this.linkedFileId = linkedFileId;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getVersionStartingNumber() {
        return versionStartingNumber;
    }

    public void setVersionStartingNumber(String versionStartingNumber) {
        this.versionStartingNumber = versionStartingNumber;
    }
}