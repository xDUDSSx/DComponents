package net.dudss.dcomponents.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.jdesktop.swingx.JXTable;

import net.miginfocom.swing.MigLayout;

public class UIUtils {
	
	public static JButton createToolbarButton(Icon icon) {
		return createToolbarButton(null, icon, null);
	}
	
	public static JButton createToolbarButton(Icon icon, String tooltip) {
		return createToolbarButton(null, icon, tooltip);
	}
	
	public static JButton createToolbarButton(String text, Icon icon) {
		return createToolbarButton(text, icon, null);
	}
	
	public static JButton createToolbarButton(String text, Icon icon, String tooltip) {
		JButton button;
		if (text == null) {
			button = new JButton(icon);
		} else {
			button = new JButton(text, icon);
			trimMargins(button);
		}
			
		if (UIManager.getColor("Button.default.background") != null) {
			button.setBackground(new Color(UIManager.getColor("Button.default.background").getRGB()));
		}
		if (tooltip != null) button.setToolTipText(tooltip);
		return button;
	}
	
	public static JButton createToolbarButton(Action action) {
		JButton btn = new JButton(action);
		btn.setHideActionText(true);
		btn.setToolTipText((String) action.getValue(Action.NAME));
		return btn;
	}
	
	public static void trimMargins(JButton button) {
		button.putClientProperty("Button.margin", new Insets(2, 4, 2, 4));
		button.setMargin(new Insets(2, 4, 2, 4));
	}
	
	public static void trimMarginsMore(JButton button) {
		button.putClientProperty("Button.margin", new Insets(0, 2, 0, 2));
		button.setMargin(new Insets(0, 2, 0, 2));
	}
	
	public static JPanel createToolbarPanel() {
		return new JPanel(new MigLayout("insets n 0 n 0, gap 2"));
	}
	
	public static JButton createUndecoratedButton(String text, Icon icon) {
		JButton button = new JButton(text, icon);
		button.setBorderPainted(false);
		button.setBorder(null);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setContentAreaFilled(false);
		button.setIcon(icon);
		return button;
	}
	
	/**
	 * Creates a pop-up menu that selects a row in a table on a right-click.
	 * @param table
	 * @return
	 */
	public static JPopupMenu createTablePopupMenu(JTable table) {
		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						int rowAtPoint = table
								.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), table));
						if (rowAtPoint > -1) {
							table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
						}
					}
				});
			}
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {}
		});
		return popupMenu;
	}
	
	public static JButton createTablePackColumnsButton(JXTable table) {
		JButton tableButtonPack = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(UIUtils.class.getResource("/res/icons8-resize-horizontal-16.png"))));
        tableButtonPack.setPreferredSize(new Dimension(18, 18));
        tableButtonPack.setToolTipText("Auto resize columns");
        tableButtonPack.setFocusable(false);
		tableButtonPack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				table.packAll();
			}
		});
		return tableButtonPack;
	}

	public static void makeTableOpaque(boolean b, JTable table, JScrollPane scrollPane) {
		table.setOpaque(b);
		scrollPane.getViewport().setOpaque(b);
		scrollPane.setOpaque(b);
	}
}
