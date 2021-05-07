package net.dudss.dcomponents.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

public class DMultilineLabel extends JScrollPane {
	private static final long serialVersionUID = 1L;
	
	protected JTextArea area;
	
	public DMultilineLabel() {
		this("");
	}
	
	public DMultilineLabel(int maxRows) {
		this("", maxRows);
	}
	
	public DMultilineLabel(String text) {
		this(text, 0);
	}
	
	public DMultilineLabel(String text, int maxRows) {
		create();
		setText(text);
		if (maxRows != 0) area.setRows(maxRows);
	}
	
	private void create() {
		area = new JTextArea() {
			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getMinimumSize() {
				Dimension d = super.getMinimumSize();
				d.height = Math.min(d.height, DMultilineLabel.this.getMaximumSize().height);
				return d;
			}
			
			@Override
			public Dimension getPreferredSize() {
				return super.getMinimumSize();
			}
			
			@Override
			public Dimension getMaximumSize() {
				return super.getPreferredSize();
			}
		};
		area.setEditable(false);
		area.setBorder(BorderFactory.createEmptyBorder());
		area.setCursor(null);  
		area.setOpaque(false);  
		area.setFocusable(true);
		area.setFont(UIManager.getFont("Label.font"));      
		area.setWrapStyleWord(true);  
		area.setLineWrap(true);
		area.setAlignmentY(Component.TOP_ALIGNMENT);
		
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
	
	public void setColor(Color c) {
		area.setForeground(c);
	}
	
	public void setColor(String c) {
		setColor(Color.decode(c));
	}
	
	public JTextArea getTextArea() {
		return area;
	}
	
	@Override
    public Dimension getPreferredSize() {
		//System.out.println("Pref " + area.getMinimumSize());
		return area.getMinimumSize();
    }
	
	@Override
	public Dimension getMaximumSize() {
		Dimension d = area.getMaximumSize();
        Insets insets = area.getInsets();

        if (area.getRows() != 0) {
        	d.height = area.getRows() * getRowHeight() + insets.top + insets.bottom;
        }
        //System.out.println("Max " + d);
        return d;
	}
	
	public int getRowHeight() {
		FontMetrics metrics = area.getFontMetrics(area.getFont());
        return metrics.getHeight();
	}
}
