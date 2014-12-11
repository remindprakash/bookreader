package com.bookreader.epub.util;

import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FontLoader {
	
	private static final Logger log = LoggerFactory.getLogger(FontLoader.class);
	
	public static Map<String, Font> fontCache = new HashMap<String, Font>();
	

	public static Map<String, Font> getFontCache() {
		return fontCache;
	}
	
	public void setFontCache(Map<String, Font> fontCache) {
		this.fontCache = fontCache;
	}


	public static Map<String, Font> loadFontFromBook(Book book) {
         
		try {
        	 List <Resource> fontResource= book.getResources().getResourcesByMediaType(MediatypeService.TTF);
        	 fontCache.clear();
        	 
        	 for(int i=0;i<fontResource.size();i++){
        		 Font tamilFont = null;
        		 Font b = null; 
        		 b = Font.createFont(Font.TRUETYPE_FONT, fontResource.get(i).getInputStream());
                 tamilFont = b.deriveFont(Font.PLAIN, 11);
                 //fontResource.get(i).getHref()
        		 fontCache.put("0", tamilFont);
        	 } 
    
         } catch(Exception e) {
             e.printStackTrace();
         }
         return fontCache;
	}
	
}
