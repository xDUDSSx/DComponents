package net.dudss.dcomponents.svg;

import java.awt.Color;
import java.awt.image.RGBImageFilter;

/**
 * A simplified RGBImageFilter that presents individual rgba components as a Color object.
 */
public abstract class FlatRGBFilter extends RGBImageFilter {

	@Override
	public int filterRGB(int x, int y, int rgb) {
		return filterRGB(new Color(rgb)).getRGB();
	}
	
	public abstract Color filterRGB(Color c);
}
