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

package net.comboro.networking;

import net.comboro.SServer;
import net.comboro.SerializableMessage;
import net.comboro.Server.ServerListener.ServerAdapter;
import net.comboro.internet.tcp.ClientTCP;
import net.comboro.internet.tcp.ServerTCP;

import java.awt.*;

import static net.comboro.SServer.*;

public class TCPServerImpl extends ServerTCP {

    private final ServerAdapter<ClientTCP> SERVER_ADAPTER = new ServerAdapter<ClientTCP>() {
        @Override
        public void onClientConnect(ClientTCP client) {
            SServer.getPluginMap().onClientConnect(client);
        }

        @Override
        public void onClientInput(ClientTCP client,
                                  SerializableMessage message) {
            if (message.getData() instanceof String)
                SServer.debug((String) message.getData());
            SServer.getPluginMap().onClientInput(client, message);
        }

        @Override
        public void onServerStart() {
            setDefaultColour(Color.MAGENTA);

            append("Server successfully started on port: " + port);
            append("Secure? " + TCPServerImpl.this.isSecure());
        }

        @Override
        public void onServerStartError(Exception e) {
            error("Error starting server");
            e.printStackTrace(System.err);
        }

        @Override
        public void onClientDisconnect(ClientTCP client) {
            SServer.getPluginMap().onClientDisconnect(client);
        }
    };

    public TCPServerImpl(int port) {
        super(port);
        setSecure(true);
        addLister(SERVER_ADAPTER);
    }

}
