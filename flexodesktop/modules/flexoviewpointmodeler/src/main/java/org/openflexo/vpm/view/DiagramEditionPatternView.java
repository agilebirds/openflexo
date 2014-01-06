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
package org.openflexo.vpm.view;

import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.vpm.VPMCst;
import org.openflexo.vpm.controller.VPMController;

/**
 * This is the module view representing a diagram EditionPattern (an EditionPattern which is part of a DiagramSpecification)<br>
 * 
 * @author sguerin
 * 
 */
public class DiagramEditionPatternView extends EditionPatternView<EditionPattern> {

	public DiagramEditionPatternView(EditionPattern editionPattern, VPMController controller) {
		super(editionPattern, controller, VPMCst.DIAGRAM_EDITION_PATTERN_VIEW_FIB);
	}

}
