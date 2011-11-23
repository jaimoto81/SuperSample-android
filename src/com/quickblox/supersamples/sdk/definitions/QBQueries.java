package com.quickblox.supersamples.sdk.definitions;

public class QBQueries {

	// Constants
	//
	public static final String APPLICATION_ID = "38";
	public static final String OWNER_ID = "831";
	public static final String STATUS = "I am";
	
	public static final String USERS_SERVICE_HOST_NAME = "users.quickblox.com";
	public static final String GEOPOS_SERVICE_HOST_NAME = "geopos.quickblox.com";
	public static final String CHAT_SERVICE_HOST_NAME = "jabber.quickblox.com";
	
	// Queries
	//
	// get all users
	public static final String GET_ALL_USERS_QUERY = String.format("http://%s/owners/%s/users.xml", 
			USERS_SERVICE_HOST_NAME, OWNER_ID);
	
	// add user
	public static final String CREATE_USER_QUERY = String.format("http://%s/users", USERS_SERVICE_HOST_NAME);
	
	// remove user by id
	public static final String REMOVE_USER_QUERY_FORMAT = String.format("http://%s/users/", USERS_SERVICE_HOST_NAME) + "%s";
	
	// authenticate user
	public static final String LOGIN_USER_QUERY = String.format("http://%s/users/authenticate", USERS_SERVICE_HOST_NAME);
	
	// identify user
	public static final String INDENTIFY_USER_QUERY = String.format("http://%s/users/identify", USERS_SERVICE_HOST_NAME);
	
	// logout
	public static final String LOGOUT_USER_QUERY = String.format("http://%s/users/logout", USERS_SERVICE_HOST_NAME);
	
	
	
	// create a geouser
	public static final String CREATE_GEOUSER_QUERY = String.format("http://%s/users", GEOPOS_SERVICE_HOST_NAME);
	
	// remove a geouser by id
	public static final String REMOVE_GEOUSER_QUERY_FORMAT = String.format("http://%s/users/", GEOPOS_SERVICE_HOST_NAME) + "%s";
	
	// get the location's data all users
	public static final String GET_ALL_LOCATIONS_QUERY = String.format("http://%s/geodata/find.xml?app.id=%s&last_only=1", 
			GEOPOS_SERVICE_HOST_NAME, APPLICATION_ID);
		
	// send the location's data of the current user
	public static final String SEND_GPS_DATA_QUERY = String.format("http://%s/geodata", GEOPOS_SERVICE_HOST_NAME);
	

	// Types of queries. They must match queries above 
	public static enum QBQueryType{
		QBQueryTypeGetAllUsers,
		QBQueryTypeCreateUser,
		QBQueryTypeRemoveUser,
		QBQueryTypeLoginUser,
		QBQueryTypeIdentifyUser,
		QBQueryTypeLogoutUser,
		
		QBQueryTypeCreateGeoUser,
		QBQueryTypeRemoveGeoUser,
		QBQueryTypeGetAllLocationsGeoUser,
		QBQueryTypeSendGPSData
	}
}
