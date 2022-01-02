/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 * 
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */

package di.internal.exception;

import org.bukkit.plugin.Plugin;

/**
 * Exception generated when not finding the DIApi.
 */
public class NoApiException extends Exception{

	private static final long serialVersionUID = -8403248971778801419L;
	
	public NoApiException(Plugin plugin) {
		plugin.getLogger().warning("The bot API could not be found. Report this bug to the developers.");
	}

}
