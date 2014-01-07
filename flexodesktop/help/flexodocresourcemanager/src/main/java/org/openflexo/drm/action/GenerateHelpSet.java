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
package org.openflexo.drm.action;

import java.io.File;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.drm.DocItemFolder;
import org.openflexo.drm.DocResourceCenter;
import org.openflexo.drm.helpset.DRMHelpSet;
import org.openflexo.drm.helpset.HelpSetConfiguration;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.toolbox.FileResource;

public class GenerateHelpSet extends FlexoAction<GenerateHelpSet, DocResourceCenter, FlexoObject> {

	private static final Logger logger = Logger.getLogger(GenerateHelpSet.class.getPackage().getName());

	public static FlexoActionType<GenerateHelpSet, DocResourceCenter, FlexoObject> actionType = new FlexoActionType<GenerateHelpSet, DocResourceCenter, FlexoObject>(
			"generate_helpset", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public GenerateHelpSet makeNewAction(DocResourceCenter focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
			return new GenerateHelpSet(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DocResourceCenter object, Vector<FlexoObject> globalSelection) {
			return isEnabledForSelection(object, globalSelection);
		}

		@Override
		public boolean isEnabledForSelection(DocResourceCenter object, Vector<FlexoObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(actionType, DocResourceCenter.class);
	}

	private String baseName;
	private String note;

	private final Vector<HelpSetConfiguration> configurations;
	private final Hashtable<HelpSetConfiguration, DRMHelpSet> generatedHelpsets;

	// private DRMHelpSet newHelpSet;
	// private Language language;

	protected GenerateHelpSet(DocResourceCenter focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		configurations = new Vector<HelpSetConfiguration>();
		generatedHelpsets = new Hashtable<HelpSetConfiguration, DRMHelpSet>();
	}

	/*
	 * public DRMHelpSet getNewHelpSet() { return newHelpSet; }
	 * 
	 * public Language getLanguage() { return language; }
	 * 
	 * public void setLanguage(Language language) { this.language = language; }
	 */

	public String getBaseName() {
		if (baseName == null) {
			baseName = "FlexoHelp";
		}
		return baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	protected void doAction(Object context) {
		logger.info("GenerateHelpSet");
		makeFlexoProgress(FlexoLocalization.localizedForKey("generating_helpset"), configurations.size() + 2);
		getHelpsetDirectory().mkdirs();
		for (HelpSetConfiguration config : configurations) {
			DRMHelpSet helpSet = new DRMHelpSet(getFocusedObject(), getHelpsetDirectory(), getBaseName(), config);
			logger.info("Generating " + helpSet.getLocalizedName());
			setProgress(FlexoLocalization.localizedForKey("generating") + " " + helpSet.getHelpSetDirectory().getName());
			helpSet.generate(getFlexoProgress());
			generatedHelpsets.put(config, helpSet);
		}
		vectorOfGeneratedHelpset = null;
		hideFlexoProgress();
	}

	private File _helpSetDirectory;

	public File getHelpsetDirectory() {
		if (_helpSetDirectory == null) {
			_helpSetDirectory = new FileResource("Help");
		}
		return _helpSetDirectory;
	}

	public void addToGeneration(String aTitle, Language aLanguage, String aDistributionName, Vector<DocItemFolder> someDocItemFolders) {
		configurations.add(new HelpSetConfiguration(aTitle, aLanguage, aDistributionName, someDocItemFolders));
	}

	private Vector<DRMHelpSet> vectorOfGeneratedHelpset;

	public Vector<DRMHelpSet> getVectorOfGeneratedHelpset() {
		if (vectorOfGeneratedHelpset == null) {
			vectorOfGeneratedHelpset = new Vector<DRMHelpSet>();
			vectorOfGeneratedHelpset.addAll(generatedHelpsets.values());
			Collections.sort(vectorOfGeneratedHelpset, new Comparator<DRMHelpSet>() {
				@Override
				public int compare(DRMHelpSet o1, DRMHelpSet o2) {
					int result = o1.getDistributionName().compareTo(o2.getDistributionName());
					if (result != 0) {
						return result;
					}
					return Collator.getInstance().compare(o1.getLanguage().getIdentifier(), o2.getLanguage().getIdentifier());
				}
			});
		}
		return vectorOfGeneratedHelpset;
	}

	public Vector<HelpSetConfiguration> getConfigurations() {
		return configurations;
	}

}
