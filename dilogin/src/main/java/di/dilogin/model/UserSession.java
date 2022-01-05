/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 * 
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */

package di.dilogin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Contains the user's session information.
 */
@Getter @Setter @AllArgsConstructor
public class UserSession {
	
	/**
	 * Player's name
	 */
	private String name;
	
	/**
	 * Player's ip.
	 */
	private String ip;
}
