package net.dudss.dcomponents.demo.demo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import net.dudss.dcomponents.components.DButton;
import net.dudss.dcomponents.components.DSeparator;
import net.dudss.dcomponents.components.DSeparator.Style;
import net.dudss.dcomponents.icons.AnimatedIcon;
import net.dudss.dcomponents.icons.RotatedIcon;
import net.dudss.dcomponents.workers.ButtonSwingWorker;
import net.miginfocom.swing.MigLayout;

public class DButtonDemo extends JPanel {
	static FlatSVGIcon spinner = new FlatSVGIcon("res/spinner2.svg", 16, 16);
	static List<RotatedIcon> spinnerAnim = generateSpinnerAnim();
	
	public DButtonDemo() {
		setLayout(new MigLayout("", "[grow]", "[][][]"));
		
		DSeparator sep = new DSeparator(Style.VERTICAL, "DButton");
		add(sep, "cell 0 0, grow");
		
		DButton btn1 = new DButton("Default");
		add(btn1, "cell 0 1");
		btn1.setShowActionCount(true); 
		btn1.setIcon(new FlatSVGIcon("res/ok.svg", 16, 16));
		
		JButton btn4 = new JButton("Action 3");
		add(btn4, "cell 0 2");
		btn4.addActionListener((e) -> {
			ButtonSwingWorker worker = new ButtonSwingWorker(btn1, createSpinnerIcon(btn1), "Action 3") {
				@Override
				protected void doInBackground() {
					longTask.run();
				}
			};
			worker.execute();
		});
		
		JButton btn3 = new JButton("Action 2");
		add(btn3, "cell 0 2");
		btn3.addActionListener((e) -> {
			ButtonSwingWorker worker = new ButtonSwingWorker(btn1, createSpinnerIcon(btn1), "Action 2") {
				@Override
				protected void doInBackground() {
					longTask.run();
				}
			};
			worker.execute();
		});
		
		JButton btn2 = new JButton("Action 1");
		add(btn2, "cell 0 2");
		btn2.addActionListener((e) -> {
			ButtonSwingWorker worker = new ButtonSwingWorker(btn1, createSpinnerIcon(btn1), "Action 1") {
				@Override
				protected void doInBackground() {
					longTask.run();
				}
			};
			worker.execute();
		});
		btn1.addActionListener((e) -> {
			System.out.println("Default button clicked!");
		});
	}
	
	Runnable longTask = () -> {
		System.out.println("Task started");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Task ended");
	};
	
	public static Icon createSpinnerIcon(JComponent comp) {
		AnimatedIcon icon = new AnimatedIcon(comp, 1000/24);
		for (Icon f : spinnerAnim) {
			icon.addIcon(f);
		}
		icon.start();
		return icon;
	}
	
	private static List<RotatedIcon> generateSpinnerAnim() {
		List<RotatedIcon> list = new ArrayList<>();
		for (int angle = 15; angle < 360; angle += 15)
		{
			list.add(new RotatedIcon(spinner, angle, true));
		}
		return list;
	}
}
