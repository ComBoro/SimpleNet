package net.comboro.server.command.defaults;

import net.comboro.internet.tcp.ClientTCP;
import net.comboro.server.Application;
import net.comboro.server.command.CommandSender;
import net.comboro.server.command.Commands;
import net.comboro.server.networking.FinalClientTCP;

public class PermissionCommand extends DefaultCommand {

    public PermissionCommand() {
        super("PermissionCommand", "Grants or denies permissions",
                "permission grant/deny <target> <permission>",
                "permission grant 127.0.0.1 this.maxCharsPerLine.view");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 3) return false;

        String action = args[0], target = args[1].replace("localhost", "127.0.0.1"), permission = args[2];

        FinalClientTCP clientTarget = null;

        if (target.contains(":")) {
            for (ClientTCP clientTCP : Application.getTCPImpl().getClientList()) {
                if (clientTCP.getDisplayName().equals(target))
                    clientTarget = FinalClientTCP.get(clientTCP);
            }
            if (clientTarget == null) {
                sender.sendMessage("Invalid target");
                return false;
            }
        }

        if (action.equalsIgnoreCase("grant")) {
            Commands.requirePermission(sender, "permission.grant");

            if (clientTarget == null) {
                Commands.linkPermissionToIP(permission, target);
            } else {
                clientTarget.permissions.add(permission);
            }

        } else if (action.equalsIgnoreCase("deny")) {
            Commands.requirePermission(sender, "permission.deny");

            if (clientTarget == null) {
                Commands.unlinkPermissionToIP(permission, target);
            } else {
                clientTarget.permissions.remove(permission);
            }
        } else return false;

        return true;
    }
}
