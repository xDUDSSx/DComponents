package net.dudss.dcomponents.components.panellist;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import net.dudss.dcomponents.misc.HSLColor;
import net.dudss.dcomponents.misc.ScrollablePanel;
import net.miginfocom.swing.MigLayout;

/**
 * A swing component which represents a {@link List} in the form of a graphical scroll-able list                             
 * similar to {@link JList} consisting of {@link JPanel}s stacked on top of each other.                                       
 * Each element is being represented by a single {@linkplain JPanel} and the element can be edited by user action             
 * defined by the {@linkplain JPanel}. The panel must be a subclass of {@link DPanelListItem}                          
 * which gives it a direct reference to the represented object that it can interact with.                                     
 * <br><br>                                                                                                                   
 * This component can also change the order of elements in the list by presenting a drag-and-drop interface to the user.      
 * The drag and drop functionality can be disabled.                                                                           
 * <br><br>                                                                                                                   
 * To ensure data integrity the {@link #DPanelList.refresh()} method should be called programmatically after            
 * the elements in the list change.
 * <br><br>                                                                                                      
 * <b>Note about concurrency:</b><br>
 * To avoid concurrency issues when the list is modified or iterated over on another thread, concurrency handling is required.
 * The ultimate method is to use a {@linkplain CopyOnWriteArrayList}.
 * You might want to use a {@linkplain Collections#synchronizedList(List)} or a {@linkplain Vector} to avoid issues during object refresh.
 * 
 * To avoid {@linkplain ConcurrentModificationException}s when drag and drop is enabled, manual synchronization when iterating over the list
 * might be necessary.
 *  
 * @author DUDSS - 22.02.2020
 *
 * @param <V> The type of the elements that make up the represented {@link List}
 * @param <T> The type of the {@link DPanelListItem} subclass that represents the elements graphically.
 */
public class DPanelList<V, T extends DPanelListItem<V>> extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private boolean debug = false;
	
	/**
	 * Selection mode of the component. 
	 * <br><br>
	 * UNSELECTION_ALLOWED - {@link DPanelList#getSelectedItem()} can return null.
	 * <br><br>
	 * SELECTION_FORCED - {@link DPanelList#getSelectedItem()} will never return null unless there are no elements in the list. 
	 * By default the first elements panel is selected.
 	 */
	public enum SelectionMode {
		UNSELECTION_ALLOWED, SELECTION_FORCED, UNSELECTION_FORCED,
		MULTI_SELECTION
	}
	
	enum DropStatus {
		ABOVE, BELOW, INVALID
	}
	
	private List<T> panels;
	private List<V> objects;
	
	private Class<T> panelClass;
	
	private boolean enableDragAndDrop = true;
	private boolean dragging = false;
	private T lastActivePanel = null;
	private T targetPanel = null;
	
	private List<T> lastSelectedPanels = new ArrayList<T>();
	private T panelToDeselectOnRelease = null;
	private T panelToSelectOnRelease = null;
	
	private Point mousePos;
	
	private JPanel innerPanel;
	
	Timer tweenTimer;
	
	public static int DEFAULT_GAP = 1;
	public static int DEFAULT_SIDEGAP = 6;
	public static int DEFAULT_SIDEGAP_BORDER_INSIDE = 0;
	
	private int gap;
	private int sideGap;
	private boolean paintBorderOutside = false;
	private int lastGap = 200;
	
	private double accelerationMax = 24d;
	private double accelerationMin = 0.2d;
	private double acceleration = 1.0;
	private int scrollDragStep = 2;
	private int scrollDragBoundsHeight = 80;
	private boolean scrollDown = false;
	private boolean scrollUp = false;

	Color defaultBackground = UIManager.getColor("Panel.background");
	Color dividerColor = UIManager.getColor("Separator.foreground");
	Color dropHighlight = UIManager.getColor("Table.dropLineColor");
	Color dropIndicator = new HSLColor(dropHighlight).adjustTone(50);
	Border highlightBorder = new MatteBorder(0, 6, 0, 6, dropHighlight);
	
	private SelectionMode selectionMode;
	private boolean paintBackgroundOnSelection = true;
	
	private ActionListener actionListener;
	private ActionListener structureListener;
	
	private static final String STRUCTURE_CHANGED = "StructureChanged";
	private static final String SELECTION_CHANGED = "SelectionChanged";
	
	/**
	 * A swing components which represents a {@link List} in the form of a graphical scroll-able list 
	 * similar to {@link JList} consisting of {@link JPanel}s stacked on top of each other.
	 * Each element is being represented by a single {@linkplain JPanel} and the element can be edited by user action
	 * defined by the {@linkplain JPanel}. The panel must be a subclass of {@link DPanelListItem}
	 * which gives it a direct reference to the represented object that it can interact with.
	 * <br><br>
	 * To ensure data integrity the {@link #DPanelList.refresh()} method should be called programmatically after
	 * the elements in the list change. This method is also called automatically when the user presses down the mouse button
	 * over the component.
	 *
	 * @param objectList The list that this component is representing and which elements will be passed to the individual item panels.
	 * @param panelClass The class of the custom {@link DPanelListItem} representing a single {@link objectList} element.
	 * @param selectionMode The selection mode of the component.
	 * 
	 * */
	public DPanelList(List<V> objectList, Class<T> panelClass, SelectionMode selectionMode) {
		this(objectList, panelClass, selectionMode, false, true, true, null, DEFAULT_GAP, DEFAULT_SIDEGAP);
	}
	
	/**
	 * A swing components which represents a {@link List} in the form of a graphical scroll-able list 
	 * similar to {@link JList} consisting of {@link JPanel}s stacked on top of each other.
	 * Each element is being represented by a single {@linkplain JPanel} and the element can be edited by user action
	 * defined by the {@linkplain JPanel}. The panel must be a subclass of {@link DPanelListItem}
	 * which gives it a direct reference to the represented object that it can interact with.
	 * <br><br>
	 * This component can also change the order of elements in the list by presenting a drag-and-drop interface to the user.
	 * The drag and drop functionality can be disabled.
	 * <br><br>
	 * To ensure data integrity the {@link #DPanelList.refresh()} method should be called programmatically after
	 * the elements in the list change. This method is also called automatically when the user presses down the mouse button
	 * over the component.
	 *
	 * @param objectList The list that this component is representing and which elements will be passed to the individual item panels.
	 * @param panelClass The class of the custom {@link DPanelListItem} representing a single {@link objectList} element.
	 * @param selectionMode The selection mode of the component.
	 * @param enableDragAndDrop Whether to enable the drag-and-drop functionality.
	 * @param paintBackgroundOnSelection Whether to set panels background color upon selection and de-selection.
	 * @param paintBorderOutside Whether to paint the selection border inside or outside of the item panels.
	 * @param headerComponent Optional header component of the {@link JScrollPane} column header.
	 * 
	 * */
	public DPanelList(List<V> objectList, Class<T> panelClass, SelectionMode selectionMode, boolean enableDragAndDrop, boolean paintBackgroundOnSelection, boolean paintBorderOutside, Component headerComponent) {
		this(objectList, panelClass, selectionMode, enableDragAndDrop, paintBackgroundOnSelection, paintBorderOutside, headerComponent, DEFAULT_GAP, paintBorderOutside ? DEFAULT_SIDEGAP : DEFAULT_SIDEGAP_BORDER_INSIDE);
	}
	
	/**
	 * A swing components which represents a {@link List} in the form of a graphical scroll-able list 
	 * similar to {@link JList} consisting of {@link JPanel}s stacked on top of each other.
	 * Each element is being represented by a single {@linkplain JPanel} and the element can be edited by user action
	 * defined by the {@linkplain JPanel}. The panel must be a subclass of {@link DPanelListItem}
	 * which gives it a direct reference to the represented object that it can interact with.
	 * <br><br>
	 * This component can also change the order of elements in the list by presenting a drag-and-drop interface to the user.
	 * The drag and drop functionality can be disabled.
	 * <br><br>
	 * To ensure data integrity the {@link #DPanelList.refresh()} method should be called programmatically after
	 * the elements in the list change. This method is also called automatically when the user presses down the mouse button
	 * over the component.
	 *
	 * @param objectList The list that this component is representing and which elements will be passed to the individual item panels.
	 * @param panelClass The class of the custom {@link DPanelListItem} representing a single {@link objectList} element.
	 * @param selectionMode The selection mode of the component.
	 * @param enableDragAndDrop Whether to enable the drag-and-drop functionality.
	 * @param paintBackgroundOnSelection Whether to set panels background color upon selection and de-selection.
	 * @param paintBorderOutside Whether to paint the selection border inside or outside of the item panels.
	 * @param headerComponent Optional header component of the {@link JScrollPane} column header.
	 * @param gap Sets the gap in-between item panels..
	 * @param sideGap Sets the left and right gap of item panels.
	 * 
	 * */
	public DPanelList(List<V> objectList, Class<T> panelClass, SelectionMode selectionMode, boolean enableDragAndDrop, boolean paintBackgroundOnSelection, boolean paintBorderOutside, Component headerComponent, int gap, int sideGap) {
		panels = new ArrayList<T>();		
		objects = objectList;
		this.panelClass = panelClass;
		this.selectionMode = selectionMode;
		this.enableDragAndDrop = enableDragAndDrop;		
		this.paintBackgroundOnSelection = paintBackgroundOnSelection;
		this.gap = gap;
		this.sideGap = sideGap;
		this.paintBorderOutside = paintBorderOutside;
		
		mousePos = new Point(0, 0);
		initUI();		
		
		if (headerComponent != null) {
			setColumnHeaderView(headerComponent);
			getColumnHeader().setBackground(defaultBackground);
		}
		
		if (objectList != null) {
			//Populate the component with current object list elements
			for (V object : objectList) {
				try {
					panels.add(panelClass.getDeclaredConstructor(object.getClass()).newInstance(object));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace(System.err);
				}
			}
		}
		
		if (selectionMode == SelectionMode.SELECTION_FORCED) {
			if (panels.size() > 0) {
				panels.get(0).select();
			}
		}
		
		if (selectionMode == SelectionMode.UNSELECTION_FORCED) {
			this.paintBackgroundOnSelection = false;
		}
		
		updateSelection();
		regenerateRows();
	}
	
	/**
	 * Sets the action listener that will get fired when selection changes.
	 * @param listener
	 */
	public void setSelectionListener(ActionListener listener) {
		this.actionListener = listener;
	}
	
	/**
	 * Sets the action listener fired upon panel refresh.
	 * @param listener
	 */
	public void setStructureListener(ActionListener listener) {
		this.structureListener = listener;
	}
	
	public void setPaintBackgroundOnSelection(boolean b) {
		paintBackgroundOnSelection = b;
	}
	
	public List<V> getList() {
		return objects;
	}
	
	public int getPanelCount() {
		return panels.size();
	}
	
	/**
	 * Sets the list of items the component should display and calls the {@link #refresh()} method.
	 * @param list
	 */
	public void setList(List<V> list) {
		this.objects = list;		
		refresh();
	}
	
	/**
	 * Sets the list of items the component should display and calls the {@link #refresh()} method.
	 * @param list
	 * @param structureChangedEvent Whether a structure changed event should be fired.
	 */
	public void setList(List<V> list, boolean structureChangedEvent) {
		this.objects = list;		
		refresh(structureChangedEvent);
	}
	
	/**
	 * Returns the currently selected element.
	 * @see {@link SelectionMode}
	 * @return
	 */
	public V getSelectedItem() {
		if (this.selectionMode == SelectionMode.UNSELECTION_FORCED) return null;
		if (lastSelectedPanels.size() > 0) {
			return lastSelectedPanels.get(0).object;
		}
		return null;
	}
	
	/**
	 * Returns all currently selected elements.
	 * @see {@link SelectionMode}
	 * @return A list with the selected elements. This list is empty if there is no selection.
	 */
	public List<V> getSelectedItems() {
		List<V> list = new ArrayList<V>();
 		if (this.selectionMode == SelectionMode.UNSELECTION_FORCED) return list;
		for (T p : lastSelectedPanels) {
			list.add(p.object);
		}
		return list;
	}
	
	public void deselectAll() {
		deselectAllPanels();
		refreshBackgrounds();
	}
	
	/**
	 * Updates the current panel list according to the referenced object list.
	 * This method ensures that the order and size of both lists is exactly the same.
	 * Panels get removed or created based on the list differences.
	 */
	public void refresh() {
		refresh(true);
	}
	
	/**
	 * Updates the current panel list according to the referenced object list.
	 * This method ensures that the order and size of both lists is exactly the same.
	 * Panels get removed or created based on the list differences.
	 * 
	 * @param fireListeners Whether to fire structure change listener or not.
	 */
	public void refresh(boolean fireListeners) {
		if (objects == null) {
			panels.clear();
			regenerateRows();
			if (structureListener != null && fireListeners) {
				structureListener.actionPerformed(generateEvent(STRUCTURE_CHANGED));
			}
			return;
		}
		
		synchronized (objects) {
			Iterator<V> itrObjects = objects.iterator();
			ListIterator<T> itrPanels = panels.listIterator();
			
			for (int i = 0; i < (objects.size() > panels.size() ? objects.size() : panels.size()); i++) {
				V obj;
				T panel;
				
				if (itrObjects.hasNext() && itrPanels.hasNext()) {
					obj = itrObjects.next();
					panel = itrPanels.next();
					
					if (panel.object == obj) {
						continue;
					} else {
						//Search for the object that is supposed to in this position
						boolean requiredPanelFound = false;
						for (int y = (i+1); y < panels.size(); y++) {
							if (panels.get(y).object == obj) {
								panels.set(i, panels.get(y));
								panels.set(y, panel);
								requiredPanelFound = true;
								break;
							} 
						}
						if (requiredPanelFound) continue;
						//An appropriate panel was not found, create a new instance						
						T newPanel;
						try {
							newPanel = panelClass.getDeclaredConstructor(obj.getClass()).newInstance(obj);
							itrPanels.previous();
							itrPanels.add(newPanel);
						} catch (Exception e) {
							e.printStackTrace(System.err);
						}
					}
				} else				
				if (itrObjects.hasNext() && !itrPanels.hasNext()) {
					//Creating new panels for the rest of the objects list
					for (int y = i; y < objects.size(); y++) {
						T newPanel;
						try {
							V objectAtY = objects.get(y);
							newPanel = panelClass.getDeclaredConstructor(objectAtY.getClass()).newInstance(objectAtY);
							panels.add(newPanel);
						} catch (Exception e) {
							e.printStackTrace(System.err);
						}					
					}
					break;
				} else 
				if (!itrObjects.hasNext() && itrPanels.hasNext()) {
					//Removing all remaining panels
					Iterator<T> removeIterator = panels.listIterator(i);
					while (removeIterator.hasNext()) {
						removeIterator.next();
						removeIterator.remove();
					}
					break;
				}
			}
		}
		
		updateSelection();
		if (lastSelectedPanels.isEmpty()) {
			if (selectionMode == SelectionMode.SELECTION_FORCED) {
				if (panels.size() > 0) {
					panels.get(0).select();
				}
			}
		}
		updateSelection();
		regenerateRows();
		if (structureListener != null && fireListeners) {
			structureListener.actionPerformed(generateEvent(STRUCTURE_CHANGED));
		}
	}
	
	private void initUI() {			
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setAutoscrolls(true);
		getVerticalScrollBar().setUnitIncrement(16);
		setScrollbarWidth(12);
		
		innerPanel = new CustomListInnerPanel();
		innerPanel.setBackground(defaultBackground);
		innerPanel.setLayout(new MigLayout("insets " + 0 + " " + sideGap + " " + 0 + " " + sideGap + ", gapy " + gap + "px", "[grow]"));

		setViewportView(innerPanel);
		setMinimumSize(new Dimension(1, 1));
		//add(scrollPane, "cell 0 0, grow, wmin 1"); //wmin 1 to fix mig layout shrinking issues when using text wrap components (eg. text area)

		if (enableDragAndDrop) {
			tweenTimer = new Timer(10, new ActionListener() {
			    @Override
				public void actionPerformed(ActionEvent evt) {
			    	if ((scrollDown || scrollUp) && !(scrollDown && scrollUp)) {
			    		JViewport viewPort = getViewport();
		                if (viewPort != null) {     	
		                	Rectangle viewRect = viewPort.getViewRect();
		                    if (scrollDown) {
		                    	viewRect.y = (int) (scrollDragStep * acceleration);
		                    } else {
		                    	viewRect.y = - (int) (scrollDragStep * acceleration);
		                    }
		                    getViewport().scrollRectToVisible(viewRect);
		                }
			    	}
			    }    
			});
			tweenTimer.start();
		}

		DPanelListMouseListener mouseListener = new DPanelListMouseListener();
		innerPanel.addMouseMotionListener(mouseListener);
		innerPanel.addMouseListener(mouseListener);
	}
	
	private DropStatus checkDropLocation() {
		if (dragging && lastActivePanel != null) {
			boolean droppedAbove = false;
			targetPanel = null;
			for (int i = 0; i < panels.size(); i++) {
				T panel = panels.get(i);
				//if (panel == lastActivePanel) continue;
				
				Rectangle panelRect = panel.getBounds();
				if (panelRect.contains(mousePos)) {
					Rectangle topHalf = new Rectangle((int) panelRect.getX(), (int) panelRect.getY(), (int) panelRect.getWidth(), (int) panelRect.getHeight()/2);
					if (topHalf.contains(mousePos)) {
						droppedAbove = true;
					} else {
						droppedAbove = false;
					}
					targetPanel = panel;
					break;
				} else {
					//Check gaps above and below the panel
					
					//Special case for the first panel
					if (i == 0) {
						if (getGapAboveFirstPanel(panel).contains(mousePos)) {
							if (lastActivePanel != panel) {
								droppedAbove = true;
								targetPanel = panel;
							}
							break;
						}
					}
					
					//Special case for the last panel
					if (i == panels.size() - 1) {
						if (getGapBelowLastPanel(panel).contains(mousePos)) {
							droppedAbove = false;
							targetPanel = panel;
							break;
						}
					}
					
					if (getGapBelowPanel(panel).contains(mousePos)) {
						droppedAbove = false;
						targetPanel = panel;
						break;
					}
				}
			}
			if (targetPanel != null) {
				if (droppedAbove) {
					return DropStatus.ABOVE;
				} else {
					return DropStatus.BELOW;
				}
			} else {
				return DropStatus.INVALID;
			}
		} else {
			return DropStatus.INVALID;
		}
	}
	
	private void moveRow(T targetPanel, T movedPanel, boolean above) {
		synchronized(objects) {
			int movedIndex = panels.indexOf(movedPanel);
			int targetIndex = panels.indexOf(targetPanel);
			
			if (debug) System.out.println("panel size: " + panels.size() + " objects size: " + objects.size() + " movedPanelIndex: " + movedIndex + " targetIndex: " + targetIndex + " before: " + above);
			
			if (targetIndex == -1 || targetPanel == movedPanel || targetIndex == movedIndex) {
				return;
			}
			
			panels.remove(movedPanel);
			V movedObject = objects.remove(movedIndex);
			
			if (movedIndex < targetIndex) {
				if (above) {
					panels.add(targetIndex - 1, movedPanel);
					objects.add(targetIndex - 1, movedObject);
				} else {
					panels.add(targetIndex, movedPanel);
					objects.add(targetIndex, movedObject);
				}
			} else {
				if (above) {
					panels.add(targetIndex, movedPanel);
					objects.add(targetIndex, movedObject);
				} else {
					panels.add(targetIndex + 1, movedPanel);
					objects.add(targetIndex + 1, movedObject);
				}
			}
		}
	}
	
	private void moveRows(T targetPanel, List<T> movedPanels, boolean above) {
		synchronized(objects) {
			if (above) {
				for (int i = 0; i < movedPanels.size(); i++) {
					moveRow(targetPanel, movedPanels.get(i), above);
				}	
			} else {
				for (int i = movedPanels.size()-1; i >= 0; i--) {
					moveRow(targetPanel, movedPanels.get(i), above);
				}
			}
		}
		regenerateRows();
		if (structureListener != null) {
			structureListener.actionPerformed(generateEvent(STRUCTURE_CHANGED));
		}
	}
	
	/**
	 * Call the {@link DPanelListItem#updateComponents(boolean)} method on all item panels.
	 */
	public void updatePanelComponents() {
		for (T panel : panels) {
			panel.updateComponents(panel.selected());
			panel.revalidate();
			panel.repaint();
		}
	}
	
	/*private void refreshListeners() {
		for (T panel : panels) {
			for (MouseMotionListener l : mouseListeners) {
				boolean listenerFound = false;
				for (int i = 0; i < panel.getMouseMotionListeners().length; i++) {
					if (panel.getMouseMotionListeners()[i] == l) {
						listenerFound = true;
						break;
					}
				}
				
				if (!listenerFound) {
					panel.addMouseMotionListener(l);
				}
			}
		}
	}*/
	
	/**
	 * Regenerates the entire layout (Time expensive)
	 */
	private void regenerateRows() {
		innerPanel.removeAll();
		for (T panel : panels) {
			innerPanel.add(panel, "wrap, grow, wmin 1"); //wmin 1 to fix mig layout shrinking issues when using text wrap components (eg. text area)
			if (paintBackgroundOnSelection) {
				if (!panel.selected()) {
					panel.setBackground(panel.getBackgroundColor());	
					if (!paintBorderOutside) {
						panel.setBorder(null);
					}
				} else {
					panel.setBackground(panel.getSelectionBackgroundColor());
					if (!paintBorderOutside) {
						panel.setBorder(highlightBorder);
					}
				}
			}
			panel.updateComponents(panel.selected());
			//panel.revalidate();
			//panel.repaint();
		}
		revalidateAndRepaint();
	}
	
	/**
	 * Updates components and backgrounds of panels.
	 */
	@SuppressWarnings("unused")
	private void refreshRows() {
		for (T panel : panels) {
			if (paintBackgroundOnSelection) {
				if (!panel.selected()) {
					panel.setBackground(panel.getBackgroundColor());	
					if (!paintBorderOutside) {
						panel.setBorder(null);
					}
				} else {
					panel.setBackground(panel.getSelectionBackgroundColor());
					if (!paintBorderOutside) {
						panel.setBorder(highlightBorder);
					}
				}
			}
			panel.updateComponents(panel.selected());
			//panel.revalidate();
			//panel.repaint();
		}
		revalidateAndRepaint();
	}
	
	/**
	 * Updates backgrounds of panels that need an update.
	 */
	private void refreshBackgrounds() {
		for (T panel : panels) {
			if (paintBackgroundOnSelection) {
				if (!panel.selected()) {
					panel.setBackground(panel.getBackgroundColor());	
					if (!paintBorderOutside) {
						panel.setBorder(null);
					}
				} else {
					panel.setBackground(panel.getSelectionBackgroundColor());
					if (!paintBorderOutside) {
						panel.setBorder(highlightBorder);
					}
				}
			}
			//panel.revalidate();
			//panel.repaint();
		}
	}
	
	@SuppressWarnings("unused")
	private void revalidateAndRepaintRows() {
		for (T panel : panels) {
			panel.updateComponents(panel.selected());
			panel.revalidate();
			panel.repaint();
		}
	}
	
	private void revalidateAndRepaint() {
		this.revalidate();
		this.repaint();
		innerPanel.revalidate();
		innerPanel.repaint();
	}
	
	/**
	 * Un-selects all panels.
	 */
	private void deselectAllPanels() {
		for (T panel : panels) {
			panel.unselect();
		}
	}
	
	private List<T> updateSelection() {
		lastSelectedPanels.clear();
		for (T panel : panels) {
			if (panel.selected()) {
				lastSelectedPanels.add(panel);
			}
		}
		return lastSelectedPanels;
	}
	
	private void fireSelectionListener() {
		if (actionListener != null) {
			actionListener.actionPerformed(generateEvent(SELECTION_CHANGED));
		}
	}
	
	private ActionEvent generateEvent(String command) {
		return new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command, System.currentTimeMillis(), 0);
	}
	
	class DPanelListMouseListener extends MouseAdapter {
		private void singleSelection(T panel) {
			deselectAllPanels();
			panel.select();
			lastActivePanel = panel;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				refresh();
			}
			if (e.getButton() != MouseEvent.BUTTON1) {
				return;
			}
			boolean selectionChanged = false;
			boolean ctrlDown = e.isControlDown();
			boolean shiftDown = e.isShiftDown();
			
			mousePos.setLocation(e.getX(), e.getY());
			boolean panelFound = false;

			for (T panel : panels) {
				Rectangle panelRect = panel.getBounds();
				if (panelRect.contains(mousePos)) {
					//Clicked on a panel

					if (selectionMode == SelectionMode.MULTI_SELECTION && (ctrlDown || shiftDown)) {
						if (ctrlDown && shiftDown) {
							if (lastActivePanel == null) {
								singleSelection(panel);
								selectionChanged = true;
							} else {
								int lastIndex = panels.indexOf(lastActivePanel);
								int currentIndex = panels.indexOf(panel);
								if (lastIndex == currentIndex) {
									//Do nothing
								} else
								if (lastIndex > currentIndex) {
									for (int i = 0; i < (lastIndex - currentIndex); i++) {
										panels.get(currentIndex + i).select();
									}
									lastActivePanel.select();
									selectionChanged = true;
								} else {
									for (int i = 0; i < (currentIndex - lastIndex); i++) {
										panels.get(lastIndex + i).select();
									}
									panel.select();
									selectionChanged = true;
								}
							}
						} else
						if (ctrlDown) {
							if (!panel.selected()) {
								panel.select();
								selectionChanged = true;
							} else {
								panelToDeselectOnRelease = panel;
							}
							lastActivePanel = panel;
						} else 
						if (shiftDown) {
							if (lastActivePanel == null) {
								singleSelection(panel);
								selectionChanged = true;
							} else {
								int lastIndex = panels.indexOf(lastActivePanel);
								int currentIndex = panels.indexOf(panel);
								if (lastIndex == currentIndex) {
									//Do nothing
								} else
								if (lastIndex > currentIndex) {
									deselectAllPanels();
									for (int i = 0; i < (lastIndex - currentIndex); i++) {
										panels.get(currentIndex + i).select();
									}
									lastActivePanel.select();
									selectionChanged = true;
								} else {
									deselectAllPanels();
									for (int i = 0; i < (currentIndex - lastIndex); i++) {
										panels.get(lastIndex + i).select();
									}
									panel.select();
									selectionChanged = true;
								}
							}
						}
					} else {
						//If panel is already selected, the selection should apply on mouse release
						if (panel.selected()) {
							panelToSelectOnRelease = panel;
						} else
						//Regular left click
						if (!panel.selected() || lastSelectedPanels.size() > 1) {
							singleSelection(panel);
							selectionChanged = true;
						}
					}
					panelFound = true;
				}
			}
			if (!panelFound) {
				if (selectionMode == SelectionMode.UNSELECTION_ALLOWED) {
					//if (selectedPanel != null) selectedPanel.setBackground(selectedPanel.getHighlightColor());
					deselectAllPanels();
					selectionChanged = true;
				}
			}
			if (debug) {
				for (T panel : panels) {
					if (panel.selected()) System.out.println("Selected panel " + panels.indexOf(panel));
				}
			}
			updateSelection();
			refreshBackgrounds();
			revalidateAndRepaint();
			
			if (selectionChanged) {
				fireSelectionListener();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (enableDragAndDrop) {
				if (dragging && lastActivePanel != null) {
					DropStatus dropStatus = checkDropLocation();
					switch (dropStatus) {
						case ABOVE:
							if (debug) System.out.println("Dropped above panel " + panels.indexOf(targetPanel));
							moveRows(targetPanel, lastSelectedPanels, true);
							if (actionListener != null) {
								actionListener.actionPerformed(generateEvent(SELECTION_CHANGED));
							}
							break;
						case BELOW:
							if (debug) System.out.println("Dropped below panel " + panels.indexOf(targetPanel));
							moveRows(targetPanel, lastSelectedPanels, false);
							if (actionListener != null) {
								actionListener.actionPerformed(generateEvent(SELECTION_CHANGED));
							}
							break;
						case INVALID:
							if (debug) System.out.println("Drop invalid");
							break;
					}
				} else {
					if (e.isControlDown() && panelToDeselectOnRelease != null) {
						panelToDeselectOnRelease.unselect();
						panelToDeselectOnRelease = null;
						updateSelection();
						refreshBackgrounds();
						fireSelectionListener();
					} else
					if (panelToSelectOnRelease != null) {
						singleSelection(panelToSelectOnRelease);
						panelToSelectOnRelease = null;
						updateSelection();
						refreshBackgrounds();
						fireSelectionListener();
					}
				}
				innerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				scrollDown = false;
				scrollUp = false;
				dragging = false;
				revalidateAndRepaint();
			}
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			mousePos.setLocation(e.getX(), e.getY());
			if (!dragging) {
				if (innerPanel.getCursor().getType() != Cursor.DEFAULT_CURSOR) {
					innerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			} else {
				if (innerPanel.getCursor().getType() != Cursor.MOVE_CURSOR) {
					innerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				}		
			}			
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			mousePos.setLocation(e.getX(), e.getY());
			if (enableDragAndDrop) {
				if (!SwingUtilities.isLeftMouseButton(e)) {
					return;
				}
				
				if (lastSelectedPanels.size() > 0) {
					if (innerPanel.getCursor().getType() != Cursor.MOVE_CURSOR) {
						innerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					}		
					
					if (lastActivePanel != null && !lastActivePanel.getBounds().contains(mousePos)) {
						if (debug) System.out.println("Dragging panel " + panels.indexOf(lastActivePanel));
						dragging = true;
					} else {
						dragging = false;
					}
				}
				
				Rectangle lowerBoundsRect = getLowerScrollBounds();
				if (lowerBoundsRect != null && lowerBoundsRect.contains(mousePos)) {
					if (debug) System.out.println("Dragged mouse entered lower scroll bounds!");
					
					int mY = mousePos.y;
					int bY = lowerBoundsRect.y;
					int bH = lowerBoundsRect.height;
					if (debug) System.out.println("mouseY: " + mY + " boundsY: " + bY + " boundsHeight: " + bH);
					double factor = 1.0d - (((bY + bH) - mY)/(double) bH);
					factor = easeOutExpoReversed(factor);
					acceleration = range(factor, 0.0, 1.0, accelerationMin, accelerationMax);
					if (debug) System.out.println("Acceleration: " + factor + " = " + acceleration);
					
					scrollDown = true;
				} else {
					scrollDown = false;
				}
				
				Rectangle upperBoundsRect = getUpperScrollBounds();
				if (upperBoundsRect != null && upperBoundsRect.contains(mousePos)) {
					if (debug) System.out.println("Dragged mouse entered upper scroll bounds!");
					
					int mY = mousePos.y;
					int bY = upperBoundsRect.y;
					int bH = upperBoundsRect.height;
					if (debug) System.out.println("mouseY: " + mY + " boundsY: " + bY + " boundsHeight: " + bH);
					
					double factor = (bH - (mY - bY)) / (double) bH;
					factor = easeOutExpoReversed(factor);
					acceleration = range(factor, 0.0, 1.0, accelerationMin, accelerationMax);
					if (debug) System.out.println("Acceleration: " + factor + " = " + acceleration);
					
					scrollUp = true;
				} else {
					scrollUp = false;
				}	
				
				revalidateAndRepaint();
			}
		}
		
		double easeOutExpoReversed(double v) {
			return Math.pow(2, -4 * (1 - v));
		}
	}
	
	private class CustomListInnerPanel extends ScrollablePanel {
		private static final long serialVersionUID = 1L;

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			//g2d.draw(new Line2D.Double(10.5, 10.5, 20.5, 20.5));
			
			if (dragging && lastActivePanel != null) {
				g2d.setColor(dropIndicator);
				for (JPanel panel : panels) {
					fillRect(getGapAbovePanel(panel), g2d);
					fillRect(getGapBelowPanel(panel), g2d);	
				}
			}
					
			for (int i = 0; i < panels.size(); i++) {
				T panel = panels.get(i);
				
				//Selection indicator
				if (paintBorderOutside) {
					if (selectionMode != SelectionMode.UNSELECTION_FORCED) {
						if (panel.selected()) {
							g2d.setColor(dropHighlight);
							Rectangle rect = panel.getBounds();
							rect.setBounds(rect.x - sideGap, rect.y, rect.width + sideGap *2, rect.height);
							fillRect(rect, g2d);
						}
					}
				}
				
				//Dividers
				g2d.setColor(dividerColor);				
				if (i != 0) {
					Rectangle rect = panel.getBounds();
					double off = gap / 2.0d;
					Line2D.Double line = new Line2D.Double(rect.x - sideGap, rect.y - off, sideGap + rect.width + sideGap, rect.y - off);
					g2d.draw(line);
				}
			}
			
			g2d.setColor(dropHighlight);
			DropStatus dropStatus = checkDropLocation();
			switch (dropStatus) {
				case ABOVE:
					fillRect(getGapAbovePanel(targetPanel), g2d);
					break;
				case BELOW:
					fillRect(getGapBelowPanel(targetPanel), g2d);
					break;
				case INVALID:
					break;
			}
		}
		
		private void fillRect(Rectangle rect, Graphics2D g2d) {
			g2d.fillRect(rect.x, rect.y, rect.width, rect.height);
		}
	}
	
	private Rectangle getGapAboveFirstPanel(JPanel panel) {
		Rectangle bounds = panel.getBounds();
		return new Rectangle(bounds.x - sideGap, bounds.y - lastGap, bounds.width + sideGap*2, lastGap);
	}
	
	private Rectangle getGapAbovePanel(JPanel panel) {
		Rectangle bounds = panel.getBounds();
		return new Rectangle(bounds.x - sideGap, bounds.y - gap, bounds.width + sideGap*2, gap);
	}
	
	private Rectangle getGapBelowPanel(JPanel panel) {
		Rectangle bounds = panel.getBounds();
		return new Rectangle(bounds.x - sideGap, bounds.y + bounds.height, bounds.width + sideGap*2, gap);
	}
	
	private Rectangle getGapBelowLastPanel(JPanel panel) {
		Rectangle bounds = panel.getBounds();
		return new Rectangle(bounds.x - sideGap, bounds.y + bounds.height, bounds.width + sideGap*2, lastGap);
	}
	
	private Rectangle getUpperScrollBounds() {
		JViewport viewPort = getViewport();
        if (viewPort != null) {
        	Rectangle bounds = viewPort.getViewRect();
        	if (bounds.height/2 <= scrollDragBoundsHeight) {
        		return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height/2);
        	}
        	return new Rectangle(bounds.x, bounds.y, bounds.width, scrollDragBoundsHeight);
        }
		return null;			
	}
	
	private Rectangle getLowerScrollBounds() {
		JViewport viewPort = getViewport();
        if (viewPort != null) {
        	Rectangle bounds = viewPort.getViewRect();
        	if (bounds.height/2 <= scrollDragBoundsHeight) {
        		return new Rectangle(bounds.x, bounds.y + bounds.height/2, bounds.width, bounds.height/2);
        	}
        	return new Rectangle(bounds.x, bounds.y + bounds.height - scrollDragBoundsHeight, bounds.width, scrollDragBoundsHeight);
        }
		return null;	
	}
	
	/**
	 * Sets the drag and drop range of the top/bottom-most panels
	 * */
	public void setLastGap(int lastGap) {
		this.lastGap = lastGap;
	}
	
	public void scrollBottom() {
		getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
	}
	
	public void scrollTop() {
		getVerticalScrollBar().setValue(0);
	}	
	
	public void setScrollbarWidth(int size) {
		setVerticalScrollbarWidth(size);
		setHorizontalScrollbarWidth(size);
	}
	
	public void setVerticalScrollbarWidth(int size) {
		getVerticalScrollBar().setPreferredSize(new Dimension(size, 0));
	}
	
	public void setHorizontalScrollbarWidth(int size) {
		getHorizontalScrollBar().setPreferredSize(new Dimension(0, size));
	}
	
	public int getScrollbarWidth() {
		return Math.max(getVerticalScrollbarWidth(), getHorizontalScrollbarWidth());
	}
	
	public int getVerticalScrollbarWidth() {
		return (int) getVerticalScrollBar().getPreferredSize().getWidth();
	}
	
	public int getHorizontalScrollbarWidth() {
		return (int) getHorizontalScrollBar().getPreferredSize().getHeight();
	}
	
	@Override
	public Dimension getMinimumSize() {
		Dimension d = super.getMinimumSize();
		Insets insets = this.getInsets();
		d.width = innerPanel.getPreferredSize().width + insets.left + insets.right + (this.getVerticalScrollBar().isVisible() ? this.getVerticalScrollbarWidth() : 0);
		return d;
	}
	
	/**Recalculates a float value into a different number range.
	 * @param OldValue The original value.
	 * @param OldMin The original lower limit of the number range.
	 * @param OldMax The original higher limit of the number range.
	 * @param NewMin The new lower limit of the number range.
 	 * @param NewMax The new higher limit of the number range.
	 */
	private static double range(double OldValue, double OldMin, double OldMax, double NewMin, double NewMax) {
		return (((OldValue - OldMin) * (NewMax - NewMin)) / (OldMax - OldMin)) + NewMin;
	}
	
	@Override
	public synchronized void addMouseMotionListener(MouseMotionListener l) {
		innerPanel.addMouseMotionListener(l);
	}
	
	@Override
	public synchronized void removeMouseMotionListener(MouseMotionListener l) {
		innerPanel.removeMouseMotionListener(l);
	}
	
	@Override
	public synchronized void addMouseListener(MouseListener l) {
		innerPanel.addMouseListener(l);
	}
	
	@Override
	public synchronized void removeMouseListener(MouseListener l) {
		innerPanel.removeMouseListener(l);
	}
	
	// SETTERS FOR UI STYLE / COLORS
	
	public void setHighlightBorder(Border b) {
		this.highlightBorder = b;
	}
	
	public void setBackground(Color c) {
		if (innerPanel != null) this.innerPanel.setBackground(c);
	}
	
	public void setSeparatorColor(Color c) {
		this.dividerColor = c;
		repaint();
	}
	
	public void setDropHighlight(Color c) {
		this.dropHighlight = c;
		repaint();
	}
	
	public void setDropIndicator(Color c) {
		this.dropIndicator = c;
		repaint();
	}
}
