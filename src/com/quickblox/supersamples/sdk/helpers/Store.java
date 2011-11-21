package com.quickblox.supersamples.sdk.helpers;

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
	 private Object currentUser;
	 
	 /*
	  * Properties
	  */
	 public Object getCurrentUser() {
		 return currentUser;
	 }
	 public void setCurrentUser(Object currentUser) {
		 this.currentUser = currentUser;
	 }
}

