package com.quickblox.supersamples.sdk.definitions;

public class QBQueries {

	// Applications settings
	//
	public static final String APPLICATION_ID = "99";
	public static final String AUTH_KEY = "63ebrp5VZt7qTOv";
	public static final String AUTH_SECRET = "YavMAxm5T59-BRw";

	
	// Server
	//
	public static final String SERVER_ZONE = "api.quickblox.com";

	// AUTH service
	//
	public static final String GET_AUTH_TOKEN_QUERY = String.format("https://%s/session", SERVER_ZONE);
	
	
	
	// get user by external user id
	public static final String GET_USER_BY_EXTERNAL_ID_QUERY_FORMAT = String.format("http://%s/users/external/", SERVER_ZONE) + "%s.xml";
	
	// add user
	public static final String CREATE_USER_QUERY = String.format("http://%s/users", SERVER_ZONE);
	
	// edit user by id
	public static final String EDIT_USER_QUERY_FORMAT = String.format("http://%s/users/", SERVER_ZONE) + "%s";
	
	// remove user by id
	public static final String REMOVE_USER_QUERY_FORMAT = String.format("http://%s/users/", SERVER_ZONE) + "%s";
	
	// authenticate user
	public static final String LOGIN_USER_QUERY = String.format("http://%s/login", SERVER_ZONE);
	
	// identify user
	public static final String INDENTIFY_USER_QUERY = String.format("http://%s/users/identify", SERVER_ZONE);
	
	// logout
	public static final String LOGOUT_USER_QUERY = String.format("http://%s/users/logout", SERVER_ZONE);
	
	
	// LOCATION service
	//
	// create geodata
	public static final String CREATE_GEODATA_QUERY = String.format("http://%s/geodata", SERVER_ZONE);
	
	// get all the location's with last_only
	public static final String GET_ALL_LOCATIONS_QUERY = String.format("http://%s/geodata/find.xml?last_only=1",
            SERVER_ZONE);
		
	// get all geodata with 'status' != null
	public static final String GET_GEODATA_WITH_STATUS_QUERY = String.format("http://%s/geodata/find.xml?page_size=20&sort_by=created_at&status=1",
            SERVER_ZONE);
	
	// Types of queries. They must match queries above 
	public static enum QBQueryType{
		// AUTH
		QBQueryTypeGetAuthToken,
		
		// USERS service
		QBQueryTypeGetUserByExternalID,
		QBQueryTypeCreateUser,
		QBQueryTypeEditUser,
		QBQueryTypeRemoveUser,
		QBQueryTypeLoginUser,
		QBQueryTypeIdentifyUser,
		QBQueryTypeLogoutUser,
		
		// LOCATION service
		QBQueryTypeCreateGeodata,
		QBQueryTypeGetAllLocations,
		QBQueryTypeGetGeodataWithStatus,
	
	}
}