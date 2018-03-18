package net.comboro.server.networking;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.comboro.Client.ClientListener;
import net.comboro.SerializableMessage;
import net.comboro.internet.tcp.ClientTCP;
import net.comboro.server.command.CommandSender;

public class FinalClientTCP implements CommandSender {

	public List<String> permissions = Arrays.asList("help", "this.name.view", "this.port.view", "this.properties.view");

	private ClientTCP client;

	public FinalClientTCP(ClientTCP client) {
		this.client = client;
	}

	public <M extends Serializable> void send(M message) {
		client.send(new SerializableMessage<>(message));
	}

	public void send(SerializableMessage<?> message) {
		client.send(message);
	}

	public void addListener(ClientListener listener) {
		client.addListener(listener);
	}

	@Override
	public boolean equals(Object obj) {
		if ((null == obj) || (obj.getClass() != FinalClientTCP.class))
			return false;
		FinalClientTCP other = (FinalClientTCP) obj;
		return client.equals(other.client);
	}

	@Override
	public String getName() {
		return client.getThreadName();
	}

	@Override
	public void sendMessage(String message) {
		this.<String>send(message);
	}

	@Override
	public Collection<String> getPermissions() {
		return permissions;
	}

}
