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

        String s = "This is a sample <img alt=\"cover\" src=\"../Images/cover.jpg\" />  <img alt=\"cover\" src=\"../Images/cover.jpg\" /> text";
       
       
       Document doc = Jsoup.parse(s); 
      String value="";
       Elements elements = doc.select("img[src]");
       for(Element element : elements)
        {
    	   value=element.attributes().html();
    	   System.out.println(value+  value.indexOf("src="));
    	  
    	   value=value.substring(18, value.length()-1);
    	  /* value=value.substring(value.indexOf("\"")+1, value.length());
    	   
    	   value=value.substring(0, value.indexOf("\"")-1);*/
    	   
    	   System.out.println(value);
    	   element.attr("src", "http:/"+value);
        }
       
       System.out.println(doc.html());
	}
	
	
	
}
