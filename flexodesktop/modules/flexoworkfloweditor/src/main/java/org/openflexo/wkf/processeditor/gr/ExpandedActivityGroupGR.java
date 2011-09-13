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
package org.openflexo.wkf.processeditor.gr;

import java.awt.Color;
import java.util.logging.Level;

import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle.DashStyle;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.ActivityGroup;
import org.openflexo.foundation.wkf.action.OpenGroup;
import org.openflexo.foundation.wkf.dm.GroupUpdated;
import org.openflexo.foundation.wkf.dm.ObjectVisibilityChanged;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.wkf.processeditor.ProcessRepresentation;


public class ExpandedActivityGroupGR extends ContainerGR<ActivityGroup> {


	private static Color makeFadedColor(Color aColor)
	{
		return aColor.brighter();
	}

	public ExpandedActivityGroupGR(ActivityGroup object, ProcessRepresentation aDrawing) 
	{
		super(object, aDrawing,object.getColor(), makeFadedColor(object.getColor()));
		setLayer(ACTIVITY_LAYER);
	}
	
	public ActivityGroup getActivityGroup()
	{
		return getDrawable();
	}
	
	@Override
	public String getLabel()
	{
		return getActivityGroup().getGroupName();
	}

	@Override
	public void closingRequested() 
	{
 		OpenGroup.actionType.makeNewAction(getActivityGroup(), null, getDrawing().getEditor()).doAction();
		/*getDrawing().invalidateGraphicalObjectsHierarchy(getActivityGroup());
		getDrawing().updateGraphicalObjectsHierarchy();*/
	}
	
	@Override
	protected void updateDecorationForeground()
	{
		super.updateDecorationForeground();
		decorationForeground = ForegroundStyle.makeStyle(mainColor);
		decorationForeground.setLineWidth(1);
		decorationForeground.setDashStyle(DashStyle.BIG_DASHES);
	}
	
	@Override
	public void update (FlexoObservable observable, DataModification dataModification)
	{
		//logger.info(">>>>>>>>>>>  Notified "+dataModification+" for "+observable);
		if (observable == getActivityGroup()) {
			if (dataModification instanceof WKFAttributeDataModification) {
				if (((WKFAttributeDataModification)dataModification).getAttributeName().equals("color")) {
					updateBackground(getActivityGroup().getColor(), makeFadedColor(getActivityGroup().getColor()));
				}
			}
			else if (dataModification instanceof GroupUpdated) {
				/*WKFGroup group = ((GroupUpdated)dataModification).newValue();
				Vector<AbstractNode> nodesThatWereInGroup = ((GroupRemoved)dataModification).getNodesThatWereInGroup();
				if (nodesThatWereInGroup != null) {
					for (AbstractNode node : nodesThatWereInGroup) {
						invalidateGraphicalObjectsHierarchy(node);
						node.setX(node.getX(BASIC_PROCESS_EDITOR)+group.getX(BASIC_PROCESS_EDITOR),BASIC_PROCESS_EDITOR);
						node.setY(node.getY(BASIC_PROCESS_EDITOR)+group.getY(BASIC_PROCESS_EDITOR),BASIC_PROCESS_EDITOR);
					}
				}*/
				logger.info("Received GroupUpdated");
				getDrawing().invalidateGraphicalObjectsHierarchy(getActivityGroup().getProcess());
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof ObjectVisibilityChanged) {
				if (logger.isLoggable(Level.INFO))
					logger.info("Group visibility changed");
				getDrawing().invalidateGraphicalObjectsHierarchy(getActivityGroup().getProcess());
				getDrawing().updateGraphicalObjectsHierarchy();
			}
		}
	}


}
