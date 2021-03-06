package com.edocs.service;

import com.edocs.entities.*;
import com.edocs.utils.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Software_Development on 12/5/2017.
 */
@Service
public interface DocumentObjectService {

    public void save(DocumentObject documentObject);

    public void saveLinkedFiles(int objectId, List<Integer> linkedFiles);

    public void removeLinkedFiles(int documentId, List<Integer> unlinkFiles);

    public void delete(int id, String comments, String changeControlNumber);

    public DocumentObject getDocumentObjectById(int id);

    void update(String id, RequestBodyObject requestBodyObject);

    List<FileNameObject> getLinkedFilesByDocumentId(DocumentObject documentObject);

    int add(DocumentObject documentObject);

    List<DocumentVersion> getVersions(int i, boolean log);

    boolean checkOutFile(DocumentObject documentObject, RequestBodyObject requestBodyObject);

    List<DocumentObject> getAllDocumentsByParentFolderId(int parentFolderId,String sortBy);

    void addComments(DocumentObject documentObject, String comments);

    boolean checkInFile(DocumentObject documentObject, RequestBodyObject requestBodyObject);

    List<DocumentObject> getAllDocumentsAndFoldersByParentFolderId(int parentFolderId, String sortBy, int limit, int offset);

    Page<DocumentObject> getAllDocumentsAndFoldersByParentFolderIdPaginated(int i, String sortBy, int pageSize, int pageNumber);

    //String getFileExtension(int documentId, String versionNumber);

    void saveToS3Bucket(DocumentObject documentObject, MultipartFile filepayload);

    void getFromS3Bucket(DocumentObject documentObject, HttpServletResponse outStream, String download);

    void getSpecificVersionFromS3(DocumentObject documentObject, HttpServletResponse response, String download, String versionId);

    DocumentObject getDocumentObjectByIdAndVersionNumber(int documentId, String versionNumber);

    Integer moveFile(DocumentObject documentObject, int parentFolderId, String aBoolean);

    List<FileNameObject> getAllDocumentsByName(String documentName);

    void cancelCheckout(DocumentObject documentObject);

    List<Comments> getCommentsByObjectId(int objectId);

}
