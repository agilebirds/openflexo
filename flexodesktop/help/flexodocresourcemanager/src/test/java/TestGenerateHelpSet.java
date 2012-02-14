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
import java.util.Arrays;

import junit.framework.TestCase;

import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.action.GenerateHelpSet;
import org.openflexo.localization.FlexoLocalization;

public class TestGenerateHelpSet extends TestCase {

	public void setup() {
		DocResourceManager.instance();
	}

	public void teardown() {

	}

	public void testGenerate() {
		GenerateHelpSet action = GenerateHelpSet.actionType.makeNewAction(null, null, null);
		// A refaire aussi
		action.addToGeneration(FlexoLocalization.localizedForKey("help_for_flexo_tool_set"), DocResourceManager.instance()
				.getDocResourceCenter().getLanguages().firstElement(), "Test",
				Arrays.asList(DocResourceManager.instance().getDocResourceCenter().getModelFolder()));
		action.setNote("none");
		action.doAction();
	}
}
