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
package org.openflexo.wkf.swleditor.gr;

import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.wkf.swleditor.SWLEditorConstants;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;


public abstract class WKFConnectorGR<O> extends ConnectorGraphicalRepresentation<O> implements GraphicalFlexoObserver,SWLEditorConstants {

	private static final Logger logger = Logger.getLogger(WKFConnectorGR.class.getPackage().getName());

	protected WKFObject startObject;
	protected WKFObject endObject;
	private WKFObjectGR<? extends WKFObject> startObjectGR;
	private WKFObjectGR<? extends WKFObject> endObjectGR;
	
	public WKFConnectorGR(
			ConnectorType aConnectorType, 
			WKFObject startObject,
			WKFObject endObject,
			O aDrawable, 
			SwimmingLaneRepresentation aDrawing) 
	{
		super(aConnectorType, 				
				getGraphicalRepresentation(startObject,aDrawing),
				getGraphicalRepresentation(endObject,aDrawing),
				aDrawable,
				aDrawing);
		this.startObject = startObject;
		this.endObject = endObject;
		updatePropertiesFromWKFPreferences();
	}

	public O getModel()
	{
		return getDrawable();
	}
	
	@Override
	public SwimmingLaneRepresentation getDrawing() 
	{
		return (SwimmingLaneRepresentation)super.getDrawing();
	}
	
	public abstract void updatePropertiesFromWKFPreferences();

	protected static <O extends WKFObject> WKFObjectGR<? extends O> getGraphicalRepresentation(O obj, SwimmingLaneRepresentation drawing)
	{
		return (WKFObjectGR<? extends O>)drawing.getGraphicalRepresentation(obj);
	}
	
	/**
	 * Bug 1007430
	 * 
	 * We have here to implement a very subtle scheme.
	 * It may happen that an edge relies two nodes, with one node beeing
	 * hidden by the container of other node. This is functionnaly correct
	 * but may lead to some graphical misunderstanding.
	 * 
	 * We try here to detect this kind of case, and if we detect that a 
	 * container shape cover one of the both nodes, we set the layer to
	 * the minimal of two layers, in order to have the edge BELOW container
	 * 
	 * @param startObjGR
	 * @param endObjGR
	 * @return
	 */
	private int computeBestLayer(ShapeGraphicalRepresentation<?> startObjGR, ShapeGraphicalRepresentation<?> endObjGR)
	{
		if (isConnectorFullyVisible(startObjGR, endObjGR)) {
			return Math.max(startObjGR.getLayer(),endObjGR.getLayer())+1;
		}
		else {
			return minimalLayerHiding(startObjGR, endObjGR);
			//return Math.min(startObjGR.getLayer(),endObjGR.getLayer());
		}
	}

	/**
	 * Bug 1007430, see above
	 * 
	 * @return
	 */
	protected boolean isConnectorFullyVisible()
	{
		return isConnectorFullyVisible(getStartObject(),getEndObject());
	}

	/**
	 * Bug 1007430, see above
	 * 
	 * @param startObjGR
	 * @param endObjGR
	 * @return
	 */
	protected boolean isConnectorFullyVisible(ShapeGraphicalRepresentation<?> startObjGR, ShapeGraphicalRepresentation<?> endObjGR)
	{
		FGEPoint startLocation = getConnector().getStartLocation();
		FGEPoint endLocation = getConnector().getEndLocation();
		
		if (startLocation == null) return true;
		if (endLocation == null) return true;

		if (!startObjGR.isPointVisible(startLocation)) {
			return false;
		}
		if (!endObjGR.isPointVisible(endLocation)) {
			return false;
		}
		
		return true;
	}

	/**
	 * Bug 1007430, see above
	 * 
	 * This method return top-most layer of shape hidding either start 
	 * location or end location of connector
	 * 
	 * @param startObjGR
	 * @param endObjGR
	 * @return
	 */
	protected int minimalLayerHiding(ShapeGraphicalRepresentation<?> startObjGR, ShapeGraphicalRepresentation<?> endObjGR)
	{
		FGEPoint startLocation = getConnector().getStartLocation();
		FGEPoint endLocation = getConnector().getEndLocation();
		
		if (startLocation == null) return -1;
		if (endLocation == null) return -1;

		ShapeGraphicalRepresentation<?> firstHiddingShape = startObjGR.shapeHiding(startLocation);
		ShapeGraphicalRepresentation<?> secondHiddingShape = endObjGR.shapeHiding(endLocation);
		
		if (firstHiddingShape == null) {
			if (secondHiddingShape == null) {
				return -1;
			}
			else {
				return secondHiddingShape.getLayer();
			}
		}
		else {
			if (secondHiddingShape == null) {
				return firstHiddingShape.getLayer();
			}
			else {
				return Math.min(firstHiddingShape.getLayer(),secondHiddingShape.getLayer());
			}
		}
		
	}


	/**
	 * 
	 * @param startObjGR
	 * @param endObjGR
	 */
	private void updateLayer(ShapeGraphicalRepresentation<?> startObjGR, ShapeGraphicalRepresentation<?> endObjGR)
	{
		if (startObjGR != null && endObjGR != null && !switchedToSelectionLayer) {
			setLayer(computeBestLayer(startObjGR,endObjGR));	
		}
	}

	@Override
	protected void refreshConnector(boolean forceRefresh) 
	{
		super.refreshConnector(forceRefresh);
		updateLayer(getStartObject(),getEndObject());
	}
	
	@Override
	public WKFObjectGR<? extends WKFObject> getStartObject()
	{
		if (startObject == null) return null;
		if (startObjectGR == null) {
			startObjectGR = getGraphicalRepresentation(startObject,getDrawing());
			enableStartObjectObserving(startObjectGR);
			updateLayer(startObjectGR,endObjectGR);
		}
		return startObjectGR;
	}
	
	@Override
	public WKFObjectGR<? extends WKFObject> getEndObject()
	{
		if (endObject == null) return null;
		if (endObjectGR == null) {
			endObjectGR = getGraphicalRepresentation(endObject,getDrawing());
			enableEndObjectObserving(endObjectGR);
			updateLayer(startObjectGR,endObjectGR);
		}
		return endObjectGR;
	}
	
	@Override
	public void notifyObjectHierarchyHasBeenUpdated() 
	{
		super.notifyObjectHierarchyHasBeenUpdated();
		if (isConnectorConsistent()) {
			updateLayer(getStartObject(),getEndObject());
			notifyConnectorChanged();
		}
	}
	
	protected void dismissGraphicalRepresentation()
	{
		disableStartObjectObserving();
		disableEndObjectObserving();
		startObjectGR = null;
		endObjectGR = null;
		getDrawing().invalidateGraphicalObjectsHierarchy(getModel());
	}
	
	private boolean switchedToSelectionLayer = false;

	protected void switchToSelectionLayer()
	{
		switchedToSelectionLayer = true;
		setLayer(SELECTION_LAYER+1);
	}

	protected void restoreNormalLayer()
	{
		if (switchedToSelectionLayer) {
			switchedToSelectionLayer = false;
		}
		updateLayer();
	}

	public void updateLayer()
	{
		setLayer(computeBestLayer(getStartObject(),getEndObject()));
	}


	
}
