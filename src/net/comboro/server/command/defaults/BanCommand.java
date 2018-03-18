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
import net.comboro.server.command.CommandSender;
import net.comboro.server.command.Commands;


public class BanCommand extends DefaultCommand {

    public BanCommand() {
        super("BanCommand",
                "Bans an internet protocol from establishing a connection",
                "ban <ip>", "ban 127.0.0.1");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
    	Commands.requirePermission(sender, "ban", "unban");
    	
        if (args.length != 1) {
            sender.sendMessage("Invalid arguments");
            return false;
        }

        Server.ban(args[0]);

        sender.sendMessage("All clients with Internet Protocol " + args[0]
                + " were banned from the server.");

        return false;
    }

}
