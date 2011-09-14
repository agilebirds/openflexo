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
package org.openflexo.foundation.wkf;

import java.util.Vector;

import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.utils.FlexoColor;
import org.openflexo.foundation.utils.FlexoFont;
import org.openflexo.foundation.wkf.MetricsValue.MetricsValueOwner;
import org.openflexo.foundation.wkf.action.AddArtefactMetricsValue;
import org.openflexo.foundation.wkf.action.DeleteMetricsValue;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.WKFNode;


public abstract class WKFArtefact extends WKFNode implements MetricsValueOwner {
	public static final String TEXT_ALIGNMENT = "textAlignment";

	private FlexoPetriGraph _petriGraph;

	private String text;

	private Vector<MetricsValue> metricsValues;

	public static FlexoActionizer<AddArtefactMetricsValue, WKFArtefact, WKFObject> addMetricsActionizer;
	public static FlexoActionizer<DeleteMetricsValue,MetricsValue,MetricsValue> deleteMetricsActionizer;

	public WKFArtefact(FlexoProcess process) {
		super(process);
		metricsValues = new Vector<MetricsValue>();
	}

    public FlexoPetriGraph getParentPetriGraph()
    {
        return _petriGraph;
    }

    public final void setParentPetriGraph(FlexoPetriGraph pg)
    {
    	_petriGraph = pg;
    }

	@Override
	public void updateMetricsValues() {
		getWorkflow().updateMetricsForArtefact(this);
	}

	@Override
	public Vector<MetricsValue> getMetricsValues() {
		return metricsValues;
	}

	public void setMetricsValues(Vector<MetricsValue> metricsValues) {
		this.metricsValues = metricsValues;
		setChanged();
	}

	@Override
	public void addToMetricsValues(MetricsValue value) {
		if (value.getMetricsDefinition()!=null) {
			metricsValues.add(value);
			value.setOwner(this);
			setChanged();
			notifyObservers(new MetricsValueAdded(value,"metricsValues"));
		}
	}

	@Override
	public void removeFromMetricsValues(MetricsValue value) {
		metricsValues.remove(value);
		value.setOwner(null);
		setChanged();
		notifyObservers(new MetricsValueRemoved(value,"metricsValues"));
	}

	public void addMetrics() {
		if (addMetricsActionizer!=null)
			addMetricsActionizer.run(this, null);
	}

	public void deleteMetrics(MetricsValue value) {
		if (deleteMetricsActionizer!=null)
			deleteMetricsActionizer.run(value, null);
	}

    /**
	 * Recursive method to determine if the current node is embedded in the Petri graph <code>petriGraph</code>
	 */
	public boolean isEmbeddedInPetriGraph(FlexoPetriGraph petriGraph) {
		if (getParentPetriGraph()==petriGraph)
			return true;
		else if (getParentPetriGraph()!=null && getParentPetriGraph().getContainer() instanceof PetriGraphNode) {
			return ((PetriGraphNode)getParentPetriGraph().getContainer()).isEmbeddedInPetriGraph(petriGraph);
		}
		return false;
	}

	public boolean isArtefact() {
		return true;
	}

	@Override
	public String getName() {
		return getText();
	}

	@Override
	public final void delete()
	{
		if (getParentPetriGraph() != null) {
			getParentPetriGraph().removeFromArtefacts(this);
		}
		super.delete();
		deleteObservers();
	}

	@Override
	public Vector<? extends WKFObject> getAllEmbeddedDeleted()
	{
		return getAllEmbeddedWKFObjects();
	}

	@Override
	public Vector<WKFObject> getAllEmbeddedWKFObjects()
	{
		Vector<WKFObject> returned = super.getAllEmbeddedWKFObjects();
		returned.add(this);
		return returned;
	}

	public FlexoLevel getLevel() {
		if (getParentPetriGraph()!=null)
			return getParentPetriGraph().getLevel();
		return null;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		if (requireChange(getText(), text)) {
			String oldText = getText();
			this.text = text;
			setChanged();
			notifyObservers(new WKFAttributeDataModification("text",oldText,text));
		}
	}

	public FlexoColor getTextColor()
	{
		return getTextColor(DEFAULT,FlexoColor.BLACK_COLOR);
	}

	public void setTextColor(FlexoColor aColor)
	{
		if (requireChange(getTextColor(), aColor)) {
			FlexoColor oldColor = getTextColor();
			setTextColor(aColor, DEFAULT);
			setChanged();
			notifyObservers(new WKFAttributeDataModification(TEXT_COLOR,oldColor,aColor));
		}
	}

	public FlexoFont getTextFont()
	{
		return getTextFont(DEFAULT);
	}

	public void setTextFont(FlexoFont aFont)
	{
		if (requireChange(getTextFont(), aFont)) {
			FlexoFont oldFont = getTextFont();
			setTextFont(aFont, DEFAULT);
			setChanged();
			notifyObservers(new WKFAttributeDataModification(TEXT_FONT,oldFont,aFont));
		}
	}

	public Object getTextAlignment()
	{
		return _graphicalPropertyForKey(TEXT_ALIGNMENT+"_"+DEFAULT);
	}

	public void setTextAlignment(Object textAlign)
	{
		if (requireChange(getTextAlignment(), textAlign)) {
			Object oldTextAlignment = getTextAlignment();
			_setGraphicalPropertyForKey(textAlign, TEXT_ALIGNMENT+"_"+DEFAULT);
			setChanged();
			notifyObservers(new WKFAttributeDataModification(TEXT_ALIGNMENT,oldTextAlignment,textAlign));
		}
	}

	@Override
	public boolean isNodeValid() {
		return getProcess()!=null && getParentPetriGraph()!=null;
	}

}
