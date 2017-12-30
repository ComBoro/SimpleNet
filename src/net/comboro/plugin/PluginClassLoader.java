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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * A class extending {@link URLClassLoader} used specially for loading plugins
 * into the runtime.
 *
 * @author Admin
 * @see Plugin
 */
public class PluginClassLoader extends URLClassLoader {

    private PluginDescription description;
    private File file, dataFolder, defaultConfig;
    private Plugin plugin;

    public PluginClassLoader(PluginDescription description, File dataFolder, File defaultConfig,
                             File file) throws MalformedURLException, PluginException {
        super(new URL[]{file.toURI().toURL()});

        this.description = description;
        this.file = file;
        this.defaultConfig = defaultConfig;
        this.dataFolder = dataFolder;

        try {
            Class<?> jarClass = null;
            try {
                jarClass = Class.forName(description.getMain(), true, this);
            } catch (ClassNotFoundException ex) {
                throw new PluginException("Cannot find main class `"
                        + description.getMain() + "'", description.getName());
            }

            Class<? extends Plugin> pluginClass = null;
            try {
                pluginClass = jarClass.asSubclass(Plugin.class);
            } catch (ClassCastException ex) {
                throw new PluginException("Main class `"
                        + description.getMain()
                        + "' does not extend FussterPlugin",
                        description.getName());
            }

            plugin = pluginClass.newInstance();
        } catch (IllegalAccessException ex) {
            throw new PluginException(ex.getMessage(), description.getName());
        } catch (InstantiationException ex) {
            throw new PluginException(ex.getMessage(), description.getName());
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }

    synchronized void initialize(Plugin plugin) {
        if (this.plugin != null) {
            throw new IllegalArgumentException("Plugin already initialized!");
        }
        plugin.initialize(description, file, dataFolder, defaultConfig,
                this);
    }

}
