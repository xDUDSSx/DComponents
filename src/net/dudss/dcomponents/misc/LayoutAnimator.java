package net.dudss.dcomponents.misc;

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.swing.Timer;

/**
 * A class that may perform animations between different layouts for containers.
 * Source: https://stackoverflow.com/questions/22135477/resizing-jpanels-with-animation
 */
public class LayoutAnimator {
	public static enum Tween {
		LINEAR, EASE_IN_OUT_QUAD, EASE_IN_OUT_SINE
	}

	/**
	 * The map from containers to {@link LayoutAnimation LayoutAnimations} that are
	 * currently running.
	 */
	private static final Map<Container, LayoutAnimation> running = new IdentityHashMap<Container, LayoutAnimation>();

	/**
	 * Execute the transition between the current layout of the given container to
	 * the new layout. This method will animate the contents of the given container,
	 * starting at its current state, towards the state that is defined by setting
	 * the given layout to the given container. The duration for the animation (in
	 * seconds) may be specified. After the animation has finished, the container
	 * will have the given layout.
	 *
	 * @param container The container
	 * @param newLayout The new layout
	 * @param durationS The duration, in seconds.
	 */
	public static synchronized void execute(Container container, LayoutManager newLayout, double durationS) {
		execute(container, newLayout, durationS, Tween.EASE_IN_OUT_QUAD);
	}
	
	/**
	 * Execute the transition between the current layout of the given container to
	 * the new layout. This method will animate the contents of the given container,
	 * starting at its current state, towards the state that is defined by setting
	 * the given layout to the given container. The duration for the animation (in
	 * seconds) may be specified. After the animation has finished, the container
	 * will have the given layout.
	 *
	 * @param container The container
	 * @param newLayout The new layout
	 * @param durationS The duration, in seconds.
	 * @param tween The easing function used for the animation.
	 */
	public static synchronized void execute(Container container, LayoutManager newLayout, double durationS, Tween tween) {
		execute(container, newLayout, durationS, tween, null);
	}
	
	/**
	 * Execute the transition between the current layout of the given container to
	 * the new layout. This method will animate the contents of the given container,
	 * starting at its current state, towards the state that is defined by setting
	 * the given layout to the given container. The duration for the animation (in
	 * seconds) may be specified. After the animation has finished, the container
	 * will have the given layout.
	 *
	 * @param container The container
	 * @param newLayout The new layout
	 * @param durationS The duration, in seconds.
	 * @param tween The easing function used for the animation.
	 * @param animCallback Callback for various layout animation states.
	 */
	public static synchronized void execute(Container container, LayoutManager newLayout, double durationS, Tween tween, LayoutAnimationCallback animCallback) {
		// If there is already a LayoutAnimation running for the
		// container, cancel it and remove it from the map
		LayoutAnimation runningLayoutAnimtion = running.get(container);
		if (runningLayoutAnimtion != null) {
			runningLayoutAnimtion.cancel();
			running.remove(container);
		}

		// Execute the layout animation. When it is finished,
		// the callback will remove it from the map of
		// running layout animations
		final LayoutAnimation layoutAnimtion = new LayoutAnimation(container, newLayout);
		running.put(container, layoutAnimtion);
		layoutAnimtion.execute(durationS, new LayoutAnimationCallback() {
			@Override
			public void animationFinished() {
				running.remove(layoutAnimtion);
			}
		}, animCallback, tween);
	}
	
	/**
	 * Interface adapter for classes that may be called when a {@link LayoutAnimation} is running.
	 */
	public static abstract class LayoutAnimationCallback {
		/**
		 * Will be called when the {@link LayoutAnimation} is finished
		 */
		public void animationFinished() {}
		
		/**
		 * Called on each frame of the animation.
		 */
		public void animationUpdated() {}
	}

	/**
	 * A layout animation. This class performs the animation between an initial
	 * state of a container, towards the state that is defined by applying a new
	 * layout to the container.
	 */
	private static class LayoutAnimation {
		/**
		 * The container on which the animation is performed
		 */
		private final Container container;

		/**
		 * The new layout towards which the container is animated
		 */
		private final LayoutManager newLayout;

		/**
		 * The timer that performs the actual layout
		 */
		private final Timer timer;

		/**
		 * The delay for the timer
		 */
		private final int delayMS = (int) 1000f/60;

		/**
		 * Creates a new LayoutAnimation for the given container, which animates towards
		 * the given layout.
		 *
		 * @param container The container
		 * @param newLayout The new layout
		 */
		LayoutAnimation(Container container, LayoutManager newLayout) {
			this.container = container;
			this.newLayout = newLayout;
			this.timer = new Timer(delayMS, null);
		}

		/**
		 * Execute the animation. This will store the current state of the container,
		 * compute the target state based on the new layout, and perform an animation
		 * towards the new state that will take the specified duration (in seconds).
		 * When the animation is finished, the given callback will be notified.
		 *
		 * @param durationS              The duration for the animation, in seconds
		 * @param layoutAnimatorCallback The callback that will be notified when the
		 *                               animation is finished.
		 */
		void execute(final double durationS, final LayoutAnimationCallback layoutAnimatorCallback, LayoutAnimationCallback userLayoutAnimationCallback, Tween tween) {

			// Store all old bounds of the components of the container
			final Map<Component, Rectangle> oldBounds = getAllBounds(container.getComponents());

			// Apply the new layout, and store the new bounds
			// of all components
			container.setLayout(newLayout);
			newLayout.layoutContainer(container);
			final Map<Component, Rectangle> newBounds = getAllBounds(container.getComponents());

			// Restore the old bounds
			container.setLayout(null);
			setAllBounds(container.getComponents(), oldBounds);

			// Create the bounds that will be animated
			final Map<Component, Rectangle> currentBounds = getAllBounds(container.getComponents());

			// Set up the timer that will perform the animation
			timer.addActionListener(new ActionListener() {
				/**
				 * The current alpha value decribing the interpolation state, between 0 and 1
				 */
				double alpha = 0;

				/**
				 * The step size for the alpha.
				 */
				double alphaStep = 1.0 / (durationS * (1000.0 / delayMS));

				@Override
				public void actionPerformed(ActionEvent e) {
					if (alpha == 1.0) {
						timer.stop();
						container.setLayout(newLayout);
						layoutAnimatorCallback.animationFinished();
						if (userLayoutAnimationCallback != null) userLayoutAnimationCallback.animationFinished();
					}
					alpha += alphaStep;
					alpha = Math.min(1.0, alpha);
					
					if (userLayoutAnimationCallback != null) userLayoutAnimationCallback.animationUpdated();
					interpolate(oldBounds, newBounds, currentBounds, alpha, tween);
					setAllBounds(container.getComponents(), currentBounds);
				}
			});
			timer.setCoalesce(true);
			timer.start();
		}

		/**
		 * Cancel this animation
		 */
		void cancel() {
			timer.stop();
		}
	}

	/**
	 * Create a map from the given components to their bounds.
	 *
	 * @param components The components
	 * @return The resulting map
	 */
	private static Map<Component, Rectangle> getAllBounds(Component components[]) {
		Map<Component, Rectangle> currentBounds = new HashMap<Component, Rectangle>();
		for (Component component : components) {
			Rectangle bounds = component.getBounds();
			currentBounds.put(component, bounds);
		}
		return currentBounds;
	}

	/**
	 * Set the bounds of the given components to the bounds that are stored in the
	 * given map.
	 *
	 * @param components The components
	 * @param newBounds  The new bounds of the components
	 */
	private static void setAllBounds(Component components[], Map<Component, Rectangle> newBounds) {
		for (Component component : components) {
			Rectangle bounds = newBounds.get(component);
			component.setBounds(bounds);
			component.validate();
		}
	}

	/**
	 * Interpolate between all rectangles from the maps <code>b0</code> and
	 * <code>b1</code> according to the given alpha value (between 0 and 1), and
	 * store the interpolated rectangles in <code>b</code>
	 *
	 * @param b0    The first input rectangles
	 * @param b1    The second input rectangles
	 * @param b     The interpolated rectangles
	 * @param alpha The alpha value, between 0 and 1
	 */
	private static void interpolate(Map<Component, Rectangle> b0, Map<Component, Rectangle> b1,
			Map<Component, Rectangle> b, double alpha, Tween tween) {
		for (Component component : b0.keySet()) {
			Rectangle r0 = b0.get(component);
			Rectangle r1 = b1.get(component);
			Rectangle r = b.get(component);
			interpolate(r0, r1, r, alpha, tween);
		}
	}

	/**
	 * Linearly interpolate between <code>r0</code> and <code>r1</code> according to
	 * the given alpha value (between 0 and 1), and store the result in
	 * <code>r</code>.
	 *
	 * @param r0    The first rectangle
	 * @param r1    The second rectangle
	 * @param r     The interpolated rectangle
	 * @param alpha
	 */
	private static void interpolate(Rectangle r0, Rectangle r1, Rectangle r, double alpha, Tween tween) {
		switch(tween) {
		case EASE_IN_OUT_QUAD:
			alpha = easeInOutQuad(alpha);
			break;
		case EASE_IN_OUT_SINE:
			alpha = easeInOutSine(alpha);
			break;
		case LINEAR:
		default:
			break;
		}
		r.x = (int) (r0.x + alpha * (r1.x - r0.x));
		r.y = (int) (r0.y + alpha * (r1.y - r0.y));
		r.width = (int) (r0.width + alpha * (r1.width - r0.width));
		r.height = (int) (r0.height + alpha * (r1.height - r0.height));
	}

	private static double easeInOutSine(double x) {
		return -(Math.cos(Math.PI * x) - 1) / 2;
	}

	private static double easeInOutQuad(double x) {
		return x < 0.5 ? 2 * x * x : 1 - Math.pow(-2 * x + 2, 2) / 2;
	}

	/**
	 * Private constructor to prevent instantiation
	 */
	private LayoutAnimator() {
	}
}