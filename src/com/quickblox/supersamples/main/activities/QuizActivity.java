package com.quickblox.supersamples.main.activities;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.helpers.ChatArrayAdapter;
import com.quickblox.supersamples.sdk.definitions.ActionResultDelegate;
import com.quickblox.supersamples.sdk.definitions.QBQueries;
import com.quickblox.supersamples.sdk.definitions.QueryMethod;
import com.quickblox.supersamples.sdk.definitions.QBQueries.QBQueryType;
import com.quickblox.supersamples.sdk.definitions.ResponseHttpStatus;
import com.quickblox.supersamples.sdk.helpers.Query;
import com.quickblox.supersamples.sdk.helpers.Store;
import com.quickblox.supersamples.sdk.objects.RestResponse;
import com.quickblox.supersamples.sdk.objects.XMLNode;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class QuizActivity extends Activity implements ActionResultDelegate{
	private EditText messageTextEdit;
	private ListView chatListView;
	private ChatArrayAdapter listAdapter;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);
        
        chatListView = (ListView)findViewById(R.id.chat_listView);
        messageTextEdit = (EditText)findViewById(R.id.message_editText);
        chatListView = (ListView)findViewById(R.id.chat_listView);
        
        // update chat periodicaly
        new Timer().schedule(new TimerTask() {
			public void run() {
				updateChat();
			}
		}, 0, 7000);
    }
	
	public void onClickButtons(View v) {
		switch (v.getId()) {
			case R.id.send_message_button:
				// check auth
				if(Store.getInstance().getCurrentUser() == null){
					Toast.makeText(this, "You mast login first",
							Toast.LENGTH_LONG).show();
					return;
				}
				
				// check message length
				String message = messageTextEdit.getText().toString();
				if(message.length() == 0){
					return;
				}
				
				// create entity for current user
				List<NameValuePair> formparamsGeoData = new ArrayList<NameValuePair>();
				formparamsGeoData.add(new BasicNameValuePair(
						"geo_data[user_id]", "244"));
				formparamsGeoData.add(new BasicNameValuePair(
						"geo_data[status]", message));
				formparamsGeoData.add(new BasicNameValuePair(
						"geo_data[latitude]", "23.534234"));
				formparamsGeoData.add(new BasicNameValuePair(
						"geo_data[longitude]", "44.523424"));

				UrlEncodedFormEntity postEntityGeoData = null;
				try {
					postEntityGeoData = new UrlEncodedFormEntity(
							formparamsGeoData, "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				//
				// make query
				Query.makeQueryAsync(QueryMethod.Post,
						QBQueries.CREATE_GEODATA_QUERY,
						postEntityGeoData, null, this,
						QBQueries.QBQueryType.QBQueryTypeCreateGeodata);

				break;
		}
	}
	
	// update chat
	private void updateChat(){
		Query.makeQueryAsync(QueryMethod.Get, QBQueries.GET_GEODATA_QUERY,
				null, null, this, QBQueries.QBQueryType.QBQueryTypeGetGeodata);
	}
	
	private void reloadList(XMLNode object){
		object.getChildren().remove(0);
		Log.i("GeoData count=", String.valueOf(object.getChildren().size()));
		
	    listAdapter = new ChatArrayAdapter(this, R.layout.chat_listview_item, 
	    		object.getChildren());
	    chatListView.setAdapter(listAdapter);
	}

	@Override
	public void completedWithResult(QBQueryType queryType, RestResponse response) {
		switch(queryType){
			case QBQueryTypeGetGeodata:
				if(response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus200){
					reloadList(response.getBody());
				}
				break;
			case QBQueryTypeCreateGeodata:
				break;
		}
	}
}
