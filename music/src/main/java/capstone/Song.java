package capstone;

import java.io.File;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import com.mpatric.mp3agic.Mp3File;

public class Song{
    private String title;
    private String artist;
    private String albumName;
    private String duration;
    private String filePath;
    private Mp3File mp3File;
    private double frameRatePerMilliseconds;
    private double weight;
    //img

    public Song(String filePath){
        this.filePath = filePath;
        try{
            mp3File = new Mp3File(filePath);
            frameRatePerMilliseconds = ((double)mp3File.getFrameCount())/mp3File.getLengthInMilliseconds();
            duration = convertToSongLengthFormat();

            //jaudio tagger library, created an audiofile object to read mp3 metadata
            AudioFile audioFile = AudioFileIO.read(new File(filePath));

            Tag tag = audioFile.getTag();
            if(tag != null){
                title = tag.getFirst(FieldKey.TITLE);
                artist = tag.getFirst(FieldKey.ARTIST);
                albumName = tag.getFirst(FieldKey.ALBUM);
            }
            else{
                title = "placeholder";
                artist = "placeholder";
                albumName = "placeholder";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String convertToSongLengthFormat(){
        long minutes = mp3File.getLengthInSeconds()/60;
        long seconds = mp3File.getLengthInSeconds()%60;
        String formattedTime = String.format("%d:%02d", minutes, seconds);

        return formattedTime;
    }

    //getters
    public String getTitle(){
        return title;
    }

    public String getArtist(){
        return artist;
    }

    public String getAlbumName(){
        return albumName;
    }

    public String getDuration(){
        return duration;
    }

    public String getFilePath(){
        return filePath;
    }

    public Mp3File getMp3File(){
        return mp3File;
    }

    public double getFrameRatePerMilliseconds(){
        return frameRatePerMilliseconds;
    }

    //setters
    public void setTitle(String t){
        title = t;
    }

    public void setArtist(String a){
        artist = a;
    }

    public void setAlbumName(String a){
        albumName = a;
    }
}