package org.hackatone.smartWalk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hackatone.criminal.CriminalData;
import org.hackatone.criminal.CriminalRecord;
import org.hackatone.smartWalk.UserSettings.INFO_TYPE;
import org.hackatone.smartWalk.UserSettings.WALKING_PRIORITY;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TravelMode;

@RestController
public class GetRoute {
	private static Logger LOGGER = Logger.getLogger(GetRoute.class.getPackage().getName());

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/get_route", method = RequestMethod.GET)
	public String getInfoAboutPoint(
			@RequestParam(value = "userID") String userID,
			@RequestParam(value = "deviceId") String deviceId,
			@RequestParam(value = "walkingPriority") String walkingPriorityInput,
			@RequestParam(value = "infoType") String infoTypeInput,
			@RequestParam(value = "startLat") String startLatInput,
			@RequestParam(value = "startlong") String startlongInput,
			@RequestParam(value = "stopLat") String stopLatInput,
			@RequestParam(value = "stoplong") String stoplongInput
			) {
		
		boolean isStatusOK = true;
		
		LOGGER.info("User:" + userID + "; walkingPriorityInput:" + walkingPriorityInput);

		WALKING_PRIORITY walkingPriority = UserSettings.getWalkingPriority(walkingPriorityInput);
		INFO_TYPE infoType = UserSettings.getInfoType(infoTypeInput);

		double startLat = Double.valueOf(startLatInput);
		double startlong = Double.valueOf(startlongInput);
		double stopLat = Double.valueOf(stopLatInput);
		double stoplong = Double.valueOf(stoplongInput);

		JSONObject response = new JSONObject();
		response.put("userID", userID);
		response.put("deviceId", deviceId);
		response.put("walkingPriority", walkingPriority.toString());
		response.put("infoType", infoType.toString());
		response.put("startLat", String.valueOf(startLat));
		response.put("startlong", String.valueOf(startlong));
		response.put("stopLat", String.valueOf(stopLat));
		response.put("stoplong", String.valueOf(stoplong));

		DirectionsRoute[] routs = getPaths(startLat, startlong, stopLat, stoplong);
		
		int selectedRoute = analysisRouts(routs, response, infoType);
		
		addSelectedRouteToResults(selectedRoute, response, routs);
		
		response.put("isStatusOK", isStatusOK);
		
		LOGGER.info("");
		LOGGER.info(response.toJSONString());
		LOGGER.info("");
		
		return response.toJSONString();
	}

	private DirectionsRoute[] getPaths(double startLat, double startlong, double stopLat, double stoplong) {
		GeoApiContext context = new GeoApiContext().setApiKey(UserSettings.GOOGLE_API_KEY);
		try {
			DirectionsRoute[] routes = DirectionsApi.newRequest(context)
			        .mode(TravelMode.WALKING)
//			        .avoid(RouteRestriction.HIGHWAYS, RouteRestriction.TOLLS, RouteRestriction.FERRIES)
//			        .units(Unit.METRIC)
			        .alternatives(true)
			        .region("us")
			        .origin(String.valueOf(startLat) + "," + String.valueOf(startlong))
			        .destination(String.valueOf(stopLat) + "," + String.valueOf(stoplong)).await();
			return routes;
		} catch (Exception e) {
			LOGGER.error("Oops! " + e);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private int analysisRouts(DirectionsRoute[] routs, JSONObject response, INFO_TYPE infoType) {
		Map<Integer, Integer> routesWeight = new HashMap<>();
		Map<Integer, Set<CriminalRecord>> routesCriminalRecords = new HashMap<>();
		
		int routeNumber = 0;
		for (DirectionsRoute route : routs) {
			Set<Map<String, Object>> points = getAllPoints(route);
			
			switch (infoType) {
			case CRIMINAL:
				LOGGER.info("Let's get criminal analysis for route # " + routeNumber + "...");
				
				CriminalData criminalData = new CriminalData();
				
				LOGGER.info("We have '" + points.size() + " ' points on our route...");
				
				int routeWeight = 0;
				Set<CriminalRecord> criminalRecordsRoute = new HashSet<>();
				for (Map<String, Object> point : points) {
					String latLng = (String) point.get("endLocation").toString();
					
					String[] latLngSplitted = latLng.split(",");
					double lat = Double.valueOf(latLngSplitted[0]);
					double lng = Double.valueOf(latLngSplitted[1]);
					
					Set<CriminalRecord> criminalRecords = criminalData.getNearEvents(lat, lng, 50);
					criminalRecordsRoute.addAll(criminalRecords);
					
					routeWeight = routeWeight + criminalRecords.size();  					
				}
				
				routesCriminalRecords.put(routeNumber, criminalRecordsRoute);
				routesWeight.put(routeNumber, criminalRecordsRoute.size());
				LOGGER.info("This route #" + routeNumber + " has " + criminalRecordsRoute.size() + " criminal records:");
				
				break;
				
			case HISTORY:
//				response.put("result", getHistory(latitude, lontitude));
				break;
				
			case NEWS:
//				response.put("result", getNews(latitude, lontitude));
				break;
				
			case WEATHER:
//				response.put("result", getWeather(latitude, lontitude));
				break;
				
			default:
				break;
			}
			
			routeNumber++;
		}
		
		
		int bestRoute = selectBestRoute(routesCriminalRecords, response);
		
		return bestRoute;		
	}

	@SuppressWarnings("unchecked")
	private int selectBestRoute(Map<Integer, Set<CriminalRecord>> routesCriminalRecords, JSONObject response) {
		int safestRoute = 0;
		int safestRoute2 = 0;
		int worstRoute = 0;
		for (int routeNumber : routesCriminalRecords.keySet()) {
			Set<CriminalRecord> criminalRecords = routesCriminalRecords.get(routeNumber);
			if (criminalRecords.size() < routesCriminalRecords.get(safestRoute).size()) {
				safestRoute2 = safestRoute; 
				safestRoute = routeNumber;
			}
			if (criminalRecords.size() > routesCriminalRecords.get(worstRoute).size()) {
				worstRoute = routeNumber;
			}
		}
		
		LOGGER.info("Route # " + safestRoute + " is selected!");
		JSONArray selectionReason = new JSONArray();
		
		JSONObject info1 = new JSONObject();
		info1.put("number_of_routes", routesCriminalRecords.size());
		selectionReason.add(info1);
		
		if (routesCriminalRecords != null && routesCriminalRecords.size() > 0) {
			JSONObject info2 = new JSONObject();
			info2.put("criminal_records_min", routesCriminalRecords.get(safestRoute).size());
//			info2.put("name", "criminal_records_min");
//			info2.put("value", routesCriminalRecords.get(safestRoute).size());
			selectionReason.add(info2);
			
			JSONObject info3 = new JSONObject();
			info3.put("criminal_records_min_second_best", routesCriminalRecords.get(safestRoute2).size());
//			info3.put("name", "criminal_records_min_second_best");
//			info3.put("value", routesCriminalRecords.get(safestRoute2).size());
			selectionReason.add(info3);
			
			JSONObject info4 = new JSONObject();
			info4.put("criminal_records_max", routesCriminalRecords.get(worstRoute).size());
//			info4.put("name", "criminal_records_max");
//			info4.put("value", routesCriminalRecords.get(worstRoute).size());
			selectionReason.add(info4);
			
			// add last criminal event info
			response.put("last_criminal_event", getLastCriminalEventInfo(selectionReason, safestRoute, routesCriminalRecords));
		}
		
		response.put("selectedReason", selectionReason);
		return safestRoute;
	}

	@SuppressWarnings("unchecked")
	private JSONArray getLastCriminalEventInfo(JSONArray selectionReason, int safestRoute, Map<Integer, Set<CriminalRecord>> routesCriminalRecords) {
		Set<CriminalRecord> criminalRecords = routesCriminalRecords.get(safestRoute);
		
		String address = null;
		String time = null;
		String category = null;
		
		Calendar cal = Calendar.getInstance();
		Calendar cal_max = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		
		if (criminalRecords != null && criminalRecords.size() > 0) {
			try {
				CriminalRecord crFirst = criminalRecords.iterator().next();
				address = crFirst.getAddress();
				time = crFirst.getTime_24HR();
				category = crFirst.getUCR_1_Category();
				cal_max.setTime(sdf.parse(crFirst.getDate()));
				
			} catch (ParseException e) {
				LOGGER.error("Oops! " + e);
			}
		}
		
		for (CriminalRecord cr: criminalRecords) {
			String date = cr.getDate();
			
			try {
				cal.setTime(sdf.parse(date));

				if (cal.getTime().getTime() > cal_max.getTime().getTime()) {
					cal_max.setTime(cal.getTime());
					address = cr.getAddress();
					time = cr.getTime_24HR();
					category = cr.getUCR_1_Category();
				}
			} catch (ParseException e) {
				LOGGER.error("Oops! Can't transform '" + date + "' to calendar Date!");
			}
		}
		
		JSONArray lastCriminalEvent = new JSONArray();		
		
		JSONObject lastCriminalEvent1 = new JSONObject();
		
		if (address != null && address.length() > 0) {
			lastCriminalEvent1.put("date", sdf.format(cal_max.getTime()));
			lastCriminalEvent1.put("address", address);
			lastCriminalEvent1.put("time", time);
			lastCriminalEvent1.put("category", category);	
		}
		
		lastCriminalEvent.add(lastCriminalEvent1);
		
		return lastCriminalEvent;
	}

	@SuppressWarnings("unchecked")
	private void addSelectedRouteToResults(int bestRoute, JSONObject response, DirectionsRoute[] routs) {
		if (routs != null && routs.length > 0 && routs.length >= bestRoute) {
//			LOGGER.info("bestRoute = " + bestRoute);
//			LOGGER.info("routs.length = " + routs.length);
			DirectionsRoute route = routs[bestRoute];
			Set<Map<String, Object>> points = getAllPoints(route);
			JSONArray steps = new JSONArray();
			
			int pointNumber = 0;
			for (Map<String, Object> point : points) {
				JSONObject step = new JSONObject();
				step.put("pointNumber", pointNumber);
				step.put("latLng", String.valueOf(point.get("endLocation")));
				step.put("duration", String.valueOf(point.get("duration")));
				step.put("distance", String.valueOf(point.get("distance")));
				step.put("htmlInstructions", String.valueOf(point.get("htmlInstructions")));
				
				steps.add(step);

				pointNumber++;
			}
			response.put("route", steps);	
		}
	}

	private Set<Map<String, Object>> getAllPoints(DirectionsRoute route) {
		Set<Map<String, Object>> results = new LinkedHashSet<Map<String, Object>>();
		
		for (DirectionsLeg leg : route.legs) {
			for (DirectionsStep step : leg.steps) {
				Map<String, Object> result = new HashMap<>();
				result.put("endLocation", step.endLocation);
				result.put("duration", step.duration);
				result.put("distance", step.distance);
				result.put("htmlInstructions", step.htmlInstructions);
				results.add(result);
			}
		}
		return results;
	}

	private String getWeather(double latitude, double lontitude) {

		return "weather";
	}

	private String getNews(double latitude, double lontitude) {

		return "news";
	}

	private String getHistory(double latitude, double lontitude) {

		return "history";
	}

	private String getCriminal(double latitude, double lontitude) {

		return "criminal";
	}
}