package net.dudss.dcomponents.demo;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;

import net.dudss.dcomponents.components.sidebar.DSideBar;
import net.dudss.dcomponents.demo.demo.DButtonDemo;
import net.dudss.dcomponents.svg.DFlatSVGIcon;
import net.miginfocom.swing.MigLayout;

public class Demo extends JFrame {
	final int sidebarIconSize = 24;
	
	public Demo() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 600, 500);
		setTitle("DComponents Demo");
		
		initUI();
		
		FlatInspector.install("ctrl shift X");
		FlatUIDefaultsInspector.install("ctrl shift C");
	}
	
	public static void main(String[] args) {
		FlatDarkLaf.install();
		new Demo().setVisible(true);
	}
	
	private void initUI() {
		JPanel panel = new JPanel();
		setContentPane(panel);
		getContentPane().setLayout(new MigLayout("insets 0", "[]0[grow]", "[grow]"));
		JPanel rightPanel = new JPanel(new MigLayout("", "[grow]", "[][grow][]"));
		
		panel.add(createSideBar(), "cell 0 0, grow");
		panel.add(rightPanel, "cell 1 0, grow");
		
		rightPanel.add(new DButtonDemo(), "cell 0 0, grow");
		
		JButton btn = new JButton("Change theme");
		rightPanel.add(btn, "cell 0 2");
		btn.addActionListener((e) -> {
			FlatAnimatedLafChange.showSnapshot();

			try {
				if (UIManager.getLookAndFeel().getClass() == FlatLightLaf.class) {
					UIManager.setLookAndFeel(new FlatDarkLaf() );
					btn.setText("Light theme");
				} else {
					UIManager.setLookAndFeel(new FlatLightLaf() );
					btn.setText("Dark theme");
				}
			} catch( Exception ex ) {
			    System.err.println( "Failed to initialize LaF" );
			}
			
			// update all components
			FlatLaf.updateUI();
			FlatAnimatedLafChange.hideSnapshotWithAnimation();
		});
	}
	
	private DSideBar createSideBar() {
		DSideBar lblNewLabel = new DSideBar();
		lblNewLabel.addTabButton(new AbstractAction("Úvodse", new DFlatSVGIcon("res/info.svg", sidebarIconSize, sidebarIconSize)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				//System.out.println("Tab1 clicked!");
			}
		});
		lblNewLabel.addTabButton(new AbstractAction("Konfigurace", new DFlatSVGIcon("res/left_right_arrows.svg", sidebarIconSize, sidebarIconSize)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractButton btn = (AbstractButton) e.getSource();
				boolean selected = btn.isSelected();
				
				//System.out.println("Konfigurace clicked! " + btn.isSelected());
			}
		});
		lblNewLabel.addPush();
		lblNewLabel.addButton(new AbstractAction("Nastavení", new DFlatSVGIcon("res/settings.svg", sidebarIconSize, sidebarIconSize)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				//System.out.println("Nastaven� clicked!");
			}
		});
		return lblNewLabel;
	}
}
