package com.quascenta.edocs.utils;

public class QueryConstants {




    private QueryConstants() {
	}

	public static final String FETCH_DOCUMENTOBJECT = "SELECT * FROM edocs.documentswithlatestversion WHERE NOT(isDeleted) ";

	public static final String FETCH_DOCUMENTOBJECT_BY_ID = FETCH_DOCUMENTOBJECT + " AND objectId = ? AND tenantId = ? LIMIT 1";

    public static final String FETCH_DOCUMENTOBJECT_BY_ID_AND_VERSIONNUMBER = "SELECT *, documentversion.*, documentversion.datecreated as dateModified " +
                                                        " FROM documentobject LEFT JOIN documentversion ON documentobject.objectId = " +
                                                        " documentversion.objectId WHERE NOT(documentobject.isDeleted) AND documentobject.objectId =? AND versionNumber=? AND tenantId = ? ";

    public static String FETCH_FOLDEROBJECT_BY_ID = "select folderobject.* from folderobject WHERE folderobject.folderId = ? AND folderobject.tenantId=? AND NOT(isDeleted)";

    public static final String FETCH_CHILDFILES_BY_PARENTFOLDER_ID = FETCH_DOCUMENTOBJECT + " AND parentFolderId = ? AND tenantId=? ";

    public static final String FETCH_DOCUMENT_COMMENTS = "select * from comments where objectId=? AND tenantId = ? ";

	public static final String INSERT_DOCUMENT_MODULE_RELATION = "INSERT INTO dmr(objectId,moduleId,tenantId) values(?,?,?)";

    public static final String INSERT_FOLDER_MODULE_RELATION = "INSERT INTO foldermodulerelation(folderId,moduleId) values(?,?)";;

    public static final String INSERT_LINKED_FILES = "CALL insert_linked_files(?,?,?)";

    public static final String DELETE_LINKED_FILES = "CALL delete_linked_files(?,?,?)";

    public static final String GET_LINKED_FILES = " select DO.objectId as linkedFileId, DO.documentName as linkedFileName, REL.objectId as srcId from documentobject as DO inner join " +
                                                  " (select DS.objectId, documentName, targetobjectId from documentobject as DS " +
                                                  " left join linkedfiles ON DS.objectId = linkedfiles.sourceobjectId where objectId = ? ) as REL " +
                                                  " where DO.objectId = REL.targetobjectId ";

    //public static final java.lang.String GET_LINKED_FILES_ID = "select targetDocumentId from linkedfiles where sourceDocumentId=?";

    public static final String GET_DOCUMENTS_BY_NAME = "SELECT objectId as linkedFileId, documentName as linkedFileName FROM edocs.documentswithlatestversion WHERE NOT(isDeleted) AND tenantId = ? and documentName like '%";

    public static final String DELETE_BY_DOCUMENT_ID ="CALL deletedocument(?,?,?,?,?,?)";
    //public static final String DELETE_BY_DOCUMENT_ID =" UPDATE documentobject set isDeleted=TRUE WHERE documentId = ?  ";

    public static final String DELETE_BY_FOLDER_ID = "CALL deletefolder(?,?,?)" ;

    public static final String INSERT_COMMENTS =" INSERT INTO comments(objectId,versionNumber,comments,dateAdded,userID,tenantId) values(?,?,?,?,?,?) ";

    public static final String INSERT_COMMENTS_FOLDER =" INSERT INTO comments(folderId,comments,dateAdded,userID) values(?,?,?,?) ";

    public static final String INSERT_CHANGE_CONTROL_DOCUMENT_ID =" INSERT INTO changecontrol(objectId,changeControlNumber,versionId) values(?,?,?) ";

    public static final String INSERT_CHANGE_CONTROL_FOLDER_ID =" INSERT INTO changecontrol(folderId,changeControlNumber) values(?,?) ";

    public static final String GET_DOCUMENT_VERSIONS =" SELECT * FROM edocs.documentversionswithcomments WHERE objectId = ? ";

    public static final String GET_DOCUMENT_MODULES =" SELECT * FROM documentswithmodules WHERE objectId = ? AND tenantId = ? ";

    public static final String PARTIAL_UPDATE_DOCUMENT_ID =" UPDATE documentobject set parentFolderId=?, locked=? where objectId = ?";

    public static final String MOVE_DOCUMENT = " UPDATE documentobject set parentFolderId=?, path=? where objectId = ?";



    public static final String INSERT_DOCUMENT_VERSION_NUMBER =" INSERT INTO documentversion(objectId,versionNumber,dateCreated) values(?,?,?) ";

    //public static final String FETCH_FILE_EXTENSION = "SELECT fileExtension FROM documentversion WHERE documentId=? AND versionNumber=?";

    //public static final String UPDATE_UPLOADED_TO_S3 = "UPDATE documentversion SET uploadedToS3=?, mimeType=?, fileName = ?, fileExtension=?, fileSize=? WHERE objectId=? AND versionNumber=? ";

    public static final String UPDATE_FILE_DETAILS = "CALL save_file(?,?,?,?,?,?,?)";

    public static final String FETCH_CHILDFOLDERS_BY_PARENTFOLDER_ID = " SELECT folderobject.* FROM folderobject  WHERE parentFolderId = ? AND tenantId = ? AND NOT (isDeleted) " ;

    public static final String FETCH_FILEANDFOLDERS_BY_PARENTFOLDERID = " SELECT * FROM edocs.filefoldercombined WHERE NOT(isDeleted) AND parentFolderId = ? AND tenantId = ? ORDER BY isFile";

    public static final String FETCH_FILEANDFOLDERS_BY_PARENTFOLDERID_ROW_COUNT = " SELECT count(*) FROM edocs.filefoldercombined WHERE NOT(isDeleted) AND parentFolderId = ? AND tenantId = ? ORDER BY isFile";

    public static final String GET_FOLDER_MODULES =" SELECT * FROM folders_with_modules WHERE documentId = ?";

    public static final String FOLDER_WITH_SAME_NAME_EXISTS = "SELECT COUNT(*) FROM folderobject WHERE NOT(isDeleted) AND folderName=? AND parentFolderId=? AND tenantId=?";

    public static final String FILE_WITH_SAME_NAME_EXISTS = "SELECT COUNT(*) FROM documentobject WHERE NOT(isDeleted) AND documentName=? AND parentFolderId=? AND tenantId = ?";

    public static final String FILE_WITH_SAME_ID_EXISTS = "SELECT COUNT(*) FROM documentobject WHERE NOT(isDeleted) AND documentId=? AND parentFolderId=? AND tenantId = ?";

    //public static final String SAME_VERSION_EXISTS = "SELECT COUNT(*) FROM documentversion WHERE NOT(isDeleted) AND documentId=? AND versionNumber=? AND tenantId = ?";

    public static final String PARENT_FOLDER_WITH_ID = "SELECT count(*) FROM folderobject WHERE NOT(isDeleted) AND folderId=? AND tenantId=?";

    public static final String GET_PARENT_FOLDER_PATH = "SELECT path FROM folderobject WHERE NOT(isDeleted) AND folderID=? AND tenantId=?";

    public static final String INSERT_EVENT_LOG = "INSERT INTO event_logs(user_id,username,activity,ip_address,type,tenant_id,created_date,component_id) values(?,?,?,?,?,?,?,?)";

    public static final String GIVEN_MODULE_EXISTS = "SELECT count(*) FROM module WHERE moduleId=? AND tenantId = ?";

    public static final String GET_WHO_LOCKED_DOCUMENT_ID =  "select username from event_logs where type=5 and component_id = ?  and activity = 'FILE_CHECK_OUT'" +
                                                              "order by created_date desc limit 1";

    public static final String FETCH_STORAGE_DETAILS = "SELECT * FROM storage WHERE tenantId = ?";

    public static final String UPDATE_STORAGE = "UPDATE storage SET allowedStorage = ? WHERE tenantId = ?";

    public static final String DELETE_STORAGE_BY_TENANT_ID = "DELETE FROM storage WHERE tenantId=?";

    public static final String FETCH_TENANT_CONFIGURATION_DETAILS = "SELECT * FROM tenantconfiguration WHERE tenantId=?";

    public static final String UPDATE_TENANT_CONFIGURATION = "UPDATE tenantconfiguration SET autoVersioning=? WHERE tenantId=?" ;

    public static final String GET_LEAST_POSSIBLE_VERSION_NUMBER = "SELECT IFNULL(FLOOR(MAX(versionNumber)+1),1) FROM edocs.documentversion WHERE documentversion.versionNumber REGEXP '^[0-9]+\\.?[0-9]*$' and objectId = ?";
}
