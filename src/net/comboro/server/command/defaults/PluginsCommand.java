/*
 * This file is part of Fusster.
 *	
 * Fusster Copyright (C) ComBoro
 *
 * Fusster is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Fusster is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fusster.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.comboro.server.command.defaults;

import net.comboro.server.Application;
import net.comboro.server.command.CommandSender;
import static net.comboro.server.command.Commands.requirePermission;
import net.comboro.server.plugin.Plugin;
import net.comboro.server.plugin.PluginDescription;
import net.comboro.server.plugin.PluginException;
import net.comboro.server.plugin.PluginLoader;

import java.io.File;
import java.util.Set;

public class PluginsCommand extends DefaultCommand {

	public PluginsCommand() {
		super("Plugin Utils", "Plugin Managing", "plugins load/reload/unload/info <plugin/fileName/all>",
				"plugins reload all");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		int plugins = Application.getPluginMap().getPlugins().size();

		if (args.length == 0) {
			requirePermission(sender, "plugins.view");
			if (plugins == 0) {
				sender.sendMessage("No plugins loaded.");
				return false;
			}

			StringBuilder string = new StringBuilder();
			for (Plugin plugin : Application.getPluginMap().getPlugins())
				string.append(", ").append(plugin.getDescription().getName());
			sender.sendMessage("Available plugins [" + plugins + "] : " + string.substring(2, string.length()));

			return true;
		}

		if (args.length > 2) {
			sender.sendMessage("Invalid Arguments");
			return false;
		}

		String action = args[0];
		String pluginName = "all";
		if (args.length > 1)
			pluginName = args[1];

		PluginLoader loader = Application.getPluginLoader();

		boolean forAll = pluginName.equalsIgnoreCase("all");

		switch (action) {
		case "load":
			requirePermission(sender, "plugins.load");
			if (forAll) {
				loader.loadAll();
				sender.sendMessage("All plugins loaded.");
			} else {
				try {
					Plugin plugin = loader
							.load(new File(loader.getDirectory().getAbsoluteFile() + "/" + pluginName + ".jar"));
					PluginDescription description = plugin.getDescription();
					sender.sendMessage("Plugin " + description.getName() + " was loaded. Plugin version: "
							+ description.getVersion() + ". Plugin author: " + description.getAuthor());
				} catch (PluginException e) {
					if (Application.isDebugging())
						e.printStackTrace();
					sender.sendMessage("Error loading plugin " + pluginName + ".");
				}
			}

			break;

		case "reload":
			requirePermission(sender, "plugins.reload");
			if (forAll) {
				loader.unloadAll();
				loader.loadAll();
				sender.sendMessage("All plugins reloaded.");
			} else {
				try {
					loader.reload(Application.getPluginMap().getPlugin(pluginName));
					sender.sendMessage("Reloading plugin " + pluginName);
				} catch (PluginException e) {
					sender.sendMessage("No such plugin \'" + pluginName + "\'.");
				}
			}
			break;

		case "unload":
			requirePermission(sender, "plugins.unload");
			if (forAll) {
				loader.unloadAll();
				sender.sendMessage("All plugins unloaded.");
			} else {
				try {
					loader.unload(Application.getPluginMap().getPlugin(pluginName));
					sender.sendMessage("Unloading plugin " + pluginName);
				} catch (PluginException e) {
					sender.sendMessage("No such plugin \'" + pluginName + "\'.");
				}
			}
			break;
		case "info":
			requirePermission(sender, "plugins.info");
			Set<Plugin> allPlugins = Application.getPluginMap().getPlugins();
			if (forAll) {
				for (int i = 0; i < allPlugins.size(); i++) {
					Plugin fp = (Plugin) Application.getPluginMap().getPlugins().toArray()[i];
					PluginDescription pd = fp.getDescription();
					sender.sendMessage(i + ") Name: " + pd.getName() + ", Version: " + pd.getVersion() + ", Author: "
							+ pd.getAuthor() + ", File:" + fp.getFile());
				}
			} else {
				try {
					Plugin fp = Application.getPluginMap().getPlugin(pluginName);
					PluginDescription pd = fp.getDescription();
					sender.sendMessage("Name: " + pd.getName() + ", Version: " + pd.getVersion() + ", Author: "
							+ pd.getAuthor() + ", File:" + fp.getFile());
				} catch (PluginException e) {
					sender.sendMessage("No such plugin \'" + pluginName + "\'.");
				}
			}
			break;

		}

		return true;
	}

}
