/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 * 
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */

package di.internal.controller;

import org.bukkit.plugin.Plugin;

import di.internal.controller.file.ConfigManager;

/**
 * Interace of internal controllers.
 */
public interface PluginController {
	
	/**
	 * @return Plugin configuration controller.
	 */
	ConfigManager getConfigManager();
	
	/**
	 * @return Bukkit Plugin.
	 */
	Plugin getPlugin();

}
