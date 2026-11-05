package capstone;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDB {
    private String connectionUrl;
    private Connection connection;
    public SQLiteDB() {
        connectionUrl = "jdbc:sqlite:music//data//songs.db";
        try {
            connection = DriverManager.getConnection(connectionUrl);
            System.out.println("database connected! ^_^");
            Statement statement = connection.createStatement();
            String createUserTblSql = "CREATE TABLE IF NOT EXISTS Users ("
            + "username varchar(30) PRIMARY KEY,"
            + "password char(60) NOT NULL);";
            String createSongTblSql = "CREATE TABLE IF NOT EXISTS Songs ("
            + "songID INTEGER PRIMARY KEY,"
            + "filePath varchar(255) NOT NULL,"
            + "user varchar(30) NOT NULL,"
            + "CONSTRAINT fk_user FOREIGN KEY (user) REFERENCES Users(username));";
            String createPlaylistTblSql = "CREATE TABLE IF NOT EXISTS Playlists ("
            + "playlistID INTEGER PRIMARY KEY,"
            + "playlistName varchar(50) NOT NULL,"
            + "pCreatorUser varchar(30) NOT NULL,"
            + "CONSTRAINT fk_pUser FOREIGN KEY (pCreatorUser) REFERENCES Users(username));";
            String createTagTblSql = "CREATE TABLE IF NOT EXISTS Tags ("
            + "tagID INTEGER PRIMARY KEY,"
            + "tagName varchar(30) NOT NULL,"
            + "user varchar(30) NOT NULL,"
            + "CONSTRAINT fk_user FOREIGN KEY (user) REFERENCES Users(username));";
            String createArtistTblSql = "CREATE TABLE IF NOT EXISTS Artists ("
            + "artistID INTEGER PRIMARY KEY,"
            + "artistName varchar(50) UNIQUE NOT NULL);";
            String createWeightTblSql = "CREATE TABLE IF NOT EXISTS Weights ("
            + "weightID INTEGER PRIMARY KEY,"
            + "user varchar(30) NOT NULL,"
            + "songID INTEGER,"
            + "playlistID INTEGER NOT NULL,"
            + "tagID INTEGER,"
            + "artistID INTEGER,"
            + "weightValue FLOAT NOT NULL,"
            + "CONSTRAINT fk_user FOREIGN KEY (user) REFERENCES Users(username),"
            + "CONSTRAINT fk_song FOREIGN KEY (songID) REFERENCES Songs(songID),"
            + "CONSTRAINT fk_playlist FOREIGN KEY (playlistID) REFERENCES Playlists(playlistID),"
            + "CONSTRAINT fk_artist FOREIGN KEY (artistID) REFERENCES Artists(artistID),"
            + "CONSTRAINT fk_tag FOREIGN KEY (tagID) REFERENCES Tags(tagID));";
            String createPlaylistSongJunction = "CREATE TABLE IF NOT EXISTS PlaylistSong ("
            + "entryID INTEGER PRIMARY KEY,"
            + "songID INTEGER NOT NULL,"
            + "playlistID INTEGER NOT NULL,"
            + "CONSTRAINT fk_song FOREIGN KEY (songID) REFERENCES Songs(songID),"
            + "CONSTRAINT fk_playlist FOREIGN KEY (playlistID) REFERENCES Playlists(playlistID));";
            String createArtistSongJunction = "CREATE TABLE IF NOT EXISTS PlaylistSong ("
            + "entryID INTEGER PRIMARY KEY,"
            + "artistID INTEGER NOT NULL,"
            + "songID INTEGER NOT NULL,"
            + "CONSTRAINT fk_artist FOREIGN KEY (artistID) REFERENCES Artists(artistID),"
            + "CONSTRAINT fk_song FOREIGN KEY (songID) REFERENCES Songs(songID));";

            statement.execute(createUserTblSql);
            statement.execute(createSongTblSql);
            statement.execute(createPlaylistTblSql);
            statement.execute(createTagTblSql);
            statement.execute(createArtistTblSql);
            statement.execute(createWeightTblSql);
            statement.execute(createPlaylistSongJunction);
            statement.execute(createArtistSongJunction);
        } catch(SQLException e) {
           System.out.println(e.getMessage());
        }
    }

    public void addSong() {

    }
    
}
