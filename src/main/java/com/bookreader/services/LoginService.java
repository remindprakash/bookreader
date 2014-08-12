package com.bookreader.services;

import com.bookreader.model.LoginRequest;
import com.bookreader.model.LoginResponse;
import com.bookreader.client.LoginClient;

public class LoginService 
{
	String username;
	String password;
	LoginRequest request;
	LoginResponse response;
	
	String UIResponse = null;
	
	public LoginService(String usr,String pwd)
	{
		username = usr;
		password = pwd;
	}
	
	public String authenticate()
	{
		createloginRequest();
		sendRequest();
		validateResponse();

		return UIResponse;
	}
	
	private void createloginRequest()
	{
		request = new LoginRequest();
		request.setUsername(username);
		request.setPassword(password);
	}
	
	private void sendRequest()
	{
		LoginClient cl = new LoginClient();
		response = cl.start(request);
	}
	
	private void validateResponse()
	{
		if(response.status.equals("success"))
			UIResponse = response.token;
		else if(response.status.equals("failed"))
			UIResponse = response.errorMessage;
	}
}
