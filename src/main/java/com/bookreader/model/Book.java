package com.bookreader.model;

public class Book 
{
	public String ID;
	public String isbn;
	public String Title;
	public String Description;
	public String contentType;
	public String coverImageUrl;
	public String downloadUrl;
	
	public String shelf_id; //denotes in which shelf the book is placed - shelfName of Shelf object is assigned here
	
	public Book()
	{
		ID = new String();
		isbn = new String();
		Title = new String();
		Description = new String();
		contentType = new String();
		coverImageUrl = new String();
		downloadUrl = new String();
		
		shelf_id = new String();
	}
	
	public void createBook(String id,String isbnNo,String title,String desc,String type,String imageUrl,String bookUrl)
	{
		ID = id;
		isbn = isbnNo;
		Title = title;
		Description = desc;
		contentType = type;
		coverImageUrl = imageUrl;
		downloadUrl = bookUrl;
	}
	
	public void assignToShelf(String shelfName)
	{
		shelf_id = shelfName;
	}
	
	public void printBook()
	{
		System.out.println("\tID : " + ID);
		System.out.println("\tisbn : " + isbn);
		System.out.println("\tbelongsTo : " + shelf_id);
		System.out.println("\tTitle : " + Title);
		System.out.println("\tDescription : " + Description);
		System.out.println("\tcontentType : " + contentType);
		System.out.println("\tcoverImageUrl : " + coverImageUrl);
		System.out.println("\tdownloadUrl : " + downloadUrl);
	}
	//add appropriate get functions
}
