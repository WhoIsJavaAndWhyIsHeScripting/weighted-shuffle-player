package capstone;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class UserUI extends JFrame implements ActionListener {
    SQLiteDB db;
    int scWidth;
    int scHeight;
    JTextField usernameInput;
    JTextField passwordInput;
    String user;
    String pass;
    public UserUI(SQLiteDB d) {
        db = d;
        setTitle("Login");

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        scWidth = (int)screen.getWidth();
        scHeight = (int)screen.getHeight();
        setSize((int)scWidth, (int)scHeight);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        UIManager.put("Label.font", new Font("Futura-Medium", Font.PLAIN, 16));
    }

    public void loginPageLayout() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20,20,20,20);
        JLabel userLabel = new JLabel();
        userLabel.setText("Username:");
        c.gridx = 0;
        c.gridy = 0;
        getContentPane().add(userLabel, c);
        JLabel passLabel = new JLabel();
        passLabel.setText("Password:");
        c.gridx = 0;
        c.gridy = 1;
        getContentPane().add(passLabel, c);
        usernameInput = new JTextField(30);
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        getContentPane().add(usernameInput, c);
        passwordInput = new JPasswordField(30);
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        getContentPane().add(passwordInput, c);
        JButton loginButton = new JButton();
        loginButton.setText("Login");
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.ipadx = 20;
        c.ipady = 20;
        c.gridwidth = 2;
        loginButton.addActionListener(this);
        getContentPane().add(loginButton, c);
        revalidate();
        repaint();
    }

    public void actionPerformed(ActionEvent e) {
        user = usernameInput.getText();
        pass = passwordInput.getText();
        db.addUser(user, pass);
    }

    public String getUsername() {
        return user;
    }

    public String getPassword() {
        return pass;
    }
}
