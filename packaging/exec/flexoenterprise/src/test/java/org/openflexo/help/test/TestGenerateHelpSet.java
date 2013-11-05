package org.openflexo.help.test;

import org.junit.Test;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.Language;
import org.openflexo.drm.action.GenerateHelpSet;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.module.UserType;

public class TestGenerateHelpSet {

	@Test
	public void testHelpSetGeneration() {
		GenerateHelpSet action = GenerateHelpSet.actionType.makeNewAction(DocResourceManager.instance().getDocResourceCenter(), null,
				new DefaultFlexoEditor(null, null));
		action.setNote("none");
		action.setBaseName("flexoenterprise");
		UserType userType = UserType.DEVELOPER;
		for (Language lg : DocResourceManager.instance().getDocResourceCenter().getLanguages()) {
			String title = lg.getName();
			action.addToGeneration(title, lg, userType.getIdentifier(), userType.getDocumentationFolders());
		}
		action.doAction();
	}
}
