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

import java.io.IOException;
import java.nio.file.Files;
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
