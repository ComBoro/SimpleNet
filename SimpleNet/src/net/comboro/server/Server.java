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

package net.comboro.server;

import net.comboro.server.plugin.Plugin;

import java.awt.*;
import java.net.InetAddress;
import java.util.Vector;

import static net.comboro.server.Application.*;
import static net.comboro.server.files.ExternalFile.*;

public final class Server {

    public static void append(String append) {
        if (betterUI != null)
            betterUI.append(append, defaultColour);
    }

    public static void append(String append, Color color) {
        betterUI.append(append, color, true);
    }

    public static void append(String append, Color color, boolean endLine){
        betterUI.append(append,color,endLine);
    }

    public static void append(String append, Color color, boolean bold, boolean endLine){
        betterUI.append(append, color, bold, endLine);
    }

    /**
     * Prevent an {@link InetAddress} from connecting to the server
     *
     * @param string the String representing an {@link InetAddress}
     */
    public static void ban(String string) {
        banListFile.ban(string);
    }

    /**
     * Check if a key is contained in the properties
     *
     * @param key an indetifier
     * @return if the properties contain the key
     */
    public static boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    public static void debug(Plugin fp, String string) {
        append("[" + fp.getName() + "] " + string);
    }

    public static void debug(String append) {
        if (betterUI.isDebugging()) {
            append(append);
        }
    }

    public static void debug(String append, Color color) {
        if (betterUI.isDebugging()) {
            append(append, color);
        }
    }

    public static void error(String error) {
        debug(error, Application.ERROR);
    }

    public static void error(Exception e){
        if(isDebugging())
            e.printStackTrace(System.err);
    }

    /**
     * Returns a server property
     *
     * @param key an indetifier
     * @return A value matching the key given
     * @see #setProperty(String, String)
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * @return The {@link Vector} of type {@link String} containing all the
     * banned Internet Protocols
     */
    public static Vector<String> getBanList() {
        return banListFile.getBanList();
    }

    /**
     * @return The name of the server
     */
    public static String getName() {
        return serverInfoFile.getName();
    }

    public static void setName(String name){
        serverInfoFile.changeName(name);
        append("Server name chanced to '" + name  +"'", Color.red,true,true);
        betterUI.setTitle(name);
    }

    /**
     * @return The port that the server is currently running on
     */
    public static int getPort() {
        return tcp_server.getPort();
    }

    public static void setPort(int port){
        serverInfoFile.changePort(port);
    }

    /**
     * Set a property in the server properties
     *
     * @param key   an indetifier
     * @param value value licked to the indetifier
     */
    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * Unban an {@link InetAddress}
     *
     * @param string the {@link InetAddress} as String
     * @return if the unban was successful
     */
    public static void unban(String string) {
        banListFile.unban(string);
    }

    public static void resetDefaultColour() {
        defaultColour = Color.BLACK;
    }

    public static Color getDefaultColour() {
        return new Color(defaultColour.getRed(), defaultColour.getGreen(), defaultColour.getBlue());
    }

    public static void setDefaultColour(Color color) {
        defaultColour = color;
    }

}