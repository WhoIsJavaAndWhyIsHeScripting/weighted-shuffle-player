package capstone;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

import javax.swing.plaf.SliderUI;

public class PlayerUI extends JFrame{

    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.CYAN;

    private MusicPlayer musicPlayer;
    private JLabel songTitle, songArtist;
    private JPanel playbackBtns;
    private JSlider playbackSlider;

    public PlayerUI(){
        super("Music Player");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(FRAME_COLOR);

        musicPlayer = new MusicPlayer(this);

        addGUIComponents(); 
    }

    private void addGUIComponents(){
        addToolbar();

        //load image of song/playlist
        JLabel songImage = new JLabel(loadImage("src/assets/record.png"));
        songImage.setBounds(0, 50, getWidth() - 20, 225);
        add(songImage);

        //title
        songTitle = new JLabel("Song Title");
        songTitle.setBounds(0, 285, getWidth()-10, 30);
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        //artist
        songArtist = new JLabel("Song Artist");
        songArtist.setBounds(0, 315, getWidth()-10, 30);
        songArtist.setFont(new Font("Dialog", Font.BOLD, 24));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        //slider
        playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(getWidth()/2 - 150, 365, 300, 40);
        playbackSlider.setBackground(null);
        playbackSlider.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                musicPlayer.pauseSong(); //stop while holding the slider
            }

            @Override
            public void mouseReleased(MouseEvent e){
                JSlider source =  (JSlider) e.getSource();
                int frame = source.getValue();
                musicPlayer.setCurrentFrame(frame);
                musicPlayer.setCurrentTimeMilliseconds((int)(frame/(musicPlayer.getCurrentSong().getFrameRatePerMilliseconds()))); //need to add same multiplier here
                musicPlayer.playCurrentSong();
                enablePauseButtonDisablePlayButton();
            }
        });
        add(playbackSlider);

        //buttons
        addPlaybackBtns();
    }

    private void addToolbar(){
    }

    private void addPlaybackBtns(){
        playbackBtns = new JPanel();
        playbackBtns.setBounds(0, 435, getWidth() - 10, 80);
        playbackBtns.setBackground(null);

        //previous

        //play

        //pause

        //next
    }

    public void setPlaybackSliderValue(int frame){
        playbackSlider.setValue(frame);
    }

    public void updateSongTitleAndArtist(Song song){
        songTitle.setText(song.getTitle());
        songArtist.setText(song.getArtist());
    }

    public void updatePlaybackSlider(Song song){
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());
    }

    public void enablePauseButtonDisablePlayButton(){
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);
        playButton.setVisible(false);
        playButton.setEnabled(false);
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    public void enablePlayButtonDisablePauseButton(){
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);
        playButton.setVisible(true);
        playButton.setEnabled(true);
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }

    private ImageIcon loadImage(String imagePath){
        try{
            BufferedImage image = ImageIO.read(new File(imagePath));
            return new ImageIcon(image);
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}