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

import net.comboro.internet.tcp.FinalClientTCP;

public abstract class ServerPlugin extends Plugin {

    /**
     * Called when a player disconnects from the server.
     *
     * @param client Details about the Player and other info.
     */
    protected abstract void onClientDisconnectEvent(FinalClientTCP client)
            throws Exception; //TODO

    /**
     * Called when a client joins the server.
     *
     * @param client Details about the {@link FinalClientTCP} and other info.
     */
    protected abstract void onClientJoinEvent(FinalClientTCP client)
            throws Exception; //TODO

}
