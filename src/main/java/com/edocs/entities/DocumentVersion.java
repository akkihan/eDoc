package com.edocs.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class DocumentVersion {
	
	private int documentVersionId;

	private String documentVersionNumber;

	private Date dateCreated;

	private String mimeType;

    private int createdUserId;

	private String latestComments;

	@JsonIgnore
	private boolean uploadedToS3;

	public int getDocumentVersionId() {
		return documentVersionId;
	}

	public void setDocumentVersionId(int documentVersionId) {
		this.documentVersionId = documentVersionId;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getDocumentVersionNumber() {
		return documentVersionNumber;
	}

	public void setDocumentVersionNumber(String documentVersionNumber) {
		this.documentVersionNumber = documentVersionNumber;
	}

	public boolean isUploadedToS3() {
		return uploadedToS3;
	}

	public void setUploadedToS3(boolean uploadedToS3) {
		this.uploadedToS3 = uploadedToS3;
	}

	public String getLatestComments() {
		return latestComments;
	}

	public void setLatestComments(String latestComments) {
		this.latestComments = latestComments;
	}

	public int getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(int createdUserId) {
		this.createdUserId = createdUserId;
	}
}
