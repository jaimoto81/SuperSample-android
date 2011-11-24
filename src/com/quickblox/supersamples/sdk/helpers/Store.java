package com.quickblox.supersamples.sdk.helpers;

import com.quickblox.supersamples.sdk.objects.XMLNode;

public class Store {
	
	/*
	 * Singleton
	 */
	 private static Store instance;
	    
	 public static synchronized Store getInstance() {
	     if (instance == null) {
	         instance = new Store();
	     } 
	     return instance;
	 }
	 
	 /*
	  * Fields
	  */
	 private XMLNode currentUser;
	 
	 /*
	  * Properties
	  */
	 public XMLNode getCurrentUser() {
		 return currentUser;
	 }
	 public void setCurrentUser(XMLNode currentUser) {
		 this.currentUser = currentUser;
	 }
}

