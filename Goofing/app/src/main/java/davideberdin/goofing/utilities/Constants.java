package davideberdin.goofing.utilities;

import java.util.ArrayList;
import java.util.HashMap;

import davideberdin.goofing.controllers.SentenceTuple;
import davideberdin.goofing.controllers.StressTuple;

public class Constants
{
    // Activities Name
    public static final String LOGIN_ACTIVITY = "LoginActivity";
    public static final String CONNECTION_ACTIVITY = "ConnectionActivity";
    public static final String REGISTRATION_ACTIVITY = "RegistrationActivity";
    public static final String PRONUNCIATION_ACTIVITY = "PronunciationActivity";

    // recognition CMU Sphinxs
    public static final String PHONE_SEARCH = "phones";

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
    public static final String GET_PHONEMES_POST = "Phonemes";
    public static final String GET_VOWELSTRESS_POST = "VowelStress";
    public static final String GET_PITCH_CHART_POST = "PitchChart";
    public static final String GET_VOWEL_CHART_POST = "VowelChart";

    // debugging and Toast strings
    public static final String TOAST_ERROR_LOGIN_ERROR = "Authentication failed";
    public static final String GENERAL_ERROR_REQUEST = "Unknown behaviour during REQUEST";
    public static final String GENERAL_ERROR_RESPONSE = "Unknown behaviour during RESPONSE";
    public static final String DIALOG_INFO_FEEDBACKS_TITLE = "Info Feedbacks";
    public static final String DIALOG_INFO_FEEDBACKS_MESSAGE = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, \n" +
            "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation \n" +
            "ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse \n" +
            "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia\n" +
            "deserunt mollit anim id est laborum.";

    // Username json keys
    public static final String GET_USERNAME_POST = "Username";
    public static final String GET_PASSWORD_POST = "Password";
    public static final String GET_GENDER_POST = "Gender";
    public static final String GET_NATIONALITY_POST = "Nationality";
    public static final String GET_OCCUPATION_POST = "Occupation";

    // sentences - KEEP THE SAME ORDER
    public static HashMap<String, SentenceTuple<String, String, ArrayList<StressTuple>>> nativeSentenceInfo = new HashMap<>();

    public static String[] nativeSentences = { "A piece of cake", "Blow a fuse", "Catch some zs", "Down to the wire",
                                               "Eager beaver", "Fair and square", "Get cold feet", "Mellow out",
                                               "Pulling your legs", "Thinking out loud" };

    public static String[] nativePhonetics = { "ɐ pˈiːs ʌv kˈeɪk", "blˈoʊ ɐ fjˈuːz", "kˈætʃ sˌʌm zˌiːˈɛs",
                                               "dˌaʊn tə ðə wˈaɪɚ", "ˈiːɡɚ bˈiːvɚ", "fˈɛɹ ænd skwˈɛɹ",
                                               "ɡɛt kˈoʊld fˈiːt", "mˈɛloʊ ˈaʊt", "pˈʊlɪŋ jʊɹ lˈɛɡz", "θˈɪŋkɪŋ ˈaʊt lˈaʊd" };

    public static String[] nativePhonemes = { "AH PIYS AHV KEYK",
                                              "BLOW AH FYUWZ",
                                              "KAECH SAHM ZIYZ",
                                              "DAWN TAH DHAH WAYER",
                                              "IYGER BIYVER",
                                              "FEHR AHND SKWEHR",
                                              "GEHT KOWLD FIYT",
                                              "MEHLOW AWT",
                                              "PUHLIHNG YUHR LEHGZ",
                                              "THIHNGKAHNG AWT LAWD" };

    public static String[] nativeStressPhonemes = { "AH, IY, AH, EY",
                                                    "OW, AH, UW",
                                                    "AE, AH, IY",
                                                    "AH, AH, AY, ER",
                                                    "IY, ER, IY, ER",
                                                    "EH, AH, EH",
                                                    "EH, OW, IY",
                                                    "EH, OW, AW",
                                                    "UH, IH, UH, EH",
                                                    "IH, AH, AW, AW"};

    public static String[] nativeStressPosition = { "0, 1, 0, 1",
                                                    "1, 0, 1",
                                                    "1, 1, 0",
                                                    "0, 0, 1, 0",
                                                    "1, 0, 1, 0",
                                                    "1, 0, 1",
                                                    "1, 1, 1",
                                                    "1, 0, 1",
                                                    "1, 0, 1, 1",
                                                    "1, 0, 1, 1"};

    // just for testing
    public static String[] userSentences = { "A piece of cake", "Blow a fuse" };

    public static String getColoredSpanned(String text, int color) {
        String hexColor = "#" + Integer.toHexString(color).substring(2);
        String input = "<font color=" + hexColor + ">" + text + "</font>";
        return input;
    }
}
