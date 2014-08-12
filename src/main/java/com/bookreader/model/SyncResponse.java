package com.bookreader.model;

import java.util.ArrayList;
import java.util.List;

public class SyncResponse 
{
	public String status;
	public List<Book> purchasedBooks;
	
	public SyncResponse()
	{
		purchasedBooks = new ArrayList<Book>();
	
	}
	
	public void addBook(Book b)
	{
		purchasedBooks.add(b);
	}
	
}
