
import java.util.ArrayList;
import java.util.Collections;

public class Playlist{
    private String name;
    private ArrayList<Song> songs;
    private String playlistLength; 
    //img

    //constructor
    public Playlist(String name){
        this.name = name;
        this.songs = new ArrayList<>();
        this.playlistLength = "0 mins 0 secs";
    }

    public void addSong(Song song){
        songs.add(song);
        reformatPlaylistLength();
    }

    public void removeSong(Song song){
        songs.remove(song);
        reformatPlaylistLength();
    }

    private void reformatPlaylistLength(){
        int totalSeconds = 0;
        for(Song song : songs){
            totalSeconds += song.getMp3File().getLengthInSeconds();
        }

        int hours = totalSeconds/3600;
        int minutes = (totalSeconds%3600)/60;
        int seconds = totalSeconds%60;

        //format based on hrs or mins
        if (hours >= 1) {
            String hrUnit = ((hours == 1) ? "hr" : "hrs");
            String minUnit = ((minutes == 1) ? "min" : "mins");
            this.playlistLength = hours + " " + hrUnit + " " + minutes + " " + minUnit;
        } 
    
        else {
            String minUnit = (minutes == 1) ? "min" : "mins";
            String secUnit = (seconds == 1) ? "sec" : "secs";
            this.playlistLength = minutes + " " + minUnit + " " + seconds + " " + secUnit;
        }
    }
    
    //random shuffle
    public void randomShuffle() {
        Collections.shuffle(this.songs);
    }

    //weighted shuffle
}