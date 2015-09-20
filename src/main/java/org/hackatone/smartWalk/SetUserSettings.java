package org.hackatone.smartWalk;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.apache.log4j.Logger;

@RestController
public class SetUserSettings {
	private static Logger LOGGER = Logger.getLogger(SetUserSettings.class.getPackage().getName());
	 
	@RequestMapping(value = "/set_user_setings", method = RequestMethod.POST)
	public String greeting(@RequestParam(value = "userID") String userID) {
		//UserSettings userSettings = new UserSettings(userID, walkingPriority);
		
		try {
//			UserSettings userSettings = new UserSettings(Long.valueOf(userID));
			return "ok";
		} catch (Exception e) {
			LOGGER.error("Oops! " + e);
			return "Oops!";
		}
	}
}
