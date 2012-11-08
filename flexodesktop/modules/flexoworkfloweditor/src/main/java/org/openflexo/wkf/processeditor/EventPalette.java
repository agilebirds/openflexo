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
package org.openflexo.wkf.processeditor;

import java.awt.Color;
import java.awt.Font;
import java.util.logging.Logger;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.controller.PaletteElement;
import org.openflexo.fge.graphics.DrawingDecorationPainter;
import org.openflexo.fge.graphics.FGEDrawingDecorationGraphics;
import org.openflexo.fge.impl.ForegroundStyleImpl;
import org.openflexo.fge.impl.TextStyleImpl;
import org.openflexo.fge.view.LabelView;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFAnnotation;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.EventNode.EVENT_TYPE;
import org.openflexo.foundation.wkf.node.EventNode.TriggerType;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.processeditor.gr.EventNodeGR;

public class EventPalette extends AbstractWKFPalette {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(EventPalette.class.getPackage().getName());

	private ContainerValidity DROP_ON_PETRI_GRAPH = new ContainerValidity() {
		@Override
		public boolean isContainerValid(WKFObject container) {
			return container instanceof FlexoPetriGraph || container instanceof FlexoProcess || container instanceof WKFAnnotation;
		}
	};

	private ContainerValidity DROP_ON_ACTIVITY = new ContainerValidity() {
		@Override
		public boolean isContainerValid(WKFObject container) {
			return container instanceof AbstractActivityNode;
		}
	};

	private ContainerValidity DROP_ON_ACTIVITY_OR_PETRIGRAPH = new ContainerValidity() {
		@Override
		public boolean isContainerValid(WKFObject container) {
			return DROP_ON_PETRI_GRAPH.isContainerValid(container) || DROP_ON_ACTIVITY.isContainerValid(container);
		}
	};

	private static final int WIDTH = 300;

	protected int START_X = !WKFPreferences.getUseSimpleEventPalette() ? 60 : 65; // Event col
	protected int GLOABAL_START_X = 50;
	protected int START2_X = 95; // Event col, only in expert mode
	protected int INTER1_X = !WKFPreferences.getUseSimpleEventPalette() ? 136 : 125; // Event col
	protected int GLOBAL_INTER1_X = !WKFPreferences.getUseSimpleEventPalette() ? 134 : 112; // Line
	protected int INTER1bis_X = !WKFPreferences.getUseSimpleEventPalette() ? 172 : 160; // Event col
	protected int GLOBAL_INTER1bis_X = !WKFPreferences.getUseSimpleEventPalette() ? 135 : 200;
	protected int INTER2_X = !WKFPreferences.getUseSimpleEventPalette() ? 207 : 175; // Event col
	protected int GLOBAL_INTER2_X = !WKFPreferences.getUseSimpleEventPalette() ? 180 : 198;
	protected int END_X = !WKFPreferences.getUseSimpleEventPalette() ? 247 : 232; // Event col
	protected int GLOBAL_END_X = !WKFPreferences.getUseSimpleEventPalette() ? 245 : 225; // Line
	protected int TOP_Y = 7;

	protected static final int OFFSET = 2;
	private static final Font FONT = FGEConstants.DEFAULT_TEXT_FONT.deriveFont(Font.ITALIC, 12);

	public class EventPaletteDecorationPainter implements DrawingDecorationPainter {

		@Override
		public boolean paintBeforeDrawing() {
			return false;
		}

		@Override
		public void paintDecoration(FGEDrawingDecorationGraphics g) {
			g.useForegroundStyle(ForegroundStyleImpl.makeStyle(Color.BLACK));
			g.useTextStyle(TextStyleImpl.makeTextStyle(Color.black, FONT));
			g.drawString(FlexoLocalization.localizedForKey("start"), GLOABAL_START_X + (GLOBAL_INTER1_X - GLOABAL_START_X) / 2, TOP_Y
					+ OFFSET, HorizontalTextAlignment.CENTER);
			g.drawString(FlexoLocalization.localizedForKey("intermediate"), GLOBAL_INTER1_X + (GLOBAL_END_X - GLOBAL_INTER1_X) / 2, TOP_Y
					+ OFFSET - 2, HorizontalTextAlignment.CENTER);
			g.drawString(FlexoLocalization.localizedForKey("end"), GLOBAL_INTER2_X + (WIDTH - GLOBAL_INTER1bis_X) / 2, TOP_Y + OFFSET,
					HorizontalTextAlignment.CENTER);
			g.drawLine(GLOBAL_INTER1_X - OFFSET, TOP_Y, GLOBAL_INTER1_X - OFFSET, g.getHeight() - OFFSET);
			// g.drawLine(INTER2_X-OFFSET, TOP_Y, INTER2_X-OFFSET, g.getHeight()-OFFSET);
			g.drawLine(GLOBAL_END_X - OFFSET, TOP_Y, GLOBAL_END_X - OFFSET, g.getHeight() - OFFSET);
		}

	}

	private DrawingDecorationPainter eventPaletteDecorationPainter;

	private static int deltaY = 39;

	public EventPalette() {
		super(WIDTH, !WKFPreferences.getUseSimpleEventPalette() ? 529 : 529 - 4 * deltaY, "event");
		eventPaletteDecorationPainter = new EventPaletteDecorationPainter();

		int y = TOP_Y;

		y += OFFSET + 12;
		makeEventElement(EventNode.makeEventNode(TriggerType.NONE, EVENT_TYPE.Start, true), START_X, y, "Default");
		makeEventElement(EventNode.makeEventNode(TriggerType.NONE, EVENT_TYPE.Intermediate, true), INTER1_X, y, "",
				DROP_ON_ACTIVITY_OR_PETRIGRAPH);
		makeEventElement(EventNode.makeEventNode(TriggerType.NONE, EVENT_TYPE.End, false), END_X, y, "");

		y = y + deltaY;
		makeEventElement(EventNode.makeEventNode(TriggerType.TERMINATE, EVENT_TYPE.End, false), END_X, y, "Terminate");

		y = y + deltaY;
		makeEventElement(EventNode.makeEventNode(TriggerType.CANCEL, EVENT_TYPE.Intermediate, true), INTER1_X, y, "Cancel",
				DROP_ON_ACTIVITY_OR_PETRIGRAPH);
		makeEventElement(EventNode.makeEventNode(TriggerType.CANCEL, EVENT_TYPE.End, false), END_X, y, "");

		y = y + deltaY;
		makeEventElement(EventNode.makeEventNode(TriggerType.MESSAGE, EVENT_TYPE.Start, true), START_X, y, "Message");
		if (!WKFPreferences.getUseSimpleEventPalette()) {
			makeEventElement(EventNode.makeEventNode(TriggerType.MESSAGE, EVENT_TYPE.NonInteruptive, true), START2_X, y, "");
		}
		makeEventElement(EventNode.makeEventNode(TriggerType.MESSAGE, EVENT_TYPE.Intermediate, true), INTER1_X, y, "",
				DROP_ON_ACTIVITY_OR_PETRIGRAPH);
		if (!WKFPreferences.getUseSimpleEventPalette()) {
			makeEventElement(EventNode.makeEventNode(TriggerType.MESSAGE, EVENT_TYPE.NonInteruptiveBoundary, true), INTER1bis_X, y, "",
					DROP_ON_ACTIVITY);
		}
		makeEventElement(EventNode.makeEventNode(TriggerType.MESSAGE, EVENT_TYPE.IntermediateDrop, false), INTER2_X, y, "");
		makeEventElement(EventNode.makeEventNode(TriggerType.MESSAGE, EVENT_TYPE.End, false), END_X, y, "");

		y = y + deltaY;
		makeEventElement(EventNode.makeEventNode(TriggerType.TIMER, EVENT_TYPE.Start, true), START_X, y, "Timer");
		if (!WKFPreferences.getUseSimpleEventPalette()) {
			makeEventElement(EventNode.makeEventNode(TriggerType.TIMER, EVENT_TYPE.NonInteruptive, true), START2_X, y, "");
		}
		makeEventElement(EventNode.makeEventNode(TriggerType.TIMER, EVENT_TYPE.Intermediate, true), INTER1_X, y, "",
				DROP_ON_ACTIVITY_OR_PETRIGRAPH);
		if (!WKFPreferences.getUseSimpleEventPalette()) {
			makeEventElement(EventNode.makeEventNode(TriggerType.TIMER, EVENT_TYPE.NonInteruptiveBoundary, true), INTER1bis_X, y, "",
					DROP_ON_ACTIVITY);
		}

		y = y + deltaY;
		makeEventElement(EventNode.makeEventNode(TriggerType.CONDITIONAL, EVENT_TYPE.Start, true), START_X, y, "Condition");
		if (!WKFPreferences.getUseSimpleEventPalette()) {
			makeEventElement(EventNode.makeEventNode(TriggerType.CONDITIONAL, EVENT_TYPE.NonInteruptive, true), START2_X, y, "");
		}
		makeEventElement(EventNode.makeEventNode(TriggerType.CONDITIONAL, EVENT_TYPE.Intermediate, true), INTER1_X, y, "",
				DROP_ON_ACTIVITY_OR_PETRIGRAPH);
		if (!WKFPreferences.getUseSimpleEventPalette()) {
			makeEventElement(EventNode.makeEventNode(TriggerType.CONDITIONAL, EVENT_TYPE.NonInteruptiveBoundary, true), INTER1bis_X, y, "",
					DROP_ON_ACTIVITY);
		}

		y = y + deltaY;
		makeEventElement(EventNode.makeEventNode(TriggerType.ERROR, EVENT_TYPE.Start, true), START_X, y, "Error");
		makeEventElement(EventNode.makeEventNode(TriggerType.ERROR, EVENT_TYPE.Intermediate, true), INTER1_X, y, "",
				DROP_ON_ACTIVITY_OR_PETRIGRAPH);
		makeEventElement(EventNode.makeEventNode(TriggerType.ERROR, EVENT_TYPE.End, false), END_X, y, "");

		y = y + deltaY;
		makeEventElement(EventNode.makeEventNode(TriggerType.SIGNAL, EVENT_TYPE.Start, true), START_X, y, "Signal");
		if (!WKFPreferences.getUseSimpleEventPalette()) {
			makeEventElement(EventNode.makeEventNode(TriggerType.SIGNAL, EVENT_TYPE.NonInteruptive, true), START2_X, y, "");
		}
		makeEventElement(EventNode.makeEventNode(TriggerType.SIGNAL, EVENT_TYPE.Intermediate, true), INTER1_X, y, "",
				DROP_ON_ACTIVITY_OR_PETRIGRAPH);
		if (!WKFPreferences.getUseSimpleEventPalette()) {
			makeEventElement(EventNode.makeEventNode(TriggerType.SIGNAL, EVENT_TYPE.NonInteruptiveBoundary, true), INTER1bis_X, y, "",
					DROP_ON_ACTIVITY);
		}
		makeEventElement(EventNode.makeEventNode(TriggerType.SIGNAL, EVENT_TYPE.IntermediateDrop, false), INTER2_X, y, "");
		makeEventElement(EventNode.makeEventNode(TriggerType.SIGNAL, EVENT_TYPE.End, false), END_X, y, "");

		y = y + deltaY;

		makeEventElement(EventNode.makeEventNode(TriggerType.LINK, EVENT_TYPE.Intermediate, true), INTER1_X, y, "Link");
		makeEventElement(EventNode.makeEventNode(TriggerType.LINK, EVENT_TYPE.IntermediateDrop, false), INTER2_X, y, "");

		if (!WKFPreferences.getUseSimpleEventPalette()) {
			y = y + deltaY;
			makeEventElement(EventNode.makeEventNode(TriggerType.ESCALATION, EVENT_TYPE.Start, true), START_X, y, "Esca-\nlation");
			makeEventElement(EventNode.makeEventNode(TriggerType.ESCALATION, EVENT_TYPE.NonInteruptive, true), START2_X, y, "");
			makeEventElement(EventNode.makeEventNode(TriggerType.ESCALATION, EVENT_TYPE.Intermediate, true), INTER1_X, y, "",
					DROP_ON_ACTIVITY_OR_PETRIGRAPH);
			makeEventElement(EventNode.makeEventNode(TriggerType.ESCALATION, EVENT_TYPE.NonInteruptiveBoundary, true), INTER1bis_X, y, "",
					DROP_ON_ACTIVITY);
			makeEventElement(EventNode.makeEventNode(TriggerType.ESCALATION, EVENT_TYPE.IntermediateDrop, false), INTER2_X, y, "");
			makeEventElement(EventNode.makeEventNode(TriggerType.ESCALATION, EVENT_TYPE.End, false), END_X, y, "");
			y = y + deltaY;
			makeEventElement(EventNode.makeEventNode(TriggerType.COMPENSATION, EVENT_TYPE.Start, true), START_X, y, "Compen-\nsation");
			makeEventElement(EventNode.makeEventNode(TriggerType.COMPENSATION, EVENT_TYPE.Intermediate, true), INTER1_X, y, "",
					DROP_ON_ACTIVITY_OR_PETRIGRAPH);
			makeEventElement(EventNode.makeEventNode(TriggerType.COMPENSATION, EVENT_TYPE.IntermediateDrop, false), INTER2_X, y, "");
			makeEventElement(EventNode.makeEventNode(TriggerType.COMPENSATION, EVENT_TYPE.End, false), END_X, y, "");
			y = y + deltaY;

			makeEventElement(EventNode.makeEventNode(TriggerType.MULTIPLE, EVENT_TYPE.Start, true), START_X, y, "Multiple");
			if (!WKFPreferences.getUseSimpleEventPalette()) {
				makeEventElement(EventNode.makeEventNode(TriggerType.MULTIPLE, EVENT_TYPE.NonInteruptive, true), START2_X, y, "");
			}
			makeEventElement(EventNode.makeEventNode(TriggerType.MULTIPLE, EVENT_TYPE.Intermediate, true), INTER1_X, y, "",
					DROP_ON_ACTIVITY_OR_PETRIGRAPH);
			makeEventElement(EventNode.makeEventNode(TriggerType.MULTIPLE, EVENT_TYPE.NonInteruptiveBoundary, true), INTER1bis_X, y, "",
					DROP_ON_ACTIVITY);
			makeEventElement(EventNode.makeEventNode(TriggerType.MULTIPLE, EVENT_TYPE.IntermediateDrop, false), INTER2_X, y, "");
			makeEventElement(EventNode.makeEventNode(TriggerType.MULTIPLE, EVENT_TYPE.End, false), END_X, y, "");

			y = y + deltaY;
			makeEventElement(EventNode.makeEventNode(TriggerType.MULTIPLEPARA, EVENT_TYPE.Start, true), START_X, y, "Multiple\nParallel");
			if (!WKFPreferences.getUseSimpleEventPalette()) {
				makeEventElement(EventNode.makeEventNode(TriggerType.MULTIPLEPARA, EVENT_TYPE.NonInteruptive, true), START2_X, y, "");
			}
			makeEventElement(EventNode.makeEventNode(TriggerType.MULTIPLEPARA, EVENT_TYPE.Intermediate, true), INTER1_X, y, "",
					DROP_ON_ACTIVITY_OR_PETRIGRAPH);
			if (!WKFPreferences.getUseSimpleEventPalette()) {
				makeEventElement(EventNode.makeEventNode(TriggerType.MULTIPLEPARA, EVENT_TYPE.NonInteruptiveBoundary, true), INTER1bis_X,
						y, "", DROP_ON_ACTIVITY);
			}
		}
		makePalettePanel();
	}

	public DrawingDecorationPainter getEventPaletteDecorationPainter() {
		return eventPaletteDecorationPainter;
	}

	private PaletteElement makeEventElement(EventNode event, int x, int y, String name) {
		return makeEventElement(event, x, y, name, DROP_ON_PETRI_GRAPH);
	}

	private PaletteElement makeEventElement(EventNode event, int x, int y, String name, ContainerValidity validity) {
		String label = null;
		if (name != null && name.trim().length() > 0) {
			label = FlexoLocalization.localizedForKey(name);
		}
		if (label != null) {
			event.setName(label);
		}
		event.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		event.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		EventNodeGR eventNodeGR = new EventNodeGR(event, null, true);
		LabelView<EventNode> labelMetricsProvider = new LabelView<EventNode>(eventNodeGR, null, null);
		eventNodeGR.setLabelMetricsProvider(labelMetricsProvider);
		int labelX = -x + eventNodeGR.getNormalizedLabelSize().width / 2;
		labelMetricsProvider.delete();
		event.setLabelX(labelX, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		event.setLabelY(EventNodeGR.EVENT_NODE_SIZE / 2, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		event.setDescription(FlexoLocalization.localizedForKey("doc_event_" + event.getTrigger() + "_" + event.getEventType()));
		return makePaletteElement(event, eventNodeGR, validity);
	}
}
