package net.comboro.server.networking;

import net.comboro.Client.ClientListener;
import net.comboro.SerializableMessage;
import net.comboro.encryption.aes.AESInformation;
import net.comboro.encryption.rsa.RSAInformation;
import net.comboro.internet.tcp.ClientTCP;
import net.comboro.server.command.CommandSender;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FinalClientTCP implements CommandSender {

    private static List<FinalClientTCP> list = new ArrayList<>();
    public final long TIME_CON_MILLS;
    public List<String> permissions = new ArrayList<>(Arrays.asList("help", "this.name.view", "this.port.view", "this.properties.view"));
    private ClientTCP client;

    private FinalClientTCP(ClientTCP client) {
        this.client = client;
        this.TIME_CON_MILLS = System.currentTimeMillis();

        list.add(this);
    }

    public static FinalClientTCP get(ClientTCP clientTCP) {
        for (FinalClientTCP finalClientTCP : list) {
            if (finalClientTCP.client.equals(clientTCP)) {
                return finalClientTCP;
            }
        }
        return new FinalClientTCP(clientTCP);
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
        return TIME_CON_MILLS == other.TIME_CON_MILLS;
    }

    @Override
    public String getName() {
        String name = client.getDisplayName();
        name = name.replace("127.0.0.1", "localhost");
        return name;
    }

    @Override
    public void sendMessage(String message) {
        this.send(message);
    }

    @Override
    public Collection<String> getPermissions() {
        return permissions;
    }

    public RSAInformation getRSAInfomarion() {
        return client.rsaInformation;
    }

    public AESInformation getAESInfomarion() {
        return client.aesInformation;
    }

}
