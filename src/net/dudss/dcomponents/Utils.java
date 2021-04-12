package net.dudss.dcomponents;

import java.awt.Color;
import java.awt.Rectangle;

public class Utils {
	private static double FACTOR = 0.7;
	
	public static Rectangle centerRectInRect(Rectangle r1, Rectangle r2) {
		int w1 = r1.width;
		int h1 = r1.height;
		int x1 = r2.x + ((r2.width - w1) / 2);
		int y1 = r2.y + ((r2.height - h1) / 2);
		return new Rectangle(x1, y1, w1, h1);
	}
	
	/**
     * @see java.awt.Color#brighter
     */
    public static Color brighter(Color c) {
    	return brighter(c, FACTOR);
    }
	
    /**
     * @see java.awt.Color#brighter
     */
    public static Color brighter(Color c, double factor) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int alpha = c.getAlpha();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int)(1.0/(1.0-factor))*15;
        if ( r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return new Color(Math.min((int)(r/factor), 255),
                         Math.min((int)(g/factor), 255),
                         Math.min((int)(b/factor), 255),
                         alpha);
    }
    
    /**
     * @see java.awt.Color#darker
     */
    public static Color darker(Color c) {
        return darker(c, FACTOR);
    }
    
    /**
     * @see java.awt.Color#darker
     */
    public static Color darker(Color c, double factor) {
        return new Color(Math.max((int)(c.getRed()  *factor), 0),
                         Math.max((int)(c.getGreen()*factor), 0),
                         Math.max((int)(c.getBlue() *factor), 0),
                         c.getAlpha());
    }
	
	public static boolean isInteger(String str) {
		if (str == null) {
	        return false;
	    }
	    int length = str.length();
	    if (length == 0) {
	        return false;
	    }
	    
	    if (isBlank(str)) return false;
	    
		try {
	        Integer.parseInt(str);
	        return true;
	    }
	    catch( Exception e ) {
	        return false;
	    }
	}
	
	public static boolean isIntegerFast(String str) {
	    if (str == null) {
	        return false;
	    }
	    int length = str.length();
	    if (length == 0) {
	        return false;
	    }
	    int i = 0;
	    if (str.charAt(0) == '-') {
	        if (length == 1) {
	            return false;
	        }
	        i = 1;
	    }
	    for (; i < length; i++) {
	        char c = str.charAt(i);
	        if (c < '0' || c > '9') {
	            return false;
	        }
	    }
	    return true;
	}
	
	/**Recalculates a float value into a different number range.
	 * @param OldValue The original value.
	 * @param OldMin The original lower limit of the number range.
	 * @param OldMax The original higher limit of the number range.
	 * @param NewMin The new lower limit of the number range.
 	 * @param NewMax The new higher limit of the number range.
	 */
	public static float range(float OldValue, float OldMin, float OldMax, float NewMin, float NewMax) {
		return (((OldValue - OldMin) * (NewMax - NewMin)) / (OldMax - OldMin)) + NewMin;
	}
	
	/**
     * Gets a CharSequence length or {@code 0} if the CharSequence is
     * {@code null}.
     */
    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }
	
	/**
     * <p>Checks if a CharSequence is empty (""), null or whitespace only.</p>
     *
     * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     */
    public static boolean isBlank(final CharSequence cs) {
        final int strLen = length(cs);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

   /**
     * <p>Checks if a CharSequence is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
