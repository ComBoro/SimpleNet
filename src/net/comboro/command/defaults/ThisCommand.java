package net.comboro.command.defaults;

import net.comboro.SServer;
import net.comboro.ServerInfo;
import net.comboro.command.CommandSender;
import net.comboro.command.ConsoleCommandSender;

import java.math.BigDecimal;

public class ThisCommand extends DefaultCommand {

	public static void clear() {
		SServer.getServerUI().clearCommandLine();
		Runtime.getRuntime().gc();
	}

	public static float usedMem() {
		float usedMemBytes = Runtime.getRuntime().totalMemory() - Runtime
				.getRuntime().freeMemory();
		float usedMemMB = usedMemBytes / (1024 * 1024);
		float usedMemMBrounded = BigDecimal.valueOf(usedMemMB)
				.setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
		return usedMemMBrounded;

	}

	public ThisCommand() {
		super("ThisCommand", "Manipulated server information",
				"this <properties/clear/name/version/mem/rst/sort>");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ConsoleCommandSender))
			return false;

		final int argsLengh = args.length;

		if (argsLengh == 0) {
			sender.sendMessage("Usage: " + super.getUsageMessage());
			return false;
		}

		switch (args[0]) {
		case "properties":
			switch (argsLengh) {
			case 1:
				sender.sendMessage("Properties: "
						+ SServer.getProperties().toString());
				break;
			case 2:
				String prop = args[1];
				if (SServer.containsKey(prop)) {
					sender.sendMessage("Value of property \'" + prop + "\': "
							+ SServer.getProperty(prop));
				} else {
					sender.sendMessage("No such property \'" + prop + "\'.");
				}
				break;
			case 4:
				if (args[1].equals("set")) {
					String key = args[2];
					String value = args[3];
					SServer.setProperty(key, value);
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
			sender.sendMessage("Server name: " + SServer.getName());
			break;
		case "version":
			sender.sendMessage("Server version: " + ServerInfo.VERSION);
			break;
		case "mem":
			sender.sendMessage("Used memory: " + usedMem() + " MB in "
					+ Thread.activeCount() + " threads.");
			break;
		case "rst":
			SServer.getPluginLoader().reloadAll();
			clear();
			break;

		default:
			sender.sendMessage("Usage: " + getUsageMessage());
			break;
		}

		return false;
	}

}
