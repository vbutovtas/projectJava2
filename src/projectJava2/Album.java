package projectJava2;

import java.util.ArrayList;
import java.util.List;

public class Album {
    private String name;
    private List<Song> songs;
    public Album(String name){
        this.name=name;
        songs=new ArrayList<>();
    }

    public String getName(){
        return name;
    }

    public List<Song> getSongs(){
        return songs;
    }

    public void addSong(Song song){
        songs.add(song);
    }

    @Override
    public boolean equals(Object obj){
        return this.name.equals(((Album)obj).name);
    }
}
