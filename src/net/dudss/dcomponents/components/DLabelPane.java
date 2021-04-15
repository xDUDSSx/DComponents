package net.dudss.dcomponents.components;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

public class DLabelPane extends JScrollPane {
	private static final long serialVersionUID = 1L;
	
	private JTextPane area;
	
	public DLabelPane() {
		this("");
	}
	
	public DLabelPane(String text) {
		create();
		setText(text);
	}
	
	private void create() {
		area = new JTextPane();
		area.setEditable(false);
		area.setBorder(BorderFactory.createEmptyBorder());
		area.setCursor(null);  
		area.setOpaque(false);  
		area.setFocusable(true);
		area.setFont(UIManager.getFont("Label.font"));
		area.setAlignmentY(Component.TOP_ALIGNMENT);
		area.setContentType("text/html");
		
		setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		setViewportView(area);
		getViewport().setOpaque(false);
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder());
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setMinimumSize(new Dimension(1, area.getMinimumSize().height));
		
		putClientProperty("JScrollBar.showButtons", false);
	}
	
	public void setText(String s) {
		area.setText(s);
	}
	
	@Override
	public void setFocusable(boolean bool) {
		area.setFocusable(bool);
	}
	
	public JTextPane getTextPane() {
		return area;
	}
}

