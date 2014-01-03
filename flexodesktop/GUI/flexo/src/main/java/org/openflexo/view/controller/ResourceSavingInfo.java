package org.openflexo.view.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceManager;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.utils.FlexoProgressFactory;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.IProgress;

public class ResourceSavingInfo {

	private static final Logger logger = FlexoLogger.getLogger(ResourceSavingInfo.class.getPackage().getName());

	private final ResourceManager resourceManager;
	private final List<ResourceSavingEntryInfo> entries;

	public ResourceSavingInfo(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
		entries = new ArrayList<ResourceSavingEntryInfo>();
	}

	public void update() {
		entries.clear();
		for (FlexoResource<?> r : resourceManager.getLoadedResources()) {
			ResourceSavingEntryInfo newResourceEntryInfo = new ResourceSavingEntryInfo(r);
			entries.add(newResourceEntryInfo);
		}
	}

	public List<ResourceSavingEntryInfo> getEntries() {
		return entries;
	}

	public void saveSelectedResources(FlexoProgressFactory progressFactory) {
		List<ResourceSavingEntryInfo> resourcesToSave = new ArrayList<ResourceSavingEntryInfo>();
		for (ResourceSavingEntryInfo e : entries) {
			if (e.saveThisResource()) {
				resourcesToSave.add(e);
			}
		}

		IProgress progress = progressFactory.makeFlexoProgress(FlexoLocalization.localizedForKey("saving_resources"),
				resourcesToSave.size());
		for (ResourceSavingEntryInfo e : entries) {
			if (e.saveThisResource()) {
				try {
					logger.info("Saving " + e.resource);
					e.saveModified(progress);
				} catch (SaveResourceException e1) {
					logger.warning("Could not save resource " + e.resource);
					e1.printStackTrace();
				}
			}
		}

		progress.hideWindow();

	}
}