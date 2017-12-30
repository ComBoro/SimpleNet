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

package net.comboro.command.defaults;

import net.comboro.SServer;
import net.comboro.command.CommandSender;

public class UnbanCommand extends DefaultCommand {

	public UnbanCommand() {
		super("UnbanCommand", "Unbans an ip", "/unban 1.1.1.1");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0)
			return false;
		String ip = args[0].trim();
		boolean status = SServer.unban(ip);
		if (status)
			sender.sendMessage("Successfully unbaned " + ip + ".");
		else
			sender.sendMessage("Failed to unban " + ip + ".");
		return status;
	}

}
