package com.quascenta.edocs.service.impl;

import com.quascenta.edocs.dao.DocumentObjectDAO;
import com.quascenta.edocs.entities.*;
import com.quascenta.edocs.service.DocumentObjectService;
import com.quascenta.edocs.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Software_Development on 12/5/2017.
 */
@Component
@Service
public class DocumentObjectImpl implements DocumentObjectService{


    @Autowired
    private DocumentObjectDAO documentObjectDAO;

    @Override
    public void save(DocumentObject documentObject) {
        documentObjectDAO.save(documentObject);
    }

    @Override
    public void saveLinkedFiles(int objectId, List<Integer> linkedFiles) {
        documentObjectDAO.saveLinkedFiles(objectId,linkedFiles);

    }

    @Override
    public void removeLinkedFiles(int documentId, List<Integer> unlinkFiles) {
        documentObjectDAO.removeLinkedFiles(documentId,unlinkFiles);
    }

    @Override
    public void delete(int id, String comments, String changeControlNumber) {
        documentObjectDAO.delete(id,comments,changeControlNumber);
    }

    @Override
    public DocumentObject getDocumentObjectById(int id) {
        return documentObjectDAO.getDocumentObjectById(id);
    }

    @Override
    public void update(String id, RequestBodyObject requestBodyObject) {
        documentObjectDAO.update(id, requestBodyObject);
    }

    @Override
    public List<FileNameObject> getLinkedFilesByDocumentId(DocumentObject documentObject) {
       return documentObjectDAO.getLinkedFilesByDocumentId(documentObject);

    }

    @Override
    public int add(DocumentObject documentObject) {
        return documentObjectDAO.addDocumentObject(documentObject);
    }

    @Override
    public List<DocumentVersion> getVersions(int objectId,boolean log) {
        return documentObjectDAO.getVersions(objectId, log);
    }


    @Override
    public boolean checkOutFile(DocumentObject documentObject, RequestBodyObject requestBodyObject) {
        return documentObjectDAO.checkOutFile(documentObject,requestBodyObject);
    }

    @Override
    public List<DocumentObject> getAllDocumentsByParentFolderId(int parentFolderId,String sortBy) {
        return documentObjectDAO.getAllDocumentsByParentFolderId(parentFolderId,sortBy);
    }

    @Override
    public void addComments(DocumentObject documentObject, String comments) {
        documentObjectDAO.addComments(documentObject, comments);
    }

    @Override
    public boolean checkInFile(DocumentObject documentObject, RequestBodyObject requestBodyObject) {
        return documentObjectDAO.checkInFile(documentObject, requestBodyObject);
    }

    @Override
    public List<DocumentObject> getAllDocumentsAndFoldersByParentFolderId(int parentFolderId, String sortBy, int limit, int offset) {
        return documentObjectDAO.getAllDocumentsAndFoldersByParentFolderId(parentFolderId,sortBy, limit, offset);
    }

    @Override
    public Page<DocumentObject> getAllDocumentsAndFoldersByParentFolderIdPaginated(int parentFolderId, String sortBy, int pageSize, int pageNumber) {
        return documentObjectDAO.getAllDocumentsAndFoldersByParentFolderIdPaginated(parentFolderId,sortBy,pageSize,pageNumber );
    }

    /*@Override
    public String getFileExtension(int documentId, String versionNumber) {
        return documentObjectDAO.getFileExtension(documentId,versionNumber);
    }*/

    @Override
    public void saveToS3Bucket(DocumentObject documentObject, MultipartFile filepayload){
        documentObjectDAO.saveToS3Bucket(documentObject,filepayload);

    }

    @Override
    public void getFromS3Bucket(DocumentObject documentObject, HttpServletResponse response, String download) {
        documentObjectDAO.getFromS3Bucket(documentObject,response,download);
    }

    @Override
    public void getSpecificVersionFromS3(DocumentObject documentObject, HttpServletResponse response, String download, String versionId) {
        documentObjectDAO.getSpecificVersionFromS3(documentObject,response,download,versionId);
    }

    @Override
    public DocumentObject getDocumentObjectByIdAndVersionNumber(int documentId, String versionNumber) {
        return documentObjectDAO.getDocumentObjectByIdAndVersionNumber(documentId, versionNumber);
    }

    @Override
    public Integer moveFile(DocumentObject documentObject, int parentFolderId, String skipassociation) {
        return documentObjectDAO.moveFile(documentObject,parentFolderId,skipassociation);
    }

    @Override
    public List<FileNameObject> getAllDocumentsByName(String documentName) {
        return documentObjectDAO.getAllDocumentsByName(documentName);
    }

    @Override
    public void cancelCheckout(DocumentObject documentObject) {
        documentObjectDAO.cancelCheckout(documentObject);
    }

    @Override
    public List<Comments> getCommentsByObjectId(int objectId) {
        return documentObjectDAO.getCommentsByObjectId(objectId);
    }

}
