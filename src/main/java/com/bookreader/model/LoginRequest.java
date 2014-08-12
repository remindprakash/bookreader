package com.bookreader.model;

public class LoginRequest 
{
	public String username;
	public String password;
	public String URL;
	public String header;
	
	public void setUsername(String usr)
	{
		username = usr;
	}
	
	public void setPassword(String pwd)
	{
		password = pwd;
	}
	
	public void setURL(String url)
	{
		URL = url;
	}
	
	public void createHeader()
	{
		header = "Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==";
		header = header +  username + "||" + password; //convert id & pwd to their base64 encoded version 
		header = header +  "\n  Contenttype:application/json  \n  Accept: application/json";	
	}
	
	//create appropriate get methods that will be used by executeSession() of loginClient
}
