package com.edocs.dao;

import com.edocs.entities.*;
import com.edocs.utils.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


public interface DocumentObjectDAO {

	//public int save(DocumentObject user);
	
	public void delete(int id, String comments, String changeControlNumber);

	DocumentObject getDocumentObjectById(int id);

	int save(DocumentObject documentObject);

	void saveLinkedFiles(int objectId, List<Integer> linkedFiles);

	void removeLinkedFiles(int documentId, List<Integer> unlinkFiles);

	List<FileNameObject> getLinkedFilesByDocumentId(DocumentObject documentObject);

	int addDocumentObject(DocumentObject documentObject);

	List<DocumentVersion> getVersions(int objectId, boolean log);

	boolean checkOutFile(DocumentObject documentObject, RequestBodyObject requestBodyObject);

    List<DocumentObject> getAllDocumentsByParentFolderId(int parentFolderId,String sortBy);

	void addComments(DocumentObject documentObject, String comments);

	boolean checkInFile(DocumentObject documentObject, RequestBodyObject requestBodyObject);

	void update(String id, RequestBodyObject requestBodyObject);

	List<DocumentObject> getAllDocumentsAndFoldersByParentFolderId(int parentFolderId, String sortBy, int limit, int offset);

	Page<DocumentObject> getAllDocumentsAndFoldersByParentFolderIdPaginated(int parentFolderId, String sortBy, int pageSize, int pageNumber);

	//String getFileExtension(int documentId, String versionNumber);

	void saveToS3Bucket(DocumentObject documentObject, MultipartFile filepayload);

	void getFromS3Bucket(DocumentObject documentObject, HttpServletResponse outStream, String download);

	void getSpecificVersionFromS3(DocumentObject documentObject, HttpServletResponse response, String download, String versionId);

	DocumentObject getDocumentObjectByIdAndVersionNumber(int documentId, String versionNumber);

	Integer moveFile(DocumentObject documentObject, int parentFolderId, String skipassociation);

	List<FileNameObject> getAllDocumentsByName(String documentName);

	void cancelCheckout(DocumentObject documentObject);

	List<Comments> getCommentsByObjectId(int objectId);

}
