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

import java.awt.*;

public class ConsoleCommandSender implements CommandSender {

    private static final ConsoleCommandSender sender = new ConsoleCommandSender();

    private ConsoleCommandSender() {
    }

    public static ConsoleCommandSender getInstance() {
        return sender;
    }

    @Override
    public String getName() {
        return "Console";
    }

    @Override
    public String getSeparator() {
        return " ";
    }

    @Override
    public void sendMessage(String message) {
        SServer.append(message, Color.darkGray);
    }

}
