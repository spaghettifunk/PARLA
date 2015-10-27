package davideberdin.goofing.controllers;

/**
 * Created by dado on 22/10/15.
 */
public class User
{
//    private static User singletonUser = new User();
//    public static User getUser() { return singletonUser; }
//
//    public static void createUser(String n, String p, String g, String nat, String occ) {
//        singletonUser = new User(n, p, g, nat, occ);
//    }

    private String currentSentence = "";
    private String currentePhonetic = "";

    private String gender = "";
    private String username = "";
    private String password = "";
    private String nationality = "";
    private String occupation = "";

    public User(String n, String p)
    {
        this.username = n;
        this.password = p;
    }

    public User(String n, String p, String g, String nat, String occ, String s, String ph) {
        this.username = n;
        this.password = p;
        this.nationality = nat;
        this.occupation = occ;
        this.gender = g;
        this.currentSentence = s;
        this.currentePhonetic = ph;
    }

    // Some public methods related to user paramentes
    public String GetUsername() { return this.username; }
    public String GetPassword() { return this.password; }
    public String GetGender() { return this.gender; }
    public String GetNationality() { return this.nationality; }
    public String GetOccupation() { return this.occupation; }

    public void SetUsername(String u) { this.username = u; }
    public void SetGender(String g) { this.gender = g; }
    public void SetNationality(String n) { this.nationality = n; }
    public void SetOccupation(String o) { this.occupation = o; }

    // sentence singleton
    public String GetCurrentSentence() { return this.currentSentence; }
    public String GetCurrentPhonetic() { return this.currentePhonetic; }
    public void SetCurrentSentence(String cs){ this.currentSentence = cs; }
    public void SetCurrentPhonetic(String cp){ this.currentePhonetic = cp; }
}