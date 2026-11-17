package capstone;

import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;

public class MusicPlayer extends PlaybackListener{
    priavte static final Object playSignal = new Object();

    private PlayerUI playerUI;

    private Song currentSong;

    private ArrayList<Song> playlist;
    private int currentPlaylistIndex;

    private AdvancedPlayer advancedPlayer;

    private boolean isPaused;
    private boolean songFinished;

    private int currentFrame;
    private int currentTimeMilliseconds;

    private int loop = 0;

    //constructor
    public MusicPlayer(PlayerUI playerUI){
        this.playerUI = playerUI;
    }

    //
    public void loadSong(Song song){
        currentSong = song;

        if(currentSong != null){
            playCurrentSong();
        }
    }

    public void pauseSong(){
        if (advancedPlayer != null){
            isPaused = true;
            stopSong();
        } 
    }

    public void stopSong(){
        if(advancedPlayer != null){
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void playCurrentSong(){
        if(currentSong == null) return;
        try{
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            startMusicThread();
            startPlaybackSliderThread();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void nextSong(){
        if(playlist == null){
            return;
        }
        if(currentPlaylistIndex+1 > playlist.size()-1){
            return;
        }
        if(!songFinished){
            stopSong();
        }
        currentPlaylistIndex++;
        currentSong = playlist.get(currentPlaylistIndex);
        currentFrame = 0;
        currentTimeMilliseconds = 0;
        playerUI.enablePauseButtonDisablePlayButton();
        playerUI.updateSongTitleAndArtist(currentSong);
        playerUI.updatePlaybackSlider(currentSong);
        playCurrentSong();
    }

    public void previousSong(){
        if(playlist == null){
            return;
        }
        if(currentPlaulistIndex-1 < 0){
            return;
        }
        if(!songFinished){
            stopSong();
        }
        currentPlaylistIndex--;
        playerUI.enablePauseButtonDisablePlayButton();
        playerUI.updateSongTitleAndArtist(currentSong);
        playerUI.updatePlaybackSlider(currentSong);
        playCurrentSong();
    }

    private void playNextSongInPlaylist(){
        if(playlist == null || playlist.isEmpty()){
            return;
        }
        stopSong();
        currentSong = playlist.get(currentPlaylistIndex);
        currentFrame = 0;
        currentTimeMilliseconds = 0;
    }

    private void startMusicThread(){
        new Thread(new Runnable() {
            @Override
            public void run(){
                try {
                    if(isPaused){
                        synchronized(playSignal){
                            isPaused = false;
                            playSignal.notify();
                        }
                        //resume
                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                    }
                    else{
                        //play music from start
                        advancedPlayer.play();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startPlaybackSliderThread(){
        new Thread(new Runnable() {
            @Override
            public void run(){
                if(isPaused){
                    try{
                        synchronized(playSignal){
                            playSignal.wait();                            
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                while(!isPaused){
                    try{
                        currentTimeMilliseconds += 1;
                        int calculatedFrame = (int)((double)currentTimeMilliseconds * currentSong.getFrameRatePerMilliseconds());  //need a multiplier and test on multiple devices, might be client side rendering issue
                        playerUI.setPlaybackSliderValue(calculatedFrame);
                        Thread.sleep(1);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt){
        System.out.println("Playback Started");
    }

    @Override
    public void playbackFinished(PlaybackEvent evt){
        System.out.println("Playback Finished");
        if(isPaused){
            currentFrame += (int)((double)evt.getFrame()*currentSong.getFrameRatePerMilliseconds());
        }
        else{
            songFinished = true;
            if(loop == 2){
                currentFrame = 0;
                currentTimeMilliseconds = 0;
                playCurrentSong()
            }
            else if(loop = 1){
                loop = 0;
                currentFrame = 0;
                currentTimeMilliseconds = 0;
                playCurrentSong()
            }
            else{
                if(playlist == null || playlist.isEmpty()){
                    playerUI.enablePlayButtonDisablePauseButton();
                }
                else{
                    if(currentPlaylistIndex+1 < playlist.size()){
                        currentPlaylistIndex++;
                    }
                    else{
                        //reshuffle
                        playerUI.enablePlayButtonDisablePauseButton();
                    }
                }
            }
        }
    }

    //setter
    //loop = 0: do not repeat
    // loop = 1: repeat once then change to 0
    // loop = 2: repeat forever
    public void setLoop(){
        if(loop==0){
            loop = 1;
        }
        else if(loop==1){
            loop = 2;
        }
        else if(loop == 2){
            loop = 0;
        }
        else{
            loop = 0;
        }
    }

     //getters
     public Song getCurrentSong(){
        return currentSong;
     }

     //setters
     public void setCurrentFrame(int frame){
        currentFrame = frame;
     }

     public void setCurrentTimeMilliseconds(int timeInMilliseconds){
        currentTimeMilliseconds = timeInMilliseconds;
     }
 }