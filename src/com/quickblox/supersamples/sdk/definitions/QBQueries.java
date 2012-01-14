package com.quickblox.supersamples.sdk.definitions;

public class QBQueries {

	// Applications settings
	//
	public static final String APPLICATION_ID = "99";
	public static final String OWNER_ID = "4343";
	public static final String AUTH_KEY = "63ebrp5VZt7qTOv";
	public static final String AUTH_SECRET = "YavMAxm5T59-BRw";

	
	// Server
	//
	public static final String SERVER_ZONE = "quickblox.com"; 
	public static final String BLOBS_SERVER_ZONE = "s3.amazonaws.com"; // blobs-test server
	
	
	// Services
	//
	public static final String USERS_SERVICE_HOST_NAME = String.format("users.%s", SERVER_ZONE);
	public static final String GEOPOS_SERVICE_HOST_NAME = String.format("location.%s", SERVER_ZONE);
	public static final String ADMIN_SERVICE_HOST_NAME = String.format("admin.%s", SERVER_ZONE);
	public static final String BLOBS_SERVICE_HOST_NAME = String.format("blobs2.%s", SERVER_ZONE);
	
	public static final String BLOBS_AMAZONAWS_SERVICE_HOST_NAME = String.format("blobs-test-oz.%s", BLOBS_SERVER_ZONE);
	
	
	// Queries
	//
	
	// AUTH
	//
	public static final String GET_AUTH_TOKEN_QUERY = String.format("https://%s/auth", ADMIN_SERVICE_HOST_NAME);
	
	// USERS service
	//
	// get all users
	public static final String GET_ALL_USERS_QUERY = String.format("http://%s/owners/%s/users.xml", 
			USERS_SERVICE_HOST_NAME, OWNER_ID);
	
	// get user by external user id
	public static final String GET_USER_BY_EXTERNAL_ID_QUERY_FORMAT = String.format("http://%s/users/external/", USERS_SERVICE_HOST_NAME) + "%s.xml";
	
	// add user
	public static final String CREATE_USER_QUERY = String.format("http://%s/users", USERS_SERVICE_HOST_NAME);
	
	// edit user by id
	public static final String EDIT_USER_QUERY_FORMAT = String.format("http://%s/users/", USERS_SERVICE_HOST_NAME) + "%s";
	
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
	public static final String GET_ALL_LOCATIONS_QUERY = String.format("http://%s/geodata/find.xml?last_only=1", 
			GEOPOS_SERVICE_HOST_NAME);
		
	// get all geodata with 'status' != null
	public static final String GET_GEODATA_WITH_STATUS_QUERY = String.format("http://%s/geodata/find.xml?page_size=20&sort_by=created_at&status=1", 
			GEOPOS_SERVICE_HOST_NAME);

	
	// BLOB service
	//
	// create blob
	public static final String CREATE_BLOB_QUERY = String.format("http://%s/blobs.xml", BLOBS_SERVICE_HOST_NAME);
	
	// upload blob
	public static final String UPLOAD_BLOB_QUERY = String.format("http://%s/", BLOBS_AMAZONAWS_SERVICE_HOST_NAME);
	
	// complete blob
	public static final String COMPLETE_BLOB_QUERY = String.format("http://%s/blobs/", BLOBS_SERVICE_HOST_NAME) + "%s/complete.xml";
	
	// get an info of the blob' file (xml)
	public static final String GET_BLOB_INFO_FORMAT = String.format("http://%s/blobs/", BLOBS_SERVICE_HOST_NAME) + "%s.xml";
	
	// get the blob' object 
	//public static final String GET_BLOB_QUERY = String.format("http://%s/blobs/", BLOBS_SERVICE_HOST_NAME) + "%s/getblobobjectbyid.xml";
	
	// download blob
	public static final String DOWNLOAD_BLOB_BY_UID_QUERY = String.format("http://%s/blobs/uid.ext", BLOBS_SERVICE_HOST_NAME);
	
	
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
		
		// BLOBS service
		// upload of a blob
		QBQueryTypeCreateBlob, 
		QBQueryTypeUploadBlob, 
		QBQueryTypeCompleteBlob, 
		
		// download of the blob
		QBQueryTypeGetBlobInfo,
		QBQueryTypeDownloadBlob, 
	
	}
}