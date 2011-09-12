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
public class RMAutomergeInitializer {

    protected static final XMLAutomaticMergeResolvingRule MAX_LAST_UNIQUE_ID_SELECTING 
	= new XMLAutomaticMergeResolvingRule() {
		@Override
		public String getMergedResult(MergeChange change) 
		{
			int leftValue = getLeftIntAttributeValue(change);
			int rightValue = getRightIntAttributeValue(change);
			return StringEncoder.encodeInteger(Math.max(leftValue, rightValue));
		}
		@Override
		public boolean isApplicable(MergeChange change) 
		{
			return isXMLAttributeValueConflict(change, "lastUniqueID");
		}			
		@Override
		public String getDescription() 
		{
			return "choosen_maximum_of_lastUniqueID_values";
		}
	};

	   protected static final XMLAutomaticMergeResolvingRule IGNORE_RESOURCES_COUNT 
		= new XMLAutomaticMergeResolvingRule() {
			@Override
			public String getMergedResult(MergeChange change) 
			{
				int leftValue = getLeftIntAttributeValue(change);
				int rightValue = getRightIntAttributeValue(change);
				return StringEncoder.encodeInteger(Math.max(leftValue, rightValue));
			}
			@Override
			public boolean isApplicable(MergeChange change) 
			{
				return isXMLValueConflict(change, "resourcesCount");
			}			
			@Override
			public String getDescription() 
			{
				return "ignore_resource_count";
			}
	   };

	   protected static final XMLAutomaticMergeResolvingRule OLDEST_LAST_IMPORT_DATE 
	   = new XMLAutomaticMergeResolvingRule() {
		   @Override
		public String getMergedResult(MergeChange change) 
		   {
			   String leftDateAsString = extractContainerAttributeValueFromLeft(change, "lastImportDate");
			   String rightDateAsString = extractContainerAttributeValueFromRight(change, "lastImportDate");
			   Date leftDate = StringEncoder.decodeObject(leftDateAsString,Date.class);
			   Date rightDate = StringEncoder.decodeObject(rightDateAsString,Date.class);
			   if (leftDate.before(rightDate)) return change.getLeftText();
			   return change.getRightText();
		   }
		   @Override
		public boolean isApplicable(MergeChange change) 
		   {
			   return isInsideAnXMLAttributeValueConflict(change, "lastImportDate");
		   }			
		   @Override
		public String getDescription() 
		   {
			   return "choosen_oldest_import_date";
		   }
	   };

	   protected static final XMLAutomaticMergeResolvingRule OLDEST_LAST_GENERATION_DATE 
	   = new XMLAutomaticMergeResolvingRule() {
		   @Override
		public String getMergedResult(MergeChange change) 
		   {
			   String leftDateAsString = extractContainerAttributeValueFromLeft(change, "lastGenerationDate");
			   String rightDateAsString = extractContainerAttributeValueFromRight(change, "lastGenerationDate");
			   Date leftDate = StringEncoder.decodeObject(leftDateAsString,Date.class);
			   Date rightDate = StringEncoder.decodeObject(rightDateAsString,Date.class);
			   if (leftDate.before(rightDate)) return change.getLeftText();
			   return change.getRightText();
		   }
		   @Override
		public boolean isApplicable(MergeChange change) 
		   {
			   return isInsideAnXMLAttributeValueConflict(change, "lastGenerationDate");
		   }			
		   @Override
		public String getDescription() 
		   {
			   return "choosen_oldest_last_generation_date";
		   }
	   };

	   protected static final XMLAutomaticMergeResolvingRule OLDEST_LAST_ACCEPTING_DATE 
	   = new XMLAutomaticMergeResolvingRule() {
		   @Override
		public String getMergedResult(MergeChange change) 
		   {
			   String leftDateAsString = extractContainerAttributeValueFromLeft(change, "lastAcceptingDate");
			   String rightDateAsString = extractContainerAttributeValueFromRight(change, "lastAcceptingDate");
			   Date leftDate = StringEncoder.decodeObject(leftDateAsString,Date.class);
			   Date rightDate = StringEncoder.decodeObject(rightDateAsString,Date.class);
			   if (leftDate.before(rightDate)) return change.getLeftText();
			   return change.getRightText();
		   }
		   @Override
		public boolean isApplicable(MergeChange change) 
		   {
			   return isInsideAnXMLAttributeValueConflict(change, "lastAcceptingDate");
		   }			
		   @Override
		public String getDescription() 
		   {
			   return "choosen_oldest_last_accepting_date";
		   }
	   };

	   protected static final XMLAutomaticMergeResolvingRule OLDEST_LAST_GENERATION_CHECKED_DATE 
	   = new XMLAutomaticMergeResolvingRule() {
		   @Override
		public String getMergedResult(MergeChange change) 
		   {
			   String leftDateAsString = extractContainerAttributeValueFromLeft(change, "lastGenerationCheckedDate");
			   String rightDateAsString = extractContainerAttributeValueFromRight(change, "lastGenerationCheckedDate");
			   Date leftDate = StringEncoder.decodeObject(leftDateAsString,Date.class);
			   Date rightDate = StringEncoder.decodeObject(rightDateAsString,Date.class);
			   if (leftDate.before(rightDate)) return change.getLeftText();
			   return change.getRightText();
		   }
		   @Override
		public boolean isApplicable(MergeChange change) 
		   {
			   return isInsideAnXMLAttributeValueConflict(change, "lastGenerationCheckedDate");
		   }			
		   @Override
		public String getDescription() 
		   {
			   return "choosen_oldest_last_generation_checked_date";
		   }
	   };

	   protected static final XMLAutomaticMergeResolvingRule OLDEST_LAST_MODEL_REINJECTING_DATE 
	   = new XMLAutomaticMergeResolvingRule() {
		   @Override
		public String getMergedResult(MergeChange change) 
		   {
			   String leftDateAsString = extractContainerAttributeValueFromLeft(change, "lastModelReinjectingDate");
			   String rightDateAsString = extractContainerAttributeValueFromRight(change, "lastModelReinjectingDate");
			   Date leftDate = StringEncoder.decodeObject(leftDateAsString,Date.class);
			   Date rightDate = StringEncoder.decodeObject(rightDateAsString,Date.class);
			   if (leftDate.before(rightDate)) return change.getLeftText();
			   return change.getRightText();
		   }
		   @Override
		public boolean isApplicable(MergeChange change) 
		   {
			   return isInsideAnXMLAttributeValueConflict(change, "lastModelReinjectingDate");
		   }			
		   @Override
		public String getDescription() 
		   {
			   return "choosen_oldest_last_model_reinjecting_date";
		   }
	   };

	   public static void initialize()
	{
		DefaultAutomaticMergeResolvingModel rmxmlAutomergeModel
		= new FlexoAutomaticMergeResolvingModel();

		rmxmlAutomergeModel.addToDetailedRules(MAX_LAST_UNIQUE_ID_SELECTING);
		rmxmlAutomergeModel.addToDetailedRules(IGNORE_RESOURCES_COUNT);
		rmxmlAutomergeModel.addToDetailedRules(OLDEST_LAST_IMPORT_DATE);
		rmxmlAutomergeModel.addToDetailedRules(OLDEST_LAST_GENERATION_DATE);
		rmxmlAutomergeModel.addToDetailedRules(OLDEST_LAST_ACCEPTING_DATE);
		rmxmlAutomergeModel.addToDetailedRules(OLDEST_LAST_GENERATION_CHECKED_DATE);
		rmxmlAutomergeModel.addToDetailedRules(OLDEST_LAST_MODEL_REINJECTING_DATE);

		ResourceType.RM.setMergedDocumentType(new DefaultMergedDocumentType(
				DelimitingMethod.XML,
				TokenMarkerStyle.XML,
				rmxmlAutomergeModel));

	}
}
