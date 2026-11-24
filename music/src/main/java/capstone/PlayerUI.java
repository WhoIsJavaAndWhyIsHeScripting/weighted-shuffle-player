package capstone;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PlayerUI extends JPanel {

    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.CYAN;
    public static final int PANEL_WIDTH = 400;
    public static final int PANEL_HEIGHT = 600;

    private MusicPlayer musicPlayer;
    private JFileChooser jFileChooser;

    private JLabel songTitle, songArtist;
    private JPanel playbackBtns;
    private JSlider playbackSlider;

    public PlayerUI() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setSize(PANEL_WIDTH, PANEL_HEIGHT);
        setBackground(FRAME_COLOR);
        setLayout(null);

        musicPlayer = new MusicPlayer(this);
        jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File("src/assets"));
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3 Files", "mp3"));

        addGUIComponents();
    }

    private void addGUIComponents() {
        JLabel songImage = new JLabel(loadImage("src/assets/record.png"));
        songImage.setBounds(0, 50, PANEL_WIDTH - 20, 225);
        songImage.setHorizontalAlignment(SwingConstants.CENTER);
        add(songImage);

        // Song title
        songTitle = new JLabel("Song Title");
        songTitle.setBounds(0, 285, PANEL_WIDTH - 10, 30);
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        // Song artist
        songArtist = new JLabel("Song Artist");
        songArtist.setBounds(0, 315, PANEL_WIDTH - 10, 30);
        songArtist.setFont(new Font("Dialog", Font.BOLD, 24));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        // Playback slider
        playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(PANEL_WIDTH / 2 - 150, 365, 300, 40);
        playbackSlider.setOpaque(false);
        playbackSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (musicPlayer.getCurrentSong() != null) {
                    musicPlayer.pauseSong();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (musicPlayer.getCurrentSong() == null) {
                    return;
                }
                JSlider source = (JSlider) e.getSource();
                int frame = source.getValue();
                musicPlayer.setCurrentFrame(frame);
                double frpm = musicPlayer.getCurrentSong().getFrameRatePerMilliseconds();
                int timeMs = frpm > 0 ? (int) (frame / frpm) : 0;
                musicPlayer.setCurrentTimeMilliseconds(timeMs);
                musicPlayer.playCurrentSong();
                enablePauseButtonDisablePlayButton();
            }
        });
        add(playbackSlider);

        // Playback buttons
        addPlaybackBtns();
    }

    private void addPlaybackBtns() {
        playbackBtns = new JPanel();
        playbackBtns.setBounds(0, 435, PANEL_WIDTH - 10, 80);
        playbackBtns.setOpaque(false);

        //Previous
        JButton prevButton = new JButton("Prev");
        prevButton.setFont(new Font("Dialog", Font.BOLD, 14));
        prevButton.addActionListener(e -> musicPlayer.previousSong());
        playbackBtns.add(prevButton);

        //Play
        JButton playButton = new JButton("Play");
        playButton.setFont(new Font("Dialog", Font.BOLD, 14));
        playButton.addActionListener(e -> {
            enablePauseButtonDisablePlayButton();
            musicPlayer.playCurrentSong();
        });
        playbackBtns.add(playButton);

        //Pause
        JButton pauseButton = new JButton("Pause");
        pauseButton.setFont(new Font("Dialog", Font.BOLD, 14));
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(e -> {
            enablePlayButtonDisablePauseButton();
            musicPlayer.pauseSong();
        });
        playbackBtns.add(pauseButton);

        //Next
        JButton nextButton = new JButton("Next");
        nextButton.setFont(new Font("Dialog", Font.BOLD, 14));
        nextButton.addActionListener(e -> musicPlayer.nextSong());
        playbackBtns.add(nextButton);

        add(playbackBtns);
    }

    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public void setPlaybackSliderValue(int frame) {
        SwingUtilities.invokeLater(() -> playbackSlider.setValue(frame));
    }

    public void updateSongTitleAndArtist(Song song) {
        if (song == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            songTitle.setText(song.getTitle());
            songArtist.setText(song.getArtist());
        });
    }

    public void updatePlaybackSlider(Song song) {
        if (song == null || song.getMp3File() == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            playbackSlider.setMaximum(song.getMp3File().getFrameCount());
            playbackSlider.setValue(0);
        });
    }

    public void enablePauseButtonDisablePlayButton() {
        SwingUtilities.invokeLater(() -> {
            JButton playButton = (JButton) playbackBtns.getComponent(1);
            JButton pauseButton = (JButton) playbackBtns.getComponent(2);
            playButton.setVisible(false);
            playButton.setEnabled(false);
            pauseButton.setVisible(true);
            pauseButton.setEnabled(true);
        });
    }

    public void enablePlayButtonDisablePauseButton() {
        SwingUtilities.invokeLater(() -> {
            JButton playButton = (JButton) playbackBtns.getComponent(1);
            JButton pauseButton = (JButton) playbackBtns.getComponent(2);
            playButton.setVisible(true);
            playButton.setEnabled(true);
            pauseButton.setVisible(false);
            pauseButton.setEnabled(false);
        });
    }

    private ImageIcon loadImage(String imagePath) {
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                BufferedImage image = ImageIO.read(file);
                return new ImageIcon(image);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}