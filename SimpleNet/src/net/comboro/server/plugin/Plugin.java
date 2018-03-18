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

package net.comboro.server.plugin;

import net.comboro.server.Application;
import net.comboro.server.Server;
import net.comboro.SerializableMessage;
import net.comboro.server.command.Command;
import net.comboro.server.command.CommandSender;
import net.comboro.server.networking.FinalClientTCP;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;

/**
 * An abstract class used to represent any non-server file that is loaded into
 * the runtime
 */
public abstract class Plugin {

    private Path defaultConfigPath;
    private PluginDescription description;
    private File file, dataFolder, defaultConfig;
    private URLClassLoader classLoader;

    public Plugin() {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof PluginClassLoader))
            throw new IllegalStateException("Plugin requires "
                    + PluginClassLoader.class.getName());
        ((PluginClassLoader) classLoader).initialize(this);
    }

    /**
     * The data folder that the plugin has by default.
     *
     * @return The Data Folder that corresponds to the plugin. Usually the
     * default data folder contains the default config file
     */
    protected final File getDataFolder() {
        return dataFolder;
    }

    /**
     * The default config corresponding to the server.
     *
     * @return The default file named "default.cfg" located in the data folder
     * @see #getDataFolder()
     */
    protected final File getDefaultConfig() {
        return defaultConfig;
    }

    /**
     * Obtain the {@link PluginDescription} used in the Plugin.
     *
     * @return The default {@link PluginDescription} used in the creating of the
     * plugin
     * @see PluginDescription
     */
    public final PluginDescription getDescription() {
        return description;
    }

    public final String getName(){
        return description.getName();
    }

    /**
     * Obtain the actual file that the plugin was loaded from.
     *
     * @return The .jar file that is the plugin
     */
    public final File getFile() {
        return file;
    }

    /**
     * @return The {@link ClassLoader} used to load that class. If loaded
     * correctly this must return an instance of
     * {@link PluginClassLoader}
     */
    public final URLClassLoader getLoader() {
        return classLoader;
    }

    final void initialize(PluginDescription description, File file, File dataFolder,
                          File defaultConfig, URLClassLoader classLoader) {
        this.classLoader = classLoader;
        this.description = description;
        this.file = file;
        this.dataFolder = dataFolder;
        this.defaultConfig = defaultConfig;
        this.defaultConfigPath = Paths.get(defaultConfig.toURI());

        try {
            onEnable();
        } catch (Exception e) {
            e.printStackTrace();
            Server.error("Error loading plugin " + getDescription().getName());
        }
    }

    /**
     * Called when a {@link FinalClientTCP} inputs a {@link SerializableMessage} to the server.
     *
     * @param client  The client sending the input
     * @param message The input send by the client
     * @return Whether the input was used successfully. By default it should be set
     * to false.
     */
    protected abstract boolean onClientInput(FinalClientTCP client, SerializableMessage<?> message) throws Exception;

    /**
     * Called when a {@link CommandSender} send a command to the server.
     *
     * @param sender The sender of the command (by default senders are: Player,
     *               Console).
     * @param label  The label of the command (the first String, in the example
     *               "plugins reload all", "plugins" is the label).
     * @param args   The arguments following the label (in the example
     *               "plugins reload all", "reload all" are the arguments).
     * @return Whether the command was used successfully. By default it should be set
     * to false.
     */
    protected abstract boolean onCommand(CommandSender sender, String label,
                                         String[] args) throws Exception;

    /**
     * Called when the plugin gets unloaded from the runtime.
     *
     * @see PluginLoader#unload(Plugin)
     */
    protected abstract void onDisable() throws Exception;

    // Plugin events

    /**
     * Called when the plugin was successfully loaded into the runtime.
     */
    protected abstract void onEnable() throws Exception;

    /**
     * The default created {@link BufferedReader} using the input stream of the
     * default config.
     *
     * @return {@link BufferedReader} created using the input stream of the
     * default config
     * @throws NullPointerException  if the config is null
     * @throws FileNotFoundException if the file does not exist, is a directory rather than a
     *                               regular file, or for some other reason cannot be opened for
     *                               reading
     * @deprecated The {@link BufferedReader} may be closed or even null
     */
    @Deprecated
    protected final BufferedReader readDefaultConfig()
            throws NullPointerException, FileNotFoundException {
        if (defaultConfig == null)
            throw new NullPointerException("The config file is null");
        return new BufferedReader(new FileReader(defaultConfig));
    }

    /**
     * Reads the config and returns all the lines in the default config file
     *
     * @return All the lines in the default config file
     * @throws IOException if an I/O error occurs reading from the file or a malformed
     *                     or unmappable byte sequence is read
     */
    protected final java.util.List<String> readDefaultConfigAllLines()
            throws IOException {
        return Files.readAllLines(defaultConfigPath);
    }

    /**
     * Used to register a command to the {@link PluginMap}
     *
     * @param label   The label of the command as it would want to be called
     * @param command The actual command instance corresponding to the label
     */
    protected final void registerCommand(String label, Command command) {
        Application.getPluginMap().registerCommand(this, command, label);
    }

    /**
     * Registers a key and a value saved in the the default auto respond list
     *
     * @param key   The key that represents a command label
     * @param value The value linked with the key
     */
    protected final void registerProperty(String key, String value) {
        Application.getProperties().put(key, value);
        Application.getPluginMap().link(this, key);
    }

    /**
     * Used to register a JComponent onto the ConsoleTabbedPane in the GUI
     *
     * @param label     The label of the JComponent as it would be displayed
     * @param component The actual JComponent that will be added
     */
    protected final void registerTab(String label, JComponent component) {
        Application.addUIComponent(label, component);
        Application.getPluginMap().link(this, component);
    }

    /**
     * The default created {@link FileWriter} using the output stream of the
     * default config.
     *
     * @return {@link FileWriter} created using the output stream of the
     * default config
     * @throws NullPointerException if the default config is null
     * @throws IOException          If an I/O error occurs
     * @deprecated The {@link FileWriter} may be closed or even null
     */
    @Deprecated
    protected final FileWriter writeDefaultConfig()
            throws NullPointerException, IOException {
        if (defaultConfig == null)
            throw new NullPointerException("The config writer is null");
        return new FileWriter(defaultConfig);
    }

    /**
     * Writes a text to the default configuration file
     *
     * @param text The text to be written
     * @throws NullPointerException if the writer is equal to null
     * @throws IOException          If an I/O error occurs
     */
    protected final void writeDefaultConfig(String text)
            throws NullPointerException, IOException {
        Files.write(Paths.get(defaultConfig.toURI()), Collections.singletonList(text),
                Charset.forName("UTF-8"), StandardOpenOption.APPEND);
    }

    /**
     * Writes an array of Strings one by one to the default configuration file
     *
     * @param text The text to be written
     * @throws NullPointerException if the writer is equal to null
     * @throws IOException          If an I/O error occurs
     */
    protected final void writeDefaultConfig(String... text)
            throws NullPointerException, IOException {
        Files.write(Paths.get(defaultConfig.toURI()), Arrays.asList(text),
                Charset.forName("UTF-8"), StandardOpenOption.APPEND);
    }

    /**
     * Appends a {@link String} in the server console
     * @param text the text to be appended
     */
    public void append(String text){
        Server.append("[" + getName() + "] ", Color.DARK_GRAY, false);
        Server.append(text);
    }

    /**
     * Appends a {@link String} in the server console if the server is debugging
     * @param text the text to be appended
     */
    public void debug(String text){
        Server.debug("[" + getName() + "] " + text);
    }

    /**
     * Appends a {@link String} in the server console in red
     * @param text the text to be appended
     */
    public void error(String text){
        Server.error("[" + getName() + "] " + text);
    }

    /**
     * Prints the stack track trace of an {@link Exception}
     * @param e The exception to be printed
     */
    public void error(Exception e){
        Server.error(e);
    }

}
