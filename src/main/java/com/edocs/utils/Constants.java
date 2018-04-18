package com.edocs.utils;

import java.math.BigInteger;

/**
 * Created by Software_Development on 12/6/2017.
 */
public class Constants {




    private Constants() {
    }

    public static final String RESULT_SUCCESS = "success";

    public static final String USER_ID = "99";
    public static final String USERNAME = "AK";

    public static final String IP_ADDRESS = "192.168.0.1";

    public static final int USER1_TENANT_ID = 33;

    public static final int USER2_TENANT_ID = 99;

    public static final String[] contentTypesAllowed = new String []{"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/vnd.ms-excel",
                                                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet","image/jpeg","image/png","application/pdf"};

    public static long fileSizeAllowed = 20971520; // 20MB in bytes 270 526 70

    public static long totalStorage = 5242880; // 5GB in KB

    public static String accessKey = "AKIAI5K3MOD65UU3MMHQ";

    public static String secretKey = "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";

    public static String bucketName = "emdu.ak";

    public static final int EVENT_LOG_TYPE_DOCUMENT = 5;

    public static final int EVENT_LOG_TYPE_FOLDER = 6;

    public static final String CREATE_FILE = "FILE_CREATED";

    public static final String FILE_MOVED = "FILE_MOVED";

    public static final String FILE_NEW_VERSION = "FILE_NEW_VERSION_CREATED";

    public static final String FILE_CHECK_OUT = "FILE_CHECK_OUT";

    public static final String CANCEL_CHECKOUT = "FILE_CHECK_OUT_CANCELLED";

    public static final String FILE_VIEWED = "FILE_VIEWED";

    public static final String FILE_CONTENT_DOWNLOADED = "FILE_CONTENT_DOWNLOADED";

    public static final String FILE_CONTENT_VIEWED = "FILE_CONTENT_VIEWED";

    public static final String FILE_VERSIONS_VIEWED = "FILE_VERSIONS_VIEWED" ;

    public static final String FILE_LINKS_ADDED = "FILE_LINKS_ADDED";

    public static final String FILE_LINKS_REMOVED = "FILE_LINKS_REMOVED";

    public static final String FILE_LINKS_VIEWED = "FILE_LINKS_VIEWED";

    public static final String FILE_DELETED = "FILE_DELETED";

    public static final String FILE_COMMENTS_ADDED = "FILE_COMMENTS_ADDED";

    public static final String FILE_COMMENTS_VIEWED = "FILE_COMMENTS_VIEWED";

    public static final String FOLDER_CREATED = "FOLDER_CREATED";

    public static final String FOLDER_DELETED = "FOLDER_DELETED";
}
