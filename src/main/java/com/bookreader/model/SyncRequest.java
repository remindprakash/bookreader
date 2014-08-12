package com.bookreader.model;

public class SyncRequest 
{
	public String userID;
	public String token;
	public String URL;
	public String header;
	public String body;
	
	public void setCredentials(String usr, String tkn)
	{
	  userID = usr;
	  token = tkn;
	}
	
	public void setUserID(String usr)
	{
		userID = usr;
	}
	
	public void setToken(String tkn)
	{
		token = tkn;
	}

	public void setURL(String url)
	{
		URL = url;
	}
	
	public void createHeader()
	{
		header = "Token: \"" + token + "\"" ;
		header = header +  "\nContent-type:application/json \nAccept: application/json";
	}
	
	public void createBody()
	{
		body = "{ accountId : \"" + userID + "\"  }";
	}
	
	//create appropriate get methods that will be used by executeSession() of syncClient
	
}
