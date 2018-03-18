package net.comboro.server.command;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import net.comboro.server.Server;

public class Commands {

	public static String NO_PERMISSON = "Insufficient permissions";

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
			
			for(String permission : permissions) {
				if(perm.equals(permission)) return true;
				
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
