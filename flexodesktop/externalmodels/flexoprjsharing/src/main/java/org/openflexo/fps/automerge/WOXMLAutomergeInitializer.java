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

import org.openflexo.diff.DelimitingMethod;
import org.openflexo.diff.merge.DefaultAutomaticMergeResolvingModel;
import org.openflexo.diff.merge.DefaultMergedDocumentType;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.toolbox.TokenMarkerStyle;

/**
 * Utility class used to define AutomaticMergeResolvingModel for the MergedDocumentType for each ResourceType.
 * 
 * @author sylvain
 */
public class WOXMLAutomergeInitializer {

	public static void initialize() {
		DefaultAutomaticMergeResolvingModel woxmlAutomergeModel = new FlexoAutomaticMergeResolvingModel();

		ResourceType.OPERATION_COMPONENT.setMergedDocumentType(new DefaultMergedDocumentType(DelimitingMethod.XML, TokenMarkerStyle.XML,
				woxmlAutomergeModel));

		ResourceType.POPUP_COMPONENT.setMergedDocumentType(new DefaultMergedDocumentType(DelimitingMethod.XML, TokenMarkerStyle.XML,
				woxmlAutomergeModel));

		ResourceType.TAB_COMPONENT.setMergedDocumentType(new DefaultMergedDocumentType(DelimitingMethod.XML, TokenMarkerStyle.XML,
				woxmlAutomergeModel));

	}
}
