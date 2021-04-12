package net.dudss.dcomponents.util;

import net.dudss.dcomponents.Common;

public class HtmlUtils {

	public static String createHtml(String s) {
		return "<html>" + s + "</html>";
	}

	public static String createBoldHtml(String s) {
		return "<html><b>" + s + "</b></html>";
	}

	public static String bHtml(String s) {
		return "<b>" + s + "</b>";
	}

	public static String createLinkHtml(String s) {
		return "<html><a href=\"#\">" + s + "</a></html>";
	}

	public static String createLinkTag(String s) {
		return "<a href=\"#\">" + s + "</a>";
	}

	public static String createItalicHtml(String s) {
		return "<html><i>" + s + "</i></html>";
	}

	public static String iHtml(String s) {
		return "<i>" + s + "</i>";
	}

	public static String colorTag(String s, String color) {
		return "<span style=\"color:" + color + "\">" + s + "</span>";
	}

	public static String createFontSizeHtml(String s, String fontChange) {
		return "<html><font size=" + fontChange + ">" + s + "</html>";
	}

	public static String createParagraphHtml(String s) {
		return createParagraphHtml(s, Common.DEFAULT_PARAGRAPH_WIDTH);
	}
	
	public static String createParagraphHtml(String s, int width) {
		return "<html><body>"
	    + "<p style='width: " + width + "px;'>"
	    + s
	    + "</p>"
	    + "</body></html>"; 
	}
}