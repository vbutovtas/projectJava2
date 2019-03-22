package projectJava2;

public class Song {
    private String name;
    private int duration;
    private String link;
    private String hashCode;

    public Song(String name, int duration, String link, String hashCode) {
        this.name = name;
        this.duration = duration;
        this.link = link;
        this.hashCode = hashCode;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public String getLink() {
        return link;
    }

    public String getHashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode.equals(((Song) obj).hashCode);
    }
}

