package net.dudss.dcomponents.components.panellist;

import java.awt.Color;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * {@link JPanel} that represents and has a direct reference to an object within a {@link DPanelList} component.
 * @see {@link DPanelList}
 * @author DUDSS
 *
 * @param <V> The type of the elements that make up the represented {@link List}.
 */
public abstract class DPanelListItem<V> extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Referenced object this panel is representing within the {@link DPanelList} component.
	 */
	protected V object;
	
	/**
	 * Flag used to determine whether this panel is part of the current {@link DPanelList} selection.
	 */
	private boolean selected = false;
	
	/**
	 * Creates a new panel list item that will represent the specified object.
	 * @param object Reference to an object it is representing within the {@link DPanelList} component.
	 */
	public DPanelListItem(V object) {
		this.object = object;
	}
	
	/**
	 * Method called upon panel list refresh to update panel information.
	 * @param selected Whether this panel is in the current {@link DPanelList}s selection.
	 */
	public abstract void updateComponents(boolean selected);
	
	
	/**
	 * The background color the panel should be set to. (Unless it is selected and {@link DPanelList#setPaintBackgroundOnSelection(boolean)} was set to true)
	 */
	public Color getBackgroundColor() {
		return UIManager.getColor("Panel.background");
	}
	
	/**
	 * The color the panel's background will be set to upon selection (if highlighting is on)
	 */
	public Color getHighlightColor() {
		return new Color(255, 221, 153);
	}
	
	void select() {this.selected = true;}
	void unselect() {this.selected = false;}
	boolean selected() {return this.selected;}
}
