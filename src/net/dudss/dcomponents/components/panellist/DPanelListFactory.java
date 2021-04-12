package net.dudss.dcomponents.components.panellist;

import java.awt.Component;
import java.util.List;

import javax.swing.UIManager;

public class DPanelListFactory {
	public static <V, T extends DPanelListItem<V>> DPanelList<V, T> listDesign(List<V> objectList, Class<T> panelClass, DPanelList.SelectionMode selectionMode, boolean enableDragAndDrop, boolean paintBackgroundOnSelection, Component headerComponent) {
		DPanelList<V, T> dPanelList= new DPanelList<V, T>(objectList, panelClass, selectionMode, enableDragAndDrop, paintBackgroundOnSelection, true, headerComponent, 1, 0);
		dPanelList.setBackground(UIManager.getColor("List.background"));
		return dPanelList;
	}
	
	public static <V, T extends DPanelListItem<V>> DPanelList<V, T> defaultDesign(List<V> objectList, Class<T> panelClass, DPanelList.SelectionMode selectionMode, boolean enableDragAndDrop, boolean paintBackgroundOnSelection, Component headerComponent) {
		DPanelList<V, T> dPanelList= new DPanelList<V, T>(objectList, panelClass, selectionMode, enableDragAndDrop, paintBackgroundOnSelection, true, headerComponent);
		return dPanelList;
	}
}
