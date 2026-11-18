package capstone;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class NewPlaylistMaker extends JFrame {
    public NewPlaylistMaker() {
        setTitle("Create New Playlist");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);

        setLayout(new GridBagLayout());
    }
    
    public void playlistForm() {
        GridBagConstraints c = new GridBagConstraints();
        
        JLabel pNameLabel = new JLabel("Playlist Name:");
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        add(pNameLabel, c);

        JTextField pNameField = new JTextField();
        c.gridx = 1;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 10, 5, 20);
        add(pNameField, c);

        JButton importButton = new JButton("Add Songs");
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(10, 40, 10, 40);
        add(importButton, c);

        JButton submitButton = new JButton("Save Changes");
        c.gridx = 1;
        submitButton.addActionListener(e -> {
            String playlistName = pNameField.getText();
            if (playlistName != null) SQLiteDB.createPlaylist(playlistName);
        });
        add(submitButton, c);
        revalidate();
        repaint();

    }
}
