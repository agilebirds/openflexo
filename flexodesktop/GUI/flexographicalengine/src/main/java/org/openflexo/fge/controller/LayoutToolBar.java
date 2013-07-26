/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fge.controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.Timer;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEIconLibrary;
import org.openflexo.fge.geom.FGEPoint;

@SuppressWarnings("serial")
public class LayoutToolBar extends JToolBar {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(LayoutToolBar.class.getPackage().getName());

	private final EditorToolbox editorToolbox;

	public LayoutToolBar(EditorToolbox editorToolbox) {
		super();
		setRollover(true);

		this.editorToolbox = editorToolbox;

		add(new JLabel(FGEIconLibrary.TOOLBAR_LEFT_ICON));
		add(new LayoutButton(FGEIconLibrary.ALIGN_LEFT_ICON, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alignLeft();
			}
		}));
		add(new JLabel(FGEIconLibrary.TOOLBAR_SPACER_ICON));
		add(new LayoutButton(FGEIconLibrary.ALIGN_CENTER_ICON, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alignCenter();
			}
		}));
		add(new JLabel(FGEIconLibrary.TOOLBAR_SPACER_ICON));
		add(new LayoutButton(FGEIconLibrary.ALIGN_RIGHT_ICON, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alignRight();
			}
		}));
		add(new JLabel(FGEIconLibrary.TOOLBAR_RIGHT_ICON));

		addSeparator();

		add(new JLabel(FGEIconLibrary.TOOLBAR_LEFT_ICON));
		add(new LayoutButton(FGEIconLibrary.ALIGN_TOP_ICON, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alignTop();
			}
		}));
		add(new JLabel(FGEIconLibrary.TOOLBAR_SPACER_ICON));
		add(new LayoutButton(FGEIconLibrary.ALIGN_MIDDLE_ICON, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alignMiddle();
			}
		}));
		add(new JLabel(FGEIconLibrary.TOOLBAR_SPACER_ICON));
		add(new LayoutButton(FGEIconLibrary.ALIGN_BOTTOM_ICON, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				alignBottom();
			}
		}));
		add(new JLabel(FGEIconLibrary.TOOLBAR_RIGHT_ICON));

		validate();
	}

	public class LayoutButton extends JButton {
		public LayoutButton(ImageIcon icon, ActionListener al) {
			super(icon);
			setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.GRAY),
					BorderFactory.createEmptyBorder(2, 0, 2, 0)));
			addActionListener(al);
		}
	}

	protected void alignLeft() {
		System.out.println("Align left with " + editorToolbox.getSelectedShapes());
		double newX = Double.POSITIVE_INFINITY;
		for (ShapeNode<?> gr : editorToolbox.getSelectedShapes()) {
			if (gr.getX() < newX) {
				newX = gr.getX();
			}
		}
		List<TranslationTransition> tts = new ArrayList<LayoutToolBar.TranslationTransition>();
		for (ShapeNode<?> gr : editorToolbox.getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(newX, gr.getY())));
		}
		performTransitions(tts);
	}

	protected void alignCenter() {
		System.out.println("Align center with " + editorToolbox.getSelectedShapes());
		double totalX = 0;
		for (ShapeNode<?> gr : editorToolbox.getSelectedShapes()) {
			totalX += gr.getX() + gr.getWidth() / 2;
		}
		double newX = totalX / editorToolbox.getSelectedShapes().size();
		List<TranslationTransition> tts = new ArrayList<LayoutToolBar.TranslationTransition>();
		for (ShapeNode<?> gr : editorToolbox.getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(newX - gr.getWidth() / 2, gr.getY())));
		}
		performTransitions(tts);
	}

	protected void alignRight() {
		System.out.println("Align right with " + editorToolbox.getSelectedShapes());
		double newX = 0;
		for (ShapeNode<?> gr : editorToolbox.getSelectedShapes()) {
			if (gr.getX() + gr.getWidth() > newX) {
				newX = gr.getX() + gr.getWidth();
			}
		}
		List<TranslationTransition> tts = new ArrayList<LayoutToolBar.TranslationTransition>();
		for (ShapeNode<?> gr : editorToolbox.getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(newX - gr.getWidth(), gr.getY())));
		}
		performTransitions(tts);
	}

	protected void alignTop() {
		System.out.println("Align top with " + editorToolbox.getSelectedShapes());
		double newY = Double.POSITIVE_INFINITY;
		for (ShapeNode<?> gr : editorToolbox.getSelectedShapes()) {
			if (gr.getY() < newY) {
				newY = gr.getY();
			}
		}
		List<TranslationTransition> tts = new ArrayList<LayoutToolBar.TranslationTransition>();
		for (ShapeNode<?> gr : editorToolbox.getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(gr.getX(), newY)));
		}
		performTransitions(tts);
	}

	protected void alignMiddle() {
		System.out.println("Align middle with " + editorToolbox.getSelectedShapes());
		double totalY = 0;
		for (ShapeNode<?> gr : editorToolbox.getSelectedShapes()) {
			totalY += gr.getY() + gr.getHeight() / 2;
		}
		double newY = totalY / editorToolbox.getSelectedShapes().size();
		List<TranslationTransition> tts = new ArrayList<LayoutToolBar.TranslationTransition>();
		for (ShapeNode<?> gr : editorToolbox.getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(gr.getX(), newY - gr.getHeight() / 2)));
		}
		performTransitions(tts);
	}

	protected void alignBottom() {
		System.out.println("Align bottom with " + editorToolbox.getSelectedShapes());
		double newY = 0;
		for (ShapeNode<?> gr : editorToolbox.getSelectedShapes()) {
			if (gr.getY() + gr.getHeight() > newY) {
				newY = gr.getY() + gr.getHeight();
			}
		}
		List<TranslationTransition> tts = new ArrayList<LayoutToolBar.TranslationTransition>();
		for (ShapeNode<?> gr : editorToolbox.getSelectedShapes()) {
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

}
