package davideberdin.goofing.utilities;

public class Constants
{
    // Activities Name
    public static final String LOGIN_ACTIVITY_NAME = "LoginActivity";
    public static final String CONNECTION_ACTIVITY = "ConnectionActivity";
    public static final String REGISTRATION_ACTIVITY = "RegistrationActivity";

    // Network stuff
    public static final String SERVER_URL = "http://10.0.2.2:8000/goofy/";
    public static final String LOGIN_URL = "login/";
    public static final String REGISTRATION_URL = "register/";

    // Network states
    public static final int NETWORKING_LOGIN_STATE = 0;
    public static final int NETWORKING_REGISTER_STATE = 1;

    // POST strings
    public static final String SUCCESS_POST = "SUCCESS";
    public static final String FAILED_POST = "FAILED";

    // debugging and Toast strings
    public static final String TOAST_ERROR_LOGIN_ERROR = "Authentication failed";
}
