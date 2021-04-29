package net.dudss.dcomponents.workers;

import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Icon;

import net.dudss.dcomponents.components.DButton;
import net.dudss.dcomponents.data.Pair;

/**
 * Swing worker that executes in the background while setting button icon to loading and overriding its text. 
 * After finishing it reverts the button back to its original state. The button is stripped of action listeners during execution.
 * 
 * Additionally the worker supports the multi-state DButton. DButton supports multiple workers at a time and is less buggy/hacky.
 * 
 * @author DUDSS
 */
public abstract class ButtonSwingWorker extends BetterSwingWorker {
	AbstractButton btn;
	String originalText;
	Icon originalIcon;
	ActionListener listeners[];
	
	//For DButton
	Pair<String, Icon> action;
	
	public ButtonSwingWorker(AbstractButton btn, Icon busyIcon, String processText) {
		this.btn = btn;
		
		if (btn instanceof DButton) {
			this.action = new Pair<String, Icon>(processText, busyIcon);
			((DButton) btn).queueAction(this.action);
		} else {
			originalText = btn.getText();
			originalIcon = btn.getIcon();
			
			listeners = btn.getActionListeners();
			for (int i = 0; i < listeners.length; i++) {
				btn.removeActionListener(listeners[i]);
			}
			btn.setIcon(busyIcon);
			btn.setText(processText);
		}
	}
	
	@Override
	abstract protected void doInBackground();
	
    @Override
	protected void done() {
    	if (btn instanceof DButton) {
    		((DButton) btn).endAction(action);
    	} else {
	    	btn.setText(originalText);
	    	btn.setIcon(originalIcon);
	    	for (int i = 0; i < listeners.length; i++) {
				btn.addActionListener(listeners[i]);
			}
    	}
    }
}