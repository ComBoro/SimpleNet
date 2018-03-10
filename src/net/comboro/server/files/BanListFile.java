package net.comboro.server.files;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Vector;

public class BanListFile extends ExternalFile{

    private Vector<String> banList = new Vector<>();

    public BanListFile(){
        super("banlist.dat");

        try {
            banList.addAll(Files.readAllLines(file.toPath()));
        } catch (IOException e) {

        }

        closeReader();
    }

    public void ban(String string){
        if(banList.contains(string))
            return;

        banList.addElement(string);
    }

    public void unban(String string){
        banList.removeElement(string);
    }

    public Vector<String> getBanList() {
        return banList;
    }

    @Override
    protected void close() {
        clearFile();
        banList.forEach(this::println);
    }
}
