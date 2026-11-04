public class Playlist{
    private String name;
    private ArrayList<Song> songs;

    public void addSong(Song song){
        songs.add(song);
    }

    public void removeSong(Song song){
        songs.remove(song);
    }

    //random shuffle

    //weighted shuffle
}