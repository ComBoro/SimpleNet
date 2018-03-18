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

public class ServerInfoFile extends ExternalFile {

    private String name = "SimpleNet Server";
    private int port = 47247;
    private int maxCharsPerLine = 200;
    private boolean debugging = false;

    ServerInfoFile() {
        super("server.info");

        // Update variables
        String line;
        while((line=readLine())!=null){
            if (line.startsWith("//"))
                continue;
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
                }
            }
        }
    }

    @Override
    protected void close() {
        clearFile();
        println("Name: " + getName());
        println("Port: " + getPort());
        println("Debugging: " + isDebugging());
        println("MaxCharsPerLine: " + getMaxCharsPerLine());
    }

    public void changeName(String name){
        this.name = name;
    }

    public void changePort(int port){
        this.port = port;
    }

    public void setDebugging(boolean debugging){
        this.debugging = debugging;
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

    public int getMaxCharsPerLine() {
        return maxCharsPerLine;
    }

    public void setMaxCharsPerLine(int maxCharsPerLine) {
        this.maxCharsPerLine = maxCharsPerLine;
    }
}
