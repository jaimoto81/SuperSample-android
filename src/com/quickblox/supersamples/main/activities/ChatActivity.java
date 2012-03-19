package com.quickblox.supersamples.main.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.flurry.android.FlurryAgent;
import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.definitions.Consts;
import com.quickblox.supersamples.main.helpers.AlertManager;
import com.quickblox.supersamples.main.helpers.ChatArrayAdapter;
import com.quickblox.supersamples.main.objects.ChatItem;
import com.quickblox.supersamples.sdk.definitions.ActionResultDelegate;
import com.quickblox.supersamples.sdk.definitions.QBQueries;
import com.quickblox.supersamples.sdk.definitions.QBQueries.QBQueryType;
import com.quickblox.supersamples.sdk.definitions.QueryMethod;
import com.quickblox.supersamples.sdk.definitions.ResponseHttpStatus;
import com.quickblox.supersamples.sdk.helpers.Query;
import com.quickblox.supersamples.sdk.helpers.Store;
import com.quickblox.supersamples.sdk.objects.RestResponse;
import com.quickblox.supersamples.sdk.objects.XMLNode;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatActivity extends Activity implements ActionResultDelegate {

    private EditText messageTextEdit;
    private ListView chatListView;
    private ChatArrayAdapter listAdapter;
    private ProgressBar queryProgressBar;
    boolean isChatUpdating;
    private Timer chatUpdateTimer;
    private Thread processChatDataThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_view);

        chatListView = (ListView) findViewById(R.id.chat_listView);
        messageTextEdit = (EditText) findViewById(R.id.message_editText);
        chatListView = (ListView) findViewById(R.id.chat_listView);
        queryProgressBar = (ProgressBar) findViewById(R.id.chatQuery_progressBar);

        messageTextEdit.clearFocus();

        List<ChatItem> chatData = new ArrayList<ChatItem>();
        listAdapter = new ChatArrayAdapter(ChatActivity.this, R.layout.chat_listview_item, chatData);
        chatListView.setAdapter(listAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Consts.FLURRY_API_KEY);
        FlurryAgent.logEvent("run ChatActivity");

    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        chatUpdateTimer.cancel();
        chatUpdateTimer = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatUpdateTimer = new Timer();
        chatUpdateTimer.schedule(new TimerTask() {

            public void run() {
                updateChat();
            }
        }, 50, Consts.CHAT_UPDATE_PERIOD);
    }

    public void onClickButtons(View v) {
        switch (v.getId()) {
            case R.id.send_message_button:
                // check auth
                if (Store.getInstance().getCurrentUser() == null) {
                    Toast.makeText(this, "You must login first. Go to Settings tab.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // check message length
                String message = messageTextEdit.getText().toString();
                if (message.length() == 0) {
                    return;
                }

                queryProgressBar.setVisibility(View.VISIBLE);

                // create entity for current user
                String lat = "45.45",
                 lng = "45.45";
                if (Store.getInstance().getCurrentLocation() != null) {
                    lat = Double.toString(Store.getInstance().getCurrentLocation().getLatitude());
                    lng = Double.toString(Store.getInstance().getCurrentLocation().getLongitude());
                }

                List<NameValuePair> formparamsGeoData = new ArrayList<NameValuePair>();
                formparamsGeoData.add(new BasicNameValuePair(
                        "geo_data[status]", message));
                formparamsGeoData.add(new BasicNameValuePair(
                        "geo_data[latitude]", lat));
                formparamsGeoData.add(new BasicNameValuePair(
                        "geo_data[longitude]", lng));
                formparamsGeoData.add(new BasicNameValuePair("token",
                        Store.getInstance().getAuthToken()));

                UrlEncodedFormEntity postEntityGeoData = null;
                try {
                    postEntityGeoData = new UrlEncodedFormEntity(
                            formparamsGeoData, "UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                //
                // make query
                Query.performQueryAsync(QueryMethod.Post,
                        QBQueries.CREATE_GEODATA_QUERY,
                        postEntityGeoData, null, this,
                        QBQueries.QBQueryType.QBQueryTypeCreateGeodata);

                break;
        }
    }

    // update chat
    private void updateChat() {
        if (isChatUpdating) {
            return;
        }

        isChatUpdating = true;

        String query = QBQueries.GET_GEODATA_WITH_STATUS_QUERY + "&token=" + Store.getInstance().getAuthToken();

        Query.performQueryAsync(QueryMethod.Get, query,
                null, null, this, QBQueries.QBQueryType.QBQueryTypeGetGeodataWithStatus);
    }

    // reload listView
    private void reloadList(final XMLNode object) {

        // empty response
        if (object.getChildren() == null) {
            isChatUpdating = false;

            return;
        }

        processChatDataThread = new Thread(new Runnable() {

            public void run() {

                // populate chats
                for (int i = object.getChildren().size() - 1; i >= 0; --i) {
                    XMLNode child = object.getChildren().get(i);

                    String status = child.findChild("status").getAttributes().get("nil");
                    if (status == null) {
                        String ID = child.findChild("id").getText();
                        String message = child.findChild("status").getText();

                        // skip if already exist
                        if (listAdapter.isHasID(ID)) {
                            continue;
                        }
                        if (listAdapter.isHasMessage(message)) {
                            continue;
                        }


                        // create new element
                        final ChatItem item = new ChatItem();
                        item.setDate(child.findChild("created-at").getText().replace("T", " ").replace("Z", " "));
                        item.setMessage(message);
                        item.setID(ID);
                        String name = child.findChild("user").findChild("full-name").getText();
                        if(name.length() == 0){
                             name = child.findChild("user").findChild("login").getText();
                        }
                        item.setUserName(name);

                        // add to list view adapter
                        ChatActivity.this.runOnUiThread(new Runnable() {

                            public void run() {
                                listAdapter.insert(item, 0);
                            }
                        });

                    }
                }

                isChatUpdating = false;
            }
        });

        processChatDataThread.start();
    }

    @Override
    public void completedWithResult(QBQueryType queryType, RestResponse response) {
        // no internet connection
        if (response == null) {
            isChatUpdating = false;
            AlertManager.showServerError(this, "Please, check your internet connection");
            return;
        }

        switch (queryType) {
            case QBQueryTypeGetGeodataWithStatus:
                // Ok
                if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus200) {
                    reloadList(response.getBody());
                } else {
                    isChatUpdating = false;
                }
                break;
            case QBQueryTypeCreateGeodata:
                queryProgressBar.setVisibility(View.GONE);
                // Created
                if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus201) {
                    if (!isChatUpdating) {
                        // add to adapter
                        ChatItem item = new ChatItem();
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        item.setDate(sdf.format(cal.getTime()));
                        item.setMessage(messageTextEdit.getText().toString());
                        String name = Store.getInstance().getCurrentUser().findChild("full-name").getText();
                        if(name.length() == 0){
                            name = Store.getInstance().getCurrentUser().findChild("login").getText();
                        }
                        item.setUserName(name);
                        item.setID(response.getBody().findChild("id").getText());
                        listAdapter.insert(item, 0);

                        Store.getInstance().setCurrentStatus(messageTextEdit.getText().toString());
                        messageTextEdit.setText("");
                        messageTextEdit.clearFocus();
                    }
                    // access denied
                } else if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus403) {
                    AlertManager.showServerError(this, "User access denied");

                    // validation error
                } else if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus422) {
                    String error = response.getBody().getChildren().get(0).getText();
                    AlertManager.showServerError(this, error);
                }
                break;
        }
    }
}