package davideberdin.goofing.utilities;

public class Constants
{
    // Activities Name
    public static final String LOGIN_ACTIVITY = "LoginActivity";
    public static final String CONNECTION_ACTIVITY = "ConnectionActivity";
    public static final String REGISTRATION_ACTIVITY = "RegistrationActivity";
    public static final String PRONUNCIATION_ACTIVITY = "PronunciationActivity";

    // Network stuff
    public static final String SERVER_URL = "http://10.0.2.2:8000/goofy/";
    public static final String LOGIN_URL = "login/";
    public static final String REGISTRATION_URL = "register/";
    public static final String HANDLE_RECORDING_URL = "pronunciation/";

    // Network states
    public static final int NETWORKING_LOGIN_STATE = 0;
    public static final int NETWORKING_REGISTER_STATE = 1;
    public static final int NETWORKING_HANDLE_RECORDED_VOICE = 2;

    // POST strings
    public static final String SUCCESS_POST = "SUCCESS";
    public static final String FAILED_POST = "FAILED";
    public static final String GET_SENTENCE_POST = "Sentence";
    public static final String GET_PHONETIC_POST = "Phonetic";
    public static final String GET_FEEDBACKS_POST = "Feedbacks";

    // debugging and Toast strings
    public static final String TOAST_ERROR_LOGIN_ERROR = "Authentication failed";
    public static final String GENERAL_ERROR_REQUEST = "Unknown behaviour during REQUEST";
    public static final String GENERAL_ERROR_RESPONSE = "Unknown behaviour during RESPONSE";
    public static final String ERROR_SELECTING_SENTENCE = "Select a sentence to listen";

    // Username json keys
    public static final String GET_USERNAME_POST = "Username";
    public static final String GET_PASSWORD_POST = "Password";
    public static final String GET_GENDER_POST = "Gender";
    public static final String GET_NATIONALITY_POST = "Nationality";
    public static final String GET_OCCUPATION_POST = "Occupation";

    // sentences
    public static String[] nativeSentences = { "A piece of cake", "Blow a fuse", "Catch some zs", "Down to the wire",
                                               "Eager beaver", "Fair and square", "Get cold feet", "Mellow out",
                                               "Pulling your legs", "Thinking out loud" };

    public static String[] nativePhonetics = { "ɐ pˈiːs ʌv kˈeɪk", "blˈoʊ ɐ fjˈuːz", "kˈætʃ sˌʌm zˌiːˈɛs",
                                               "dˌaʊn tə ðə wˈaɪɚ", "ˈiːɡɚ bˈiːvɚ", "fˈɛɹ ænd skwˈɛɹ",
                                               "ɡɛt kˈoʊld fˈiːt", "mˈɛloʊ ˈaʊt", "pˈʊlɪŋ jʊɹ lˈɛɡz", "θˈɪŋkɪŋ ˈaʊt lˈaʊd" };

    public static String[] userSentences = { "A piece of cake", "Blow a fuse" };
}
