package gitlet;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;


public class Commit implements Serializable {
    private final String parent;
    private final String message;
    private String commitDate;
    //    Change: Contents: filename -- SHA1
    private HashMap<String, String> contents = new HashMap<>();
    private String ID;
    private boolean isSplit;

    public Commit(String message, HashMap<String, String> stagingArea, String gitletDirectory) {
        parent = null;
        this.message = message;
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        commitDate = ft.format(date);
        isSplit = false;
    }

    public Commit(String parentId, String message, HashMap<String, String> stagingArea,
                  HashMap<String, Commit> commits, String gitletDirectory,
                  HashSet<String> removes) throws IOException {
        parent = parentId;
        this.message = message;
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        commitDate = ft.format(date);
        isSplit = false;
        if (parentId != null) {
            contents = commits.get(parentId).contents;
        } else {
            contents = new HashMap<>();
        }

        for (String checkName : removes) {
            if (contents.containsKey(checkName)) {
                contents.remove(checkName);
            }
        }

        HashMap<String, String> temp2 = copyHash(stagingArea);
        for (String keyName : temp2.keySet()) {
            if (contents.containsKey(keyName)
                    && contents.get(keyName).equals(stagingArea.get(keyName))) {
                File file = new File(gitletDirectory + "StagingArea/" + stagingArea.get(keyName));
                file.delete();
            } else {
                contents.put(keyName, stagingArea.get(keyName));
                if (!new File(gitletDirectory + stagingArea.get(keyName)).exists()) {
                    Files.move(Paths.get(gitletDirectory
                                    + "StagingArea/" + stagingArea.get(keyName)),
                            Paths.get(gitletDirectory + stagingArea.get(keyName)));
                }
            }
        }
    }

    public static HashMap<String, String> copyHash(HashMap<String, String> hash) {
        HashMap<String, String> newHash = new HashMap<>();
        for (String keyName : hash.keySet()) {
            newHash.put(keyName, hash.get(keyName));
        }
        return newHash;
    }

    public File serialize(String gitletDirectory) throws IOException {
        File outFile = new File(gitletDirectory + "commit/" + "a");
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(this);
            out.close();
        } catch (IOException excp) {
            System.out.println("3Error!");
            return null;
        }
        File newOut = new File(
                gitletDirectory + "commit/" + Utils.sha1(Utils.readContents(outFile)));
        newOut.createNewFile();
        Utils.writeContents(newOut, Utils.readContents(outFile));
        outFile.delete();
        return newOut;
    }

    public static Commit deserialize(String myDirectory, String commitID) {
        try {
            Commit myCommit;
            String path = myDirectory + "commit/" + commitID;
            File inFile = new File(path);
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(inFile));
            myCommit = (Commit) in.readObject();
            myCommit.initializeID(myDirectory);
            in.close();
            return myCommit;
        } catch (IOException i) {
            System.out.println("4Error!");
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Commit class not found");
            return null;
        }
    }

    public String initializeID(String gitletDirectory) throws IOException {
        ID = serialize(gitletDirectory).getName();
        return ID;
    }

    public String getID() {
        return ID;
    }

    public String getMessage() {
        return message;
    }


    public String getParent() {
        return parent;
    }

    public HashMap<String, String> getContents() {
        return contents;
    }


    public String toString() {
        return "===\nCommit " + ID + "\n" + commitDate + "\n" + message;
    }
}
