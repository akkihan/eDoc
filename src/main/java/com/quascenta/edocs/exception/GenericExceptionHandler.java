package com.quascenta.edocs.exception;

import org.springframework.http.HttpStatus;

/**
 * Created by Software_Development on 12/14/2017.
 */
public class GenericExceptionHandler extends RuntimeException {

    private int id;

    private String versionNumber;
    private String message = "Data Not Found!!!";
    private HttpStatus httpStatus;


    public GenericExceptionHandler(int id){
        super();
        this.message = "Data with id = " + id + " not found!";
    }

    public GenericExceptionHandler(int documentId, String versionNumber) {
        this.id = documentId;
        versionNumber=versionNumber;
        this.message = "No such version(" + versionNumber +") for this id = " + id;
    }

    public GenericExceptionHandler(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public GenericExceptionHandler(String message) {
        this.message = message;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
