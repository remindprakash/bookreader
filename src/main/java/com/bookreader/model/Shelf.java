package com.bookreader.model;



import java.util.ArrayList;
import java.util.List;

import com.bookreader.model.Book;

public class Shelf 
{	
	public String shelfID;
	public String shelfName;
	public List<Book> book;
	public static int shelf_count = 0;
	
	public Shelf()
	{
		shelfID = new String();
		shelfName = new String();
		book = new ArrayList<Book>();

		shelf_count++;
	}
	
	public void createShelf(String sName)
	{
		shelfID = "S" + shelf_count;
		shelfName = sName;		
	}
	
	public void addBook(Book b)
	{
		book.add(b);
	}
	
	public int getBookCount()
	{
		return book.size();
	}
	
	public void PrintShlef()
	{
		System.out.println("***********************************************************************************");
		System.out.println("\t\t\t" + shelfName );//+ "\tNo. of Books:" + book.size());
		System.out.println("***********************************************************************************");
		
		for (int i = 0; i < book.size(); i++) 
		{
			System.out.println("");
			book.get(i).printBook();
		}
	}
}
