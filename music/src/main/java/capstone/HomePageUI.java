package capstone;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class HomePageUI extends JFrame implements ActionListener {
    private int scWidth;
    private int scHeight;
    public HomePageUI() {
        setTitle("Home");
        // page automatically fills screen
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        scWidth = (int)screen.getWidth();
        scHeight = (int)screen.getHeight();
        setSize((int)scWidth, (int)scHeight);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setBackground(new Color(0xF3F3F3));
    }

    public void homePageLayout() {
        GridBagConstraints c = new GridBagConstraints();
  
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(0xFFFFFF));
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.2;
        c.weighty = 1;
        c.gridheight = 3;
        add(sidebar, c);

        JPanel main = new JPanel();
        main.setBackground(new Color(0xF0F0F0));
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.weightx = 0.8;
        add(main, c);

        JButton createPlaylistButton = new JButton("Create Playlist");
        createPlaylistButton.addActionListener(this);
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 10;
        c.ipady = 10;
        sidebar.add(createPlaylistButton, c);

        revalidate();
        repaint();
    }

    public void actionPerformed(ActionEvent e) {
        // opens a new dialog for setting your playlist's name and importing songs
        NewPlaylistMaker n = new NewPlaylistMaker();
    }
}
