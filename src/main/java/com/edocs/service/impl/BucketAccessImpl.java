package com.edocs.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.edocs.entities.DocumentObject;
import com.edocs.entities.StorageDetails;
import com.edocs.exception.GenericExceptionHandler;
import com.edocs.service.BucketAccesService;
import com.edocs.utils.Constants;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.edocs.utils.Constants.bucketName;

/**
 * Created by Software_Development on 1/24/2018.
 */
@Component
@Service
public class BucketAccessImpl implements BucketAccesService {



    public BucketAccessImpl(){

    }

    private AWSCredentials getCredentials(){
        AWSCredentials credentials;
        try {
            credentials = new BasicAWSCredentials(Constants.accessKey,Constants.secretKey);

        } catch (Exception e) {
            throw new GenericExceptionHandler("Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location and is in valid format.");
        }
        return credentials;
    }

    public StorageDetails bucketSize(){

        StorageDetails storageDetails = new StorageDetails();

        long totalSize = 0;

        int numberOfObjects=0;

        try {
            final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(getCredentials())).withRegion(Regions.US_EAST_1).build();

            ObjectListing listing = s3.listObjects(new ListObjectsRequest().withBucketName(bucketName));
            for (S3ObjectSummary objectSummary : listing.getObjectSummaries()) {
                numberOfObjects += 1;
                totalSize += objectSummary.getSize() / 1024;
            }

            storageDetails.setStorageId(numberOfObjects);
            storageDetails.setTotalAllowedStorage(Constants.totalStorage);
            storageDetails.setTotalUsedStorage(totalSize); // set in KB
            storageDetails.setTotalFreeStorage(Constants.totalStorage - totalSize);

            storageDetails.setTotalFreeStorageInPercentage(storageDetails.getTotalFreeStorage() * 100 / Constants.totalStorage);
            storageDetails.setTotalUsedStorageInPercentage(storageDetails.getTotalUsedStorage() * 100 / Constants.totalStorage);

        } catch (AmazonServiceException ase) {
            String message = ase.getMessage() + "/n" + ase.getStatusCode() + "/n" + ase.getErrorCode() + "/n" + ase.getErrorType() + "/n" + ase.getRequestId();
            throw new GenericExceptionHandler(message, HttpStatus.BAD_REQUEST);

        } catch (AmazonClientException ace) {
            String message = ace.getMessage();
            throw new GenericExceptionHandler(message,HttpStatus.SERVICE_UNAVAILABLE);
        }

        return storageDetails;
    }



    public String createFolder(String folderPath) {

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(getCredentials())).withRegion(Regions.US_EAST_1).build();

        String key = folderPath;

        try {
            InputStream input = new ByteArrayInputStream(new byte[0]);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(0);
            s3.putObject(new PutObjectRequest(bucketName, key, input, metadata));
        }catch (AmazonServiceException ase) {
            String message = ase.getMessage() + "/n" + ase.getStatusCode() + "/n" + ase.getErrorCode() + "/n" + ase.getErrorType() + "/n" + ase.getRequestId();
            throw new GenericExceptionHandler(message, HttpStatus.BAD_REQUEST);

        } catch (AmazonClientException ace) {

            String message = ace.getMessage();
            throw new GenericExceptionHandler(message,HttpStatus.SERVICE_UNAVAILABLE);
        }

        return "success";
    }

    public void deleteFolder(String folderPath) {

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(getCredentials())).withRegion(Regions.US_EAST_1).build();

        String key = folderPath;
        try {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName,key);
            s3.deleteObject(deleteObjectRequest);

        } catch (AmazonServiceException ase) {
            String message = ase.getMessage() + "/n" + ase.getStatusCode() + "/n" + ase.getErrorCode() + "/n" + ase.getErrorType() + "/n" + ase.getRequestId();
            throw new GenericExceptionHandler(message, HttpStatus.BAD_REQUEST);

        } catch (AmazonClientException ace) {
            String message = ace.getMessage();
            throw new GenericExceptionHandler(message,HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public String saveFile(DocumentObject documentObject,MultipartFile multipartFile) throws IOException {


        String file_name;
        String fileExtension;
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(getCredentials())).withRegion(Regions.US_EAST_1).build();

        try {
            InputStream inputStream=null;
            if(multipartFile!=null){
                inputStream=multipartFile.getInputStream();   //get file in form of inputstream
            }
            ObjectMetadata md = new ObjectMetadata();
            md.setContentLength(multipartFile.getSize());
            md.setContentType(multipartFile.getContentType());

            fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            file_name = documentObject.getPath() + "_" + documentObject.getVersionNumber();
            System.out.println("filename is " + file_name);
            String mimeType = multipartFile.getContentType();


            PutObjectResult putObjectResult = new PutObjectResult();
            putObjectResult = s3.putObject(bucketName,file_name,inputStream,md);
            return putObjectResult.getVersionId();

        } catch (AmazonServiceException ase) {
            String message = ase.getMessage() + "/n" + ase.getStatusCode() + "/n" + ase.getErrorCode() + "/n" + ase.getErrorType() + "/n" + ase.getRequestId();
            throw new GenericExceptionHandler(message, HttpStatus.BAD_REQUEST);

        } catch (AmazonClientException ace) {
            String message = ace.getMessage();
            throw new GenericExceptionHandler(message,HttpStatus.SERVICE_UNAVAILABLE);
        }

    }

    public boolean readFromS3Bucket(DocumentObject documentObject,HttpServletResponse response, String download) throws IOException {
        String file_name="";
        ServletOutputStream outputStream;
        outputStream = response.getOutputStream();
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(getCredentials())).withRegion(Regions.US_EAST_1).build();

        try {

            file_name = documentObject.getPath() + "_" + documentObject.getVersionNumber();
            String fullFileName = documentObject.getFileName() + "." + documentObject.getFileExtension();
            GetObjectRequest objectRequest = new GetObjectRequest(bucketName,file_name);
            S3Object s3object = s3.getObject(objectRequest);
            try {
                byte[] bbuf = new byte[(int) s3object.getObjectMetadata().getContentLength() + 1024];
                ObjectMetadata objectMetadata = s3object.getObjectMetadata();
                String key = s3object.getKey();

                if(download.equalsIgnoreCase("true"))
                    response.setHeader("Content-Disposition","attachment;filename=" + fullFileName);
                else
                    response.setHeader("Content-Disposition","inline;filename=" + fullFileName);

                response.setContentType(objectMetadata.getContentType());
                DataInputStream in = new DataInputStream(s3object.getObjectContent());

                byte[] buffer = new byte[(int) s3object.getObjectMetadata().getContentLength() + 1024];

                int buf = 0;
                while((buf = in.read(buffer)) > 0)
                {
                    outputStream.write(buffer, 0, buf);
                }
                in.close();

                outputStream.flush();
                outputStream.close();
            }
            catch(IOException io){
                throw new GenericExceptionHandler("Input error occured!", HttpStatus.BAD_REQUEST);
            }
        } catch (AmazonServiceException ase) {
            String message = ase.getMessage() + "/n" + ase.getStatusCode() + "/n" + ase.getErrorCode() + "/n" + ase.getErrorType() + "/n" + ase.getRequestId();
            throw new GenericExceptionHandler(message, HttpStatus.BAD_REQUEST);

        } catch (AmazonClientException ace) {

            String message = ace.getMessage();
            throw new GenericExceptionHandler(message,HttpStatus.SERVICE_UNAVAILABLE);
        }
        return true;
    }

    public void readVersions(DocumentObject documentObject,HttpServletResponse response, String download) throws IOException {

        String file_name="";
        String versionNumber="";
        ServletOutputStream outputStream;

        outputStream = response.getOutputStream();

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(getCredentials())).withRegion(Regions.US_EAST_1).build();

        try {

            file_name = documentObject.getPath() + "_" + documentObject.getVersionNumber();
            versionNumber = documentObject.getVersionNumber();
            String fullFileName = documentObject.getFileName() + "." + documentObject.getFileExtension();
            GetObjectRequest objectRequest = new GetObjectRequest(bucketName,file_name);

            S3Object s3object = s3.getObject(objectRequest);
            try {

                byte[] bbuf = new byte[(int) s3object.getObjectMetadata().getContentLength() + 1024];
                ObjectMetadata objectMetadata = s3object.getObjectMetadata();
                String key = s3object.getKey();


                System.out.println("content disposition "+ objectMetadata.getContentDisposition());
                if(download.equalsIgnoreCase("true"))
                    response.setHeader("Content-Disposition","attachment;filename=" + fullFileName);
                else
                    response.setHeader("Content-Disposition","inline;filename=" + fullFileName);

                response.setContentType(objectMetadata.getContentType());
                DataInputStream in = new DataInputStream(s3object.getObjectContent());

                byte[] buffer = new byte[(int) s3object.getObjectMetadata().getContentLength() + 1024];

                int buf = 0;
                while((buf = in.read(buffer)) > 0)
                {
                    outputStream.write(buffer, 0, buf);
                }
                in.close();

                outputStream.flush();
                outputStream.close();
            }
            catch(IOException io){

            }
        } catch (AmazonServiceException ase) {
            String message = ase.getMessage() + "/n" + ase.getStatusCode() + "/n" + ase.getErrorCode() + "/n" + ase.getErrorType() + "/n" + ase.getRequestId();
            throw new GenericExceptionHandler(message, HttpStatus.BAD_REQUEST);

        } catch (AmazonClientException ace) {

            String message = ace.getMessage();
            throw new GenericExceptionHandler(message,HttpStatus.SERVICE_UNAVAILABLE);
        }

    }
    public void moveFile(String oldPath, String newPath) {

        System.out.println();
        String file_name;
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(getCredentials())).withRegion(Regions.US_EAST_1).build();
        try {
            s3.copyObject(bucketName, oldPath, bucketName,newPath);
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName,oldPath);
            s3.deleteObject(deleteObjectRequest);
        } catch (AmazonServiceException ase) {
            String message = ase.getMessage() + "/n" + ase.getStatusCode() + "/n" + ase.getErrorCode() + "/n" + ase.getErrorType() + "/n" + ase.getRequestId();
            throw new GenericExceptionHandler(message, HttpStatus.BAD_REQUEST);

        } catch (AmazonClientException ace) {
            String message = ace.getMessage();
            throw new GenericExceptionHandler(message,HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
    public boolean delete(String path) {
        String file_name="";
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(getCredentials())).withRegion(Regions.US_EAST_1).build();
        try {

            file_name = path;
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName,file_name);
            s3.deleteObject(deleteObjectRequest);
        } catch (AmazonServiceException ase) {
            String message = ase.getMessage() + "/n" + ase.getStatusCode() + "/n" + ase.getErrorCode() + "/n" + ase.getErrorType() + "/n" + ase.getRequestId();
            throw new GenericExceptionHandler(message, HttpStatus.BAD_REQUEST);

        } catch (AmazonClientException ace) {
            String message = ace.getMessage();
            throw new GenericExceptionHandler(message,HttpStatus.SERVICE_UNAVAILABLE);
        }
        return true;
    }


}
