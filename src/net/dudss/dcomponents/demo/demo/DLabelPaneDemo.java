package net.dudss.dcomponents.demo.demo;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.dudss.dcomponents.DUtils;
import net.dudss.dcomponents.components.DLabelPane;
import net.dudss.dcomponents.components.DSeparator;
import net.dudss.dcomponents.components.DSeparator.Style;
import net.miginfocom.swing.MigLayout;

public class DLabelPaneDemo extends JPanel {
	public DLabelPaneDemo() {
		setLayout(new MigLayout("", "[grow]", "[grow]"));
		JButton show = new JButton("Show DLabelPane");
		DSeparator sep = new DSeparator(Style.VERTICAL, "DLabelPane");
		add(sep, "wrap, grow");
		add(show);
		
		show.addActionListener((e) -> {
			JFrame frame = new JFrame();
			frame.setTitle("DLabelPane Demo");
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setSize(new Dimension(300, 300));
			frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[grow]"));
			frame.add(new DLabelPane("<p><b>Lorem ipsum dolor sit amet</b>, consectetur adipiscing <i>elit</i>, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in <u>voluptate velit esse cillum dolore eu fugiat nulla pariatur</u>. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
					+ "</p>"
					+ "Uicons by <a href=\"https://www.flaticon.com/uicons\">Flaticon</a>"), "cell 0 0, grow");
			frame.setVisible(true);
			DUtils.centerWindow(frame);
		});
	}
}
