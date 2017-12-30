package net.comboro.plugin;

import net.comboro.internet.tcp.FinalClientTCP;

public abstract class ServerPlugin extends Plugin {

	/**
	 * Called when a player disconnects from the server.
	 * 
	 * @param client
	 *            Details about the Player and other info.
	 */
	protected abstract void onClientDisconnectEvent(FinalClientTCP client)
			throws Exception; //TODO

	/**
	 * Called when a client joins the server.
	 * 
	 * @param client
	 *            Details about the {@link Client} and other info.
	 */
	protected abstract void onClientJoinEvent(FinalClientTCP client)
			throws Exception; //TODO

}
