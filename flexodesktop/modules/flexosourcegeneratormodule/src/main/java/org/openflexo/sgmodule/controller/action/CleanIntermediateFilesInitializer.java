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
package org.openflexo.sgmodule.controller.action;

import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.logging.Logger;


import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.cg.version.CGRelease;
import org.openflexo.foundation.cg.version.action.CleanIntermediateFiles;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.InfoLabelParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class CleanIntermediateFilesInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CleanIntermediateFilesInitializer(SGControllerActionInitializer actionInitializer)
	{
		super(CleanIntermediateFiles.actionType,actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() 
	{
		return (SGControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CleanIntermediateFiles> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<CleanIntermediateFiles>() {
			@Override
			public boolean run(ActionEvent e, CleanIntermediateFiles action)
			{
				ParameterDefinition[] params = new ParameterDefinition[action.getRepository().getReleases().size()+2];
				params[0] = new InfoLabelParameter("info","info",action.getLocalizedDescription(),false,6,30);
				params[1] = new CheckboxParameter("beforeFirstRelease","clean_all_intermediate_versions_before_first_release",true);
				action.getRepository().ensureReleasesAreSorted();
				for (int i=0; i<action.getRepository().getReleases().size(); i++) {
					CGRelease release = action.getRepository().getReleases().elementAt(i);
					params[i+2] = new CheckboxParameter("after"+i,null,true);
					params[i+2].setLocalizedLabel(FlexoLocalization.localizedForKey("clean_all_intermediate_versions_after_release")+" "+release.getVersionIdentifier().versionAsString());
				}

				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(
						getProject(), 
						null,
						action.getLocalizedName(), FlexoLocalization.localizedForKey("please_choose_releases_to_clean"), params);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					action.setCleanBeforeFirstRelease(((CheckboxParameter)params[1]).getValue());
					Vector<CGRelease> releasesToClean = new Vector<CGRelease>();
					for (int i=0; i<action.getRepository().getReleases().size(); i++) {
						if (((CheckboxParameter)params[i+2]).getValue()) releasesToClean.add(action.getRepository().getReleases().elementAt(i));
					}
					action.setReleasesToClean(releasesToClean);
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CleanIntermediateFiles> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<CleanIntermediateFiles>() {
			@Override
			public boolean run(ActionEvent e, CleanIntermediateFiles action)
			{
				return true;
			}
		};
	}

}
