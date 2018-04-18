package com.edocs.controller;

import com.edocs.entities.*;
import com.edocs.utils.UtilMethods;
import com.edocs.exception.GenericExceptionHandler;
import com.edocs.service.DocumentObjectService;
import com.edocs.utils.MessageConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Created by Software_Development on 12/5/2017.
 */

@RestController
@Api(value="Working with files", description="Operations related to files")
public class DocumentObjectController {

    @Autowired
    private DocumentObjectService documentObjectService;

    @Autowired
    private HttpServletRequest request;

    @ApiOperation(value = "View details of document object", response = DocumentObject.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved object"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(method = RequestMethod.GET, value = "/documentObjects/{id}", produces = "application/json")
    public DocumentObject getDocumentObject(@PathVariable String id){
        DocumentObject documentObject = documentObjectService.getDocumentObjectById(Integer.parseInt(id));

        if(!documentObject.isDeleted())
            return documentObject;
        else
            return null;
    }


    @ApiOperation(value = "To save new document object", response = DocumentObject.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Document has been saved"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(method = RequestMethod.POST, value = "/documentObjects", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DocumentObject> saveDocumentObject(@Valid @RequestBody DocumentObject documentObject){

        int objectId = documentObjectService.add(documentObject);
        DocumentObject documentObject1 = documentObjectService.getDocumentObjectById(objectId);

        HttpHeaders headers = UtilMethods.setHeaders("message",MessageConstants.SAVE_SUCCESS);
        URI locationURI = URI.create(request.getRequestURL() + "/" + documentObject.getDocumentId());
        headers.setLocation(locationURI);

        ResponseEntity<DocumentObject> responseEntity = new ResponseEntity<DocumentObject>(documentObject1,headers, HttpStatus.CREATED);

        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/documentObjects/{id}/saveFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentObject> saveDocumentObjectFile(@PathVariable String id, @RequestPart(required = true) MultipartFile filepayload,@RequestParam(required = true) String versionNumber) {
        DocumentObject documentObject = documentObjectService.getDocumentObjectByIdAndVersionNumber(Integer.parseInt(id),versionNumber);
        HttpHeaders headers = new HttpHeaders();
            if (!documentObject.isUploadedToS3()) {
                headers = UtilMethods.setHeaders("message", MessageConstants.UPLOAD_SUCCESS);
                documentObjectService.saveToS3Bucket(documentObject, filepayload);
                URI locationURI = URI.create(request.getRequestURL() + "/" + documentObject.getDocumentId());
                headers.setLocation(locationURI);
            } else {
                throw new GenericExceptionHandler("This version number alreday contains file!",HttpStatus.CONFLICT);
            }
            ResponseEntity<DocumentObject> responseEntity = new ResponseEntity<DocumentObject>(documentObject, headers, HttpStatus.CREATED);
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/documentObjects/{id}/getFile")
    public void download(@PathVariable String id,@RequestParam(name="download", required = false, defaultValue = "false") String download, HttpServletResponse response) throws IOException {

        HttpHeaders headers = UtilMethods.setHeaders("message", MessageConstants.FILES_RETREIVED);
        DocumentObject documentObject = documentObjectService.getDocumentObjectById(Integer.parseInt(id));
        ServletOutputStream outStream = response.getOutputStream();
        response.setContentType("");

        documentObjectService.getFromS3Bucket(documentObject,response,download);
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/documentObjects/{id}", consumes = "application/json")
    public ResponseEntity<Object>  deleteDocumentObject(@PathVariable String id, @RequestBody RequestBodyObject requestBody){


        DocumentObject documentObject = documentObjectService.getDocumentObjectById(Integer.parseInt(id));
        if(requestBody.getComments() == null || requestBody.getChangeControlNumber() == null || requestBody.getComments().isEmpty() || requestBody.getChangeControlNumber().isEmpty())
            throw new GenericExceptionHandler("Comments and/or change control number are required",HttpStatus.NOT_ACCEPTABLE);
        documentObjectService.delete(Integer.parseInt(id),requestBody.getComments(),requestBody.getChangeControlNumber());

        HttpHeaders headers = UtilMethods.setHeaders("message", MessageConstants.DELETE_SUCCESS);
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(headers,HttpStatus.NO_CONTENT);
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/documentObjects/{id}/comments", consumes = "application/json")
    public ResponseEntity<Object> addComments(@PathVariable String id, @RequestBody RequestBodyObject requestBodyObject){
        DocumentObject documentObject = documentObjectService.getDocumentObjectById(Integer.parseInt(id));

        String message=MessageConstants.NO_ACTION;

        //to be moved into service object
        if(requestBodyObject.getComments()!= null) {
            documentObjectService.addComments(documentObject,requestBodyObject.getComments());
            message = MessageConstants.COMMENTS_SUCCESS;
        }else{
            throw new GenericExceptionHandler("Comments are required!",HttpStatus.BAD_REQUEST);
        }
        HttpHeaders headers = UtilMethods.setHeaders("message", message);
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(headers,HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/documentObjects/{id}/comments", consumes = "application/json")
    public List<Comments> getComments(@PathVariable String id){
        List<Comments> commentsList = documentObjectService.getCommentsByObjectId(Integer.parseInt(id));

        HttpHeaders headers = UtilMethods.setHeaders("message",MessageConstants.COMMENTS_RETREIVED);
        return commentsList;
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/documentObjects/{id}/moveDocument", consumes = "application/json")
    public ResponseEntity<Object> moveDocument(@PathVariable String id, @RequestBody RequestBodyObject requestBodyObject, @RequestParam(name = "skipassosiation",required = false, defaultValue = "false") String skipassocination) {
        DocumentObject documentObject = documentObjectService.getDocumentObjectById(Integer.parseInt(id));

        String message = MessageConstants.NO_ACTION;

        if (requestBodyObject.getParentFolderId() != 0) {
            documentObject.setParentFolderId(requestBodyObject.getParentFolderId());
            int associated = documentObjectService.moveFile(documentObject,requestBodyObject.getParentFolderId(),skipassocination);
            if (associated == -1)
                throw new GenericExceptionHandler("File and folder are assosiated with different modules",HttpStatus.CONFLICT);
            else
                message = MessageConstants.MOVE_SUCCESS;
        }else{
            throw new GenericExceptionHandler("New parent folder id must be specified",HttpStatus.NOT_ACCEPTABLE);
        }
        HttpHeaders headers = UtilMethods.setHeaders("message", message);
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(headers,HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/documentObjects/{id}/links", consumes = "application/json")
    public ResponseEntity<Object> saveLinkedFiles(@PathVariable String id, @RequestBody(required = true) RequestBodyObject requestBodyObject){
        DocumentObject documentObject = documentObjectService.getDocumentObjectById(Integer.parseInt(id));
        System.out.println("in insert links = " + documentObject.isLocked());

        String message=MessageConstants.NO_ACTION;
        if(requestBodyObject == null) System.out.println("null body");

        System.out.println(requestBodyObject.getLinkedFileId());
        if(requestBodyObject.getLinkedFileId() == null || requestBodyObject.getLinkedFileId().size() < 1){
            message = MessageConstants.ADD_LINKED_FILES;
            throw new GenericExceptionHandler("File id to be linked missing!",HttpStatus.NOT_ACCEPTABLE);
        }else {
            if (documentObject.getObjectId() > 0)
                documentObjectService.saveLinkedFiles(documentObject.getObjectId(), requestBodyObject.getLinkedFileId());
            message = MessageConstants.FILES_LINKED;
        }
        HttpHeaders headers = UtilMethods.setHeaders("message",message);
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(headers,HttpStatus.OK);
        return responseEntity;
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/documentObjects/{id}/links", consumes = "application/json")
    public ResponseEntity<Object> deleteLinkedFiles(@PathVariable String id, @RequestBody RequestBodyObject requestBodyObject){
        DocumentObject documentObject = documentObjectService.getDocumentObjectById(Integer.parseInt(id));
        System.out.println("in remove links = " + documentObject.isLocked());
        String message=MessageConstants.NO_ACTION;

        if(requestBodyObject.getLinkedFileId() == null || requestBodyObject.getLinkedFileId().size() < 1){
            message = MessageConstants.ADD_LINKED_FILES;
            throw new GenericExceptionHandler("File id to be linked missing!",HttpStatus.NOT_ACCEPTABLE);
        }else {
            if (documentObject.getDocumentId() > 0)
                documentObjectService.removeLinkedFiles(documentObject.getObjectId(), requestBodyObject.getLinkedFileId());
        }
        message = MessageConstants.UNLINK_FILES_SUCCESS;
        HttpHeaders headers = UtilMethods.setHeaders("message",message);
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(headers,HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/documentObjects/{id}/links")
    public List<FileNameObject> getLinkedFileDetails(@PathVariable String id){
        DocumentObject documentObject = documentObjectService.getDocumentObjectById(Integer.parseInt(id));

        if(documentObject.getHasLinkedFiles()) {
            List<FileNameObject> linkedFiles = documentObjectService.getLinkedFilesByDocumentId(documentObject);
            return linkedFiles;
        } else
            throw new GenericExceptionHandler("This file has no links!",HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/documentObjects/{id}/versions")
    public List<DocumentVersion> getDocumentVersions(@PathVariable String id){
        DocumentObject documentObject = documentObjectService.getDocumentObjectById(Integer.parseInt(id));
        List<DocumentVersion> documentVersions = documentObjectService.getVersions(Integer.parseInt(id), true);

        return documentVersions;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/documentObjects/{id}/versions/content/{versionId:.+}")
    public String getDocumentVersions(@PathVariable("id") String id, @PathVariable("versionId") String versionId,
                                      @RequestParam(name="download", required = false, defaultValue = "") String download, HttpServletResponse response) throws IOException {
        DocumentObject documentObject = documentObjectService.getDocumentObjectByIdAndVersionNumber(Integer.parseInt(id), versionId);

        System.out.println("Version number is " + versionId);
        ServletOutputStream outStream = response.getOutputStream();
        response.setContentType("");

        documentObjectService.getSpecificVersionFromS3(documentObject,response,download,versionId);
        return "success";
    }



    @RequestMapping(method = RequestMethod.PATCH, value = "/documentObjects/{id}/checkin")
    public ResponseEntity<Object> checkinDocumentObject(@PathVariable String id,@RequestBody RequestBodyObject requestBodyObject){

        DocumentObject documentObject = documentObjectService.getDocumentObjectById(Integer.parseInt(id));
        String message = MessageConstants.CHECKIN_SUCCESS;
        if(documentObjectService.checkInFile(documentObject, requestBodyObject))
            documentObject = documentObjectService.getDocumentObjectById(Integer.parseInt(id));
        HttpHeaders headers = UtilMethods.setHeaders("message", message);
        return new ResponseEntity<Object>(headers,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/documentObjects/{id}/checkout")
    public ResponseEntity<Object> checkOutDocumentObject(@PathVariable String id,@RequestBody RequestBodyObject requestBodyObject){

        DocumentObject documentObject = documentObjectService.getDocumentObjectById(Integer.parseInt(id));
        String message = MessageConstants.CHECKEDOUT_SUCCESS;
        if(requestBodyObject.getComments()==null || requestBodyObject.getComments().isEmpty() || requestBodyObject.getChangeControlNumber() == null ||
                requestBodyObject.getChangeControlNumber().isEmpty())
            throw new GenericExceptionHandler("Comments and change control number are required",HttpStatus.NOT_ACCEPTABLE);
         documentObjectService.checkOutFile(documentObject,requestBodyObject);

        HttpHeaders headers = UtilMethods.setHeaders("message", message);
        return new ResponseEntity<Object>(headers,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/documentObjects/{id}/cancelCheckout", consumes = "application/json")
    public ResponseEntity<Object> cancelCheckOut(@PathVariable String id) {
        DocumentObject documentObject = documentObjectService.getDocumentObjectById(Integer.parseInt(id));

        String message = MessageConstants.CANCEL_CHECKEDOUT_SUCCESS;

        documentObjectService.cancelCheckout(documentObject);
        HttpHeaders headers = UtilMethods.setHeaders("message", message);
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(headers,HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/documentObjects/{id}/review/reject")
    public ResponseEntity<Object> rejectReview(@PathVariable String id){
        String message=MessageConstants.NO_ACTION;

        HttpHeaders headers = UtilMethods.setHeaders("message",MessageConstants.REVIEW_REJECTED);
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(headers,HttpStatus.OK);
        return responseEntity;
    }


    @RequestMapping(method = RequestMethod.PUT, value = "/documentObjects/{id}/review/accept")
    public ResponseEntity<Object> acceptReview(@PathVariable String id){

        String message=MessageConstants.NO_ACTION;

        HttpHeaders headers = UtilMethods.setHeaders("message",MessageConstants.REVIEW_ACCEPTED);
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(headers,HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/Filesbyparentfolder/{parentFolderid}")
    public ResponseEntity<List<DocumentObject>> getAllDocumentsByParentFolderId(@PathVariable String parentFolderid, @RequestParam(name = "sortBy",required = false, defaultValue = "") String sortBy){
        HttpHeaders headers = UtilMethods.setHeaders("message",MessageConstants.FILES_RETREIVED);
        List<DocumentObject> documentObjects = documentObjectService.getAllDocumentsByParentFolderId(Integer.parseInt(parentFolderid),sortBy);
        ResponseEntity<List<DocumentObject>> responseEntity = new ResponseEntity<List<DocumentObject>>(documentObjects,headers,HttpStatus.OK);
        return responseEntity;

    }

    @RequestMapping(method = RequestMethod.GET, value = "/documentObjects")
    public ResponseEntity<List<FileNameObject>> getAllDocumentsByName(@RequestParam(required = true) String documentName){
        System.out.println("searched files is " + documentName);

        List<FileNameObject> documentObjects = documentObjectService.getAllDocumentsByName(documentName);
        HttpHeaders headers = UtilMethods.setHeaders("message",MessageConstants.FILES_RETREIVED);
        ResponseEntity<List<FileNameObject>> responseEntity = new ResponseEntity<List<FileNameObject>>(documentObjects,headers,HttpStatus.OK);
        return responseEntity;
    }

}
