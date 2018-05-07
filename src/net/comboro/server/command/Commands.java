package net.comboro.server.command;

import net.comboro.server.Server;
import net.comboro.server.networking.FinalClientTCP;

import java.awt.*;
import java.util.*;

public class Commands {

    public static final String NO_PERMISSON = "Deficient permissions";

    public static final CommandSender UI = new CommandSender() {
        @Override
        public String getName() {
            return "SimpleNet UI";
        }

        @Override
        public void sendMessage(String message) {
            Server.append(message, Color.darkGray);
        }

        @Override
        public Collection<String> getPermissions() {
            return Collections.singletonList("*");
        }
    }, CONSOLE = new CommandSender() {
        @Override
        public String getName() {
            return "Console";
        }

        @Override
        public void sendMessage(String message) {
            Server.append(message, Color.darkGray);
        }

        @Override
        public Collection<String> getPermissions() {
            return Collections.singletonList("*");
        }
    };

    private static Map<String, Set<String>> ipPermList = new HashMap<>(); //<IP address, Permissions>

    public static void linkPermissionToIP(String permission, String ip) {
        if (!ipPermList.containsKey(ip))
            ipPermList.put(ip, new HashSet<>());

        ipPermList.get(ip).add(permission);
    }

    public static void unlinkPermissionToIP(String permission, String ip) {
        if (ipPermList.containsKey(ip))
            ipPermList.get(ip).remove(permission);
    }

    public static void attachPermissions(FinalClientTCP finalClientTCP) {
        String ip = finalClientTCP.getName().replace("localhost", "127.0.0.1").split(":")[0];
        if (ipPermList.containsKey(ip)) {
            Set<String> permissions = ipPermList.get(ip);
            finalClientTCP.permissions.addAll(permissions);
        }
    }

    public static Map<String, Set<String>> getIpPermList() {
        return new HashMap<>(ipPermList);
    }

    public static boolean hasPermission(CommandSender sender, String permission) {
        for (String perm : sender.getPermissions()) {
            if (perm.equals("*") || perm.equals(permission))
                return true;
            if (perm.contains(".") && permission.contains(".")) {
                String[] split1 = perm.split("."), split2 = permission.split(".");
                if (split1.length < split2.length) {
                    for (int i = 0; i < split1.length; i++) {
                        if (split1[i].equals("*"))
                            return true;
                        if (!split1[i].equals(split2[i]))
                            return false;
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasPermission(CommandSender sender, String... permissions) {
        for (String perm : sender.getPermissions()) {
            if (perm.equals("*"))
                return true;

            for (String permission : permissions) {
                if (perm.equals(permission)) return true;

                if (perm.contains(".") && permission.contains(".")) {
                    String[] split1 = perm.split("."), split2 = permission.split(".");
                    if (split1.length < split2.length) {
                        for (int i = 0; i < split1.length; i++) {
                            if (split1[i].equals("*"))
                                return true;
                            if (!split1[i].equals(split2[i]))
                                return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void requirePermission(CommandSender sender, String permission)
            throws InsufficientPermissionsException {
        if (!hasPermission(sender, permission))
            throw new InsufficientPermissionsException(sender, permission);
    }

    public static void requirePermission(CommandSender sender, String... permissions)
            throws InsufficientPermissionsException {
        if (!hasPermission(sender, permissions))
            throw new InsufficientPermissionsException(sender, Arrays.toString(permissions));
    }

    public static boolean hasAnyPermission(CommandSender sender, String command) {
        for (String perm : sender.getPermissions()) {
            if (perm.startsWith(command) || perm.equals("*"))
                return true;
        }
        return false;
    }

}
