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

import net.comboro.Client;
import net.comboro.SServer;
import net.comboro.SerializableMessage;
import net.comboro.command.Command;
import net.comboro.command.CommandMap;
import net.comboro.command.CommandSender;

import javax.swing.*;
import java.util.*;

public class PluginMap {

    private final Map<Plugin, Set<Object>> synmap;

    /**
     * Generates an empty synchronised map.
     */
    public PluginMap() {
        synmap = Collections
                .synchronizedMap(new HashMap<Plugin, Set<Object>>());
    }

    public void clear() {
        getPlugins().forEach(this::unregister);

        synmap.clear();
    }

    /**
     * @return The whole map of plugins and commands
     * @see #getPlugins() if you want to get only the set of plugins
     */
    public Map<Plugin, Set<Object>> getMap() {
        synchronized (synmap) {
            return synmap;
        }
    }

    /**
     * Tries to get a plugin from the list
     *
     * @param name The name of the plugin
     * @return The plugin found by the name given
     * @throws PluginException if there is no plugin found
     */
    public Plugin getPlugin(String name) throws PluginException {
        synchronized (synmap) {
            for (Plugin plugin : getPlugins())
                if (plugin.getDescription().getName().equals(name))
                    return plugin;
            throw new PluginException("Plugin not found");
        }
    }

    /**
     * @return The set of plugins
     */
    public Set<Plugin> getPlugins() {
        synchronized (synmap) {
            return synmap.keySet();
        }
    }

    /**
     * @param plugin The plugin to list all the commands from
     * @return all the registered command by a plugin
     */
    public Set<Command> getRegisteredCommands(Plugin plugin) {
        synchronized (synmap) {
            Set<Command> cmds = new HashSet<>();
            for (Object object : synmap.get(plugin)) {
                if (object instanceof Command)
                    cmds.add((Command) object);
            }
            return cmds;
        }
    }

    /**
     * Links an Object to a FussterPlugin
     *
     * @param plugin The plugin the object is going to be linked with
     * @param link   The actual object being linked
     */
    public void link(Plugin plugin, Object link) {
        if (!synmap.containsKey(plugin))
            register(plugin);
        synmap.get(plugin).add(link);
    }

    /**
     * Notifies all the plugins about a new command and returns a result
     *
     * @param sender The sender of the command
     * @param label  The label of the command
     * @param args   The arguments following the command
     * @return if the command was used successfully
     * @see Plugin#onCommand(CommandSender, String, String[])
     */
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        synchronized (synmap) {
            boolean success = false;
            for (Plugin fp : synmap.keySet()) {
                try {
                    boolean result = fp.onCommand(sender, label, args);
                    if (result) {
                        success = true;
                        break;
                    }
                } catch (Exception exception) {
                    success = false;
                    SServer.debug(fp, " Error executing command with label: "
                            + label + ", args: " + Arrays.toString(args)
                            + ". Message: " + exception.getMessage());
                }
            }
            return success;
        }
    }

    //TODO: Write it
    public boolean onClientInput(Client client, SerializableMessage<?> message) {
        synchronized (synmap) {
            //boolean success = false;
        }
        return false;
    }

    /**
     * Registers a plugin into the map with an empty set of commands.
     *
     * @param toRegister The plugin to register
     */
    public void register(Plugin toRegister) {
        synchronized (synmap) {
            if (synmap.containsKey(toRegister))
                return;

            List<String> pluginNames = new ArrayList<>();

            for (Plugin plugin : getPlugins()) {
                pluginNames.add(plugin.getDescription().getName());
            }

            if (pluginNames.contains(toRegister.getDescription().getName()))
                return;

            synmap.put(toRegister, new HashSet<>());

            SServer.getServerUI().updatePluginsPane();
        }
    }

    /**
     * Links a command to a plugin. When the plugin gets unloaded all the bound
     * commands get unregistered. Also registers the plugin if the plugin isn't
     * registered
     *
     * @param plugin  The plugin that the command will be linked to
     * @param command The actual command to bound
     * @param label   The label that the command should react to
     * @see CommandMap#register(String, Command)
     */
    public void registerCommand(Plugin plugin, Command command,
                                String label) {
        synchronized (synmap) {
            if (!synmap.keySet().contains(plugin))
                register(plugin);
            synmap.get(plugin).add(command);
            CommandMap.register(label, command);
        }
    }

    /**
     * Unlinks an {@link Object} from a {@link Plugin}
     *
     * @param plugin The plugin the object is going to be unlicked from
     * @param link   The actual object being unlinked
     */
    public void unlink(Plugin plugin, Object link) {
        if (synmap.containsKey(plugin))
            synmap.get(plugin).remove(link);
    }

    /**
     * Unregisters a plugin from the map.
     *
     * @param plugin The plugin to unregister
     */
    private void unregister(Plugin plugin) {
        unregister(plugin, false);
    }

    /**
     * Unregisters a plugin from the map.
     *
     * @param plugin The plugin to unregister
     * @param remove if it should be removed from the map as well
     */
    public void unregister(Plugin plugin, boolean remove) {
        synchronized (synmap) {
            if (!synmap.containsKey(plugin))
                return;
            for (Object object : synmap.get(plugin)) {
                if (object instanceof JComponent) { // Remove plugin created
                    // tabs
                    SServer.getServerUI().getConsoleTabbedPane()
                            .remove((JComponent) object);
                } else if (object instanceof String) { // Remove potencial
                    // properties
                    String str = (String) object;
                    if (SServer.containsKey(str))
                        SServer.getProperties().remove(str);
                }
            }
            unrgisterAllCommands(plugin);
            // Remove from map
            if (remove)
                synmap.remove(plugin);

            SServer.getServerUI().updatePluginsPane();
        }
    }

    /**
     * Unlinks a command from its linked plugin. The command gets unregistered
     * afterwards.
     *
     * @param plugin  The plugin that the command is linked to
     * @param command The actual command to unregister
     * @see CommandMap#unregister(Command)
     */
    public void unregisterCommand(Plugin plugin, Command command) {
        synchronized (synmap) {
            if (!synmap.keySet().contains(plugin)
                    && !synmap.get(plugin).contains(command))
                return;
            synmap.get(plugin).remove(command);
            CommandMap.unregister(command);
        }
    }

    /**
     * Clears all the commands from a plugin and unregisters them.
     *
     * @param plugin The plugin to clear
     * @see CommandMap#unregister(Command)
     */
    public void unrgisterAllCommands(Plugin plugin) {
        synchronized (synmap) {

            for (Object object : synmap.get(plugin)) {
                if (object == null) continue;
                if (object instanceof Command) {
                    CommandMap.unregister((Command) object);
                }

            }
            synmap.get(plugin).clear();
            synmap.remove(plugin);
        }
    }

}
