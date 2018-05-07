package net.comboro.server.command;

public class InsufficientPermissionsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private CommandSender sender;
    private String permission;

    public InsufficientPermissionsException(CommandSender sender, String permission) {
        super(permission);
        this.sender = sender;
        this.permission = permission;
    }

    public CommandSender getCommandSender() {
        return sender;
    }

    public String getPermission() {
        return permission;
    }

}
