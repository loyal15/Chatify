package vsppsgv.chatify.im.model;


import vsppsgv.chatify.im.common.ui.CIPackageRunningMode;

/**
 * Created by administrator on 6/17/15.
 */
public class CIConst {

    // Broadcast Message
    public static final String BROADCAST_PUBNUB_READY = "BROADCAST_PUBNUB_READY";
    public static final String BROADCAST_NEW_MESSAGE_ARRIVED = "BROADCAST_NEW_MESSAGE_ARRIVED";
    public static final String BROADCAST_MESSAGE_UPDATED = "BROADCAST_MESSAGE_UPDATED";
    public static final String BROADCAST_RECOMMEND_UPGRADE_APPLICATION = "BROADCAST_RECOMMEND_UPGRADE_APPLICATION";
    public static final String BROADCAST_REQUEST_UPGRADE_APPLICATION = "BROADCAST_REQUEST_UPGRADE_APPLICATION";
    public static final String BROADCAST_LOCALE_CHANGED = "BROADCAST_LOCALE_CHANGED";
    public static final String BROADCAST_COUNTRYLIST_CHANGED = "BROADCAST_COUNTRYLIST_CHANGED";

    public static CIPackageRunningMode PACKAGE_RUNNING_MODE = CIPackageRunningMode.PACKAGE_MODE_DEV_DEVELOPMENT;
    public static boolean LOGIN_WITHOUT_CORRECT_PASSWORD = false;

    /********************* Active Configuration *********************/
    // Remote Logger for Logentires
    public static String REMOTE_LOGGER_TOKEN = null;
    public static final int LOG_LEVEL_VERBOSE = 0;
    public static final int LOG_LEVEL_DEBUG = 1;
    public static final int LOG_LEVEL_INFO = 2;
    public static final int LOG_LEVEL_WARNING = 3;
    public static final int LOG_LEVEL_ERROR = 4;
    public enum LogLevel {
        LOG_LEVEL_VERBOSE,
        LOG_LEVEL_DEBUG,
        LOG_LEVEL_INFO,
        LOG_LEVEL_WARNING,
        LOG_LEVEL_ERROR,
    }
    public static LogLevel REMOTE_LOGGER_LOGGING_LEVEL;

    // Application Required Spec
    public final static int APP_MIN_REQUIRED_VERSION_CODE = 209;

    // App DB
    public final static int APP_LOCAL_DB_VERSION = 5;
    public static String APP_LOCAL_DATABASE_NAME = null;

    // Mint
    public static String MINT_API_KEY = null;

    /********************* Dev Configuration *********************/
    // Remote Logger for Logentires
    public static String DEV_REMOTE_LOGGER_TOKEN = "";
    public static LogLevel DEV_REMOTE_LOGGER_LOGGING_LEVEL = LogLevel.LOG_LEVEL_VERBOSE;

    // App DB
    public static String DEV_APP_LOCAL_DATABASE_NAME = "photoalbum_%s_dev.db";

    // Mint
    public final static String DEV_MINT_API_KEY = "ac250685";

    /********************* Product Configuration *********************/
    // Remote Logger for Logentires
    public static String PROD_REMOTE_LOGGER_TOKEN = "";
    public static LogLevel PROD_REMOTE_LOGGER_LOGGING_LEVEL = LogLevel.LOG_LEVEL_DEBUG;

    // App DB
    public static String PROD_APP_LOCAL_DATABASE_NAME = "photoalbum_%s.db";

    // Mint
    public final static String PROD_MINT_API_KEY = "";

    // Parse
    public final static String DEV_PARSE_APP_ID = "2lzrm6yTOMlAsz7JBA4q5VTQWExhHCGubfV7GII1";
    public final static String DEV_PARSE_CLIENT_KEY = "1yPUZSQa0qvGsupKM6GBUtI9YdbJKdZfi84G3O2a";

    // Aviary
    public final static String DEV_AVIARY_CLIENT_SECRET = "78b5a36c-bf63-47ef-91b6-d8da3046a852";
    public final static String DEV_AVIARY_CLIENT_ID = "83af573e8c2b4848b62ee1d863926d36";

    // Sinch
    public final static String DEV_SINCH_APP_KEY = "";
    public final static String PROD_SINCH_APP_KEY = "";

    public final static String PROD_PARSE_APP_ID = "";
    public final static String PROD_PARSE_CLIENT_KEY = "";

    public final static int MIN_YEAR = 1985;
    public final static int MAX_YEAR = 2030;

    public static final int COUNT_EVENT_LOADMORE = 20;
    public static final int COUNT_MOMENT_LOADMORE = 10;
    public static final int COUNT_COMMENT_LOADMORE = 10;
    public static final int IMAGE_COMPRESS_RATIO = 70;
    public static final int IMAGE_THUMB_EVENT_WIDTH = 200;
    public static final int IMAGE_AVATAR_WIDTH = 200;
    public static final int IMAGE_THUMB_AVATAR_WIDTH = 100;
    public static final int IMAGE_THUMB_AVATAR_HEIGHT = 100;
    public static final int IMAGE_THUMB_MOMENT_WIDTH = 400;
    public static final int IMAGE_MOMENT_WIDTH = 600;
}
