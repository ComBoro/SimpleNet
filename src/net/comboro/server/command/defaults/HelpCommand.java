/*
 * This file is part of Fusster.
 *	
 * Fusster Copyright (C) ComBoro
 *
 * Fusster is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Fusster is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fusster.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.comboro.server.command.defaults;

import net.comboro.server.Server;
import net.comboro.server.command.Command;
import net.comboro.server.command.CommandMap;
import net.comboro.server.command.CommandSender;

public class HelpCommand extends DefaultCommand {

    public HelpCommand() {
        super("Help Command", "Lists all registered commands",
                "help all/<cmdName>", "help help");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        boolean listAll = false;
        if (args.length != 0)
            listAll = args[0].equalsIgnoreCase("all");

        if (args.length > 0 && !listAll) {

            if (args[0].equals("threads")) {
                sender.sendMessage("Active Threads : " + Thread.activeCount());
                return true;
            }

            Command command = CommandMap.getCommand(args[0]);
            if (command == null) {
                sender.sendMessage("Command not found");
                return true;
            }
            sender.sendMessage(printCommand(command));
            return true;
        } else {
            Server.append("Listing all hidden & unhidden commands");
            sender.sendMessage("List of Commands: ");
            for (int i = 0; i < CommandMap.getCommands().values().size(); i++) {
                Command command = (Command) CommandMap.getCommands().values()
                        .toArray()[i];
                if (command.isListable() || listAll)
                    sender.sendMessage(printCommand(command));
                // else i--;
            }
            return true;
        }

    }

    private String printCommand(Command cmd){
        StringBuilder sb = new StringBuilder("Name: ");
        sb.append(cmd.getName());
        sb.append(", Description: ");
        sb.append(cmd.getDescription());
        sb.append(", Syntax: ");
        sb.append(cmd.getSyntax());
        sb.append(", Example: ");
        sb.append(cmd.getExample());
        return sb.toString();
    }

}
