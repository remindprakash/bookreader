package com.bookreader.client;

import com.bookreader.model.LoginRequest;
import com.bookreader.model.LoginResponse;

public class LoginClient 
{
	LoginRequest request;
	LoginResponse response;
	
	public LoginClient()
	{
		request = new LoginRequest();
		response = new LoginResponse();
	}
	
	public LoginResponse start(LoginRequest r)
	{
		request = r;

		createSession();
		setRequestURL();
		createRequestHeader();
		executeSession();	
		closeSession();
		
		return response;
	}

	private void createSession()
	{ 
		//set up a connection between client and server		
	}

	
	private void setRequestURL()
	{
		request.setURL("https://api.bookreader.com/1.0/account/auth"); //Login URL can be put in a config file and retrieved here
	}
	
	private void createRequestHeader()
	{ 
		request.createHeader();
	}
	
	private void executeSession()
	{
		//send the request header to Server
		//wait for the response 
		//response received as byte[]

		byte[] temp = new byte[1000]; //save the byte[] response in temp variable
		retriveResponseMessage(temp);
	}
	
	private void retriveResponseMessage(byte[] resp)
	{	
		//retrieve status and token/error message from the server Response	
		String stat = "success";
		String tkn = "e72e16c7e42f292c6912e7710c838347ae178b4a";
		String errMsg = "Failed to login Invalid credentials";

		//assign the response to the loginResponse object
		if(tkn.isEmpty())
		{
			response.status = "failed";
			response.token = null;
			response.errorMessage = errMsg;
		}
		else
		{
			response.status = "success";
			response.token = tkn;
			response.errorMessage = null;			
		}
	}
	
	private void closeSession()
	{
		//close the created session
	}
}
