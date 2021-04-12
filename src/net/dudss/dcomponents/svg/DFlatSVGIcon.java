package net.dudss.dcomponents.svg;

/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RGBImageFilter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatIconColors;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLaf.DisabledIconProvider;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.Graphics2DProxy;
import com.formdev.flatlaf.util.GrayFilter;
import com.formdev.flatlaf.util.MultiResolutionImageSupport;
import com.formdev.flatlaf.util.UIScale;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;

/**
 * An icon that loads and paints SVG.
 *
 * @author Karl Tauber
 */
public class DFlatSVGIcon
	extends ImageIcon
	implements DisabledIconProvider
{
	// use own SVG universe so that it can not be cleared from anywhere
	private static final SVGUniverse svgUniverse = new SVGUniverse();

	private final String name;
	private final int width;
	private final int height;
	private final float scale;
	private final boolean disabled;
	private final ClassLoader classLoader;

	private RGBImageFilter userFilter = null;

	private SVGDiagram diagram;
	private boolean dark;

	/**
	 * Creates an SVG icon from the given resource name.
	 * <p>
	 * The SVG attributes {@code width} and {@code height} (or {@code viewBox})
	 * in the tag {@code <svg>} are used as icon size.
	 *
	 * @param name the name of the SVG resource (a '/'-separated path)
	 * @see ClassLoader#getResource(String)
	 */
	public DFlatSVGIcon( String name ) {
		this( name, -1, -1, 1, false, null );
	}

	/**
	 * Creates an SVG icon from the given resource name.
	 * The SVG file is loaded from the given class loader.
	 * <p>
	 * The SVG attributes {@code width} and {@code height} (or {@code viewBox})
	 * in the tag {@code <svg>} are used as icon size.
	 *
	 * @param name the name of the SVG resource (a '/'-separated path)
	 * @param classLoader the class loader used to load the SVG resource
	 * @see ClassLoader#getResource(String)
	 */
	public DFlatSVGIcon( String name, ClassLoader classLoader ) {
		this( name, -1, -1, 1, false, classLoader );
	}

	/**
	 * Creates an SVG icon from the given resource name with the given width and height.
	 * <p>
	 * The icon is scaled if the given size is different to the size specified in the SVG file.
	 *
	 * @param name the name of the SVG resource (a '/'-separated path)
	 * @param width the width of the icon
	 * @param height the height of the icon
	 * @see ClassLoader#getResource(String)
	 */
	public DFlatSVGIcon( String name, int width, int height ) {
		this( name, width, height, 1, false, null );
	}

	/**
	 * Creates an SVG icon from the given resource name with the given width and height.
	 * The SVG file is loaded from the given class loader.
	 * <p>
	 * The icon is scaled if the given size is different to the size specified in the SVG file.
	 *
	 * @param name the name of the SVG resource (a '/'-separated path)
	 * @param width the width of the icon
	 * @param height the height of the icon
	 * @param classLoader the class loader used to load the SVG resource
	 * @see ClassLoader#getResource(String)
	 */
	public DFlatSVGIcon( String name, int width, int height, ClassLoader classLoader ) {
		this( name, width, height, 1, false, classLoader );
	}

	/**
	 * Creates an SVG icon from the given resource name that is scaled by the given amount.
	 * <p>
	 * The SVG attributes {@code width} and {@code height} (or {@code viewBox})
	 * in the tag {@code <svg>} are used as base icon size, which is multiplied
	 * by the given scale factor.
	 *
	 * @param name the name of the SVG resource (a '/'-separated path)
	 * @param scale the amount by which the icon size is scaled
	 * @see ClassLoader#getResource(String)
	 */
	public DFlatSVGIcon( String name, float scale ) {
		this( name, -1, -1, scale, false, null );
	}

	/**
	 * Creates an SVG icon from the given resource name that is scaled by the given amount.
	 * The SVG file is loaded from the given class loader.
	 * <p>
	 * The SVG attributes {@code width} and {@code height} (or {@code viewBox})
	 * in the tag {@code <svg>} are used as base icon size, which is multiplied
	 * by the given scale factor.
	 *
	 * @param name the name of the SVG resource (a '/'-separated path)
	 * @param scale the amount by which the icon size is scaled
	 * @param classLoader the class loader used to load the SVG resource
	 * @see ClassLoader#getResource(String)
	 */
	public DFlatSVGIcon( String name, float scale, ClassLoader classLoader ) {
		this( name, -1, -1, scale, false, classLoader );
	}

	private DFlatSVGIcon( String name, int width, int height, float scale, boolean disabled, ClassLoader classLoader ) {
		this.name = name;
		this.classLoader = classLoader;
		this.width = width;
		this.height = height;
		this.scale = scale;
		this.disabled = disabled;
	}

	/**
	 * Sets an RGBImageFilter to be used when painting the icon.
	 * For simple RGB modifications you can use the {@link FlatRGBFilter}.
	 * @param filter
	 */
	public void setFilter(RGBImageFilter filter) {
		this.userFilter = filter;
	}

	public RGBImageFilter getFilter() {
		return userFilter;
	}

	/**
	 * Creates a new icon with given width and height, which is derived from this icon.
	 *
	 * @param width the width of the new icon
	 * @param height the height of the new icon
	 * @return a new icon
	 */
	public DFlatSVGIcon derive( int width, int height ) {
		if( width == this.width && height == this.height )
			return this;

		DFlatSVGIcon icon = new DFlatSVGIcon( name, width, height, scale, false, classLoader );
		icon.diagram = diagram;
		icon.dark = dark;
		return icon;
	}

	/**
	 * Creates a new icon with given scaling, which is derived from this icon.
	 *
	 * @param scale the amount by which the icon size is scaled
	 * @return a new icon
	 */
	public DFlatSVGIcon derive( float scale ) {
		if( scale == this.scale )
			return this;

		DFlatSVGIcon icon = new DFlatSVGIcon( name, width, height, scale, false, classLoader );
		icon.diagram = diagram;
		icon.dark = dark;
		return icon;
	}

	/**
	 * Creates a new icon with disabled appearance, which is derived from this icon.
	 *
	 * @return a new icon
	 */
	@Override
	public Icon getDisabledIcon() {
		if( disabled )
			return this;

		DFlatSVGIcon icon = new DFlatSVGIcon( name, width, height, scale, true, classLoader );
		icon.diagram = diagram;
		icon.dark = dark;
		return icon;
	}

	private void update() {
		if( dark == isDarkLaf() && diagram != null )
			return;

		dark = isDarkLaf();
		URL url = getIconURL( name, dark );
		if( url == null & dark )
			url = getIconURL( name, false );

		// load/get image
		try {
			diagram = svgUniverse.getDiagram( url.toURI() );
		} catch( URISyntaxException ex ) {
			ex.printStackTrace();
		}
	}

	private URL getIconURL( String name, boolean dark ) {
		if( dark ) {
			int dotIndex = name.lastIndexOf( '.' );
			name = name.substring( 0, dotIndex ) + "_dark" + name.substring( dotIndex );
		}

		ClassLoader cl = (classLoader != null) ? classLoader : DFlatSVGIcon.class.getClassLoader();
		return cl.getResource( name );
	}

	/**
	 * Returns whether the SVG file was found.
	 *
	 * @return whether the SVG file was found
	 */
	public boolean hasFound() {
		update();
		return diagram != null;
	}

	/**
	 * Returns the scaled width of the icon.
	 */
	@Override
	public int getIconWidth() {
		if( width > 0 )
			return scaleSize( width );

		update();
		return scaleSize( (diagram != null) ? Math.round( diagram.getWidth() ) : 16 );
	}

	/**
	 * Returns the scaled height of the icon.
	 */
	@Override
	public int getIconHeight() {
		if( height > 0 )
			return scaleSize( height );

		update();
		return scaleSize( (diagram != null) ? Math.round( diagram.getHeight() ) : 16 );
	}

	private int scaleSize( int size ) {
		int scaledSize = UIScale.scale( size );
		if( scale != 1 )
			scaledSize = Math.round( scaledSize * scale );
		return scaledSize;
	}

	@Override
	public void paintIcon( Component c, Graphics g, int x, int y ) {
		update();

		// check whether icon is outside of clipping area
		Rectangle clipBounds = g.getClipBounds();
		if( clipBounds != null && !clipBounds.intersects( new Rectangle( x, y, getIconWidth(), getIconHeight() ) ) )
			return;

		// get gray filter
		RGBImageFilter grayFilter = null;
		if( disabled ) {
			Object grayFilterObj = UIManager.get( "Component.grayFilter" );
			grayFilter = (grayFilterObj instanceof RGBImageFilter)
				? (RGBImageFilter) grayFilterObj
				: GrayFilter.createDisabledIconFilter( dark );
		}

		Graphics2D g2 = new GraphicsFilter( (Graphics2D) g.create(), ColorFilter.getInstance(), grayFilter, this.userFilter );

		try {
			FlatUIUtils.setRenderingHints( g2 );
			g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );

			paintSvg( g2, x, y );
		} finally {
			g2.dispose();
		}
	}

	private void paintSvg( Graphics2D g, int x, int y ) {
		if( diagram == null ) {
			paintSvgError( g, x, y );
			return;
		}

		g.translate( x, y );
		g.clipRect( 0, 0, getIconWidth(), getIconHeight() );

		UIScale.scaleGraphics( g );
		if( width > 0 || height > 0 ) {
			double sx = (width > 0) ? width / diagram.getWidth() : 1;
			double sy = (height > 0) ? height / diagram.getHeight() : 1;
			if( sx != 1 || sy != 1 )
				g.scale( sx, sy );
		}
		if( scale != 1 )
			g.scale( scale, scale );

		diagram.setIgnoringClipHeuristic( true );

		try {
			diagram.render( g );
		} catch( SVGException ex ) {
			paintSvgError( g, 0, 0 );
		}
	}

	private void paintSvgError( Graphics2D g, int x, int y ) {
		g.setColor( Color.red );
		g.fillRect( x, y, getIconWidth(), getIconHeight() );
	}

	@Override
	public Image getImage() {
		update();

		// base size
		int iconWidth = getIconWidth();
		int iconHeight = getIconHeight();

		Dimension[] dimensions = new Dimension[] {
			new Dimension( iconWidth, iconHeight ),
			new Dimension( iconWidth * 2, iconHeight * 2 ),
		};

		Function<Dimension, Image> producer = size -> {
			BufferedImage image = new BufferedImage( size.width, size.height, BufferedImage.TYPE_INT_ARGB );
			Graphics2D g = image.createGraphics();
			try {
				// scale from base size to passed size
				double sx = (size.width > 0) ? (float) size.width / iconWidth : 1;
				double sy = (size.height > 0) ? (float) size.height / iconHeight : 1;
				if( sx != 1 || sy != 1 )
					g.scale( sx, sy );

				paintIcon( null, g, 0, 0 );
			} finally {
				g.dispose();
			}
			return image;
		};

		return MultiResolutionImageSupport.create( 0, dimensions, producer );
	}

	private static Boolean darkLaf;

	private static boolean isDarkLaf() {
		if( darkLaf == null ) {
			lafChanged();

			UIManager.addPropertyChangeListener( e -> {
				lafChanged();
			} );
		}

		return darkLaf;
	}

	private static void lafChanged() {
		darkLaf = FlatLaf.isLafDark();
	}

	//---- class ColorFilter --------------------------------------------------

	public static class ColorFilter
	{
		private static ColorFilter instance;

		private final Map<Integer, String> rgb2keyMap = new HashMap<>();
		private final Map<Color, Color> color2colorMap = new HashMap<>();

		public static ColorFilter getInstance() {
			if( instance == null )
				instance = new ColorFilter();
			return instance;
		}

		public ColorFilter() {
			for( FlatIconColors c : FlatIconColors.values() )
				rgb2keyMap.put( c.rgb, c.key );
		}

		public void addAll( Map<Color, Color> from2toMap ) {
			color2colorMap.putAll( from2toMap );
		}

		public void add( Color from, Color to ) {
			color2colorMap.put( from, to );
		}

		public void remove( Color from ) {
			color2colorMap.remove( from );
		}

		public Color filter( Color color ) {
			Color newColor = color2colorMap.get( color );
			if( newColor != null )
				return newColor;

			String colorKey = rgb2keyMap.get( color.getRGB() & 0xffffff );
			if( colorKey == null )
				return color;

			newColor = UIManager.getColor( colorKey );
			if( newColor == null )
				return color;

			return (newColor.getAlpha() != color.getAlpha())
				? new Color( (newColor.getRGB() & 0x00ffffff) | (color.getRGB() & 0xff000000) )
				: newColor;
		};
	}

	//---- class GraphicsFilter -----------------------------------------------

	private static class GraphicsFilter
		extends Graphics2DProxy
	{
		private final ColorFilter colorFilter;
		private final RGBImageFilter grayFilter;
		private final RGBImageFilter userFilter;

		public GraphicsFilter( Graphics2D delegate, ColorFilter colorFilter, RGBImageFilter grayFilter, RGBImageFilter userFilter) {
			super( delegate );
			this.colorFilter = colorFilter;
			this.grayFilter = grayFilter;
			this.userFilter = userFilter;
		}

		@Override
		public void setColor( Color c ) {
			super.setColor( filterColor( c ) );
		}

		@Override
		public void setPaint( Paint paint ) {
			if( paint instanceof Color )
				paint = filterColor( (Color) paint );
			super.setPaint( paint );
		}

		private Color filterColor( Color color ) {
			if( userFilter != null ) {
				int oldRGB = color.getRGB();
				int newRGB = userFilter.filterRGB( 0, 0, oldRGB );
				color = (newRGB != oldRGB) ? new Color( newRGB, true ) : color;
			}
			if( colorFilter != null )
				color = colorFilter.filter( color );
			if( grayFilter != null ) {
				int oldRGB = color.getRGB();
				int newRGB = grayFilter.filterRGB( 0, 0, oldRGB );
				color = (newRGB != oldRGB) ? new Color( newRGB, true ) : color;
			}
			return color;
		}
	}
}
