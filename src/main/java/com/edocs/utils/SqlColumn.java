package com.edocs.utils;

import java.util.Date;

public class SqlColumn {




	private SqlColumn() {
	}

	/*FolderObject Table Start*/
    public static final String FOLDER_ID = "folderId";
	public static final String FOLDER_NAME = "folderName" ;
	public static final String PARENT_FOLDER_ID = "parentFolderId" ;


	/* documentObject Table Start */
	public static final String OBJECT_ID = "objectId";
	public static final String DOCUMENT_ID = "documentId";
	public static final String DOCUMENT_NAME = "documentName";
	public static final String DOCUMENT_TYPE = "documentType";
	public static final String DOCUMENT_CHANGECONTROLNUMBER = "changeControlNumber";
	public static final String PATH = "path";
	public static final String PARENT_OBJECT_ID = "parentFolderId";
	public static final String COMMENTS = "comments";
	public static final String DATE_CREATED = "datecreated";
	public static final String DATE_DELETED = "datedeleted";
	public static final String HAS_LINKED_FILES = "hasLinkedFiles";
	public static final String VERSION_NUMBER = "versionNumber";
	public static final String UPLOADED_TO_S3 = "uploadedToS3";
	public static final String FILE_NAME = "fileName";
	public static final String FILE_EXTENSION = "fileExtension";
	public static final String IS_FILE = "isFile";
	public static final String LOCKED = "locked";
	public static final String DATE_MODIFIED = "dateModified";
	public static final String IS_DELETED = "isDeleted";
	public static final String TENANT_ID = "tenantId";

	
	/* documentObject Table End */

	/* module Table start */
	public static final String MODULE_ID = "moduleId";
	public static final String MODULE_NAME = "moduleName";

	/*module Table end */

	/* linked files Table */
	public static final String LINKED_FILE_ID = "linkedFileId";
	public static final String LINKED_FILE_NAME = "linkedFileName";

	public static final Object TARGET_DOCUMENT_ID = "targetDocumentId";
	/*linked files Table end*/

	public static final String VERSION_ID = "versionId";
	public static final String MIME_TYPE = "mimeType";


	public static final String DATE_ADDED = "dateAdded";
	public static final String USER_ID = "userId";

	public static final String STORAGE_ID = "storageId";
	public static final String ALLOWED_STORAGE = "allowedStorage";
	public static final String USER_STORAGE = "usedStorage";


	public static final String TENANT_CONFIGURATION_ID = "tenanconfigurationid";
	public static final String AUTO_VERSIONING = "autoVersioning";





}
