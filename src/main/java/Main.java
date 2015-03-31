import com.mpatric.mp3agic.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by vako on 3/31/15.
 */
public class Main {


    private static Scanner scanner;
    private static File destination;

    public static void main(String[] args) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {

        scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nwhat to do, analyze or quit? (a / Q) ");
            String answer = scanner.next();
            if (answer.equals("analyze") || answer.equals("a")) {
                System.out.println("\nenter path to music directory: ");
                String path = scanner.next().trim();
                destination = new File(path + "/renamed");
                List<File> mp3files = analyze(path);
                if (mp3files == null) continue;
                System.out.println("\nstart setting title and artist? (y / N) ");
                answer = scanner.next();
                if (answer.equalsIgnoreCase("y")) {
                    if (!destination.exists()) {
                        destination.mkdir();
                    }
                    letsDoIt(mp3files);
                }
            } else
                break;
        }


        System.out.println("\nexiting program, bye bye");
    }

    private static void letsDoIt(List<File> mp3files) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {
        List<File> errorFiles = new ArrayList<File>();
        System.out.println("ask confirm for every mp3 file? (Y / n) ");
        boolean confirm = !scanner.next().equalsIgnoreCase("n");
        for (File mp3file : mp3files) {
            String[] aAndT = mp3file.getName().replaceAll(".mp3", "").split("-");
            if (aAndT.length == 2) {
                aAndT[0] = aAndT[0].trim();
                aAndT[1] = aAndT[1].trim();
                System.out.print("for file: \"" + mp3file.getName() + "\"\n\t\tartist is: \"" + aAndT[0] + "\"\n\t\ttitle is: \"" + aAndT[1] + "\"\n");
                if (confirm) {
                    System.out.print("\tcorrect? (Y / n) ");
                    if (!scanner.next().equalsIgnoreCase("n"))
                        setMetaDataAndSave(mp3file, aAndT[0], aAndT[1]);
                    else
                        errorFiles.add(mp3file);
                } else {
                    setMetaDataAndSave(mp3file, aAndT[0], aAndT[1]);
                }

            } else {
                errorFiles.add(mp3file);
            }
        }
    }

    private static void setMetaDataAndSave(File file, String artist, String title) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {

        Mp3File mp3file = new Mp3File(file.getPath());
        ID3v2 id3v2Tag;
        if (mp3file.hasId3v2Tag()) {
            id3v2Tag = mp3file.getId3v2Tag();
        } else {
            id3v2Tag = new ID3v24Tag();
            mp3file.setId3v2Tag(id3v2Tag);
        }

        id3v2Tag.setArtist(artist);
        id3v2Tag.setTitle(title);

        mp3file.save(destination.getPath() + "/" + file.getName());
    }

    public static List<File> analyze(String path) {
        File rootDir = new File(path);
        if (rootDir.isDirectory()) {
            System.out.println("analyzing mp3 files in :" + rootDir.getAbsolutePath());
            File[] files = rootDir.listFiles();
            List<File> list = Arrays.asList(files != null ? files : new File[0]);
            List<File> mp3List = new ArrayList<File>();
            for (File file : list) {
                if (file.isFile() && file.getName().endsWith(".mp3"))
                    mp3List.add(file);
            }
            System.out.println("found " + mp3List.size() + " mp3 files:");
            for (File file : mp3List) {
                System.out.println("\t" + (list.indexOf(file) + 1) + ") " + file.getName());
            }
            return list;
        } else {
            System.out.println("not a directory: " + rootDir.getAbsolutePath());
        }
        return null;
    }
}
