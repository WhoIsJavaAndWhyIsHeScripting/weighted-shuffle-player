package capstone;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ImportPageUI extends JFrame implements ActionListener {
    int scWidth;
    int scHeight;
    File pickedSong;
    public ImportPageUI() {
        setTitle("Weighted Shuffle Player");

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        scWidth = (int)screen.getWidth();
        scHeight = (int)screen.getHeight();
        setSize((int)scWidth, (int)scHeight);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
    }

    public void basicPageLayout() {
        getContentPane().setBackground(new Color(0xF3F3F3));

        JLabel title = new JLabel();
        int titleW = 200;
        int titleH = 50;
        title.setText("Import Songs");
        title.setFont(new Font("Futura-CondensedMedium", Font.BOLD, 24));
        title.setBounds(scWidth/2-titleW/2, 50, titleW, titleH);
        getContentPane().add(title);

        JButton songChooser = new JButton();
        int songChW = 250;
        int songChH = 100;
        songChooser.setBounds(scWidth/2-songChW/2, scHeight/2-songChH/2, songChW, songChH);
        songChooser.setText("Select From Files");
        songChooser.addActionListener(this);
        getContentPane().add(songChooser);

    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser filePick = new JFileChooser();
        int response = filePick.showOpenDialog(null);
        if (response == JFileChooser.APPROVE_OPTION) {
            pickedSong = filePick.getSelectedFile();
        }
        System.out.println(pickedSong.toString());
    }
}
