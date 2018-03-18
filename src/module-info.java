module SimpleNet {
	exports net.comboro.server.command;
	exports net.comboro.server.networking;
	exports net.comboro.server.plugin;
	exports net.comboro.server.files;

	requires transitive cmbrNetLibrary;
	requires transitive java.desktop;
}