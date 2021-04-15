package net.dudss.dcomponents.components.sidebar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.UIManager;

import net.dudss.dcomponents.DUtils;
import net.dudss.dcomponents.misc.ClickSensitivityControl;
import net.dudss.dcomponents.misc.NoneSelectedButtonGroup;
import net.miginfocom.swing.MigLayout;

/**
 * A component that acts mostly as a button bar and similarly to a tabbed pane.
 * Inspired by the Visual Studio Code left side bar.
 * 
 * The buttons are displayed only as Icons with tooltips.
 * Optionally the buttons can be exclusively toggled and draw a selection indicator.
 * 
 * For styling the component tries to use the default UIManager values.
 * The colors can be set individually using the color getters and setters.
 * 
 * Note: The component combines using other components and custom drawing.
 * It was not developed with compliance with the standard Swing architecture is does not follow
 * the usual MVC pattern.
 * 
 * @author DUDSS
 */
public class DSideBar extends JPanel {
	JPanel contentPanel;
	
	MouseAdapter mouseAdapter = new DButtonBarMouseAdapter();
	
	final int DEFAULT_BUTTON_SIZE = 48;
	final int DEFAULT_ICON_SIZE = 24;
	static int buttonGap = 0;
	
	Insets buttonInsets;
	Dimension iconSize = new Dimension(DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE);
	Dimension buttonSize = new Dimension(DEFAULT_BUTTON_SIZE, DEFAULT_BUTTON_SIZE);
	
	// Model
	ButtonGroup toggleButtonGroup = new NoneSelectedButtonGroup();
	
	// UI properties
	protected Color backgroundColor;
	protected Color foregroundColor;
	
	protected Color buttonBackgroundColor;
	protected Color buttonForegroundColor;
	
	protected Color buttonBackgroundHoverColor;
	protected Color buttonForegroundHoverColor;
	
	protected Color buttonPressedBackgroundColor;
	
	protected Color buttonActiveBackgroundColor;
	protected Color buttonActiveForegroundColor;
	
	public DSideBar() {
		this("insets 0, gap " + buttonGap);
	}
	
	public DSideBar(String constraints) {
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
		
		update();
		installDefaults();
		initUI(constraints);
		
		ClickSensitivityControl.install();
	}
	
	void initUI(String constraints) {
		contentPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(backgroundColor);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		
		this.setLayout(new BorderLayout());
		this.add(contentPanel, BorderLayout.CENTER);
		
		contentPanel.setLayout(new MigLayout(constraints));
	}
	
	public void setIconSize(int width, int height) {
		this.iconSize = new Dimension(width, height);
		update();
	}
	
	public void setButtonSize(int width, int height) {
		this.buttonSize = new Dimension(width, height);
		update();
	}
	
	private void update() {
		this.buttonInsets = new Insets(	buttonSize.height - iconSize.height,
										buttonSize.width - iconSize.width,
										buttonSize.height - iconSize.height,
										buttonSize.width - iconSize.width);
		revalidate();
		repaint();
	}
	
	/**
	 * Creates a regular button for the action.
	 * Action should have at least a NAME and SMALL_ICON values.
	 * 
	 * The SMALL_ICON should be a FlatSVGIcon.
	 * 
	 * @param action
	 */
	public void addButton(Action action) {
		contentPanel.add(new DSideBarButton(action, buttonSize, buttonInsets, this), "wrap");
	}
	
	/**
	 * Creates a toggle button that can be exclusively selected.
	 * Action should have at least a NAME and SMALL_ICON values.
	 * 
	 * The SMALL_ICON should be a FlatSVGIcon.
	 * 
	 * The button can be retrieved from the action events it fires and its
	 * state can be checked (using btn.isSelected())
	 * Example:
	 * <pre>AbstractButton btn = (AbstractButton) e.getSource();
boolean selected = btn.isSelected();</pre>
	 * @param action
	 */
	public void addTabButton(Action action) {
		addTabButton(action, false);
	}
	
	/**
	 * Creates a toggle button that can be exclusively selected.
	 * Action should have at least a NAME and SMALL_ICON values.
	 * 
	 * The SMALL_ICON should be a FlatSVGIcon.
	 * 
	 * The button can be retrieved from the action events it fires and its
	 * state can be checked (using btn.isSelected())
	 * Example:
	 * <pre>AbstractButton btn = (AbstractButton) e.getSource();
boolean selected = btn.isSelected();</pre>
	 * @param action
	 */
	public void addTabButton(Action action, boolean selected) {
		AbstractButton btn = new DSideBarToggleButton(action, buttonSize, buttonInsets, this);
		toggleButtonGroup.add(btn);
		btn.setSelected(selected);
		contentPanel.add(btn, "wrap");
	}
	
	/**
	 * Adds a fixed size empty space.
	 * @param height
	 */
	public void addSpace(int height) {
		contentPanel.add(createSpace(1, height), "wrap");
	}
	
	/**
	 * Adds an empty space that grows as much as it can.
	 */
	public void addPush() {
		contentPanel.add(createSpace(), "push, wrap");
	}
	
	private static Component createSpace() {
	    return createSpace(1, 1);
	}
	
	private static Component createSpace(int width, int height) {
	    return Box.createRigidArea(new Dimension(width, height));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(backgroundColor);
		g2d.fillRect(0, 0, getWidth(), getHeight());
	}
	
	class DButtonBarMouseAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		//return super.getPreferredSize();
		return new Dimension((int) buttonSize.getWidth(), (int) super.getPreferredSize().getHeight());
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
	public void updateUI() {
		super.updateUI();
		this.installDefaults();
		repaint();
	}
	
	void installDefaults() {
		backgroundColor = UIManager.getColor("MenuBar.background");
		foregroundColor = UIManager.getColor("Menu.disabledForeground");
		
		buttonBackgroundColor = backgroundColor;
		buttonForegroundColor = foregroundColor;
		
		buttonPressedBackgroundColor = buttonBackgroundColor;
		
		buttonActiveBackgroundColor = buttonBackgroundColor;
		buttonActiveForegroundColor = DUtils.brighter(UIManager.getColor("Menu.foreground"));
		
		buttonBackgroundHoverColor = buttonBackgroundColor;
		buttonForegroundHoverColor = buttonActiveForegroundColor;
	}
	
	void installDebug() {
		backgroundColor = UIManager.getColor("MenuBar.background");
		foregroundColor = UIManager.getColor("Menu.disabledForeground");
		
		buttonBackgroundColor = DUtils.darker(backgroundColor);
		buttonForegroundColor =	DUtils.darker(foregroundColor);
		
		buttonPressedBackgroundColor =  Color.RED;
		
		buttonActiveBackgroundColor = Color.BLACK;
		buttonActiveForegroundColor = DUtils.brighter(UIManager.getColor("Menu.foreground"));
		
		buttonBackgroundHoverColor = Color.CYAN;
		buttonForegroundHoverColor = Color.DARK_GRAY;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getForegroundColor() {
		return foregroundColor;
	}

	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	public Color getButtonBackgroundColor() {
		return buttonBackgroundColor;
	}

	public void setButtonBackgroundColor(Color buttonBackgroundColor) {
		this.buttonBackgroundColor = buttonBackgroundColor;
	}

	public Color getButtonForegroundColor() {
		return buttonForegroundColor;
	}

	public void setButtonForegroundColor(Color buttonForegroundColor) {
		this.buttonForegroundColor = buttonForegroundColor;
	}

	public Color getButtonBackgroundHoverColor() {
		return buttonBackgroundHoverColor;
	}

	public void setButtonBackgroundHoverColor(Color buttonBackgroundHoverColor) {
		this.buttonBackgroundHoverColor = buttonBackgroundHoverColor;
	}

	public Color getButtonForegroundHoverColor() {
		return buttonForegroundHoverColor;
	}

	public void setButtonForegroundHoverColor(Color buttonForegroundHoverColor) {
		this.buttonForegroundHoverColor = buttonForegroundHoverColor;
	}

	public Color getButtonPressedBackgroundColor() {
		return buttonPressedBackgroundColor;
	}

	public void setButtonPressedBackgroundColor(Color buttonPressedBackgroundColor) {
		this.buttonPressedBackgroundColor = buttonPressedBackgroundColor;
	}

	public Color getButtonActiveBackgroundColor() {
		return buttonActiveBackgroundColor;
	}

	public void setButtonActiveBackgroundColor(Color buttonActiveBackgroundColor) {
		this.buttonActiveBackgroundColor = buttonActiveBackgroundColor;
	}

	public Color getButtonActiveForegroundColor() {
		return buttonActiveForegroundColor;
	}

	public void setButtonActiveForegroundColor(Color buttonActiveForegroundColor) {
		this.buttonActiveForegroundColor = buttonActiveForegroundColor;
	}
}
