package com.edocs.entities;

/**
 * Created by Software_Development on 12/14/2017.
 */
public class ErrorMessage {

    private String errorMessage;

    private int errorCode;

    private String documentation;


    public ErrorMessage(String errorMessage,int errorCode, String documentation){
        super();
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.documentation = documentation;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
}
