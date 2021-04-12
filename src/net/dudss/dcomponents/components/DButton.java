package net.dudss.dcomponents.components;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import net.dudss.dcomponents.data.Pair;

/**
 * A multi state button that can represent ongoing actions.
 * It was designed for informing the user that an action is currently happening.
 * An action in this context is a pair of String and Icon. The String describes the process that is currently ongoing
 * and the icon would usually be a spinner/throbber of some sort.
 * 
 * The button can hold information about multiple actions in its internal list.
 * Once informed that a particular action has ended. The button will either show the next one in the list
 * or revert back to its original text / icon.
 * 
 * While any actions are ongoing no action listeners get fired until done.
 * 
 * Original text / icon is whatever set by the setText() and setIcon().
 * 
 * @author DUDSS - 25.02.2021
 *
 */
public class DButton extends JButton {
	private LinkedList<Pair<String, Icon>> actionQueue = new LinkedList<>(); 
	private boolean showActionCount = false;
	
	String originalText;
	Icon originalIcon;
	
	public DButton() {
		super();
	}

	public DButton(Action a) {
		super(a);
	}

	public DButton(Icon icon) {
		super(icon);
	}

	public DButton(String text, Icon icon) {
		super(text, icon);
	}

	public DButton(String text) {
		super(text);
	}

	public void setShowActionCount(boolean b) {
		showActionCount = b;
	}
	
	public void queueAction(Pair<String, Icon> action) {
		actionQueue.add(action);
		updateActions();
	}
	
	public void endAction(Pair<String, Icon> action) {
		actionQueue.remove(action);
		updateActions();
	}
	
	@Override
	protected void fireActionPerformed(ActionEvent event) {
		if (actionQueue.isEmpty()) {
			super.fireActionPerformed(event);
		}
	}
	
	@Override
	public void setText(String text) {
		super.setText(text);
		originalText = text;
	}
	
	@Override
	public void setIcon(Icon defaultIcon) {
		super.setIcon(defaultIcon);
		originalIcon = defaultIcon;
	}
	
	private void setActionText(String text) {
		super.setText(text);
	}
	
	private void setActionIcon(Icon icon) {
		super.setIcon(icon);
	}
	
	private void updateActions() {
		if (actionQueue.isEmpty()) {
			this.setActionText(originalText);
			this.setActionIcon(originalIcon);
			return;
		}
		if (actionQueue.size() > 1 && showActionCount) {
			this.setActionText(actionQueue.getFirst().getL() + " (" + actionQueue.size() + ")");
			this.setActionIcon(actionQueue.getFirst().getR());
		} else {
			this.setActionText(actionQueue.getFirst().getL());
			this.setActionIcon(actionQueue.getFirst().getR());
		}
	}
}
