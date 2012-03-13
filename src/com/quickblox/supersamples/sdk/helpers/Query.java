package com.quickblox.supersamples.sdk.helpers;

import android.os.Handler;
import android.util.Log;
import com.quickblox.supersamples.sdk.definitions.ActionResultDelegate;
import com.quickblox.supersamples.sdk.definitions.QBQueries;
import com.quickblox.supersamples.sdk.definitions.QueryMethod;
import com.quickblox.supersamples.sdk.definitions.ResponseHttpStatus;
import com.quickblox.supersamples.sdk.objects.RestResponse;
import com.quickblox.supersamples.sdk.objects.XMLNode;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Query {
	
	private static Handler mHandler;

	/*
	 *  Methods
	 */
	
	// sync query
	public static RestResponse performQuery(QueryMethod queryMethod, String query, HttpEntity queryEntity, Header []headers){
		HttpClient httpclient = new DefaultHttpClient();

		try {
			if(queryEntity != null){
				Log.i("Input entity=", EntityUtils.toString(queryEntity));
			}
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
			Log.e("makeQuery, ClientProtocolException:", e.getMessage().toString());
			return null;
		} catch (IOException e) {
			Log.e("makeQuery, IOException:", e.getMessage().toString());
			return null;
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
				case 302:
					status = ResponseHttpStatus.ResponseHttpStatus302;	
				break;
				case 401:
					status = ResponseHttpStatus.ResponseHttpStatus401;
				break;
				case 403:
					status = ResponseHttpStatus.ResponseHttpStatus403;
					break;
				case 404:
					status = ResponseHttpStatus.ResponseHttpStatus404;
					break;
				case 422:
					status = ResponseHttpStatus.ResponseHttpStatus422;
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
			
			if(responseEntity != null && responseEntity.length() > 1){
				// parse entity
				XMLParser parser = new XMLParser();
	            Object [] parsedBody = parser.parseXmlString(responseEntity);
	            parser = null;
	             
	            restResponse.setResponseBodyType(parsedBody[0]);
	 			restResponse.setBody((XMLNode)parsedBody[1]);
			}

			return restResponse;
		}
		
		return null;
	}
	
	// async query
	public static void performQueryAsync(final QueryMethod queryMethod, final String query, 
			final HttpEntity queryEntity, final Header []headers, final ActionResultDelegate delegate, final QBQueries.QBQueryType queryType){
		
		if (mHandler == null){
			mHandler = new Handler();
		}
		
		new Thread(new Runnable() {
			public void run() {
				final RestResponse restResponse = Query.performQuery(queryMethod, query, queryEntity, headers);
				
				mHandler.post(new Runnable() {
		            public void run() {
		            	delegate.completedWithResult(queryType, restResponse);
		            }
		        });
            }
		}).start();
	}
	
	// authorize Application
	public static void authorizeApp(final ActionResultDelegate delegate){
	
		// make query
		long timestamp = System.currentTimeMillis()/1000;
		int nonce = new Random().nextInt();
		String signatureParams = String.format("application_id=%s&auth_key=%s&nonce=%s&timestamp=%s",
				QBQueries.APPLICATION_ID, QBQueries.AUTH_KEY, nonce, timestamp);
		String signature = null;
				
		try {
			signature = Signature.calculateHMAC_SHA(signatureParams, QBQueries.AUTH_SECRET);
		} catch (SignatureException e) {
			e.printStackTrace();
		}
		
		// create entity
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("application_id", QBQueries.APPLICATION_ID));
		formparams.add(new BasicNameValuePair("timestamp", String.valueOf(timestamp)));
		formparams.add(new BasicNameValuePair("nonce", String.valueOf(nonce)));
		formparams.add(new BasicNameValuePair("auth_key", QBQueries.AUTH_KEY));
		formparams.add(new BasicNameValuePair("signature", signature));
		UrlEncodedFormEntity postEntity = null;
		try {
			postEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
				
		Query.performQueryAsync(QueryMethod.Post, QBQueries.GET_AUTH_TOKEN_QUERY, 
				postEntity, null, delegate, QBQueries.QBQueryType.QBQueryTypeGetAuthToken);
	}
}