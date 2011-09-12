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
package org.openflexo.fps.automerge;

import java.util.Date;

import org.openflexo.diff.DelimitingMethod;
import org.openflexo.diff.merge.DefaultAutomaticMergeResolvingModel;
import org.openflexo.diff.merge.DefaultMergedDocumentType;
import org.openflexo.diff.merge.MergeChange;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.toolbox.TokenMarkerStyle;
import org.openflexo.xmlcode.StringEncoder;


/**
 * Utility class used to define AutomaticMergeResolvingModel for the
 * MergedDocumentType for each ResourceType.
 * 
 * @author sylvain
 */
public class DMAutomergeInitializer {

    protected static final XMLAutomaticMergeResolvingRule NEWEST_LAST_UPDATE 
   = new XMLAutomaticMergeResolvingRule() {
	   @Override
	public String getMergedResult(MergeChange change) 
	   {
		   String leftDateAsString = extractContainerAttributeValueFromLeft(change, "lastUpdate");
		   String rightDateAsString = extractContainerAttributeValueFromRight(change, "lastUpdate");
		   Date leftDate = StringEncoder.decodeObject(leftDateAsString,Date.class);
		   Date rightDate = StringEncoder.decodeObject(rightDateAsString,Date.class);
		   if (leftDate.after(rightDate)) {
			return change.getLeftText();
		}
		   return change.getRightText();
	   }
	   @Override
	public boolean isApplicable(MergeChange change) 
	   {
		   return isInsideAnXMLAttributeValueConflict(change, "lastUpdate");
	   }			
	   @Override
	public String getDescription() 
	   {
		   return "choosen_newest_last_update";
	   }
   };

	public static void initialize()
	{
		DefaultAutomaticMergeResolvingModel dmAutomergeModel
		= new FlexoAutomaticMergeResolvingModel();

		dmAutomergeModel.addToDetailedRules(NEWEST_LAST_UPDATE);

		ResourceType.DATA_MODEL.setMergedDocumentType(new DefaultMergedDocumentType(
				DelimitingMethod.XML,
				TokenMarkerStyle.XML,
				dmAutomergeModel));

	}
}
