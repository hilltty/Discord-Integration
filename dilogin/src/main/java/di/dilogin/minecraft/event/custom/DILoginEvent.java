/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 * 
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */

package di.dilogin.minecraft.event.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This event is generated when logging into DILogin
 */
@Getter @AllArgsConstructor
public final class DILoginEvent extends Event {
	
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	
	private Player player;

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}
}