package net.dudss.dcomponents.demo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import net.dudss.dcomponents.misc.LayoutAnimator;

// Demo for the LayoutAnimator
public class LayoutAnimatorDemo
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI()
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a component where optimized drawing
        // is disabled, to avoid flickering when
        // components overlap
        JComponent c = new JComponent()
        {
            private static final long serialVersionUID =
                -8793865141504880212L;
            @Override
            public boolean isOptimizedDrawingEnabled()
            {
                return false;
            }
        };
        f.setContentPane(c);

        Container container = f.getContentPane();
        container.setLayout(new FlowLayout());

        // Create buttons to switch between layouts
        JButton c0 = new JButton("FlowLayout");
        JButton c1 = new JButton("GridLayout");
        JButton c2 = new JButton("BorderLayout");
        JButton c3 = new JButton("GridBagLayout");

        // Create a slider for the animation duration
        JComponent c4 = new JPanel(new BorderLayout());
        c4.add(new JLabel("Duration (ms) :"), BorderLayout.WEST);
        JSlider slider = new JSlider(0, 2000);
        slider.setMinimumSize(new Dimension(100, 100));
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(500);
        slider.setPaintLabels(true);
        c4.add(slider, BorderLayout.CENTER);
        BoundedRangeModel b = slider.getModel();

        // Attach ActionListeners to the buttons that perform
        // animations to the different layouts
        connect(c0, container, new FlowLayout(), b);
        connect(c1, container, new GridLayout(2,3), b);
        connect(c2, container, createBorderLayout(c0, c1, c2, c3, c4), b);
        connect(c3, container, createGridBagLayout(c0, c1, c2, c3, c4), b);

        container.add(c0);
        container.add(c1);
        container.add(c2);
        container.add(c3);
        container.add(c4);

        f.setSize(800, 600);
        f.setVisible(true);
    }

    // Attach an ActionListener to the given button that will animate
    // the contents of the given container towards the given layout,
    // with a duration (in milliseconds) that is taken from the
    // given BoundedRangeModel
    private static void connect(
        JButton button, final Container container,
        final LayoutManager layoutManager,
        final BoundedRangeModel boundedRangeModel)
    {
        button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                double durationS = boundedRangeModel.getValue() / 1000.0;
                LayoutAnimator.execute(container, layoutManager, durationS);
            }
        });
    }

    // Create a predefined BorderLayout
    private static LayoutManager createBorderLayout(
        Component c0, Component c1, Component c2, Component c3, Component c4)
    {
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.addLayoutComponent(c0, BorderLayout.NORTH);
        borderLayout.addLayoutComponent(c1, BorderLayout.CENTER);
        borderLayout.addLayoutComponent(c2, BorderLayout.SOUTH);
        borderLayout.addLayoutComponent(c3, BorderLayout.WEST);
        borderLayout.addLayoutComponent(c4, BorderLayout.EAST);
        return borderLayout;
    }

    // Create a predefined GridBagLayout
    private static LayoutManager createGridBagLayout(
        Component c0, Component c1, Component c2, Component c3, Component c4)
    {
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints c = null;

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        gridBagLayout.addLayoutComponent(c0, c);

        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.fill = GridBagConstraints.BOTH;
        gridBagLayout.addLayoutComponent(c1, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.25;
        c.weighty = 0.5;
        c.fill = GridBagConstraints.BOTH;
        gridBagLayout.addLayoutComponent(c2, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.75;
        c.weighty = 0.5;
        c.fill = GridBagConstraints.BOTH;
        gridBagLayout.addLayoutComponent(c3, c);

        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.fill = GridBagConstraints.BOTH;
        gridBagLayout.addLayoutComponent(c4, c);

        return gridBagLayout;
    }

}