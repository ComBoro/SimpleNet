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

/**
 * Class extending exception used for expressing exceptions that have occurred
 * while managing plugins.
 *
 * @author Admin
 * @see Plugin
 */
public class PluginException extends Exception {
    private static final long serialVersionUID = 1L;

    private String pluginName;

    public PluginException(String message) {
        super(message);
    }

    public PluginException(String message, Plugin plugin) {
        this(message, plugin.getDescription().getName());
    }

    public PluginException(String message, String pluginName) {
        super("[" + pluginName + "] " + message);
        this.pluginName = pluginName;
    }

    public PluginException(String message, String pluginName,
                           Throwable throwable) {
        super("[" + pluginName + "] " + message, throwable);
        this.pluginName = pluginName;
    }

    public PluginException(Throwable throwable) {
        super(throwable);
    }

    public String getPluginName() {
        return pluginName;
    }

}