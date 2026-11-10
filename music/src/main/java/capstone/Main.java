package capstone;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SQLiteDB db = new SQLiteDB();
        UserUI loginPage = new UserUI(db);
        loginPage.loginPageLayout();
        
    }
}