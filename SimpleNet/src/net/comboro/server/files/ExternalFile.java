/*
 *   ComBoro's Network Server
 *   Copyright (C) 2018  ComBoro
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.comboro.server.files;

import net.comboro.server.Loader;

import java.io.*;

public class ExternalFile {

    public static LogFile logFile;
    public static ServerInfoFile serverInfoFile = new ServerInfoFile();
    public static BanListFile banListFile = new BanListFile();
    
    static {
    	Loader.loadDirectory("logs");
    	logFile = new LogFile();
    }

    public static void closeStatic(){
        serverInfoFile.closeFile();
        banListFile.closeFile();
        logFile.closeFile();
    }

    protected File file;
    protected FileWriter fileWriter;
    protected BufferedReader bufferedReader;

    public ExternalFile(String name){
        this.file = Loader.loadFile(name);
        openReader();
        openWriter();
    }

    public void clearFile(){
        closeWriter();
        try {
            new FileOutputStream(file).close();
        } catch (IOException e) {

        }
        openWriter();
    }

    public String readLine(){
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    public void print(String string){
        try {
            fileWriter.write(string);
            fileWriter.flush();
        } catch (IOException e) {

        }
    }

    public void println(String string){
        this.print(string + System.lineSeparator());
    }

    protected void openReader(){
        try {
            this.bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {

        }
    }

    protected void openWriter(){
        try {
            this.fileWriter = new FileWriter(file, true);
        } catch (IOException e) {

        }

    }

    public void closeWriter(){
        try {
            fileWriter.close();
        } catch (IOException e) {

        }
    }

    public void closeReader(){
        try {
            bufferedReader.close();
        } catch (IOException e) {

        }
    }

    public void closeFile(){
        close();
        closeReader();
        closeWriter();
    }

    protected void close(){}

}
