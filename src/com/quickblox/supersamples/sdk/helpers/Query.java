package com.quickblox.supersamples.sdk.helpers;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.quickblox.supersamples.sdk.definitions.ActionResultDelegate;
import com.quickblox.supersamples.sdk.definitions.QBQueries;
import com.quickblox.supersamples.sdk.definitions.QueryMethod;
import com.quickblox.supersamples.sdk.definitions.ResponseHttpStatus;
import com.quickblox.supersamples.sdk.objects.RestResponse;
import com.quickblox.supersamples.sdk.objects.XMLNode;

import android.os.Handler;
import android.util.Log;

public class Query {
	
	private static Handler mHandler;

	/*
	 *  Methods
	 */
	
	// sync query
	public static RestResponse makeQuery(QueryMethod queryMethod, String query, HttpEntity queryEntity, Header []headers){
		HttpClient httpclient = new DefaultHttpClient();
		
		try {
			Log.i("Input entity=", EntityUtils.toString(queryEntity));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("Input Query=", query);
		
		// set method
		HttpRequestBase httpQuery = null;
		switch(queryMethod){
			case Get:
				httpQuery =  new HttpGet(query);
				
				break;
			case Post:
				HttpPost postQuery = new HttpPost(query);
				
				// set entity
				if(queryEntity != null){
					postQuery.setEntity(queryEntity);
				}
				
				httpQuery =  postQuery;
				break;
			case Put:
				HttpPut putQuery = new HttpPut(query);
				
				// set entity
				if(queryEntity != null){
					putQuery.setEntity(queryEntity);
				}
				
				httpQuery = putQuery;   
				break;
			case Delete:
				httpQuery =  new HttpDelete(query);
				break;
		}
		
		// add headers
		if(headers != null){
			httpQuery.setHeaders(headers);
		}

		HttpResponse response = null;
		try {
			response = httpclient.execute(httpQuery);
		} catch (ClientProtocolException e) {
			Log.e("makeQuery, ClientProtocolException:", e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("makeQuery, IOException:", e.toString());
			e.printStackTrace();
		}
		
		if(response != null){
			RestResponse restResponse = new RestResponse();
			
			// set status
			ResponseHttpStatus status = null;
			switch(response.getStatusLine().getStatusCode()){
				case 200:
					status = ResponseHttpStatus.ResponseHttpStatus200;
				break;
				case 201:
					status = ResponseHttpStatus.ResponseHttpStatus201;
				break;
				case 202:
					status = ResponseHttpStatus.ResponseHttpStatus202;
				break;
				case 422:
					status = ResponseHttpStatus.ResponseHttpStatus422;
				break;
				case 404:
					status = ResponseHttpStatus.ResponseHttpStatus404;
				break;
			}
			restResponse.setResponseStatus(status);
			
			// set headers
			restResponse.setHeaders(restResponse.getHeaders());
			
			// set body
			String responseEntity = null;
			try {
				responseEntity = EntityUtils.toString(response.getEntity());
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Log.i("responseStatus", String.valueOf(response.getStatusLine().getStatusCode()));
			Log.i("responseEntity", responseEntity);

			// parse entity
			XMLParser parser = new XMLParser();
            Object [] parsedBody = parser.parseXmlString(responseEntity);
            parser = null;
             
            restResponse.setResponseBodyType(parsedBody[0]);
 			restResponse.setBody((XMLNode)parsedBody[1]);

			return restResponse;
		}
		
		return null;
	}
	
	// async query
	public static void makeQueryAsync(final QueryMethod queryMethod, final String query, 
			final HttpEntity queryEntity, final Header []headers, final ActionResultDelegate delegate, final QBQueries.QBQueryType queryType){
		
		if (mHandler == null){
			mHandler = new Handler();
		}
		
		new Thread(new Runnable() {
			public void run() {
				final RestResponse restResponse = Query.makeQuery(queryMethod, query, queryEntity, headers);
				
				mHandler.post(new Runnable() {
		            public void run() {
		            	delegate.completedWithResult(queryType, restResponse);
		            }
		        });
            }
		}).start();
	}
}