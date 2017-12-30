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

package net.comboro.plugin;

import net.comboro.SServer;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {

    private File directory;
    private PluginMap pluginMap;
    private Map<File, Boolean> jars = new HashMap<File, Boolean>();

    /**
     * Creates a PluginLoader and lists all the jars but does NOT load them
     *
     * @param pluginMap The plugin map
     * @see #listJars(File)
     */
    public PluginLoader(PluginMap pluginMap, File directory) {
        this.pluginMap = pluginMap;

        this.directory = directory;

        listJars(directory);
    }

    /**
     * Gets the extension of a file
     *
     * @param file The file thats extension is going to be returned
     * @return The extension of the file
     */
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else
            return "";
    }

    /**
     * Extracts the {@link PluginDescription} from a file
     *
     * @param file The file to extract the description from
     * @return The description of the plugin
     * @throws PluginException if an error occurs or the file is wrongly formated
     */
    private PluginDescription getDescription(File file) throws PluginException {
        try {
            JarFile jarFile = new JarFile(file);
            JarEntry entry = jarFile.getJarEntry("info.dat");

            if (entry == null) {
                jarFile.close();
                throw new PluginException("Missing file 'info.dat' in plugin "
                        + file.getAbsolutePath());
            }

            InputStream stream = jarFile.getInputStream(entry);

            PluginDescription description = new PluginDescription(stream);

            jarFile.close();

            return description;
        } catch (Exception e) {
            if (e instanceof PluginException)
                throw (PluginException) e;
            else
                throw new PluginException(e.getMessage()
                        + ". Something went wrong reading the jar file: "
                        + file.getAbsolutePath());
        }
    }

    /**
     * @return The directory where all plugin files are stored
     */
    public File getDirectory() {
        return directory;
    }

    /**
     * Lists all the files ending in .jar
     */
    private File[] listJars(File directory) {
        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isDirectory() && pathname.canRead()
                        && getFileExtension(pathname).equals("jar");
            }
        });

        for (File file : files)
            if (!this.jars.containsKey(file))
                this.jars.put(file, false);

        return files;
    }

    @SuppressWarnings("resource")
    /**
     * Gets a file and tries to load it into the runtime
     * @param file The .jar file to load
     * @return The loaded plugin
     * @throws PluginException if the file is not a plugin or does not contain the essential elements to be one
     */
    public Plugin load(File file) throws PluginException {
        if (!file.exists() || file.isDirectory())
            throw new PluginException("File not found", file.getName());
        if (!getFileExtension(file).equals("jar"))
            throw new PluginException("Invalid file format", file.getName());

        try {
            PluginDescription description = getDescription(file);

            File dataFolder = new File(file.getParentFile(),
                    description.getName());

            if (!dataFolder.exists())
                dataFolder.mkdir();

            // Final Validation
            if (!dataFolder.exists() || !dataFolder.isDirectory())
                throw new PluginException("Invalid data folder", file.getName());

            File defaultConfig = new File(dataFolder, "default.cfg");

            if (!defaultConfig.exists())
                defaultConfig.createNewFile();

            // Final Validation
            if (!defaultConfig.exists() || !defaultConfig.canRead()
                    || !defaultConfig.canWrite())
                throw new PluginException("Invalid default config file",
                        file.getName());

            PluginClassLoader loader = new PluginClassLoader(description, dataFolder, defaultConfig, file);
            Plugin plugin = loader.getPlugin();
            jars.put(file, true);
            pluginMap.register(plugin);
            return plugin;
        } catch (PluginException | IOException e) {
            if (e instanceof PluginException)
                throw (PluginException) e;
            else
                throw new PluginException(e.getMessage(), file.getName());
        }
    }

    public void loadAll() {
        this.loadAll(directory);
    }

    /**
     * Lists all the files and loads all the plugins that are not loaded
     */
    public void loadAll(File directory) {
        if (!directory.isDirectory())
            return;
        listJars(directory);
        for (File file : jars.keySet()) {
            if (!jars.get(file).booleanValue())
                try {
                    load(file);
                } catch (PluginException e) {
                    SServer.error(e.getMessage());
                }
        }
    }

    /**
     * @param plugin The {@link Plugin} to unload
     * @see #unload(Plugin)
     * @deprecated Should never be used outside of
     * {@link #unload(Plugin)}
     */
    @Deprecated
    private void rawUnload(Plugin plugin) {
        if (plugin == null)
            return;
        try {
            plugin.onDisable();
        } catch (Exception e) {
            SServer.debug("Error disabling plugin "
                    + plugin.getDescription().getName()
                    + ". Moving on regardless...");
        }

        File file = plugin.getFile();
        if (jars.containsKey(file) && jars.get(file).booleanValue())
            jars.put(file, false);

    }

    /**
     * Unloads and then loads a plugin
     *
     * @param plugin The plugin to load and unload
     * @see #unload(Plugin)
     * @see #load(File)
     */
    public void reload(Plugin plugin) {
        unload(plugin);
        try {
            load(plugin.getFile());
        } catch (PluginException e) {
            e.printStackTrace();
        }
    }

    public void reloadAll() {
        unloadAll();
        loadAll();
    }

    /**
     * Unloads a plugin from the runtime and removes it from the list of plugins
     *
     * @param plugin The plugin to unload
     */
    public void unload(Plugin plugin) {
        rawUnload(plugin);
        pluginMap.unrgisterAllCommands(plugin);
        pluginMap.unregister(plugin, true);
    }

    /**
     * Unloads all the plugins from the runtime and clears the list
     */
    public void unloadAll() {
        pluginMap.getPlugins().forEach(pl -> rawUnload(pl));
        pluginMap.clear();
        jars.clear();
    }

}
