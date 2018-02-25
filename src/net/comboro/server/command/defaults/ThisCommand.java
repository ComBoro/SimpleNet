package net.comboro.server.command.defaults;

import net.comboro.server.Application;
import net.comboro.server.Server;
import net.comboro.server.command.CommandSender;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ThisCommand extends DefaultCommand {

    public ThisCommand() {
        super("ThisCommand", "Adjusts or checks server information",
                "this <properties/clear/name/version/mem/rst/sort>", "this clear");
    }

    public static void clear() {
        Application.clearConsole();
        Runtime.getRuntime().gc();
    }

    private static float usedMem() {
        float usedMemBytes = Runtime.getRuntime().totalMemory() - Runtime
                .getRuntime().freeMemory();
        float usedMemMB = usedMemBytes / (1024 * 1024);
        return BigDecimal.valueOf(usedMemMB)
                .setScale(4, RoundingMode.HALF_UP).floatValue();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender == CommandSender.CONSOLE))
            return false;

        final int argsLengh = args.length;

        if (argsLengh == 0) {
            sender.sendMessage("Syntax: " + super.getSyntax());
            return false;
        }

        switch (args[0]) {
            case "properties":
                switch (argsLengh) {
                    case 1:
                        sender.sendMessage("Properties: "
                                + Application.getProperties().toString());
                        break;
                    case 2:
                        String prop = args[1];
                        if (Server.containsKey(prop)) {
                            sender.sendMessage("Value of property \'" + prop + "\': "
                                    + Server.getProperty(prop));
                        } else {
                            sender.sendMessage("No such property \'" + prop + "\'.");
                        }
                        break;
                    case 4:
                        if (args[1].equals("set")) {
                            String key = args[2];
                            String value = args[3];
                            Server.setProperty(key, value);
                            sender.sendMessage("Property \'" + key + "\' set to: "
                                    + value);
                        }
                        break;
                }
                break;
            case "clear":
                clear();
                break;
            case "name":
                sender.sendMessage("Server name: " + Server.getName());
                break;
            case "mem":
                sender.sendMessage("Used memory: " + usedMem() + " MB in "
                        + Thread.activeCount() + " threads.");
                break;
            case "rst":
                Application.getPluginLoader().reloadAll();
                clear();
                break;

            default:
                sender.sendMessage("Syntax: " + getSyntax());
                break;
        }

        return false;
    }

}
