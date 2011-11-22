package com.quickblox.supersamples.main.activities;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.quickblox.supersamples.R;
import com.quickblox.supersamples.sdk.definitions.QBQueries;
import com.quickblox.supersamples.sdk.helpers.Store;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;


public class QuizActivity extends Activity {
	private EditText messageTextEdit;
	private ListView chatListView;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);
        
        messageTextEdit = (EditText)findViewById(R.id.message_editText);
        chatListView = (ListView)findViewById(R.id.chat_listView);
    }
	
	public void onClickButtons(View v) {
		switch (v.getId()) {
			case R.id.send_message_button:
				// check auth
				if(Store.getInstance().getCurrentUser() == null){
					return;
				}
				
				// check message length
				String message = messageTextEdit.getText().toString();
				if(message.length() == 0){
					return;
				}
				
	
				break;
		}
	}
}
