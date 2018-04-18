package com.quascenta.edocs.controller;

import com.quascenta.edocs.entities.TenantConfiguration;
import com.quascenta.edocs.service.TenantConfigurationService;
import com.quascenta.edocs.utils.UtilMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * Created by ak on 3/5/2018.
 */
@Controller
public class TenantConfigurationController {

    @Autowired
    private TenantConfigurationService tenantConfigurationService;

    @GetMapping("/tenantConfiguration")
    public ResponseEntity<TenantConfiguration> getTenantConfiguration(){


        HttpHeaders headers = UtilMethods.setHeaders("message:", "Tenant Configuration Retreived");
        TenantConfiguration tenantConfiguration = tenantConfigurationService.getTenantConfiguration();
        ResponseEntity<TenantConfiguration> responseEntity = new ResponseEntity<TenantConfiguration>(tenantConfiguration,headers, HttpStatus.OK);
        return responseEntity;
    }

    @PostMapping("/tenantConfiguration")
    public ResponseEntity<TenantConfiguration> setTenantAutoConfig(@Valid @RequestBody TenantConfiguration tenantConfiguration){

        tenantConfigurationService.add(tenantConfiguration);
        HttpHeaders headers = UtilMethods.setHeaders("message:","Tenant Configuration Saved!");
        ResponseEntity<TenantConfiguration> responseEntity = new ResponseEntity<TenantConfiguration>(tenantConfiguration,headers,HttpStatus.OK);
        return responseEntity;
    }

    @PutMapping("/tenantConfiguration")
    public ResponseEntity<TenantConfiguration> updateTenantConfiguration(@RequestBody TenantConfiguration tenantConfiguration){
        tenantConfigurationService.save(tenantConfiguration);

        HttpHeaders headers = UtilMethods.setHeaders("message","Tenant Configuration Updated!");
        ResponseEntity<TenantConfiguration> responseEntity = new ResponseEntity<TenantConfiguration>(tenantConfiguration,headers,HttpStatus.OK);
        return responseEntity;
    }
}
