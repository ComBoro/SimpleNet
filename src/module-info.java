module cmbrNetServer {
    requires java.desktop;
    requires transitive cmbrNetLibrary;

    exports net.comboro.server.plugin;
    exports net.comboro.server.command;
    exports net.comboro.server.networking;
    exports net.comboro.server;
}