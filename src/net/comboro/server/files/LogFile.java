package net.comboro.server.files;

public class LogFile extends ExternalFile {

    public LogFile(){
        super("logs/" + System.currentTimeMillis() + ".log");
        closeReader();
    }

    public void log(String string){
        println(string);
    }

}
