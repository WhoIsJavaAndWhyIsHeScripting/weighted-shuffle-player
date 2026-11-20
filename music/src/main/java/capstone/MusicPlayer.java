package capstone;

import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;

public class MusicPlayer extends PlaybackListener {
    private static final Object playSignal = new Object();

    private PlayerUI playerUI;

    private Song currentSong;

    private ArrayList<Song> playlist;
    private int currentPlaylistIndex;

    private AdvancedPlayer advancedPlayer;

    private boolean isPaused;
    private boolean songFinished;

    private int currentFrame;
    private int currentTimeMilliseconds;

    private byte loop = 0;

    // 0 <= previousLocation < previousSongs.size(): currently playing inside previousSongs
    // previousLocation == previousSongs.size(): outside previousSongs (playing normally)
    private ArrayList<Song> previousSongs;
    private int previousLocation;
    private ArrayList<Song> QueuedSongs;

    // 0 = not enabled (playlist order)
    // 1 = random shuffle
    // 2 = weighted shuffle
    private byte shuffleType = 0;

    public MusicPlayer(PlayerUI playerUI) {
        this.playerUI = playerUI;
        this.previousSongs = new ArrayList<>();
        this.QueuedSongs = new ArrayList<>();
        this.previousLocation = 0;
    }

    public void loadSong(Song song) {
        if (song == null) {
            return;
        }
        if (!songFinished) {
            stopSong();
        }
        startSong(song, true);
    }

    public void pauseSong() {
        if (advancedPlayer != null) {
            isPaused = true;
            stopSong();
        }
    }

    public void stopSong() {
        if (advancedPlayer != null) {
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void playCurrentSong() {
        if (currentSong == null) {
            return;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            startMusicThread();
            startPlaybackSliderThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void nextSong() {
        if (playlist == null || playlist.isEmpty()) {
            return;
        }
        if (currentPlaylistIndex + 1 >= playlist.size()) {
            return;
        }
        if (!songFinished) {
            stopSong();
        }

        // If currently inside previousSongs, step forward in history first or move to next playlist song
        if (previousLocation >= 0 && previousLocation + 1 < previousSongs.size()) {
            previousLocation++;
            Song nextHist = previousSongs.get(previousLocation);
            syncPlaylistIndex(nextHist);
            startSong(nextHist, false);
            return;
        }

        previousLocation = previousSongs.size();
        currentPlaylistIndex++;
        startSong(playlist.get(currentPlaylistIndex), true);
    }

    public void previousSong() {
        if (previousSongs == null || previousSongs.isEmpty()) {
            return;
        }

        if (!songFinished) {
            stopSong();
        }

        // If currently outside previousSongs (at size()), step to the last recorded song (size() - 1)
        if (previousLocation >= previousSongs.size()) {
            previousLocation = previousSongs.size() - 1;
        } else if (previousLocation > 0) {
            previousLocation--;
        } else {
            // Already at index 0; restart it
            previousLocation = 0;
        }

        Song target = previousSongs.get(previousLocation);
        syncPlaylistIndex(target);
        startSong(target, false);
    }

    private void syncPlaylistIndex(Song song) {
        if (playlist != null && song != null) {
            int idx = playlist.indexOf(song);
            if (idx != -1) {
                currentPlaylistIndex = idx;
            }
        }
    }

    private void startMusicThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isPaused) {
                        synchronized (playSignal) {
                            isPaused = false;
                            playSignal.notify();
                        }
                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                    } else {
                        advancedPlayer.play();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startPlaybackSliderThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isPaused) {
                    try {
                        synchronized (playSignal) {
                            playSignal.wait();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                while (!isPaused) {
                    try {
                        currentTimeMilliseconds += 1;
                        int calculatedFrame = (int) ((double) currentTimeMilliseconds * currentSong.getFrameRatePerMilliseconds());
                        if (playerUI != null) {
                            playerUI.setPlaybackSliderValue(calculatedFrame);
                        }
                        Thread.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        System.out.println("Playback Started");
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        System.out.println("Playback Finished");
        if (isPaused) {
            currentFrame += (int) ((double) evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
            return;
        }

        songFinished = true;

        // 1. Loop forever
        if (loop == 2) {
            restartCurrentSong();
            return;
        }

        // 2. Loop once
        if (loop == 1) {
            loop = 0;
            restartCurrentSong();
            return;
        }

        // 3. Not looping: record current song if not already inside previousSongs
        recordPrevSong(currentSong);

        // 4. Check queue first
        if (QueuedSongs != null && !QueuedSongs.isEmpty()) {
            Song nextQueuedSong = QueuedSongs.remove(0);
            previousLocation = previousSongs.size();
            startSong(nextQueuedSong, true);
            return;
        }

        // 5. Check if still inside previousSongs with another song after current location
        if (previousSongs != null && previousLocation >= 0 && previousLocation + 1 < previousSongs.size()) {
            previousLocation++;
            Song nextFromHistory = previousSongs.get(previousLocation);
            syncPlaylistIndex(nextFromHistory);
            startSong(nextFromHistory, false);
            return;
        }

        // Leaving previousSongs (was at size() - 1 or already at size())
        if (previousSongs != null) {
            previousLocation = previousSongs.size();
        }

        // 6. Check playlist next
        if (playlist != null && !playlist.isEmpty()) {
            if (currentPlaylistIndex + 1 < playlist.size()) {
                currentPlaylistIndex++;
                startSong(playlist.get(currentPlaylistIndex), true);
                return;
            }

            // Reached end of playlist: reshuffle & loop if shuffleType is active (1 = random, 2 = weighted)
            if (shuffleType == 1) {
                Collections.shuffle(playlist);
                avoidImmediateRepeat(currentSong);
                currentPlaylistIndex = 0;
                startSong(playlist.get(currentPlaylistIndex), true);
                return;
            } else if (shuffleType == 2) {
                // Plug in your weighted shuffle logic on `playlist` here
                currentPlaylistIndex = 0;
                startSong(playlist.get(currentPlaylistIndex), true);
                return;
            }
        }

        // 7. End of playback
        currentFrame = 0;
        currentTimeMilliseconds = 0;
        if (playerUI != null) {
            playerUI.enablePlayButtonDisablePauseButton();
        }
    }

    private void avoidImmediateRepeat(Song justFinished) {
        if (justFinished != null && playlist != null && playlist.size() > 1 && playlist.get(0) == justFinished) {
            Collections.swap(playlist, 0, 1);
        }
    }

    private void restartCurrentSong() {
        if (currentSong == null) {
            return;
        }
        currentFrame = 0;
        currentTimeMilliseconds = 0;
        songFinished = false;
        if (playerUI != null) {
            playerUI.enablePauseButtonDisablePlayButton();
            playerUI.updateSongTitleAndArtist(currentSong);
            playerUI.updatePlaybackSlider(currentSong);
        }
        playCurrentSong();
    }

    private void startSong(Song s, boolean recordInHistory) {
        if (s == null) {
            return;
        }
        currentSong = s;
        currentFrame = 0;
        currentTimeMilliseconds = 0;
        songFinished = false;

        if (recordInHistory) {
            recordPrevSong(currentSong);
        }

        if (playerUI != null) {
            playerUI.enablePauseButtonDisablePlayButton();
            playerUI.updateSongTitleAndArtist(currentSong);
            playerUI.updatePlaybackSlider(currentSong);
        }
        playCurrentSong();
    }

    private void recordPrevSong(Song s) {
        if (s == null || previousSongs == null) {
            return;
        }
        // Only append when playing outside previousSongs (at size())
        if (previousLocation >= previousSongs.size()) {
            if (previousSongs.isEmpty() || previousSongs.get(previousSongs.size() - 1) != s) {
                previousSongs.add(s);
            }
            previousLocation = previousSongs.size();
        }
    }

    // loop = 0: do not repeat
    // loop = 1: repeat once then change to 0
    // loop = 2: repeat forever
    public void setLoop() {
        if (loop == 0) {
            loop = 1;
        } else if (loop == 1) {
            loop = 2;
        } else {
            loop = 0;
        }
    }

    // 0 = not enabled (playlist order)
    // 1 = random shuffle
    // 2 = weighted shuffle
    public void setShuffleType() {
        if (shuffleType == 0) {
            shuffleType = 1;
        } else if (shuffleType == 1) {
            shuffleType = 2;
        } else {
            shuffleType = 0;
        }
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }

    public void setCurrentTimeMilliseconds(int timeInMilliseconds) {
        currentTimeMilliseconds = timeInMilliseconds;
    }
}