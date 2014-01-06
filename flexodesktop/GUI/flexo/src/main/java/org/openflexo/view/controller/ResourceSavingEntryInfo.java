package org.openflexo.view.controller;

import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.IProgress;

public class ResourceSavingEntryInfo {
	private static final Logger logger = FlexoLogger.getLogger(ResourceSavingEntryInfo.class.getPackage().getName());

	protected FlexoResource<?> resource;
	protected boolean saveThisResource = true;

	public ResourceSavingEntryInfo(FlexoResource<?> resource) {
		this.resource = resource;
		reviewModifiedResource();
	}

	public void delete() {
		resource = null;
	}

	public Icon getIcon() {
		return FlexoController.statelessIconForObject(resource);
	}

	public String getName() {
		return resource.getName() + (isModified() ? " [" + FlexoLocalization.localizedForKey("modified") + "]" : "");
	}

	public String getType() {
		if (resource.getResourceDataClass() == null) {
			logger.warning("Resource " + this + " has no resource data class");
			return null;
		}
		return resource.getResourceDataClass().getSimpleName();
	}

	public boolean isModified() {
		try {
			return resource.getResourceData(null).isModified();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean saveThisResource() {
		return saveThisResource;
	}

	public void setSaveThisResource(boolean saveThisResource) {
		this.saveThisResource = saveThisResource;
	}

	public void reviewModifiedResource() {
		saveThisResource = isModified();
	}

	public void saveModified(IProgress progress) throws SaveResourceException {
		progress.setProgress(FlexoLocalization.localizedForKey("saving") + " " + resource.getName());
		if (saveThisResource) {
			resource.save(progress);
		}
	}

}