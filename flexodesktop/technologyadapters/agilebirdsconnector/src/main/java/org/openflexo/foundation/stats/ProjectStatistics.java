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
package org.openflexo.foundation.stats;

import java.util.Vector;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;

/**
 * @author gpolet
 * 
 */
public class ProjectStatistics extends FlexoStatistics<FlexoProject> {

	private int processCount = -1;

	private int activityCount = -1;

	private int operationCount = -1;

	private int actionCount = -1;

	private int realActionCount = -1;

	private int realOperationCount = -1;

	private int realActivityCount = -1;

	private int pageCount = -1;

	private int popupCount = -1;

	private int tabCount = -1;

	private int eoEntityCount = -1;

	private int eoAttributeCount = -1;

	private int eoRelationshipCount = -1;

	/**
	 * @param object
	 */
	public ProjectStatistics(FlexoProject object) {
		super(object);
		refresh();
	}

	public String excel() {
		StringBuilder sb = new StringBuilder();
		sb.append("# processes ; ");
		sb.append("# activities ; ");
		sb.append("# operations ; ");
		sb.append("# actions ; ");
		sb.append("# activities (begin/end excl.) ; ");
		sb.append("# operations (begin/end excl.) ; ");
		sb.append("# actions (begin/end excl.) ; ");

		sb.append("# pages ; ");
		sb.append("# popups ; ");
		sb.append("# tabs ; ");

		sb.append("# entities ; ");
		sb.append("# attributes ; ");
		sb.append("# relationships\n");

		sb.append(getProcessCount());
		sb.append(" ; ");
		sb.append(getActivityCount());
		sb.append(" ; ");
		sb.append(getOperationCount());
		sb.append(" ; ");
		sb.append(getActionCount());
		sb.append(" ; ");
		sb.append(getRealActivityCount());
		sb.append(" ; ");
		sb.append(getRealOperationCount());
		sb.append(" ; ");
		sb.append(getRealActionCount());
		sb.append(" ; ");
		sb.append(getPageCount());
		sb.append(" ; ");
		sb.append(getPopupCount());
		sb.append(" ; ");
		sb.append(getTabCount());
		sb.append(" ; ");
		sb.append(getEoEntityCount());
		sb.append(" ; ");
		sb.append(getEoAttributeCount());
		sb.append(" ; ");
		sb.append(getEoRelationshipCount());
		sb.append("\n");
		return sb.toString();
	}

	/**
     * 
     */
	@Override
	public void refresh() {
		Vector<FlexoProcess> processes = getObject().getFlexoWorkflow().getAllLocalFlexoProcesses();
		setProcessCount(processes.size());
		int actCount = 0; // Actvivties
		int oCount = 0; // Operations
		int aCount = 0; // Actions
		int rActCount = 0; // Actvivties
		int rOCount = 0; // Operations
		int rACount = 0; // Actions

		// The Workflow
		for (FlexoProcess process : processes) {
			process.getStatistics().refresh();
			actCount += process.getStatistics().getActivityCount();
			oCount += process.getStatistics().getOperationCount();
			aCount += process.getStatistics().getActionCount();
			rActCount += process.getStatistics().getRealActivityCount();
			rOCount += process.getStatistics().getRealOperationCount();
			rACount += process.getStatistics().getRealActionCount();
		}
		setActivityCount(actCount);
		setOperationCount(oCount);
		setActionCount(aCount);
		setRealActivityCount(rActCount);
		setRealOperationCount(rOCount);
		setRealActionCount(rACount);

		// The DataModel
		getObject().getDataModel().getStatistics().refresh();
		setEoEntityCount(getObject().getDataModel().getStatistics().getEoEntityCount());
		setEoAttributeCount(getObject().getDataModel().getStatistics().getEoAttributeCount());
		setEoRelationshipCount(getObject().getDataModel().getStatistics().getEoRelationshipCount());

		// The Component Library
		getObject().getFlexoComponentLibrary().getStatistics().refresh();
		setPageCount(getObject().getFlexoComponentLibrary().getStatistics().getPageCount());
		setPopupCount(getObject().getFlexoComponentLibrary().getStatistics().getPopupCount());
		setTabCount(getObject().getFlexoComponentLibrary().getStatistics().getTabCount());
	}

	public int getActionCount() {
		return actionCount;
	}

	private void setActionCount(int actionCount) {
		int old = this.actionCount;
		this.actionCount = actionCount;
		if (old != actionCount) {
			setChanged();
			notifyObservers(new StatModification("actionCount", old, actionCount));
		}
	}

	public int getActivityCount() {
		return activityCount;
	}

	private void setActivityCount(int activityCount) {
		int old = this.activityCount;
		this.activityCount = activityCount;
		if (old != activityCount) {
			setChanged();
			notifyObservers(new StatModification("activityCount", old, activityCount));
		}
	}

	public int getRealActionCount() {
		return realActionCount;
	}

	private void setRealActionCount(int realActionCount) {
		int old = this.realActionCount;
		this.realActionCount = realActionCount;
		if (old != realActionCount) {
			setChanged();
			notifyObservers(new StatModification("realActionCount", old, realActionCount));
		}
	}

	public int getRealOperationCount() {
		return realOperationCount;
	}

	private void setRealOperationCount(int realOperationCount) {
		int old = this.realOperationCount;
		this.realOperationCount = realOperationCount;
		if (old != realOperationCount) {
			setChanged();
			notifyObservers(new StatModification("realOperationCount", old, realOperationCount));
		}
	}

	public int getRealActivityCount() {
		return realActivityCount;
	}

	private void setRealActivityCount(int realActivityCount) {
		int old = this.realActivityCount;
		this.realActivityCount = realActivityCount;
		if (old != realActivityCount) {
			setChanged();
			notifyObservers(new StatModification("realActivityCount", old, realActivityCount));
		}
	}

	public int getEoAttributeCount() {
		return eoAttributeCount;
	}

	private void setEoAttributeCount(int eoAttributeCount) {
		int old = this.eoAttributeCount;
		this.eoAttributeCount = eoAttributeCount;
		if (old != eoAttributeCount) {
			setChanged();
			notifyObservers(new StatModification("eoAttributeCount", old, eoAttributeCount));
		}
	}

	public int getEoEntityCount() {
		return eoEntityCount;
	}

	private void setEoEntityCount(int eoEntityCount) {
		int old = this.eoEntityCount;
		this.eoEntityCount = eoEntityCount;
		if (old != eoEntityCount) {
			setChanged();
			notifyObservers(new StatModification("eoEntityCount", old, eoEntityCount));
		}
	}

	public int getEoRelationshipCount() {
		return eoRelationshipCount;
	}

	private void setEoRelationshipCount(int eoRelationshipCount) {
		int old = this.eoRelationshipCount;
		this.eoRelationshipCount = eoRelationshipCount;
		if (old != eoRelationshipCount) {
			setChanged();
			notifyObservers(new StatModification("eoRelationshipCount", old, eoRelationshipCount));
		}
	}

	public int getOperationCount() {
		return operationCount;
	}

	private void setOperationCount(int operationCount) {
		int old = this.operationCount;
		this.operationCount = operationCount;
		if (old != operationCount) {
			setChanged();
			notifyObservers(new StatModification("operationCount", old, operationCount));
		}
	}

	public int getProcessCount() {
		return processCount;
	}

	private void setProcessCount(int processCount) {
		int old = this.processCount;
		this.processCount = processCount;
		if (old != processCount) {
			setChanged();
			notifyObservers(new StatModification("processCount", old, processCount));
		}
	}

	public int getPageCount() {
		return pageCount;
	}

	private void setPageCount(int pageCount) {
		int old = this.pageCount;
		this.pageCount = pageCount;
		if (old != pageCount) {
			setChanged();
			notifyObservers(new StatModification("pageCount", old, pageCount));
		}
	}

	public int getPopupCount() {
		return popupCount;
	}

	private void setPopupCount(int popupCount) {
		int old = this.popupCount;
		this.popupCount = popupCount;
		if (old != popupCount) {
			setChanged();
			notifyObservers(new StatModification("popupCount", old, popupCount));
		}
	}

	public int getTabCount() {
		return tabCount;
	}

	private void setTabCount(int tabCount) {
		int old = this.tabCount;
		this.tabCount = tabCount;
		if (old != tabCount) {
			setChanged();
			notifyObservers(new StatModification("tabCount", old, tabCount));
		}
	}

}
