package spring.restapi.utils;

public class Constants {
    public static final String IV_PARAM = "smartdoor_202510";

    public static final int STATE_DISABLE = 0;
    public static final int STATE_REQUEST = 1;
    public static final int STATE_DELETE = 2;
    public static final int STATE_NORMAL = 3;

    public static final String ROLE_TYPE_ADMIN = "admin";
    public static final String ROLE_TYPE_USER = "user";

    public static final String INSTALL_PLACE_HOME = "home";
    public static final String INSTALL_PLACE_BUSINESS = "business";

    public static final String DEVICE_INFO_TYPE_SOCKET = "socket";
    public static final String DEVICE_INFO_TYPE_BLUETOOTH = "bluetooth";
    public static final String DEVICE_INFO_TYPE_BATTERY = "battery";

    public static final String DEVICE_PROPERTY_ENCRYPTION_KEY = "encryption_key";
    public static final String DEVICE_PROPERTY_BLUETOOTH_ADDRESS = "bluetooth_address";

    public static final String DEVICE_OPERATE_TYPE_REQUEST = "request";
    public static final String DEVICE_OPERATE_TYPE_OPEN = "open";

    public static final String DEVICE_OPERATE_MODE_KEY = "key";
    public static final String DEVICE_OPERATE_MODE_PHONE = "phone";
    public static final String DEVICE_OPERATE_MODE_PALM = "palm";
    public static final String DEVICE_OPERATE_MODE_FINGER = "finger";
    public static final String DEVICE_OPERATE_MODE_PIN = "pin";


    public static final String[] LOGIN_MESSAGES = {
            "Successfully logged in",
            "Disabled user",
            "Requested user, wait until agreement",
            "Deleted user",
            "No-exist user",
            "No-role user",
            "No-linked device",
            "Invalid device",
            "No-district",
            "Incorrect password"
    };

    public static final String[] CHECK_REGISTER_MESSAGES = {
            "Success, can register",
            "Disabled user, cannot register",
            "Requested user, cannot register",
            "Normal user, cannot register"
    };

    public static final String[] REGISTER_MESSAGES = {
            "Success, wait until agreement",
            "Disabled user, cannot register",
            "Requested user, cannot register",
            "Normal user, cannot register",
            "Cannot find smart door device",
            "Invalid business"
    };

    public static final String[] REQUEST_ADMIN_AUTH_MESSAGES = {
            "Success",
            "Failure",
            "Disabled role",
            "Requested.Wait until agreement",
            "Already accepted"
    };

    public static final String[] DEFAULT_MESSAGES = {
            "Success",
            "Failure",
            "Data not exist",
            "Admin user not connected"
    };

    public static String getLoginMessage(LOGIN_CODES loginCode) {
        return LOGIN_MESSAGES[loginCode.ordinal()];
    }

    public static String getLoginCode(LOGIN_CODES loginCode) {
        return String.format("%02d", loginCode.ordinal());
    }

    public static String getCheckRegisterMessage(CHECK_REGISTER_CODES code) {
        return CHECK_REGISTER_MESSAGES[code.ordinal()];
    }

    public static String getCheckRegisterCode(CHECK_REGISTER_CODES code) {
        return String.format("%02d", code.ordinal());
    }

    public static String getRegisterMessage(REGISTER_CODES code) {
        return REGISTER_MESSAGES[code.ordinal()];
    }

    public static String getRegisterCode(REGISTER_CODES code) {
        return String.format("%02d", code.ordinal());
    }

    public static String getRequestAdminAuthMessage(REQUEST_ADMIN_AUTH_CODES code) {
        return REQUEST_ADMIN_AUTH_MESSAGES[code.ordinal()];
    }

    public static String getRequestAdminAuthCode(REQUEST_ADMIN_AUTH_CODES code) {
        return String.format("%02d", code.ordinal());
    }

    public static String getDefaultMessage(DEFAULT_CODES code) {
        return DEFAULT_MESSAGES[code.ordinal()];
    }

    public static String getDefaultCode(DEFAULT_CODES code) {
        return String.format("%02d", code.ordinal());
    }

    public enum LOGIN_CODES {
        SUCCESS,
        DISABLE,
        REQUEST,
        DELETE,
        NO_EXIST,
        NO_ROLE,
        NO_LINKED_DEVICE,
        INVALID_DEVICE,
        NO_DISTRICT,
        INCORRECT_PASSWORD
    }

    public enum CHECK_REGISTER_CODES {
        SUCCESS,
        DISABLE,
        REQUEST,
        NORMAL
    }

    public enum REGISTER_CODES {
        SUCCESS,
        DISABLE,
        REQUEST,
        NORMAL,
        NO_DEVICE,
        NO_BUSINESS
    }

    public enum REQUEST_ADMIN_AUTH_CODES {
        SUCCESS,
        FAILURE,
        DISABLE,
        REQUEST,
        NORMAL
    }

    public enum DEFAULT_CODES {
        SUCCESS,
        FAILURE,
        NO_DATA,
        NO_CONNECTED_ADMIN
    }
}
