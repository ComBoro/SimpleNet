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

import net.comboro.server.command.Commands;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ServerInfoFile extends ExternalFile {

    private String name = "SimpleNet Server";
    private int port = 47247;
    private int maxCharsPerLine = 200;
    private boolean debugging = false;

    ServerInfoFile() {
        super("server.info");

        boolean readingPermissions = false;

        String ip = null;
        // Update variables
        String line;
        while ((line = readLine()) != null) {
            if (line.startsWith("//"))
                continue;
            if (readingPermissions) {

                if(line.equals("{")) {
                    readingPermissions = false;
                    continue;
                }

                if(line.contains("\"")){
                    String content = line.substring(
                            line.indexOf('\"') + 1,
                            line.lastIndexOf('\"'));

                    if (line.contains(":"))
                        ip = content;
                    else {
                        if (ip != null)
                            Commands.linkPermissionToIP(content, ip);
                    }
                }

                continue;
            }

            if (line.contains(":")) {
                String[] split = line.split(":");
                String variable = split[0].trim();
                String value = split[1].trim();
                switch (variable) {
                    case "Name":
                        name = String.valueOf(value);
                        break;
                    case "Port":
                        port = Integer.valueOf(value);
                        break;
                    case "Debugging":
                        debugging = Boolean.valueOf(value);
                        break;
                    case "MaxCharsPerLine":
                        setMaxCharsPerLine(Integer.valueOf(value));
                        break;
                    case "Permissions":
                        readingPermissions = true;
                        break;
                }
            }
        }
        closeReader();
    }

    @Override
    protected void close() {
        clearFile();
        println("Name: " + getName());
        println("Port: " + getPort());
        println("Debugging: " + isDebugging());
        println("MaxCharsPerLine: " + getMaxCharsPerLine());
        printPermissions();
    }

    private void printPermissions() {
        print("Permissions : {" + System.lineSeparator() + "   ");
        Map<String, Set<String>> perms = Commands.getIpPermList();
        Iterator<Map.Entry<String, Set<String>>> entryIterator = perms.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, Set<String>> e = entryIterator.next();
            println(" \"" + e.getKey() + "\" : {");
            Iterator<String> permissionsIterator = e.getValue().iterator();
            while (permissionsIterator.hasNext()) {
                println("        \"" + permissionsIterator.next() + '\"' + (permissionsIterator.hasNext() ? "," : ""));
            }
            print("    }" + (entryIterator.hasNext() ? "," : ""));
        }
        println(System.lineSeparator() + "}");
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changePort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public boolean isDebugging() {
        return debugging;
    }

    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }

    public int getMaxCharsPerLine() {
        return maxCharsPerLine;
    }

    public void setMaxCharsPerLine(int maxCharsPerLine) {
        this.maxCharsPerLine = maxCharsPerLine;
    }
}
