package projectJava2;

import java.util.ArrayList;
import java.util.List;

public class Singer {
    public String getName() {
        return name;
    }

    private String name;
    private List<Album> albums;

    public Singer(String name){
        this.name=name;
        albums=new ArrayList<>();
    }

    public void addAlbum(Album album){
        albums.add(album);
    }

    public List<Album> getAlbums(){
        return albums;
    }
    @Override
    public boolean equals(Object obj){
        return this.name.equals(((Singer)obj).name);
    }
}
