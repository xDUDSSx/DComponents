package net.dudss.dcomponents.components.sidebar;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.DefaultButtonModel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter;

import net.dudss.dcomponents.DUtils;

class DSideBarButton extends AbstractButton implements MouseListener {
	DSideBar sidebar;
	
	DefaultButtonModel model = new DefaultButtonModel();
	
	Action action;
	int width;
	int height;
	Insets margins;
	
	Dimension size;	
	Rectangle contentBounds;
	
	public DSideBarButton(Action action, Dimension size, Insets margins, DSideBar sidebar) {	
		this(action, (int) size.getWidth(), (int) size.getHeight(), margins, sidebar);
	}
	
	public DSideBarButton(Action action, int width, int height, Insets margins, DSideBar sidebar) {
		this.sidebar = sidebar;
		this.action = action;
		this.width = width;
		this.height = height;
		this.margins = margins;
		
		this.setModel(model);
		if (action != null) {
			this.addActionListener(action);
			this.setToolTipText((String) action.getValue(Action.NAME));
		}
		
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		addMouseListener(this);
		update();
	}

	@Override
	public String getName() {
		if (action == null || DUtils.isEmpty((String) action.getValue(Action.NAME))) {
			return super.getName();
		}
		return (String) action.getValue(Action.NAME);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		paintBody(g2d);
		paintIcon(g2d);
	}
	
	private void paintBody(Graphics2D g2d) {
		if (model.isPressed()) {
			g2d.setColor(sidebar.buttonPressedBackgroundColor);
		} else if (model.isRollover()) {
			g2d.setColor(sidebar.buttonBackgroundHoverColor);
		} else {
			g2d.setColor(sidebar.buttonBackgroundColor);
		}
		
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
	}
	
	private void paintError(Graphics2D g) {
		g.setColor(Color.red );
		g.fillRect(0, 0, getWidth(), getHeight());
	}
	
	private void paintIcon(Graphics2D g2d) {
		if (action == null) {
			paintError(g2d);
		}
		Object o = this.action.getValue(Action.SMALL_ICON);
		if (o != null && o instanceof FlatSVGIcon) {
			FlatSVGIcon icon = (FlatSVGIcon) o;
			icon.setColorFilter(new ColorFilter(color -> {
				if (model.isSelected()) {
					return sidebar.buttonActiveForegroundColor;
				}
				if (model.isRollover()) {
					return sidebar.buttonForegroundHoverColor;
				}
				return sidebar.buttonForegroundColor;
			}));
			Rectangle iconRect = DUtils.centerRectInRect(new Rectangle(0, 0, icon.getIconWidth(), icon.getIconHeight()), contentBounds);
			icon.paintIcon(this, g2d, iconRect.x, iconRect.y);
		} else {
			paintError(g2d);
		}
	}
	
	private void update() {
		this.size = new Dimension(width, height);
		this.contentBounds = new Rectangle(margins.left, margins.top, width - margins.left - margins.right, height - margins.top - margins.bottom);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return size;
	}
	
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		fireActionPerformed(new ActionEvent(this,  ActionEvent.ACTION_PERFORMED, getName(), System.currentTimeMillis(), 0));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		model.setPressed(true);
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		model.setPressed(false);
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		model.setRollover(true);
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		model.setRollover(false);
		repaint();
	}
}
