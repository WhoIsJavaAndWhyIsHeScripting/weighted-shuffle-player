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

    public static boolean checkPassword (String p) {
        System.out.println(password);
        System.out.println(p);
        return (password.equals(p));
    }
}
