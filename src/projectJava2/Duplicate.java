package projectJava2;

import java.util.ArrayList;
import java.util.List;

public class Duplicate {
    private List<Song> songs;

    public Duplicate(){
        songs=new ArrayList<Song>();

    }

    public  void addSong(Song song){
        this.songs.add(song);
    }
    public List<Song> getSongs(){
        return songs;
    }

}
