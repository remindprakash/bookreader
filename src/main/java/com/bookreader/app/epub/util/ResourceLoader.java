package com.bookreader.app.epub.util;

import java.awt.Font;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.text.html.StyleSheet;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceLoader {
	
	private static final Logger log = LoggerFactory.getLogger(ResourceLoader.class);
	
	public static Map<String, Font> fontCache = new HashMap<String, Font>();
	public static Map<String, InputStream> CSSCache = new HashMap<String, InputStream>();
	public static Map<String, Image> imageCache = new HashMap<String, Image>();
	

	public static Map<String, Font> getFontCache() {
		return fontCache;
	}
	
	public void setFontCache(Map<String, Font> fontCache) {
		this.fontCache = fontCache;
	}

	public static Map<String, InputStream> getCSSCache() {
		return CSSCache;
	}

	public static void setCSSCache(Map<String, InputStream> cSSCache) {
		CSSCache = cSSCache;
	}
	
	public static Map<String, Image> getImageCache() {
		return imageCache;
	}

	public static void setImageCache(Map<String, Image> imageCache) {
		ResourceLoader.imageCache = imageCache;
	}

	public static void loadResourceFromBook(Book book) {
         
		try {
        	 List <Resource> fontResource= book.getResources().getResourcesByMediaType(MediatypeService.TTF);
        	 
        	 List <Resource> cssResource= book.getResources().getResourcesByMediaType(MediatypeService.CSS);
        	 
        	 List<Resource> imageResource= book.getResources().getResourcesByMediaType(MediatypeService.JPG);
        	 
        	 
        	 fontCache.clear();
        	 CSSCache.clear();
        	 
        	 for(int i=0;i<fontResource.size();i++){
        		 Font tamilFont = null;
        		 Font b = null; 
        		 b = Font.createFont(Font.TRUETYPE_FONT, fontResource.get(i).getInputStream());
                 tamilFont = b.deriveFont(Font.PLAIN, 12);
        		 fontCache.put("0", tamilFont);
        	 } 
        	 
        	 for(int i=0;i<cssResource.size();i++){
        		 
        		 CSSCache.put("0", cssResource.get(i).getInputStream());
        	 }
        	 
        	 for(int i=0;i<imageResource.size();i++){
        		 imageCache.put("http:/../"+imageResource.get(i).getHref(), createImage(imageResource.get(i)));
        	 }
        	 
        	 
    
         } catch(Exception e) {
             e.printStackTrace();
         }
         
	}
	
	private static Image createImage(Resource imageResource) {
		Image result = null;
		try {
			result = ImageIO.read(imageResource.getInputStream());
			result = result.getScaledInstance(400, 600, Image.SCALE_SMOOTH);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return result;
	}
	
}
