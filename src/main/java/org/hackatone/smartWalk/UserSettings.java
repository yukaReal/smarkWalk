package org.hackatone.smartWalk;

import org.apache.log4j.Logger;

public class UserSettings {
	private static Logger LOGGER = Logger.getLogger(UserSettings.class.getPackage().getName());

	public static String GOOGLE_API_KEY = "AIzaSyD4SG9ASn6sv9GifaizQseWJql35XTQddU";
	
	public enum WALKING_PRIORITY {
		SAFE, SHORT, INTERESTING, NEW
	}

	public enum INFO_TYPE {
		CRIMINAL, HISTORY, NEWS, WEATHER
	}
	
	private long id;
	private WALKING_PRIORITY walkingPriority;

	public UserSettings(long id, WALKING_PRIORITY walkingPriority) {
		this.id = id;
		this.walkingPriority = walkingPriority;
	}

	public UserSettings(long id) {
		this.id = id;
		this.walkingPriority = WALKING_PRIORITY.SAFE;
	}

	public long getId() {
		return id;
	}

	public WALKING_PRIORITY getWalkingPriority() {
		return walkingPriority;
	}

	public static WALKING_PRIORITY getWalkingPriority(String walkingPriorityInput) {
		switch (walkingPriorityInput) {
		case "safe":
			return WALKING_PRIORITY.SAFE;
		case "interesting":
			return WALKING_PRIORITY.INTERESTING;
		case "new":
			return WALKING_PRIORITY.NEW;
		case "short":
			return WALKING_PRIORITY.SHORT;
		}
		LOGGER.warn("Oops! Wrong 'walkingPriorityInput' value (" + walkingPriorityInput + ")! Selecting default value - 'safe'...");
		return WALKING_PRIORITY.SAFE;
	}
	
	public static INFO_TYPE getInfoType(String infoType) {
		switch (infoType) {
		case "criminal":
			return INFO_TYPE.CRIMINAL;
		case "history":
			return INFO_TYPE.HISTORY;
		case "news":
			return INFO_TYPE.NEWS;
		case "weather":
			return INFO_TYPE.WEATHER;
		}
		LOGGER.warn("Oops! Wrong 'infoType' value (" + infoType + ")! Selecting default value - 'news'...");
		return INFO_TYPE.NEWS;
	}
}
