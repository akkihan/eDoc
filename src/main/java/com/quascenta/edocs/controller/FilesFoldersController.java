package com.quascenta.edocs.controller;

import com.quascenta.edocs.entities.DocumentObject;
import com.quascenta.edocs.service.DocumentObjectService;
import com.quascenta.edocs.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

/**
 * Created by Software_Development on 12/9/2017.
 */
@RestController
public class FilesFoldersController {

    @Autowired
    DocumentObjectService documentObjectService;

    @RequestMapping(method = RequestMethod.GET, value = "/FileFolders/{parentFolderid}")
    public ResponseEntity<List<DocumentObject>> getAllDocumentsAndFilesByParentFolderId(@PathVariable String parentFolderid,
                                                                                       @RequestParam(name = "sortBy",required = false, defaultValue = "") String sortBy,
                                                                                       @RequestParam(name= "per_page", required = false, defaultValue = "0") String limit,
                                                                                       @RequestParam(name = "offset", required = false, defaultValue = "-1") String offset,
                                                                                       HttpServletRequest request){
        System.out.println("getting child folders and files for id is " + parentFolderid + " sortby is " + sortBy );
        System.out.print("per page = " + limit + " offset " + offset);


        //List<DocumentObject> documentObjects= documentObjectService.getAllDocumentsAndFoldersByParentFolderId(Integer.parseInt(parentFolderid),sortBy,Integer.parseInt(limit),Integer.parseInt(offset));

        Page<DocumentObject> resultPage = documentObjectService.getAllDocumentsAndFoldersByParentFolderIdPaginated(Integer.parseInt(parentFolderid),sortBy,Integer.parseInt(limit),Integer.parseInt(offset));

        System.out.println("URI is " + request.getRequestURL());
        HttpHeaders headers = new HttpHeaders();

        String prevPage;
        String nextPage;
        String firstPage;
        String lastPage;
        String totalRecords;

        totalRecords =  "" + resultPage.getTotalRecordCount();
        headers.set("totalrecordsnumber:",totalRecords);

        if(resultPage.getPreviousPage() >= 0) {
            prevPage = request.getRequestURL() + "?per_page=" + limit + "&pffset=" + resultPage.getPreviousPage();
            firstPage = request.getRequestURL() + "?per_page=" + limit + "&pffset=" + resultPage.getFirstPage();
            headers.set("first:",firstPage);
            headers.set("prev:", prevPage);
        }

        if(resultPage.getNextPage() >= 0){
            nextPage = request.getRequestURL() + "?per_page=" + limit + "&pffset=" + resultPage.getNextPage();
            lastPage = request.getRequestURL() + "?per_page=" + limit + "&pffset=" + resultPage.getLastPage();
            headers.set("next:", nextPage);
            headers.set("last:",lastPage);
        }





        ResponseEntity<List<DocumentObject>> responseEntity = new ResponseEntity<List<DocumentObject>>(resultPage.getPageItems(),headers, HttpStatus.OK);
        return responseEntity;
        }
        }
