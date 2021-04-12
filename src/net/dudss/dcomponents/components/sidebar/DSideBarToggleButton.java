package net.dudss.dcomponents.components.sidebar;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.ButtonGroup;

class DSideBarToggleButton extends DSideBarButton {
	protected final int SELECTION_INDICATOR_WIDTH = 2;
	
	public DSideBarToggleButton(Action action, Dimension size, Insets margins, DSideBar sidebar) {
		super(action, size, margins, sidebar);
	}
	
	public DSideBarToggleButton(Action action, int width, int height, Insets margins, DSideBar sidebar) {
		super(action, width, height, margins, sidebar);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		paintSelectionIndicator(g2d);
	}
	
	private void paintSelectionIndicator(Graphics2D g2d) {
		if (this.model.isSelected()) {
			g2d.setColor(sidebar.buttonActiveForegroundColor);
			g2d.fillRect(0, 0, SELECTION_INDICATOR_WIDTH, getHeight());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		setSelected(!model.isSelected());
		super.mouseClicked(e);
	}
	
	@Override
	public void setSelected(boolean b) {
		ButtonGroup group = model.getGroup();
        if (group != null) {
            // use the group model instead
            group.setSelected(model, b);
            b = group.isSelected(model);
        }
		super.setSelected(b);
	}
}
