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

import net.comboro.server.networking.FinalClientTCP;

public abstract class ServerPlugin extends Plugin {

    /**
     * Called when a player disconnects from the server.
     *
     * @param client Details about the Player and other info.
     */
    protected abstract void onClientDisconnect(FinalClientTCP client)
            throws Exception;

    /**
     * Called when a client joins the server.
     *
     * @param client Details about the {@link FinalClientTCP} and other info.
     */
    protected abstract void onClientConnect(FinalClientTCP client)
            throws Exception;

}
