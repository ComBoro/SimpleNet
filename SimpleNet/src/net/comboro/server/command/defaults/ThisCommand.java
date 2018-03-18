package net.comboro.server.command.defaults;

import net.comboro.server.Application;
import net.comboro.server.Server;
import net.comboro.server.command.CommandSender;
import net.comboro.server.command.Commands;
import net.comboro.server.files.ExternalFile;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static net.comboro.server.command.Commands.*;

public class ThisCommand extends DefaultCommand {

	public ThisCommand() {
		super("ThisCommand", "Adjusts or checks server information",
				"this <properties/clear/name/version/maxCharsPerLine/mem/rst/sort>", "this clear");
	}

	public static void clear() {
		Application.clearConsole();
		Runtime.getRuntime().gc();
	}

	private static float usedMem() {
		float usedMemBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		float usedMemMB = usedMemBytes / (1024 * 1024);
		return BigDecimal.valueOf(usedMemMB).setScale(4, RoundingMode.HALF_UP).floatValue();
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		final int argsLengh = args.length;

		if (argsLengh == 0) {
			sender.sendMessage("Syntax: " + super.getSyntax());
			return false;
		}

		switch (args[0]) {
		case "properties":
			if (Commands.hasAnyPermission(sender, "this.properties"))
				switch (argsLengh) {
				case 1:
					requirePermission(sender, "this.properties.view");
					sender.sendMessage("Properties: " + Application.getProperties().toString());
					break;
				case 2:
					String prop = args[1];
					requirePermission(sender, "this.properties.view");
					if (Server.containsKey(prop)) {
						sender.sendMessage("Value of property \'" + prop + "\': " + Server.getProperty(prop));
					} else {
						sender.sendMessage("No such property \'" + prop + "\'.");
					}
					break;
				case 4:
					requirePermission(sender, "this.properties.set");
					if (args[1].equals("set")) {
						String key = args[2];
						String value = args[3];
						Server.setProperty(key, value);
						sender.sendMessage("Property \'" + key + "\' set to: " + value);
					}
					break;
				}
			break;
		case "clear":
			Commands.requirePermission(sender, "this.clear");
			clear();
			break;
		case "name":
			if (argsLengh == 1) {
				Commands.requirePermission(sender, "this.name.view");
				sender.sendMessage("Server name: " + Server.getName());
			} else {
				Commands.requirePermission(sender, "this.name.set");
				Server.setName(args[1]);
			}
			break;
		case "port":
			if (argsLengh == 1) {
				Commands.requirePermission(sender, "this.port.view");
				sender.sendMessage("Server port: " + Server.getPort());
			} else {
				requirePermission(sender, "this.port.set");
				try {
					int port = Integer.parseInt(args[1]);
					if (port < 0 || port > 65_535)
						throw new NumberFormatException();
					Server.setPort(port);
				} catch (NumberFormatException nfe) {
					sender.sendMessage("Invalid port. Nothing was changed");
				}
			}
			break;
		case "maxCharsPerLine":
			if (argsLengh == 1) {
				requirePermission(sender, "this.maxCharsPerLine.view");
				sender.sendMessage("Max chars per line: " + ExternalFile.serverInfoFile.getMaxCharsPerLine());
			} else
				requirePermission(sender, "this.maxCharsPerLine.set");
				try {
					int maxCharsPerLine = Integer.parseInt(args[1]);
					ExternalFile.serverInfoFile.setMaxCharsPerLine(maxCharsPerLine);
				} catch (NumberFormatException nfe) {
					sender.sendMessage("Not a number. Nothing was changed");
				}
			break;
		case "mem":
			requirePermission(sender, "this.mem");
			sender.sendMessage("Used memory: " + usedMem() + " MB in " + Thread.activeCount() + " threads.");
			break;
		case "rst":
			requirePermission(sender, "this.rst");
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
