package com.quickblox.supersamples.sdk.helpers;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.util.Log;

import com.quickblox.supersamples.sdk.definitions.QBQueries;

public class ChatCenter {
	/*
	 * Singleton
	 */
	private static ChatCenter instance;
	
	private  ChatCenter(){};
	
	public static synchronized  ChatCenter getInstance(){
		if(instance == null){
			instance = new ChatCenter();
		}
		
		return instance;
	}
	
	
	/*
	 * Fields
	 */
	
	private XMPPConnection connection;
	
	
	/*
	 * Properties 
	 */
	
	XMPPConnection getConnection() {
		return connection;
	}

	void setConnection(XMPPConnection connection) {
		this.connection = connection;
	}
	
	
	/*
	 * Methods (object)
	 */
	
	// open connection
	public boolean connect(){
		ConnectionConfiguration connConfig = new ConnectionConfiguration(QBQueries.CHAT_SERVICE_HOST_NAME);
	    setConnection(new XMPPConnection(connConfig));
	    try {
			getConnection().connect();
		} catch (XMPPException e) {
			Log.e("ChatManager, connect Error", e.getMessage());
			return false;
		}
	    
	    if(getConnection().isConnected()){
	    	return true;
	    }
	    
	    return false;
	}
	
	// close connection
	public void disconnect(){
		getConnection().disconnect();
	}
	
	// register new user
	public boolean registerAccount(String login, String password, String name){
		AccountManager am = new AccountManager(getConnection());
	    
		Map<String, String> attributes = new HashMap<String, String>();
	    attributes.put("name", name);
	        
	    try {
			am.createAccount(login, password, attributes);
		} catch (XMPPException e) {
			Log.e("ChatManager, registerAccount Error", e.getMessage());
			return false;
		}

	    return true;
	}
	
	// login
	public boolean login(String username, String password){
		try {
			getConnection().login(username, password);
		} catch (XMPPException e) {
			Log.e("ChatManager, login Error", e.getMessage());
			return false;
		}
		
		if(getConnection().isAuthenticated()){
			return true;
		}
		
		ChatManager chatmanager = connection.getChatManager();
		
		return false;
	}
}