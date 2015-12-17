package davideberdin.goofing.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import davideberdin.goofing.utilities.Constants;

public class User
{
    private String currentSentence = "";
    private String currentPhonetic = "";

    private String gender = "";
    private String username = "";
    private String password = "";
    private String nationality = "";
    private String occupation = "";

    private HashMap<String, SentenceTuple<String, String, ArrayList<Tuple>>> nativeSentenceInfo = new HashMap<>();

    public User(String n, String p) {
        this.username = n;
        this.password = p;

        fillSentencesMap();
    }

    public User(String n, String p, String g, String nat, String occ, String s, String ph) {
        this.username = n;
        this.password = p;
        this.nationality = nat;
        this.occupation = occ;
        this.gender = g;
        this.currentSentence = s;
        this.currentPhonetic = ph;

        fillSentencesMap();
    }

    // Some public methods related to user parameters
    public String GetUsername() { return this.username; }
    public String GetPassword() { return this.password; }
    public String GetGender() { return this.gender; }
    public String GetNationality() { return this.nationality; }
    public String GetOccupation() { return this.occupation; }

    // sentence singleton
    public String GetCurrentSentence() { return this.currentSentence; }
    public String GetCurrentPhonetic() { return this.currentPhonetic; }
    public void SetCurrentSentence(String cs){ this.currentSentence = cs; }
    public void SetCurrentPhonetic(String cp){ this.currentPhonetic = cp; }
    public HashMap<String, SentenceTuple<String, String, ArrayList<Tuple>>> getNativeSentenceInfo() { return this.nativeSentenceInfo; }

    public void fillSentencesMap() {

        // better to rebuild every time
        this.nativeSentenceInfo.clear();

        for (int i = 0; i < Constants.nativeSentences.length; i++) {

            String stressPhonemes = Constants.nativeStressPhonemes[i];
            String stressPosition = Constants.nativeStressPosition[i];

            List<String> phonemes = Arrays.asList(stressPhonemes.split("\\s*,\\s*"));
            List<String> positions = Arrays.asList(stressPosition.split("\\s*,\\s*"));

            ArrayList<Tuple> stressNative = new ArrayList<Tuple>();
            for (int j = 0; j < phonemes.size(); j++) {
                Tuple stress = new Tuple(phonemes.get(j), positions.get(j));
                stressNative.add(stress);
            }

            SentenceTuple<String, String, ArrayList<Tuple>> tuple = new SentenceTuple<>(Constants.nativePhonetics[i], Constants.nativePhonemes[i], stressNative);
            this.nativeSentenceInfo.put(Constants.nativeSentences[i], tuple);
        }
    }
}