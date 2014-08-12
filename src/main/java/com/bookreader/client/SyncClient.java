package com.bookreader.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.bookreader.model.Book;
import com.bookreader.model.SyncRequest;
import com.bookreader.model.SyncResponse;

public class SyncClient 
{
	SyncRequest request;
	SyncResponse response;
	
	public SyncClient()
	{
		request = new SyncRequest();
		response = new SyncResponse();
	}
	
	public SyncResponse start(SyncRequest r)
	{
		request = r;

		createSession();
		setRequestURL();
		createRequestHeader();
		createRequestBody();
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
		request.setURL("https://api.bookreader.com/1.0/account/sync"); //Login URL can be put in a config file and retrieved here
	}

	private void createRequestHeader()
	{ 
		request.createHeader();
	}
	
	private void createRequestBody()
	{ 
		request.createBody();
	}
	
	private void executeSession()
	{
		//send the request header to Server
		//wait for the response 
		//response received as byte[]

		byte[] temp = new byte[1000]; //save the byte[] response in temp variable
		parseJSON(temp);
	}
	
	private void parseJSON(byte[] resp)
	{	
		response.status = "success";
	
		if(response.status.equals("success"))
		{
			//User's book details are received. We have to add the books to the syncResponse Object
			//parse the JSON response from the server to retrieve the books
			
			/**************************************************************************
			 * Creating the book list from a file in local machine
			 **************************************************************************/
			String line; 
			String ID,isbn,belongsTo,Title,Description,contentType,coverImageUrl,downloadUrl;
			
		    try
		    {
		           File file = new File("D:/reader/daya's library/library_new.txt");
		           FileReader fileReader  = new FileReader(file);
		           BufferedReader in = new BufferedReader(fileReader);
		           		           
		           while ((line = in.readLine()) != null) 
		           {
		               String bookData[] = line.split("#");
		               ID = bookData[0];
		               isbn = bookData[1];
		               belongsTo = bookData[2]; //This field is not obtained from server response since shelf concept is only in the local application
		               Title = bookData[3];
		               Description = bookData[4];
		               contentType = bookData[5];
		               coverImageUrl = bookData[8]; // book cover jpg name
		               downloadUrl = bookData[9];  //book PDF name
		               
		               Book b = new Book();
		               b.createBook(ID,isbn,Title,Description,contentType,coverImageUrl,downloadUrl);
		               b.assignToShelf(belongsTo);
		               
		               //adding book to purchasedBooks of syncResponse object
		               response.addBook(b);
		           }
		           fileReader.close();  
		           in.close();
		    }
		    catch(IOException e)
		    {
		          e.printStackTrace();
		    }
		    
			/**************************************************************************
			 * Creating the book list from a file in local machine
			 **************************************************************************/
			
		}
		
	}
	
	private void closeSession()
	{
		//close the created session
	}
}
