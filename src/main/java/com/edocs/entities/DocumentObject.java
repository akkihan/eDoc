package com.edocs.entities;

import com.edocs.validator.ManualVersionExists;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Required;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class DocumentObject extends FileSystemObject {

    @NotEmpty(message = "Must specify document name!")
    @Pattern(regexp = "[^/]*", message = "/ character in name is not allowed")
	private String documentName;

    @NotNull(message = "Must specify document ID")
    @Min(value = 1, message = "Must be greater than or equal to 1")
    private int documentId;

	private int tenantID;

	@NotNull
    @Size(min = 1, message = "At least on module must be selected")
    private List<Module> modules = new ArrayList<Module>();
	
	private boolean hasLinkedFiles;

	@ManualVersionExists
	@Pattern(regexp = "[^/]*", message = "/ character in name is not allowed")
	private String versionNumber;


	@Pattern(regexp = "(?<=^| )\\d+",message = "Version starting number can only be positive integer number")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String versionStartingNumber;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private List<Integer> linkedFileId = new ArrayList<Integer>();

	@JsonIgnore
	private String fileExtension;

	@JsonIgnore
	private String fileName;

    @NotEmpty(message = "Must specify a document type")
	private String documentType;

	private boolean isFile;

	private boolean locked;

    @JsonIgnore
    private boolean uploadedToS3;

    @NotEmpty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String changeControlNumber;

	public String getDocumentName() {
		return documentName;
	}


	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public int getDocumentId() {
		return documentId;
	}

	@Required
	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}

	public List<Module> getModules() {
		return modules;
	}

	@Required
	public void setModules(List<Module> modules) {
		this.modules = modules;
	}

	public void addModule(Module module){
		this.modules.add(module);
		}

	public String getDocumentType() {
		return documentType;
	}

	@Required
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public boolean getHasLinkedFiles() {
		return hasLinkedFiles;
	}

	public void setHasLinkedFiles(boolean hasLinkedFiles) {
		this.hasLinkedFiles = hasLinkedFiles;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String verisionNumber) {
		this.versionNumber = verisionNumber;
	}

	public boolean isFile() {
		return isFile;
	}

	public void setisFile(boolean file) {
		isFile = file;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

    public boolean isUploadedToS3() {
        return uploadedToS3;
    }

    public void setUploadedToS3(boolean uploadedToS3) {
        this.uploadedToS3 = uploadedToS3;
    }

	public List<Integer> getLinkedFileId() {
		return linkedFileId;
	}

	public void setLinkedFileId(List<Integer> linkedFileId) {
		this.linkedFileId = linkedFileId;
	}

    public String getChangeControlNumber() {
        return changeControlNumber;
    }

    public void setChangeControlNumber(String changeControlNumber) {
        this.changeControlNumber = changeControlNumber;
    }

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getTenantID() {
		return tenantID;
	}

	public void setTenantID(int tenantID) {
		this.tenantID = tenantID;
	}

	public String getVersionStartingNumber() {
		return versionStartingNumber;
	}

	public void setVersionStartingNumber(String versionStartingNumber) {
		this.versionStartingNumber = versionStartingNumber;
	}


	//private Date createdDate;
	
	
}
