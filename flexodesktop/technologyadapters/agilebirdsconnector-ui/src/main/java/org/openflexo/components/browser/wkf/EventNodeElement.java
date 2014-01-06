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
package org.openflexo.components.browser.wkf;

import java.util.Enumeration;

import javax.swing.Icon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.EventNode.EVENT_TYPE;
import org.openflexo.icon.WKFIconLibrary;

/**
 * Browser element representing an Event Node
 * 
 * @author sguerin
 * 
 */
public class EventNodeElement extends BrowserElement {

	public EventNodeElement(EventNode node, ProjectBrowser browser, BrowserElement parent) {
		super(node, BrowserElementType.EVENT_NODE, browser, parent);
	}

	@Override
	public String getName() {
		if (getEventNode().getName() == null) {
			return getEventNode().getDefaultName();
		}
		return getEventNode().getName();
	}

	@Override
	protected void buildChildrenVector() {
		// We add post conditions
		for (Enumeration e = getEventNode().getOutgoingPostConditions().elements(); e.hasMoreElements();) {
			addToChilds((FlexoPostCondition) e.nextElement());
		}

	}

	protected EventNode getEventNode() {
		return (EventNode) getObject();
	}

	@Override
	public Icon getIcon() {
		if (getEventNode().isTriggerNone()) {
			if (getEventNode().getEventType() == EVENT_TYPE.Start) {
				return WKFIconLibrary.DEFAULT_START_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.DEFAULT_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.End) {
				return WKFIconLibrary.DEFAULT_END_ICON;
			}
		} else if (getEventNode().isTriggerMessage()) {
			if (getEventNode().getEventType() == EVENT_TYPE.Start) {
				return WKFIconLibrary.MAIL_IN_START_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.NonInteruptive) {
				return WKFIconLibrary.MAIL_IN_START_NON_INTERRUPT_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.MAIL_IN_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.NonInteruptiveBoundary) {
				return WKFIconLibrary.MAIL_IN_BOUNDARY_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.IntermediateDrop) {
				return WKFIconLibrary.MAIL_OUT_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.End) {
				return WKFIconLibrary.MAIL_OUT_END_ICON;
			}
		} else if (getEventNode().isTriggerTimer()) {
			if (getEventNode().getEventType() == EVENT_TYPE.Start) {
				return WKFIconLibrary.TIMER_START_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.NonInteruptive) {
				return WKFIconLibrary.TIMER_START_NON_INTERRUPT_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.TIMER_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.NonInteruptiveBoundary) {
				return WKFIconLibrary.TIMER_BOUNDARY_ICON;
			}
		} else if (getEventNode().isTriggerError()) {
			if (getEventNode().getEventType() == EVENT_TYPE.Start) {
				return WKFIconLibrary.ERROR_START_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.ERROR_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.End) {
				return WKFIconLibrary.ERROR_END_ICON;
			}
		} else if (getEventNode().isTriggerEscalation()) {
			if (getEventNode().getEventType() == EVENT_TYPE.Start) {
				return WKFIconLibrary.ESCALATION_START_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.NonInteruptive) {
				return WKFIconLibrary.ESCALATION_START_NON_INTERRUPT_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.ESCALATION_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.NonInteruptiveBoundary) {
				return WKFIconLibrary.ESCALATION_BOUNDARY_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.IntermediateDrop) {
				return WKFIconLibrary.ESCALATION_DROP_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.End) {
				return WKFIconLibrary.ESCALATION_DROP_END_ICON;
			}
		} else if (getEventNode().isTriggerCancel()) {
			if (getEventNode().getEventType() == EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.CANCEL_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.End) {
				return WKFIconLibrary.CANCEL_END_ICON;
			}
		} else if (getEventNode().isTriggerCompensation()) {
			if (getEventNode().getEventType() == EVENT_TYPE.Start) {
				return WKFIconLibrary.COMPENSATION_START_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.COMPENSATION_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.IntermediateDrop) {
				return WKFIconLibrary.COMPENSATION_DROP_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.End) {
				return WKFIconLibrary.COMPENSATION_DROP_END_ICON;
			}
		} else if (getEventNode().isTriggerConditional()) {
			if (getEventNode().getEventType() == EVENT_TYPE.Start) {
				return WKFIconLibrary.CONDITION_START_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.NonInteruptive) {
				return WKFIconLibrary.CONDITION_START_NON_INTERRUPT_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.CONDITION_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.NonInteruptiveBoundary) {
				return WKFIconLibrary.CONDITION_BOUNDARY_ICON;
			}
		} else if (getEventNode().isTriggerLink()) {
			if (getEventNode().getEventType() == EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.LINK_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.IntermediateDrop) {
				return WKFIconLibrary.LINK_DROP_INTER_ICON;
			}
		} else if (getEventNode().isTriggerSignal()) {
			if (getEventNode().getEventType() == EVENT_TYPE.Start) {
				return WKFIconLibrary.SIGNAL_START_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.NonInteruptive) {
				return WKFIconLibrary.SIGNAL_START_NON_INTERRUPT_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.SIGNAL_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.NonInteruptiveBoundary) {
				return WKFIconLibrary.SIGNAL_BOUNDARY_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.IntermediateDrop) {
				return WKFIconLibrary.SIGNAL_DROP_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.End) {
				return WKFIconLibrary.SIGNAL_DROP_TERMINATE_ICON;
			}
		} else if (getEventNode().isTriggerTerminate()) {
			if (getEventNode().getEventType() == EVENT_TYPE.End) {
				return WKFIconLibrary.TERMINATE_ICON;
			}
		} else if (getEventNode().isTriggerMultiple()) {
			if (getEventNode().getEventType() == EVENT_TYPE.Start) {
				return WKFIconLibrary.MULTIPLE_START_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.NonInteruptive) {
				return WKFIconLibrary.MULTIPLE_START_NON_INTERRUPT_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.MULTIPLE_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.NonInteruptiveBoundary) {
				return WKFIconLibrary.MULTIPLE_BOUNDARY_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.IntermediateDrop) {
				return WKFIconLibrary.MULTIPLE_DROP_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.End) {
				return WKFIconLibrary.MULTIPLE_DROP_END_ICON;
			}
		} else if (getEventNode().isTriggerMultiplePara()) {
			if (getEventNode().getEventType() == EVENT_TYPE.Start) {
				return WKFIconLibrary.MULTIPLE_PARA_START_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.NonInteruptive) {
				return WKFIconLibrary.MULTIPLE_PARA_START_NON_INTERRUPT_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.MULTIPLE_PARA_INTER_ICON;
			}
			if (getEventNode().getEventType() == EVENT_TYPE.NonInteruptiveBoundary) {
				return WKFIconLibrary.MULTIPLE_PARA_BOUNDARY_ICON;
			}
		}
		return super.getIcon();
	}

	@Override
	public boolean isNameEditable() {
		return true;
	}

	@Override
	public void setName(String aName) {
		getEventNode().setName(aName);
	}

}
