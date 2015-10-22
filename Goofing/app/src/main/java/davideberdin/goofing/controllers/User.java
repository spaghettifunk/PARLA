package davideberdin.goofing.controllers;

/**
 * Created by dado on 22/10/15.
 */
public class User
{
    private static User singletonUser = new User();
    public static User getUser() { return singletonUser; }

    public static void createUser(String n, String p, String g, String nat, String occ) {
        singletonUser = new User(n, p, g, nat, occ);
    }

    private String gender = "";
    private String username = "";
    private String password = "";
    private String nationality = "";
    private String occupation = "";

    private User() { }

    private User(String n, String p, String g, String nat, String occ) {
        this.username = n;
        this.password = p;
        this.nationality = nat;
        this.occupation = occ;
        this.gender = g;
    }

    /*
     * Some public methods related to user paramentes
    */
    public String GetUsername() { return this.username; }
    public String GetPassword() { return this.password; }
    public String GetGender() { return this.gender; }
    public String GetNationality() { return this.nationality; }
    public String GetOccupation() { return this.occupation; }
}