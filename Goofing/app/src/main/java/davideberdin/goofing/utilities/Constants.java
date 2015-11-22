package davideberdin.goofing.utilities;

import java.util.ArrayList;
import java.util.HashMap;

import davideberdin.goofing.controllers.SentenceTuple;
import davideberdin.goofing.controllers.Tuple;

public class Constants {
    // Activities Name
    public static final String LOGIN_ACTIVITY = "LoginActivity";
    public static final String CONNECTION_ACTIVITY = "ConnectionActivity";
    public static final String REGISTRATION_ACTIVITY = "RegistrationActivity";
    public static final String PRONUNCIATION_ACTIVITY = "PronunciationActivity";
    public static final String HISTORY_ACTIVITY = "HistoryActivity";

    // recognition CMU Sphinxs
    public static final String SENTENCES_SEARCH = "sentences";
    public static final String PHONE_SEARCH = "phones";

    // Network stuff
    //public static final String SERVER_URL = "http://192.168.1.137:8000/goofy/";
    public static final String SERVER_URL = "http://10.0.2.2:8000/goofy/";
    public static final String PHONEME_SERVICE_URL = "http://10.0.2.2:9099/phonemes/transcription/";

    public static final String LOGIN_URL = "login/";
    public static final String REGISTRATION_URL = "register/";
    public static final String HANDLE_RECORDING_URL = "pronunciation/";
    public static final String HANDLE_FETCH_HISTORY_URL = "history/";

    // Network states
    public static final int NETWORKING_LOGIN_STATE = 0;
    public static final int NETWORKING_REGISTER_STATE = 1;
    public static final int NETWORKING_HANDLE_RECORDED_VOICE = 2;
    public static final int NETWORKING_FETCH_HISTORY = 3;
    public static final int NETWORKING_FETCH_PHONEMES = 4;

    // POST strings
    public static final String SUCCESS_POST = "SUCCESS";
    public static final String FAILED_POST = "FAILED";
    public static final String GET_SENTENCE_POST = "Sentence";
    public static final String GET_PHONETIC_POST = "Phonetic";
    public static final String GET_PHONEMES_POST = "Phonemes";
    public static final String GET_WER_POST = "WER";
    public static final String GET_VOWEL_STRESS_POST = "VowelStress";
    public static final String GET_PITCH_CHART_POST = "PitchChart";
    public static final String GET_VOWEL_CHART_POST = "VowelChart";

    // debugging and Toast strings
    public static final String TOAST_ERROR_LOGIN_ERROR = "Authentication failed";
    public static final String GENERAL_ERROR_REQUEST = "Unknown behaviour during REQUEST";
    public static final String GENERAL_ERROR_RESPONSE = "Unknown behaviour during RESPONSE";
    public static final String ERROR_WRONG_WORDS = "You haven't spelled the sentence correctly, try again...";
    public static final String FETCHING_HISTORY_TITLE = "Processing";
    public static final String FETCHING_HISTORY = "Fetching history...";

    // Username json keys
    public static final String GET_USERNAME_POST = "Username";
    public static final String GET_PASSWORD_POST = "Password";
    public static final String GET_GENDER_POST = "Gender";
    public static final String GET_NATIONALITY_POST = "Nationality";
    public static final String GET_OCCUPATION_POST = "Occupation";
    public static final String GET_VOWEL_HISTORY_DATE = "VowelsDate";
    public static final String GET_VOWEL_HISTORY = "VowelsImages";
    public static final String GET_RESULT_PHONEMES_SERVICE = "Phonemes";

    // sentences - KEEP THE SAME ORDER
    public static HashMap<String, SentenceTuple<String, String, ArrayList<Tuple>>> nativeSentenceInfo = new HashMap<>();

    public static String[] nativeSentences = {"A piece of cake", "Blow a fuse", "Catch some zs", "Down to the wire",
            "Eager beaver", "Fair and square", "Get cold feet", "Mellow out",
            "Pulling your legs", "Thinking out loud"};

    public static String[] nativePhonetics = {"ɐ pˈiːs ʌv kˈeɪk", "blˈoʊ ɐ fjˈuːz", "kˈætʃ sˌʌm zˌiːˈɛs",
            "dˌaʊn tə ðə wˈaɪɚ", "ˈiːɡɚ bˈiːvɚ", "fˈɛɹ ænd skwˈɛɹ",
            "ɡɛt kˈoʊld fˈiːt", "mˈɛloʊ ˈaʊt", "pˈʊlɪŋ jʊɹ lˈɛɡz", "θˈɪŋkɪŋ ˈaʊt lˈaʊd"};

    public static String[] nativePhonemes = {"AH PIYS AHV KEYK",
            "BLOW AH FYUWZ",
            "KAECH SAHM ZIYZ",
            "DAWN TAH DHAH WAYER",
            "IYGER BIYVER",
            "FEHR AHND SKWEHR",
            "GEHT KOWLD FIYT",
            "MEHLOW AWT",
            "PUHLIHNG YUHR LEHGZ",
            "THIHNGKAHNG AWT LAWD"};

    public static String[] nativeStressPhonemes = {"AH, IY, AH, EY",
            "OW, AH, UW",
            "AE, AH, IY",
            "AH, AH, AY, ER",
            "IY, ER, IY, ER",
            "EH, AH, EH",
            "EH, OW, IY",
            "EH, OW, AW",
            "UH, IH, UH, EH",
            "IH, AH, AW, AW"};

    public static String[] nativeStressPosition = {"0, 1, 0, 1",
            "1, 0, 1",
            "1, 1, 0",
            "0, 0, 1, 0",
            "1, 0, 1, 0",
            "1, 0, 1",
            "1, 1, 1",
            "1, 0, 1",
            "1, 0, 1, 1",
            "1, 0, 1, 1"};

    public static HashMap<String, Tuple> meaningExampleMap = new HashMap<>();

    public static String[] sentenceMeaning = {"To refer to something as a piece of cake means that you consider it to be very easy.",
            "If you blow a fuse, you suddenly lose your temper and become very angry.",
            "The idiom catch some Z's means to sleep. \"Zzzz\" is the sound that a person makes when they sleep, so when you catch some Z's, you are sleeping.",
            "If something such as project or a match goes down to the wire, the situation can change up until the last possible moment.",
            "The term eager beaver refers to a person who is hardworking and enthusiastic, sometimes considered overzealous.",
            "If something is obtained or won fair and square, it is done in an honest and open manner, the rules are respected and there is no cheating or lying.",
            "If you get cold feet about something, you begin to hesitate about doing it; you are no longer sure whether you want to do it or not.",
            "To become more relaxed and calm, or to make someone more relaxed and calm",
            "If you pull somebody's leg, you tease them by telling them something that is not true.",
            "To say your thoughts aloud"};

    public static String[] sentencesExample = {"The English test was a piece of cake!",
            "Charlie blew a fuse yesterday then he discovered that his ipod had been stolen.",
            "I've been working hard today. I need to catch some Z's.",
            "There's nothing as exciting as watching a game that goes down to the wire.",
            "The new accountant works all the time - first to arrive and last to leave. He's a real eager beaver!",
            "Gavin won the competition fair and square - there was no doubt about the result.",
            "I wanted to enter the competition but at the last minute I got cold feet.",
            "He needs to mellow out a little.",
            "Of course I'm not going to buy a sports car. I was just pulling your leg!",
            "I'm thinking out loud now, but it looks as if I can meet you Tuesday."};

    // just for testing
    public static ArrayList<String> userSentences = new ArrayList<>();

    public static String getColoredSpanned(String text, int color) {
        String hexColor = "#" + Integer.toHexString(color).substring(2);
        String input = "<font color=" + hexColor + ">" + text + "</font>";
        return input;
    }

    public static void createMeaningExampleDictionary(){
        int index = 0;
        for (String sentence : nativeSentences){
            meaningExampleMap.put(sentence, new Tuple(sentenceMeaning[index], sentencesExample[index]));
            index++;
        }
    }
}
