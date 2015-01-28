package com.bookreader.app.main;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;







import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ImgTest {
	
	public static void main(String[] args) {

        String s = "This is a sample  <img src=\"../Images/cover.jpg\" alt=\"cover\"  /> <img alt=\"cover\" src=\"../Images/cover.jpg\" /> text";
       
       
       Document doc = Jsoup.parse(s); 
      String value="";
      String src="";
       Elements elements = doc.select("img[src]");
       for(Element element : elements)
        {
    	  
    	   value=element.attributes().html();
    	  
    	   src=value.substring(value.indexOf("src=")+5, value.length());
    	   src=src.substring(0, src.indexOf("\""));
    	   System.out.println(src);
    	   
    	   value=value.substring(value.indexOf("src=")+5, value.length());
    	  /* value=value.substring(value.indexOf("\"")+1, value.length());
    	   
    	   value=value.substring(0, value.indexOf("\"")-1);*/
    	   
    	   //System.out.println(value);
    	   element.attr("src", "http:/"+value);
        }
       
       //System.out.println(doc.html());
	}
	
	
	
}
