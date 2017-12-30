package net.comboro.networking;

import net.comboro.Server.ServerListener.ServerAdapter;
import net.comboro.internet.tcp.ClientTCP;
import net.comboro.internet.tcp.ServerTCP;

import static net.comboro.SServer.*;

import java.awt.Color;

import net.comboro.SerializableMessage;

public class TCPServerImpl extends ServerTCP{
	
	final ServerAdapter<ClientTCP> SERVER_ADAPTER = new ServerAdapter<ClientTCP>() {
		@Override
		public void onClientConnect(ClientTCP client) {
			append("New client connected. ");
		}
		@Override
		public void onClientInput(ClientTCP client,
				SerializableMessage message) {
			if(message.getData() instanceof String) {
				append("Client input: " + (String) message.getData());
			} else {
				append("Client non-string input.");
			}
		}
		@Override
		public void onServerStart() {
			setDefaultColour(Color.MAGENTA);
						
			append("Server successfully started on port: " + port);
			append("Secure? " + TCPServerImpl.this.isSecure());
		};
		
		@Override
		public void onServerStartError(Exception e) {
			error("Error starting server");
			e.printStackTrace(System.err);
		}
		@Override
		public void onClientDisconnect(ClientTCP client) {
			append("Client disconnected");
		}
	};
	
	public TCPServerImpl(int port) {
		super(port);
		setSecure(true);
		addLister(SERVER_ADAPTER);
	}

}
