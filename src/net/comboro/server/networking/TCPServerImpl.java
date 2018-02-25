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

package net.comboro.server.networking;

import net.comboro.server.Application;
import net.comboro.server.Server;
import net.comboro.SerializableMessage;
import net.comboro.Server.ServerListener.ServerAdapter;
import net.comboro.internet.tcp.ClientTCP;
import net.comboro.internet.tcp.ServerTCP;

import java.awt.*;
import java.lang.reflect.Field;
import java.net.Socket;

import static net.comboro.server.Server.*;

public class TCPServerImpl extends ServerTCP {

    public TCPServerImpl(int port) {
        super(port);
        setSecure(true);
        ServerAdapter<ClientTCP> SERVER_ADAPTER = new ServerAdapter<>() {
            @Override
            public void onClientConnect(ClientTCP client) {
                //TODO: ban list
                Application.getPluginMap().onClientConnect(client);
                append("Client connected",Color.GREEN);
            }

            @Override
            public void onClientInput(ClientTCP client,
                                      SerializableMessage message) {
                if (message.getData() instanceof String)
                    Server.debug((String) message.getData());
                Application.getPluginMap().onClientInput(client, message);
            }

            @Override
            public void onServerStart() {
                setDefaultColour(Color.RED);

                append("Server successfully started on port: " + port);
                append("Secure? " + TCPServerImpl.this.isSecure());
                resetDefaultColour();
            }

            @Override
            public void onServerStartError(Exception e) {
                error("Error starting server");
                e.printStackTrace(System.err);
            }

            @Override
            public void onClientDisconnect(ClientTCP client) {
                Application.getPluginMap().onClientDisconnect(client);
                append("Client disconnected", Color.RED);
            }
        };
        addLister(SERVER_ADAPTER);
    }

}
