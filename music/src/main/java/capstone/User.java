package capstone;

public class User {
    private static String username;
    private static String password;

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setUsername(String u) {
        username = u;
    }

    public static void setPassword(String p) {
        password = p;
    }
    // p: the password passed in, usually the one input into the login form 
    // returns whether the password matches
    public static boolean checkPassword (String p) {
        return (password.equals(p));
    }
}
