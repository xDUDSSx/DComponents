package net.dudss.dcomponents.components;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import net.dudss.dcomponents.DUtils;
import net.dudss.dcomponents.util.HtmlUtils;

/**
 * Just a simple {@linkplain JButton} that looks like a hyperlink.
 * Can optionally open a website in a default browser.
 */
public class DLinkButton extends JButton {
	public DLinkButton(String title, String url) {
		this(title);
		addActionListener(new WebsiteLoaderListener(url));
	}
	
	public DLinkButton(String title) {
		super(HtmlUtils.createLinkHtml(title));
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setBorderPainted(false);
		setContentAreaFilled(false);
		setFocusPainted(false);
	}
	
	public static class WebsiteLoaderListener implements ActionListener {
		private String url = "";
		
		public WebsiteLoaderListener(String url) {
			this.url = url;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			DUtils.openWebsite(url);
		}
	}
}