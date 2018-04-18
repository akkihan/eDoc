package com.edocs.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by ak on 3/5/2018.
 */
public class UtilMethods {

    public static HttpHeaders setHeaders(String title, String message){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(title,message);
        return httpHeaders;
    }

    public static int getTenantId(){
        int tenantId;

        String currentUserName = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }
        if(currentUserName.equals("user1"))
            tenantId = Constants.USER1_TENANT_ID;
        else
            tenantId = Constants.USER2_TENANT_ID;

        return tenantId;
    }
}
