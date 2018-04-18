package com.edocs.service;

import com.edocs.entities.DocumentObject;
import com.edocs.entities.StorageDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

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
