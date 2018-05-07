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

import net.comboro.Client;
import net.comboro.encryption.rsa.RSAInformation;
import net.comboro.server.Application;
import net.comboro.server.Server;
import net.comboro.SerializableMessage;
import net.comboro.TaggedMessage;
import net.comboro.Server.ServerListener.ServerAdapter;
import net.comboro.internet.tcp.ClientTCP;
import net.comboro.internet.tcp.ServerTCP;
import net.comboro.server.command.CommandMap;
import net.comboro.server.command.defaults.BanCommand;

import java.awt.*;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import static net.comboro.server.Server.*;

public class TCPServerImpl extends ServerTCP {

    public TCPServerImpl(int port) {
        super(port);
        setSecure(true);
        ServerAdapter<ClientTCP> SERVER_ADAPTER = new ServerAdapter<>() {
            @Override
            public void onClientConnect(ClientTCP client) {
                boolean isBanned = Server.getBanList().contains(client.getDisplayName().split(":")[0]);
                if(isBanned){
                    client.send("The ban hammer has spoken.");
                    removeClient(client);
                    return;
                }

                Application.getPluginMap().onClientConnect(client);

                append("Client connected",Color.GREEN);
            }

            @Override
            public void onClientInput(ClientTCP client,
                                      SerializableMessage<?> message) {
                if (message.getData() instanceof String)
                    Server.debug((String) message.getData());
                
                if(message instanceof TaggedMessage<?>) {
                	TaggedMessage<?> firstUnbox = (TaggedMessage<?>) message;
                	String[] tags = firstUnbox.getTags();
                	for(int i = 0; i < tags.length; i++) {
                		String tag = tags[i];
                		if(tag.equalsIgnoreCase("Command") || tag.equalsIgnoreCase("cmd")) {
                            FinalClientTCP finalClientTCP = FinalClientTCP.get(client);
                			String cmd = (String) message.getData();
                			CommandMap.dispatch(finalClientTCP, cmd);
                			return;
                		}
                	}
                	
                }
                
                Application.getPluginMap().onClientInput(client, message);

                Application.updateClientsPane(null, false);
            }

            @Override
            public void onServerStart() {
                setDefaultColour(Color.RED);

                append("Starting server '" + Server.getName() + "'");
                append("Port: " + port);
                append("Secure? " + TCPServerImpl.this.isSecure());
                RSAInformation information = TCPServerImpl.this.rsa.getInformation();
                append("Modulus: " + information.getModulus());
                append("Public key: " + information.getPublicKey());
                try {
                    append("Internal IP: " + InetAddress.getLocalHost().getHostAddress());
                } catch (UnknownHostException e) {}
                resetDefaultColour();

                synchronized (Application.startLock){
                    Application.startLock.notify();
                }
            }

            @Override
            public void onServerStartError(Exception e) {
                error("Error starting server");
                if(e instanceof BindException){
                    append("Port taken, please input new one!",Color.RED, true, true);
                    String cmd = CommandMap.nextCommand();
                    try{
                        int port = Integer.parseInt(cmd);
                        setPort(port < 0 || port > 65_535 ? 0 : port);
                    } catch (NumberFormatException nfe){
                        setPort(0);
                    }
                    Application.clearConsole();
                    startServer();
                } else {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClientDisconnect(ClientTCP client) {
                Application.getPluginMap().onClientDisconnect(client);
                append("Client disconnected", Color.RED);
            }

        };
        addLister(SERVER_ADAPTER);
    }

    @Override
    protected void start() throws IOException {
        append( getName() + " successfully started on port " + getPort(),Color.DARK_GRAY,true,true);
        super.start();
    }
}
