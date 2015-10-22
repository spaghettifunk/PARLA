package davideberdin.goofing;

/**
 * Created by dado on 09/10/15.
 */
public class User {

    enum Gender {
        Male,
        Female
    }

    private static User singletonUser = new User();
    public static User getUser() {
        return singletonUser;
    }

    public static void createUser(String n, String g, String ra, String nat, String occ) {
        singletonUser = new User(n, g, ra, nat, occ);
    }

    private Gender gender;
    private String username = "";
    private String regional_area = "";
    private String nationality = "";
    private String occupation = "";

    private User() { }

    private User(String n, String g, String ra, String nat, String occ) {
        this.username = n;
        this.regional_area = ra;
        this.nationality = nat;
        this.occupation = occ;

        assert g.isEmpty() == false;
        if (g == "Male")
            this.gender = Gender.Male;
        else
            this.gender = Gender.Female;
    }

    /*
     * Some public methods related to user paramentes
    */
    public Gender GetGender() { return this.gender; }
    public String GetUsername() { return this.username; }
    public String GetNationality() { return this.nationality; }
    public String GetRegionalArea() { return this.regional_area; }
    public String GetOccupation() { return this.occupation; }
}
