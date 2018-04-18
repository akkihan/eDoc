package com.quascenta.edocs.controller;

import com.quascenta.edocs.entities.StorageDetails;
import com.quascenta.edocs.service.StorageService;
import com.quascenta.edocs.utils.MessageConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by Software_Development on 1/24/2018.
 */
@RestController
public class StorageController {

    @Autowired
    StorageService storageService;

    @RequestMapping(method = RequestMethod.GET, value = "/storage")
    public ResponseEntity<StorageDetails> getStorageDetails(){

        int id =9;
        HttpHeaders headers = new HttpHeaders();
        StorageDetails storageDetails = storageService.getStorageDetails(id);
        headers.set("message:", MessageConstants.FILES_RETREIVED);
        ResponseEntity<StorageDetails> responseEntity = new ResponseEntity<StorageDetails>(storageDetails,headers, HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/storage")
    public ResponseEntity<StorageDetails> setStorageDetails(@Valid @RequestBody StorageDetails storageDetails){

        storageService.add(storageDetails);

        HttpHeaders headers = new HttpHeaders();
        headers.set("message:", MessageConstants.FILES_RETREIVED);
        ResponseEntity<StorageDetails> responseEntity = new ResponseEntity<StorageDetails>(storageDetails,headers, HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/storage")
    public ResponseEntity<StorageDetails> updateStorageDetails(@Valid @RequestBody StorageDetails storageDetails){

        storageService.save(storageDetails);

        HttpHeaders headers = new HttpHeaders();
        headers.set("message:", MessageConstants.FILES_RETREIVED);
        ResponseEntity<StorageDetails> responseEntity = new ResponseEntity<StorageDetails>(storageDetails,headers, HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/storage/{tenantId}", consumes = "application/json")
    public ResponseEntity<Object>  deleteStorageDetails(@PathVariable String tenantId){

        HttpHeaders headers = new HttpHeaders();
        storageService.delete(Integer.parseInt(tenantId));

        headers.set("message:", MessageConstants.DELETE_SUCCESS);
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(headers,HttpStatus.NO_CONTENT);
        return responseEntity;
    }
}

