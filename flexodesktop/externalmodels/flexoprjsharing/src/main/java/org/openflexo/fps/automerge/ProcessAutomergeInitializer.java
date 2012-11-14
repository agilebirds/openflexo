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

import java.awt.Point;
import java.util.Date;

import org.openflexo.diff.DelimitingMethod;
import org.openflexo.diff.merge.DefaultAutomaticMergeResolvingModel;
import org.openflexo.diff.merge.DefaultMergedDocumentType;
import org.openflexo.diff.merge.MergeChange;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.toolbox.TokenMarkerStyle;
import org.openflexo.xmlcode.StringEncoder;

/**
 * Utility class used to define AutomaticMergeResolvingModel for the MergedDocumentType for each ResourceType.
 * 
 * @author sylvain
 */
public class ProcessAutomergeInitializer {

	protected static final XMLAutomaticMergeResolvingRule AVERAGE_POSIX = new XMLAutomaticMergeResolvingRule() {
		@Override
		public String getMergedResult(MergeChange change) {
			int leftValue = getLeftIntAttributeValue(change);
			int rightValue = getRightIntAttributeValue(change);
			return StringEncoder.encodeInteger((leftValue + rightValue) / 2);
		}

		@Override
		public boolean isApplicable(MergeChange change) {
			return isXMLAttributeValueConflict(change, "posiX");
		}

		@Override
		public String getDescription() {
			return "computed_average_of_posiX_values";
		}
	};

	protected static final XMLAutomaticMergeResolvingRule AVERAGE_POSIY = new XMLAutomaticMergeResolvingRule() {
		@Override
		public String getMergedResult(MergeChange change) {
			int leftValue = getLeftIntAttributeValue(change);
			int rightValue = getRightIntAttributeValue(change);
			return StringEncoder.encodeInteger((leftValue + rightValue) / 2);
		}

		@Override
		public boolean isApplicable(MergeChange change) {
			return isXMLAttributeValueConflict(change, "posiY");
		}

		@Override
		public String getDescription() {
			return "computed_average_of_posiY_values";
		}
	};

	protected static final XMLAutomaticMergeResolvingRule MAX_WIDTH = new XMLAutomaticMergeResolvingRule() {
		@Override
		public String getMergedResult(MergeChange change) {
			int leftValue = getLeftIntAttributeValue(change);
			int rightValue = getRightIntAttributeValue(change);
			return StringEncoder.encodeInteger(Math.max(leftValue, rightValue));
		}

		@Override
		public boolean isApplicable(MergeChange change) {
			return isXMLAttributeValueConflict(change, "width");
		}

		@Override
		public String getDescription() {
			return "choosen_maximum_of_width_values";
		}
	};

	protected static final XMLAutomaticMergeResolvingRule MAX_HEIGHT = new XMLAutomaticMergeResolvingRule() {
		@Override
		public String getMergedResult(MergeChange change) {
			int leftValue = getLeftIntAttributeValue(change);
			int rightValue = getRightIntAttributeValue(change);
			return StringEncoder.encodeInteger(Math.max(leftValue, rightValue));
		}

		@Override
		public boolean isApplicable(MergeChange change) {
			return isXMLAttributeValueConflict(change, "height");
		}

		@Override
		public String getDescription() {
			return "choosen_maximum_of_height_values";
		}
	};

	protected static final XMLAutomaticMergeResolvingRule MIDDLE_OF_INDUCED_LOCATION = new XMLAutomaticMergeResolvingRule() {
		@Override
		public String getMergedResult(MergeChange change) {
			Point leftValue = getLeftPointAttributeValue(change);
			Point rightValue = getRightPointAttributeValue(change);
			return StringEncoder.encodeObject(new Point((leftValue.x + rightValue.x) / 2, (leftValue.y + rightValue.y) / 2));
		}

		@Override
		public boolean isApplicable(MergeChange change) {
			return isXMLAttributeValueConflict(change, "inducedLocation");
		}

		@Override
		public String getDescription() {
			return "choosen_middle_of_induced_location";
		}
	};

	protected static final XMLAutomaticMergeResolvingRule MIDDLE_OF_DEDUCED_LOCATION = new XMLAutomaticMergeResolvingRule() {
		@Override
		public String getMergedResult(MergeChange change) {
			Point leftValue = getLeftPointAttributeValue(change);
			Point rightValue = getRightPointAttributeValue(change);
			return StringEncoder.encodeObject(new Point((leftValue.x + rightValue.x) / 2, (leftValue.y + rightValue.y) / 2));
		}

		@Override
		public boolean isApplicable(MergeChange change) {
			return isXMLAttributeValueConflict(change, "deducedLocation");
		}

		@Override
		public String getDescription() {
			return "choosen_middle_of_deduced_location";
		}
	};

	protected static final XMLAutomaticMergeResolvingRule MIDDLE_OF_INDUCED_DEDUCED_LOCATION = new XMLAutomaticMergeResolvingRule() {
		@Override
		public String getMergedResult(MergeChange change) {
			Point leftValue = getLeftPointAttributeValue(change);
			Point rightValue = getRightPointAttributeValue(change);
			return StringEncoder.encodeObject(new Point((leftValue.x + rightValue.x) / 2, (leftValue.y + rightValue.y) / 2));
		}

		@Override
		public boolean isApplicable(MergeChange change) {
			return isXMLAttributeValueConflict(change, "inducedDeducedLocation");
		}

		@Override
		public String getDescription() {
			return "choosen_middle_of_induced_deduced_location";
		}
	};

	protected static final XMLAutomaticMergeResolvingRule NEWEST_LAST_UPDATE = new XMLAutomaticMergeResolvingRule() {
		@Override
		public String getMergedResult(MergeChange change) {
			String leftDateAsString = extractContainerAttributeValueFromLeft(change, "lastUpdate");
			String rightDateAsString = extractContainerAttributeValueFromRight(change, "lastUpdate");
			Date leftDate = StringEncoder.decodeObject(leftDateAsString, Date.class);
			Date rightDate = StringEncoder.decodeObject(rightDateAsString, Date.class);
			if (leftDate.after(rightDate)) {
				return change.getLeftText();
			}
			return change.getRightText();
		}

		@Override
		public boolean isApplicable(MergeChange change) {
			return isInsideAnXMLAttributeValueConflict(change, "lastUpdate");
		}

		@Override
		public String getDescription() {
			return "choosen_newest_last_update";
		}
	};

	public static void initialize() {
		DefaultAutomaticMergeResolvingModel processAutomergeModel = new FlexoAutomaticMergeResolvingModel();

		processAutomergeModel.addToPrimaryRules(new IgnoreRightUnsignificantChangesRemovedByOpposite("lastUpdate", "posiX", "posiY",
				"width", "height", "inducedLocation", "deducedLocation", "inducedDeducedLocation"));
		processAutomergeModel.addToPrimaryRules(new IgnoreLeftUnsignificantChangesRemovedByOpposite("lastUpdate", "posiX", "posiY",
				"width", "height", "inducedLocation", "deducedLocation", "inducedDeducedLocation"));

		processAutomergeModel.addToDetailedRules(AVERAGE_POSIX);
		processAutomergeModel.addToDetailedRules(AVERAGE_POSIY);
		processAutomergeModel.addToDetailedRules(MAX_WIDTH);
		processAutomergeModel.addToDetailedRules(MAX_HEIGHT);
		processAutomergeModel.addToDetailedRules(MIDDLE_OF_INDUCED_LOCATION);
		processAutomergeModel.addToDetailedRules(MIDDLE_OF_DEDUCED_LOCATION);
		processAutomergeModel.addToDetailedRules(MIDDLE_OF_INDUCED_DEDUCED_LOCATION);
		processAutomergeModel.addToDetailedRules(NEWEST_LAST_UPDATE);

		ResourceType.PROCESS.setMergedDocumentType(new DefaultMergedDocumentType(DelimitingMethod.XML, TokenMarkerStyle.XML,
				processAutomergeModel));

	}
}
