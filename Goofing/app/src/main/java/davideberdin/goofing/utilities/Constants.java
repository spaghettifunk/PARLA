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
    public static final String GET_SENTENCE_URL = "get-sentence/";

    // Network states
    public static final int NETWORKING_LOGIN_STATE = 0;
    public static final int NETWORKING_REGISTER_STATE = 1;
    public static final int NETWORKING_GET_SENTENCE = 2;

    // POST strings
    public static final String SUCCESS_POST = "SUCCESS";
    public static final String FAILED_POST = "FAILED";
    public static final String GET_SENTENCE_POST = "Sentence";
    public static final String GET_PHONETIC_POST = "Phonetic";

    // debugging and Toast strings
    public static final String TOAST_ERROR_LOGIN_ERROR = "Authentication failed";
    public static final String GENERAL_ERROR_REQUEST = "Unknown behaviour during REQUEST";
    public static final String GENERAL_ERROR_RESPONSE = "Unknown behaviour during RESPONSE";

    // Username json keys
    public static final String GET_USERNAME_POST = "Username";
    public static final String GET_GENDER_POST = "Gender";
    public static final String GET_NATIONALITY_POST = "Nationality";
    public static final String GET_OCCUPATION_POST = "Occupation";
}
