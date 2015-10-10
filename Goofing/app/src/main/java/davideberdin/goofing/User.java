package davideberdin.goofing;

/**
 * Created by dado on 09/10/15.
 */
public class User {
    private static User singletonUser = new User();

    public static User getUser() {
        return singletonUser;
    }

    public static void createUser(String n, String ra, String nat, String occ) {
        singletonUser = new User(n, ra, nat, occ);
    }

    private String username = "";
    private String regional_area = "";
    private String nationality = "";
    private String occupation = "";

    private User() { }

    private User(String n, String ra, String nat, String occ) {
        this.username = n;
        this.regional_area = ra;
        this.nationality = nat;
        this.occupation = occ;
    }
}
