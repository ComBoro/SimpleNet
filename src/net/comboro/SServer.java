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

package net.comboro;

import net.comboro.command.CommandMap;
import net.comboro.networking.TCPServerImpl;
import net.comboro.plugin.Plugin;
import net.comboro.plugin.PluginLoader;
import net.comboro.plugin.PluginMap;
import net.comboro.ui.BetterUI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

public final class SServer {

	private static BetterUI betterUI;
	private static ServerInfo serverInfo;
	private static PluginLoader pluginLoader;
	private static PluginMap pluginMap;
	private static boolean offline = false;
	private static String publicIP = "127.0.0.1";
	public static String localIP = new String(publicIP);
	private static int port;

	public static final Color error = new Color(178, 34, 34);
	private static Color defaultColour = Color.BLACK;

	public static final String BAN_MESSEGE = "Connection establishment prohibited.";

	private static Properties properties = new Properties();
	
	public static TCPServerImpl tcp_server;

	public static void append(String append) {
		if (betterUI != null)
			betterUI.append(append, defaultColour);
	}

	public static void append(String append, Color color) {
		betterUI.append(append, color);
	}

	/**
	 * Prevent an {@link InetAddress} from connecting to the server
	 * 
	 * @param string
	 *            the String representing an {@link InetAddress}
	 */
	public static void ban(String string) {
		serverInfo.ban(string);
	}

	/**
	 * Check if a key is contained in the properties
	 * 
	 * @param key
	 *            an indetifier
	 * @return if the properties contain the key
	 */
	public static boolean containsKey(String key) {
		return properties.containsKey(key);
	}

	public static void debug(Plugin fp, String string) {
		append("[" + fp.getDescription().getName() + "] " + string);
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
		debug(error, SServer.error);
	}

	private static void findIP() {
		try {
			publicIP = new BufferedReader(new InputStreamReader(new URL(
					"http://bot.whatismyipaddress.com/").openStream()))
					.readLine();
			localIP = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			SServer.setOffline(true);
		}
	}

	/**
	 * @return The {@link Vector} of type {@link String} containing all the
	 *         banned Internet Protocols
	 */
	public static Vector<String> getBanList() {
		return serverInfo.getBanList();
	}

	/**
	 * Gets the public address of the server
	 * 
	 * @return The public address of the server
	 */
	public static String getIP() {
		return publicIP;
	}

	/**
	 * @return The name of the server
	 */
	public static String getName() {
		return serverInfo.getName();
	}

	/**
	 * @return The default {@link PluginLoader} initialised in the main method
	 */
	public static PluginLoader getPluginLoader() {
		return pluginLoader;
	}

	/**
	 * @return The default {@link PluginMap} initialised in the main method
	 */
	public static PluginMap getPluginMap() {
		return pluginMap;
	}

	/**
	 * @return The port that the server is currently running on
	 */
	public static int getPort() {
		return port;
	}

	/**
	 * Gets the {@link Properties} instance.
	 * 
	 * @return the {@link Properties} instance
	 */
	public static Properties getProperties() {
		return properties;
	}

	/**
	 * Returns a server property
	 * 
	 * @param key
	 *            an indetifier
	 * @return A value matching the key given
	 * @see #setProperty(String, String)
	 */
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * @return an instance of the {@link BetterUI}
	 */
	public static BetterUI getServerUI() {
		return betterUI;
	}

	private static void initProperties() {
		properties.setProperty("name", serverInfo.getName());
		properties.setProperty("version", ServerInfo.VERSION);
	}

	/**
	 * @return the server debugging state
	 */
	public static boolean isDebugging() {
		return serverInfo.isDebugging();
	}

	/**
	 * @return if the server is offline
	 */
	public static boolean isOffline() {
		return offline;
	}

	public static void log(String string) {
		if (serverInfo.logger != null)
			serverInfo.logger.print(string);
	}

	public static void main(String[] args) {
		// Load GUI
		betterUI = new BetterUI();
		// Load the server configuration
		serverInfo = new ServerInfo();
		// Get config port
		port = serverInfo.getPort();
		
		tcp_server = new TCPServerImpl(port);
		tcp_server.startServer();
		
		// Basic auto response
		initProperties();
		// Register default commands
		CommandMap.addDefaults();
		// Get the plublic ip
		findIP();
		// Loader the basic Plugin Map
		pluginMap = new PluginMap();
		// Loader the loader loading plugins from 'plugins'
		pluginLoader = new PluginLoader(pluginMap, Loader.loadDirectory("plugins"));
		// Load all plugins
		pluginLoader.loadAll();
	}

	/**
	 * Change the debugging state of the server
	 * 
	 * @param debugging
	 *            the new debugging state
	 */
	public static void setDebugging(boolean debugging) {
		serverInfo.setDebugging(debugging);
	}

	/**
	 * Set the server as online or offline.
	 * 
	 * @param offline
	 *            set the server's offline state
	 */
	public static void setOffline(boolean offline) {
		SServer.offline = offline;
	}

	/**
	 * Set a property in the server properties
	 * 
	 * @param key
	 *            an indetifier
	 * @param value
	 *            value licked to the indetifier
	 */
	public static void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	/**
	 * Shuts down the server and disconnects all players sending them the server
	 * close message.
	 * 
	 * @param halt
	 *            if the FussterServer proccess should be terminated
	 */
	public static void shutdown(boolean halt) {
		serverInfo.logger.close();
		properties.clear();
		if (pluginLoader != null)
			getPluginLoader().unloadAll();
		if (halt)
			Runtime.getRuntime().halt(0);
	}

	/**
	 * Unban an {@link InetAddress}
	 * 
	 * @param string
	 *            the {@link InetAddress} as String
	 * @return if the unban was successful
	 */
	public static boolean unban(String string) {
		return serverInfo.unban(string);
	}
	
	public static void setDefaultColour(Color color) {
		defaultColour = color;
	}
	
	public static void resetDefaultColour() {
		defaultColour = Color.BLACK;
	}
	
	public static Color getDefaultColour() {
		return new Color(defaultColour.getRed(), defaultColour.getGreen(), defaultColour.getBlue());
	}

}
