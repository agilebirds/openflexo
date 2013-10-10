package org.openflexo.fge.control.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.Timer;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.view.DianaViewFactory;

/**
 * Represent a widget allowing to edit a layout, associated with a {@link DianaInteractiveViewer}
 * 
 * @author sylvain
 * 
 * @param <C>
 * @param <F>
 * @param <ME>
 */
public abstract class DianaLayoutWidget<C, F extends DianaViewFactory<F, ? super C>, ME> extends DianaToolImpl<C, F, ME> {

	/**
	 * Return the technology-specific component representing the widget
	 * 
	 * @return
	 */
	public abstract C getComponent();

	public void alignLeft() {
		System.out.println("Align left with " + getSelectedShapes());
		double newX = Double.POSITIVE_INFINITY;
		for (ShapeNode<?> gr : getSelectedShapes()) {
			if (gr.getX() < newX) {
				newX = gr.getX();
			}
		}
		List<TranslationTransition> tts = new ArrayList<TranslationTransition>();
		for (ShapeNode<?> gr : getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(newX, gr.getY())));
		}
		performTransitions(tts);
	}

	public void alignCenter() {
		System.out.println("Align center with " + getSelectedShapes());
		double totalX = 0;
		for (ShapeNode<?> gr : getSelectedShapes()) {
			totalX += gr.getX() + gr.getWidth() / 2;
		}
		double newX = totalX / getSelectedShapes().size();
		List<TranslationTransition> tts = new ArrayList<TranslationTransition>();
		for (ShapeNode<?> gr : getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(newX - gr.getWidth() / 2, gr.getY())));
		}
		performTransitions(tts);
	}

	public void alignRight() {
		System.out.println("Align right with " + getSelectedShapes());
		double newX = 0;
		for (ShapeNode<?> gr : getSelectedShapes()) {
			if (gr.getX() + gr.getWidth() > newX) {
				newX = gr.getX() + gr.getWidth();
			}
		}
		List<TranslationTransition> tts = new ArrayList<TranslationTransition>();
		for (ShapeNode<?> gr : getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(newX - gr.getWidth(), gr.getY())));
		}
		performTransitions(tts);
	}

	public void alignTop() {
		System.out.println("Align top with " + getSelectedShapes());
		double newY = Double.POSITIVE_INFINITY;
		for (ShapeNode<?> gr : getSelectedShapes()) {
			if (gr.getY() < newY) {
				newY = gr.getY();
			}
		}
		List<TranslationTransition> tts = new ArrayList<TranslationTransition>();
		for (ShapeNode<?> gr : getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(gr.getX(), newY)));
		}
		performTransitions(tts);
	}

	public void alignMiddle() {
		System.out.println("Align middle with " + getSelectedShapes());
		double totalY = 0;
		for (ShapeNode<?> gr : getSelectedShapes()) {
			totalY += gr.getY() + gr.getHeight() / 2;
		}
		double newY = totalY / getSelectedShapes().size();
		List<TranslationTransition> tts = new ArrayList<TranslationTransition>();
		for (ShapeNode<?> gr : getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(gr.getX(), newY - gr.getHeight() / 2)));
		}
		performTransitions(tts);
	}

	public void alignBottom() {
		System.out.println("Align bottom with " + getSelectedShapes());
		double newY = 0;
		for (ShapeNode<?> gr : getSelectedShapes()) {
			if (gr.getY() + gr.getHeight() > newY) {
				newY = gr.getY() + gr.getHeight();
			}
		}
		List<TranslationTransition> tts = new ArrayList<TranslationTransition>();
		for (ShapeNode<?> gr : getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(gr.getX(), newY - gr.getHeight())));
		}
		performTransitions(tts);
	}

	public class TranslationTransition {
		private ShapeNode<?> shapeNode;
		private FGEPoint oldLocation;
		private FGEPoint newLocation;

		public TranslationTransition(ShapeNode<?> shapeNode, FGEPoint oldLocation, FGEPoint newLocation) {
			super();
			this.shapeNode = shapeNode;
			this.oldLocation = oldLocation;
			this.newLocation = newLocation;
		}

		public void performStep(int step, int totalSteps) {
			shapeNode.setLocation(new FGEPoint(oldLocation.x - (oldLocation.x - newLocation.x) * step / totalSteps, oldLocation.y
					- (oldLocation.y - newLocation.y) * step / totalSteps));
		}
	}

	public class Animation {
		private int currentStep = 0;
		private int steps;
		private List<TranslationTransition> transitions;
		private Timer timer;

		public Animation(List<TranslationTransition> transitions, int steps) {
			super();
			this.currentStep = 0;
			this.steps = steps;
			this.transitions = transitions;
		}

		public void performAnimation() {
			currentStep = 0;
			timer = new Timer(0, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (final TranslationTransition tt : transitions) {
						tt.performStep(currentStep, steps);
					}
					currentStep++;
					if (currentStep > steps) {
						timer.stop();
					}
				}
			});
			timer.start();
		}
	}

	public void performTransitions(final List<TranslationTransition> transitions) {
		new Animation(transitions, 10).performAnimation();
	}

	@Override
	public void update(Observable o, Object arg) {
	}

}