package com.edocs.controller;

import com.edocs.entities.FolderObject;
import com.edocs.entities.RequestBodyObject;
import com.edocs.exception.GenericExceptionHandler;
import com.edocs.service.FolderObjectService;
import com.edocs.utils.MessageConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * Created by Software_Development on 12/7/2017.
 */

@RestController
public class FolderObjectController {

    @Autowired
    FolderObjectService folderObjectService;

    @RequestMapping(method = RequestMethod.GET, value = "/folderObjects/{id}")
    public FolderObject geFolderObject(@PathVariable String id){
        System.out.println("getting folder id is " + id );
        FolderObject folderObject = folderObjectService.getFolderObjectById(Integer.parseInt(id));
        return folderObject;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/folderObjects")
    public ResponseEntity<FolderObject> saveDocumentObject(@Valid @RequestBody FolderObject folderObject, HttpServletRequest request){
        int folderID = folderObjectService.add(folderObject);
        folderObject = folderObjectService.getFolderObjectById(folderID);

        HttpHeaders headers = new HttpHeaders();
        URI locationURI = URI.create("http://localhost:8080/documentObjects/" + folderObject.getFolderID());
        headers.setLocation(locationURI);
        headers.set("message:", MessageConstants.FOLDER_SAVE_SUCCESS);

        ResponseEntity<FolderObject> responseEntity = new ResponseEntity<FolderObject>(folderObject,headers, HttpStatus.CREATED);

        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/folderObjects/{id}", consumes = "application/json")
    public ResponseEntity<Object>  deleteFolderObject(@PathVariable String id, @RequestBody RequestBodyObject requestBody){
        System.out.println("Commencts are " + requestBody.getComments());
        if(requestBody.getComments() == null || requestBody.getChangeControlNumber() == null || requestBody.getComments().isEmpty() || requestBody.getChangeControlNumber().isEmpty())
            throw new GenericExceptionHandler("Comments and/or change control number are required",HttpStatus.NOT_ACCEPTABLE);
        String message = MessageConstants.NO_ACTION;
        HttpHeaders headers = new HttpHeaders();
        int deleteResult = folderObjectService.delete(Integer.parseInt(id),requestBody.getComments(),requestBody.getChangeControlNumber());
        if(deleteResult == 4) message = MessageConstants.FOLDER_ALREADY_DELETED;
        if(deleteResult == 3) message = MessageConstants.FOLDER_CONTAINS_FILES;
        if(deleteResult == 2) message = MessageConstants.FOLDER_CONTAINS_FOLDERS;
        if(deleteResult == 1) message = MessageConstants.FODLER_DELETE_SUCCESS;

        headers.set("message:", message);
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(headers,HttpStatus.NO_CONTENT);
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/folderObjects/{parentFolderid}/childFolders")
    public List<FolderObject> getChildFolderObjects(@PathVariable String parentFolderid,@RequestParam(name = "sortBy",required = false, defaultValue = "") String sortBy){
        System.out.println("getting child folders for id is " + parentFolderid );
        List<FolderObject> folderObject = folderObjectService.getChildFoldersByFolderId(Integer.parseInt(parentFolderid),sortBy);
        return folderObject;

        /*
         @RequestParam(name = "date")
         @DateTimeFormat(iso = ISO.DATE)
         LocalDate date) {
        */
    }
}
