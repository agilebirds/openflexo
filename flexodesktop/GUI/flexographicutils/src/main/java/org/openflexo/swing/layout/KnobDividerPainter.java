package org.openflexo.swing.layout;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;

import org.openflexo.swing.layout.JXMultiSplitPane.DividerPainter;
import org.openflexo.swing.layout.MultiSplitLayout.Divider;

public class KnobDividerPainter extends DividerPainter {

	private static final int KNOB_SIZE = 5;
	private static final int KNOB_SPACE = 2;
	private static final int DIVIDER_SIZE = KNOB_SIZE + 2 * KNOB_SPACE;
	private static final int DIVIDER_KNOB_SIZE = 3 * KNOB_SIZE + 2 * KNOB_SPACE;

	private static final Paint KNOB_PAINTER = new RadialGradientPaint(new Point((KNOB_SIZE - 1) / 2, (KNOB_SIZE - 1) / 2),
			(KNOB_SIZE - 1) / 2, new float[] { 0.0f, 1.0f }, new Color[] { Color.GRAY, Color.LIGHT_GRAY });

	@Override
	protected void doPaint(Graphics2D g, Divider divider, int width, int height) {
		if (!divider.isVisible()) {
			return;
		}
		if (divider.isVertical()) {
			int x = (width - KNOB_SIZE) / 2;
			int y = (height - DIVIDER_KNOB_SIZE) / 2;
			for (int i = 0; i < 3; i++) {
				Graphics2D graph = (Graphics2D) g.create(x, y + i * (KNOB_SIZE + KNOB_SPACE), KNOB_SIZE + 1, KNOB_SIZE + 1);
				graph.setPaint(KNOB_PAINTER);
				graph.fillOval(0, 0, KNOB_SIZE, KNOB_SIZE);
			}
		} else {
			int x = (width - DIVIDER_KNOB_SIZE) / 2;
			int y = (height - KNOB_SIZE) / 2;
			for (int i = 0; i < 3; i++) {
				Graphics2D graph = (Graphics2D) g.create(x + i * (KNOB_SIZE + KNOB_SPACE), y, KNOB_SIZE + 1, KNOB_SIZE + 1);
				graph.setPaint(KNOB_PAINTER);
				graph.fillOval(0, 0, KNOB_SIZE, KNOB_SIZE);
			}
		}
	}
}