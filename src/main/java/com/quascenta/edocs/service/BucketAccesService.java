package com.quascenta.edocs.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.quascenta.edocs.entities.DocumentObject;
import com.quascenta.edocs.entities.StorageDetails;
import com.quascenta.edocs.exception.GenericExceptionHandler;
import com.quascenta.edocs.utils.Constants;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static com.quascenta.edocs.utils.Constants.bucketName;

/**
 * Created by Software_Development on 12/10/2017.
 */
@Service
public interface BucketAccesService{

    StorageDetails bucketSize();

    String createFolder(String folderPath);

    void deleteFolder(String folderPath);

    String saveFile(DocumentObject documentObject,MultipartFile multipartFile) throws IOException;

    boolean readFromS3Bucket(DocumentObject documentObject,HttpServletResponse response, String download) throws IOException;

    void readVersions(DocumentObject documentObject,HttpServletResponse response, String download) throws IOException;

    void moveFile(String oldPath, String newPath);

    boolean delete(String path);

}
