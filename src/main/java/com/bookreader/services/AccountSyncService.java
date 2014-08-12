package com.bookreader.services;

import java.util.ArrayList;
import java.util.List;

import com.bookreader.client.SyncClient;
import com.bookreader.model.Book;
import com.bookreader.model.Shelf;
import com.bookreader.model.SyncRequest;
import com.bookreader.model.SyncResponse;

public class AccountSyncService 
{
	String userID;
	String Token;
	SyncRequest request;
	SyncResponse response;
	public List<Shelf> UserShelf= null;
	
	public AccountSyncService(String user,String tkn)
	{
		userID = user;
		Token = tkn;
		request = new SyncRequest();
		response = new SyncResponse();
		UserShelf = new ArrayList<Shelf>();
	}
	
	public List<Shelf> checkUpdates()
	{
		createSyncRequest();
		sendRequest();
		createShelves();

		return UserShelf;
	}
	
	private void createSyncRequest()
	{
		request.setUserID(userID);
		request.setToken(Token);
	}
	
	private void sendRequest()
	{
		SyncClient cl = new SyncClient();
		response = cl.start(request);
	}
	
	private void createShelves()
	{
		String shelf_name = null;
		int i,j;
		
		//Creating "General" Shelf
		Shelf gen = new Shelf();
		gen.createShelf("General");
		
		//adding general to shelf List
		UserShelf.add(gen);
		
		for (i = 0; i < response.purchasedBooks.size(); i++) 
		{
			shelf_name = response.purchasedBooks.get(i).shelf_id;
			
			for (j = 0; j < UserShelf.size(); j++) 
			{
				if((shelf_name.equals("_general") || shelf_name.equals("_newbooks")) && UserShelf.get(j).shelfName.equals("General") )
				{
					UserShelf.get(j).addBook(response.purchasedBooks.get(i)); //adding book to "General" shelf
					break;
				}
				else if(UserShelf.get(j).shelfName.equals(shelf_name)) //shelf Found 
				{
					UserShelf.get(j).addBook(response.purchasedBooks.get(i)); //adding book to the shelf
					break;
				}
			}
			
			if(j == UserShelf.size()) //Shelf not found in the list. So we have to create a new shelf and add the book to the new shelf 
			{
				//Creating a new Shelf
				Shelf s = new Shelf();
				s.createShelf(shelf_name);
				
				//adding book to the shelf
				s.addBook(response.purchasedBooks.get(i));
				
				//adding the shelf to the shelf List
				UserShelf.add(s);				
			}
		}
	}
	
}
