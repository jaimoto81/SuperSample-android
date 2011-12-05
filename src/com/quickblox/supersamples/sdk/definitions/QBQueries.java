package com.quickblox.supersamples.sdk.definitions;

public class QBQueries {

	// Applications settings
	//
	public static final String APPLICATION_ID = "9";
	public static final String OWNER_ID = "210";
	public static final String AUTH_KEY = "r8z8xMnexVYCAss";
	public static final String AUTH_SECRET = "UtcvFsw9FX2uJ9B";

	
	// Server
	//
	                                      // "quickblox.com" - production server
	public static final String SERVER_ZONE = "qbtest01.quickblox.com"; // test server
	
	
	// Services
	//
	public static final String USERS_SERVICE_HOST_NAME = String.format("users.%s", SERVER_ZONE);
	public static final String GEOPOS_SERVICE_HOST_NAME = String.format("location.%s", SERVER_ZONE);
	
	
	// Queries
	//
	
	// AUTH
	//
	public static final String GET_AUTH_TOKEN_FORMAT = String.format("http://admin.quickblox.com/token?app_id=%s&auth_key=%s", 
			APPLICATION_ID, AUTH_KEY) + "&timestamp=%s&nonce=%s&signature=%s";
	
	// USERS service
	//
	// get all users
	public static final String GET_ALL_USERS_QUERY = String.format("http://%s/owners/%s/users.xml", 
			USERS_SERVICE_HOST_NAME, OWNER_ID);
	
	// get user by external user id
	public static final String GET_USER_BY_EXTERNAL_ID_QUERY_FORMAT = String.format("http://%s/users/external/", USERS_SERVICE_HOST_NAME) + "%s.xml";
	
	// add user
	public static final String CREATE_USER_QUERY = String.format("http://%s/users", USERS_SERVICE_HOST_NAME);
	
	// edit user
	public static final String EDIT_USER_QUERY = String.format("http://%s/users", USERS_SERVICE_HOST_NAME);
	
	// remove user by id
	public static final String REMOVE_USER_QUERY_FORMAT = String.format("http://%s/users/", USERS_SERVICE_HOST_NAME) + "%s";
	
	// authenticate user
	public static final String LOGIN_USER_QUERY = String.format("http://%s/users/authenticate", USERS_SERVICE_HOST_NAME);
	
	// identify user
	public static final String INDENTIFY_USER_QUERY = String.format("http://%s/users/identify", USERS_SERVICE_HOST_NAME);
	
	// logout
	public static final String LOGOUT_USER_QUERY = String.format("http://%s/users/logout", USERS_SERVICE_HOST_NAME);
	
	
	// LOCATION service
	//
	// create geodata
	public static final String CREATE_GEODATA_QUERY = String.format("http://%s/geodata", GEOPOS_SERVICE_HOST_NAME);
	
	// get all the location's with last_only
	public static final String GET_ALL_LOCATIONS_QUERY = String.format("http://%s/geodata/find.xml?app.id=%s&last_only=1", 
			GEOPOS_SERVICE_HOST_NAME, APPLICATION_ID);
		
	// get all geodata with 'status' != null
	public static final String GET_GEODATA_WITH_STATUS_QUERY = String.format("http://%s/geodata/find.xml?app.id=%s&page_size=20&sort_by=created_at&status=1", GEOPOS_SERVICE_HOST_NAME, APPLICATION_ID);

	
	
	
	// Types of queries. They must match queries above 
	public static enum QBQueryType{
		// AUTH
		QBQueryTypeGetAuthToken,
		
		// USERS service
		QBQueryTypeGetAllUsers,
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