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

package net.comboro.command;

import net.comboro.SServer;
import net.comboro.command.defaults.*;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandMap {

    private static boolean commandRequested = false;
    private static String command = "";
    private static final Object lock = new Object();

    private static final Map<String, Command> commands = new HashMap<>();

    public static void addDefaults() {
        register("help", new HelpCommand());
        register("plugins", new PluginsCommand());
        register("ban", new BanCommand());
        register("unban", new UnbanCommand());
        register("serverinfo", new ServerInfoCommand());
        register("this", new ThisCommand());
    }

    /**
     * Executes the command and notifies all the plugins
     *
     * @param sender      The commandsender of the command. By default(
     *                    {@link ConsoleCommandSender} and {@link CommandSender})
     * @param commandLine The raw command line enetered
     */
    public static void dispatch(CommandSender sender, String commandLine) {
        if (commandRequested) {
            commandRequested = false;
            command = commandLine;
            synchronized (lock) {
                lock.notify();
            }
            return;
        }

        SServer.debug("Command: " + commandLine, Color.GRAY);
        SServer.debug("Sender: " + sender.getName(), Color.GRAY);

        String[] args = commandLine.split(sender.getSeparator());

        Command command;

        if (args.length == 0)
            command = getCommand(commandLine);
        else
            command = getCommand(args[0]);

        if (command != null)
            command.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        else {
            boolean result = SServer.getPluginMap().onCommand(sender, args[0],
                    Arrays.copyOfRange(args, 1, args.length));
            if (!result)
                SServer.append(sender.getName() + " send invalid command.");
        }
    }

    /**
     * Gets a command by its label.
     *
     * @param name The label of the command
     * @return The command if found else returns null
     */
    public static Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    public static Map<String, Command> getCommands() {
        return commands;
    }

    public static String nextCommand() {
        commandRequested = true;
        synchronized (lock) {
            try {
                lock.wait(0);
                SServer.getServerUI().clearCommandLine();
                return command;
            } catch (InterruptedException e) {
                return null;
            }
        }
    }

    /**
     * Registers a command.
     *
     * @param label   The label that the command wants to be called by
     * @param command The {@link Command} class representative
     * @return if the command was registered successfully
     */
    public static boolean register(String label, Command command) {
        label = label.toLowerCase().trim();
        if (commands.containsKey(label) || commands.containsValue(command))
            return false;
        commands.put(label, command);
        return true;
    }

    /**
     * Unregisters a command.
     *
     * @param command The command class that will be unregistered
     */
    public static void unregister(Command command) {
        if (!commands.values().contains(command))
            return;
        String cmd = null;

        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            if (entry.getValue().equals(command)) {
                cmd = entry.getKey();
                break;
            }
        }

        if (cmd == null)
            return;
        commands.remove(cmd);
    }

}
