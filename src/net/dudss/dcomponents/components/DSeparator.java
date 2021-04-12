package net.dudss.dcomponents.components;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

public class DSeparator extends JPanel {
	public static final float DEFAULT_FONT_DELTA = 2f;
	public static final String DEFAULT_INSETS = "insets 0";
	public static final int SEPARATOR_MIN_HEIGHT = 2;
	
	public enum Style {
		VERTICAL,
		HORIZONTAL
	}
	
	private JLabel label;
	private JSeparator separator;
		
	/**
	 * @wbp.parser.constructor
	 */
	public DSeparator() {
		createVertical("Example", null, DEFAULT_FONT_DELTA, DEFAULT_INSETS);
	}
	
	public DSeparator(Style style, String title) {
		this(style, title, null);
	}
	
	public DSeparator(Style style, String title, Icon icon) {
		this(style, title, icon, DEFAULT_FONT_DELTA);
	}
	
	public DSeparator(Style style, String title, float deltaFont) {
		this(style, title, null, deltaFont, DEFAULT_INSETS);
	}
	
	public DSeparator(Style style, String title, Icon icon, float deltaFont) {
		this(style, title, icon, deltaFont, DEFAULT_INSETS);
	}
	
	public DSeparator(Style style, String title, Icon icon, float deltaFont, String insets) {
		switch (style) {
			case HORIZONTAL:
				createHorizontal(title, icon, deltaFont, insets);
				break;
			case VERTICAL:
			default:
				createVertical(title, icon, deltaFont, insets);        
				break;
		}
	}
	
	public static DSeparator createSmallVerticalSeparator(String title) {
		return new DSeparator(Style.VERTICAL, title, 0);
	}
	
	public static DSeparator createSmallHorizontalSeparator(String title) {
		return new DSeparator(Style.HORIZONTAL, title, 0);
	}
	
	private void createHorizontal(String title, Icon icon, float deltaFont, String insets) {
		if (insets == null) insets = "";
		setLayout(new MigLayout(insets, "[][grow]", "[]"));
		
		label = new JLabel(title);
		if (icon != null) label.setIcon(icon);
		if (deltaFont != 0) label.setFont(label.getFont().deriveFont(label.getFont().getSize() + deltaFont));
		add(label, "cell 0 0,alignx left");
		
		separator = new JSeparator();
		separator.setMinimumSize(new Dimension(separator.getMinimumSize().width, SEPARATOR_MIN_HEIGHT));
		add(separator, "cell 1 0,growx,aligny center");
	}
	
	private void createVertical(String title, Icon icon, float deltaFont, String insets) {
		if (insets == null) insets = "";
		setLayout(new MigLayout(insets, "[grow]", "[]2[]"));
		
		label = new JLabel(title);
		if (icon != null) label.setIcon(icon);
		if (deltaFont != 0) label.setFont(label.getFont().deriveFont(label.getFont().getSize() + deltaFont));
		add(label, "cell 0 0,alignx left");
		
		separator = new JSeparator();
		separator.setMinimumSize(new Dimension(separator.getMinimumSize().width, SEPARATOR_MIN_HEIGHT));
		add(separator, "cell 0 1,grow");
	}

}
