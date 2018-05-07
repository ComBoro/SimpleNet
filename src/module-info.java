module SimpleNet {
    requires transitive SimpleNetLibrary;
    requires java.desktop;

	exports net.comboro.server.command;
	exports net.comboro.server.networking;
	exports net.comboro.server.plugin;
	exports net.comboro.server.files;
}