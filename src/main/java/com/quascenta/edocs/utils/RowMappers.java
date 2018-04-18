package com.quascenta.edocs.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.quascenta.edocs.entities.*;

public class RowMappers {

	private RowMappers() {
	}


    public static DocumentObject documentObjectMapper(ResultSet rs) throws SQLException {
                DocumentObject documentObject = new DocumentObject();
                documentObject.setObjectId(rs.getInt(SqlColumn.OBJECT_ID));
                documentObject.setDocumentId(rs.getInt(SqlColumn.DOCUMENT_ID));
                documentObject.setTenantID(rs.getInt(SqlColumn.TENANT_ID));
                documentObject.setDocumentName(rs.getString(SqlColumn.DOCUMENT_NAME));
                documentObject.setDocumentType(rs.getString(SqlColumn.DOCUMENT_TYPE));
                documentObject.setPath(rs.getString(SqlColumn.PATH));
                documentObject.setParentFolderId(rs.getInt(SqlColumn.PARENT_FOLDER_ID));
                documentObject.setDateCreated(rs.getDate(SqlColumn.DATE_CREATED));
                documentObject.setHasLinkedFiles(rs.getBoolean(SqlColumn.HAS_LINKED_FILES));
                documentObject.setVersionNumber(rs.getString(SqlColumn.VERSION_NUMBER));
                documentObject.setUploadedToS3(rs.getBoolean(SqlColumn.UPLOADED_TO_S3));
                documentObject.setFileName(rs.getString(SqlColumn.FILE_NAME));
                documentObject.setFileExtension(rs.getString(SqlColumn.FILE_EXTENSION));
                documentObject.setisFile(rs.getBoolean(SqlColumn.IS_FILE));
                documentObject.setLocked(rs.getBoolean(SqlColumn.LOCKED));
                documentObject.setDateModified(rs.getDate(SqlColumn.DATE_MODIFIED));
                documentObject.setDeleted(rs.getBoolean(SqlColumn.IS_DELETED));

        return documentObject;
    }
	
	public static Module moduleMapper(ResultSet rs) throws SQLException {


		Module module = new Module();
		
		module.setModuleId(rs.getInt(SqlColumn.MODULE_ID));
		module.setModuleName(rs.getString(SqlColumn.MODULE_NAME));
		
		return module;
	}

    public static FileNameObject linkedFileObject(ResultSet rs) throws SQLException {
        FileNameObject fileNameObject = new FileNameObject();
        fileNameObject.setObjectId(rs.getInt(SqlColumn.LINKED_FILE_ID));
        fileNameObject.setFileName(rs.getString(SqlColumn.LINKED_FILE_NAME));


        return fileNameObject;
    }

    public static DocumentVersion documentVersion(ResultSet rs) throws SQLException {
        DocumentVersion documentVersion = new DocumentVersion();
        documentVersion.setDocumentVersionId(rs.getInt(SqlColumn.VERSION_ID));
        documentVersion.setDocumentVersionNumber(rs.getString(SqlColumn.VERSION_NUMBER));
        documentVersion.setMimeType(rs.getString(SqlColumn.MIME_TYPE));
        documentVersion.setDateCreated(rs.getDate(SqlColumn.DATE_CREATED));
        documentVersion.setLatestComments(rs.getString(SqlColumn.COMMENTS));
        documentVersion.setCreatedUserId(rs.getInt(SqlColumn.USER_ID));


        return documentVersion;
    }

    public static Comments commentsObjectMapper(ResultSet resultSet) throws SQLException{
        Comments comments = new Comments();

        comments.setFolderId(resultSet.getInt(SqlColumn.FOLDER_ID));
        comments.setObjectId(resultSet.getInt(SqlColumn.OBJECT_ID));
        comments.setComments(resultSet.getString(SqlColumn.COMMENTS));
        comments.setDateAdded(resultSet.getDate(SqlColumn.DATE_ADDED));
        comments.setUserId(resultSet.getInt(SqlColumn.USER_ID));

        return comments;
    }

    public static FolderObject folderObjectMapper(ResultSet resultSet) throws SQLException {
        FolderObject folderObject = folderObject = new FolderObject();

        folderObject.setFolderID(resultSet.getInt(SqlColumn.FOLDER_ID));
        folderObject.setFolderName(resultSet.getString(SqlColumn.FOLDER_NAME));
        folderObject.setParentFolderId(resultSet.getInt(SqlColumn.PARENT_FOLDER_ID));
        folderObject.setPath(resultSet.getString(SqlColumn.PATH));
        folderObject.setDateCreated(resultSet.getDate(SqlColumn.DATE_CREATED));
        folderObject.setDateDeleted(resultSet.getDate(SqlColumn.DATE_DELETED));
        folderObject.setDeleted(resultSet.getBoolean(SqlColumn.IS_DELETED));
        folderObject.setTenantID(resultSet.getInt(SqlColumn.TENANT_ID));

        return folderObject;
    }

    public static StorageDetails storageDetailsObjectMapper(ResultSet resultSet) throws SQLException{
        StorageDetails storageDetails = new StorageDetails();

        storageDetails.setStorageId(resultSet.getInt(SqlColumn.STORAGE_ID));
        storageDetails.setTenantId(resultSet.getInt(SqlColumn.TENANT_ID));
        storageDetails.setTotalAllowedStorage(resultSet.getInt(SqlColumn.ALLOWED_STORAGE));
        storageDetails.setTotalUsedStorage(resultSet.getInt(SqlColumn.USER_STORAGE));

        return storageDetails;
    }

    public static TenantConfiguration tenantConfigurationObjectMapper(ResultSet resultSet) throws SQLException{

        TenantConfiguration tenantConfiguration = new TenantConfiguration();

        tenantConfiguration.setTenantId(resultSet.getInt(SqlColumn.TENANT_CONFIGURATION_ID));
        tenantConfiguration.setTenantId(resultSet.getInt(SqlColumn.TENANT_ID));
        tenantConfiguration.setAutoVersioning(resultSet.getBoolean(SqlColumn.AUTO_VERSIONING));

        return tenantConfiguration;
    }

}
