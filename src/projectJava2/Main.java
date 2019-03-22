package projectJava2;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.ID3v22Tag;
import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;
import javax.xml.bind.DatatypeConverter;



public class Main {

    public static void main(String[] args) throws IOException, ReadOnlyFileException, TagException, InvalidAudioFrameException, CannotReadException, NoSuchAlgorithmException {

        long startTime = System.currentTimeMillis();

        ArrayList<File> fileList=new ArrayList<File>();
        for(int i=0;i<args.length;i++) {
            if((new File(args[i]).isDirectory())){
                processFilesFromFolder(args[i], fileList);
            }

        }

        ArrayList<Singer> singers=new ArrayList<Singer>();
        singers.addAll(getSinger(fileList));
        writeHTML1(singers);

        List<Song> songs=new ArrayList<>();
        List<Song> song_clone=new ArrayList<>();
        for(Singer singer:singers){
            for(Album album:singer.getAlbums()){
                for(Song song:album.getSongs()){
                   songs.add(song);
                }
            }
        }
        
        List<Duplicate> duplicates=new ArrayList<Duplicate>();
        duplicates=getDuplicate(songs);
        writeHTML2(duplicates);

        generateHTML3( singers);



        long timeSpent = System.currentTimeMillis() - startTime;
        System.out.println("программа выполнялась " + timeSpent + " миллисекунд");

    }

    //метод, возвращающий адресс файла для записи
    private static File way(String fileName){
        String separator=File.separator;
        String filePath="E:"+separator+"6сем"+separator+"projectJava2"+separator+fileName;
        File testFile=new File(filePath);
        testFile.delete();

        return testFile;
    }

    //метод, записывающий первое задание в HTML-страницу
    private static void writeHTML1(List<Singer> singers){
        try{
            File testFile=way("HTML1.html");
            String textForFile;
            PrintWriter writer=new PrintWriter(new BufferedWriter(new FileWriter(testFile,true)));
            for(Singer singer:singers){
                textForFile="<span><b>"+singer.getName()+"</b><br /></span>";
                writer.println(textForFile);

                for(Album album:singer.getAlbums()){
                    textForFile="<span><b>&nbsp&nbsp"+album.getName()+"</b><br /></span>";
                    writer.println(textForFile);

                    for(Song song:album.getSongs()){
                        textForFile="<span>&nbsp&nbsp&nbsp&nbsp"+song.getName()+"  "+(song.getDuration()/60)+":"+(song.getDuration()%60)+"  "+
                               "<a href="+song.getLink()+">("+song.getLink()+")</a><br /></span>" ;
                        writer.println(textForFile);
                    }
                }
            }

            writer.flush();
            writer.close();
            System.out.println("Файл успешно записан!\n");
        }
        catch(IOException ex){
            ex.printStackTrace();
            System.out.println("Ошибка записи файла!\n");
        }
    }

    //метод, записывающий второе задание в HTML-страницу
    private static void writeHTML2(List<Duplicate> dublicates){
        try{
            File testFile=way("HTML2.html");
            String textForFile;
            PrintWriter writer=new PrintWriter(new BufferedWriter(new FileWriter(testFile,true)));
            int count=1;
            for(Duplicate dublicate:dublicates){
                textForFile="<span><b> Дубликаты"+count+"</b><br /></span>";
                writer.println(textForFile);

                for(Song song:dublicate.getSongs()){
                    textForFile="<span><b>&nbsp&nbsp"+song.getLink()+"</b><br /></span>";
                    writer.println(textForFile);
                }
                count++;
            }

            writer.flush();
            writer.close();
            System.out.println("Файл успешно записан!\n");
        }
        catch(IOException ex){
            ex.printStackTrace();
            System.out.println("Ошибка записи файла!\n");
        }
    }

    //функции, извлекающие все мп3-файлы из католога и его подкатологов
    private static ArrayList<File> getFileList(String dirPath) {
        File dir = new File(dirPath);

        List<File> fileList =new ArrayList<File>();
        Collections.addAll(fileList,dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".mp3");
            }
        }));

        return (ArrayList<File>) fileList;
    }

    static private void processFilesFromFolder(String foldStr,List<File> fileList) throws IOException {
        File folder=new File(foldStr);
        fileList.addAll(getFileList(foldStr));
        File[] folderEntries = folder.listFiles();
        for (File entry : folderEntries)
        {
            if (entry.isDirectory())
            {
                processFilesFromFolder(entry.getPath(),fileList);
            }
        }
    }

    //метод, возвращающий список исполнителей
    static public List<Singer> getSinger(List<File> files) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException, NoSuchAlgorithmException {
        List<Singer> singers=new ArrayList<Singer>();

        for(File file:files) {
            MP3File audioFile = (MP3File) AudioFileIO.read(file);
            Tag tag = audioFile.getTag();
            if (tag == null) {
                tag = new ID3v22Tag();
            }

            List<TagField> nameFields = tag.getFields(FieldKey.TITLE);
            List<TagField> albumFields = tag.getFields(FieldKey.ALBUM);
            List<TagField> artistFields = tag.getFields(FieldKey.ARTIST);
            int duration = audioFile.getAudioHeader().getTrackLength();
        if(singers.contains(new Singer(pattStr(artistFields)))){
            Singer currSing=singers.get(singers.indexOf(new Singer(pattStr(artistFields))));
            if (currSing.getAlbums().contains(new Album(pattStr(albumFields)))) {
                Album currAlb=currSing.getAlbums().get(currSing.getAlbums().indexOf(new Album(pattStr(albumFields))));
                currAlb.addSong(new Song(pattStr(nameFields),duration,file.getPath(),generateHash(file)));
            }
            else{
                currSing.getAlbums().add(new Album(pattStr(albumFields)));
                Album currAlb=currSing.getAlbums().get(currSing.getAlbums().indexOf(new Album(pattStr(albumFields))));
                currAlb.addSong(new Song(pattStr(nameFields),duration,file.getPath(),generateHash(file)));
            }
        }
        else{
            singers.add(new Singer(pattStr(artistFields)));
            Singer currSing=singers.get(singers.indexOf(new Singer(pattStr(artistFields))));
            currSing.addAlbum(new Album(pattStr(albumFields)));
            Album currAlb=currSing.getAlbums().get(currSing.getAlbums().indexOf(new Album(pattStr(albumFields))));
            currAlb.addSong(new Song(pattStr(nameFields),duration,file.getPath(),generateHash(file)));
        }
        }
        return singers;
    }

    //метод, преобразующий List<TagField> в String
    static public String pattStr(List<TagField> fields){
        String mydata = ""+fields;
        Pattern pattern = Pattern.compile("\"(.*?)\"");
        Matcher matcher = pattern.matcher(mydata);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        else
        {
            return "";
        }
    }

    //метод, возвращающий hash-код файла
    public static String generateHash(File file) throws NoSuchAlgorithmException,IOException {

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(Files.readAllBytes(file.toPath()));
        byte[] hash = messageDigest.digest();

        return DatatypeConverter.printHexBinary(hash).toUpperCase();
    }
/*
    public static List<Duplicate>  getDuplicate(List<Song> songs){
        List<Duplicate> duplicates=new ArrayList<>();
        while(songs.size()!=1){
            Song song=songs.get(0);
            if (songs.stream().anyMatch((s)-> s.getHashCode().equals(song.getHashCode()))) {
                List<Optional<Song>> dublicate=songs.stream().filter((s)->s.getHashCode().equals(song.getHashCode()))
            }
        }
        return duplicates;
    }
*/
    //метод, добавляющий в List<> дубликаты(hash)
    public static void addDublicate(Duplicate currDubl,List<Song> songs){
        Set<Song> set=new HashSet<Song>();
        set.add(currDubl.getSongs().get(0));
        int i=0;
        while(songs.contains(currDubl.getSongs().get(0)))
                if(currDubl.getSongs().get(0).getHashCode().equals(songs.get(i).getHashCode())){
                currDubl.addSong(songs.get(i));
                songs.remove(songs.get(i));
            }
        else{
            i++;
        }
    }

    //метод, возвращающий дубликаты, у которых равны hash-коды
    public static List<Duplicate>  getDuplicate(List<Song> songs){
        List<Duplicate> duplicates=new ArrayList<>();
        while(songs.size()!=0){
            Song song=songs.remove(0);
            if(songs.contains(song)){
                duplicates.add(new Duplicate());
                duplicates.get(duplicates.size()-1).addSong(song);
                addDublicate(duplicates.get(duplicates.size()-1),songs);
            }
        }
        return duplicates;
    }

    //метод, формирующий третий HTML-файл с третьим заданием
    public static void generateHTML3(List<Singer> singers){
        try{
            File testFile=way("HTML3.html");
            String textForFile;
            PrintWriter writer=new PrintWriter(new BufferedWriter(new FileWriter(testFile,true)));
            for(Singer singer:singers){
                for(Album album:singer.getAlbums()){
                    List<Duplicate> duplicates = getDuplicateHTML3(album.getSongs());
                    if(duplicates.size()!=0){
                        for(Duplicate duplicate:duplicates){
                            textForFile="<span><b>"+singer.getName()+", "+album.getName()+" ,"
                                    +duplicate.getSongs().get(0).getName()+"</b><br /></span>";
                            writer.println(textForFile);
                            for(Song song:duplicate.getSongs()){
                                textForFile="<span>"+song.getLink()+"<br /></span>";
                                writer.println(textForFile);
                            }

                        }
                    }
                }
            }
            writer.flush();
            writer.close();
            System.out.println("Файл успешно записан!\n");
        }
        catch(IOException ex){
            ex.printStackTrace();
            System.out.println("Ошибка записи файла!\n");
        }

    }

    //метод, возвращающий дубликаты для третьего задания
    public static List<Duplicate>  getDuplicateHTML3(List<Song> songs) {
        List<Duplicate> duplicates = new ArrayList<>();
        List<Song> song_clone=new ArrayList<>(songs);

        Duplicate dublicate=new Duplicate();
        while(song_clone.size()!=0){
            Song currSong=song_clone.remove(0);
            int i=0;
            while(song_clone.size()!=0){
                if(currSong.getName().equals(song_clone.get(i).getName())){
                    dublicate.addSong(song_clone.remove(i));
                }
            }
            if(dublicate.getSongs().size()!=0){
                dublicate.addSong(currSong);
                duplicates.add(dublicate);
                dublicate=new Duplicate();
            }
        }

        return duplicates;
    }

    public static void searchDublicate(List<Song> songs){
        List<String> hashesSongs=songs.stream()
                .map(Song::getHashCode)
                .collect(Collectors.toList());
        List<Song> dublicate=new ArrayList<>();
                songs.stream()
                .sorted()
                .filter(i->Collections.frequency(hashesSongs,i.getHashCode())>1)
                .forEach(e->dublicate.add(e));

        for(Song song:dublicate)
        System.out.println(song.getName());
    }
}

