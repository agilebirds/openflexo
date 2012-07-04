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
package org.openflexo.foundation.cg.templates;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.cg.dm.CGDataModification;
import org.openflexo.foundation.rm.FlexoProject;

public abstract class CGTemplateRepository extends CGTemplateObject {

	private CGTemplates _templates;
	private File _directory;
	private CGDirectoryTemplateSet commonTemplates;
	private Hashtable<TargetType, TargetSpecificCGTemplateSet> targetSpecificTemplates;
	private Vector<TargetType> availableTargets;

	public CGTemplateRepository(File directory, CGTemplates templates, Vector<TargetType> availableTargets) {
		super();
		_templates = templates;
		_directory = directory;
		this.availableTargets = availableTargets;
		commonTemplates = makeCommonTemplateSet();
		targetSpecificTemplates = new Hashtable<TargetType, TargetSpecificCGTemplateSet>();
		update();
	}

	public CGDirectoryTemplateSet makeCommonTemplateSet() {
		return new CommonCGTemplateSet(getDirectory(), this, false);
	}

	@Override
	public FlexoProject getProject() {
		return _templates.getProject();
	}

	@Override
	public void update() {
		commonTemplates.update();
		if (availableTargets != null) {
			for (TargetType target : availableTargets) {
				TargetSpecificCGTemplateSet specificTargetSet = getTemplateSetForTarget(target);
				if (specificTargetSet != null) {
					specificTargetSet.update();
				}
			}
		}
	}

	@Override
	public String getClassNameKey() {
		return "template_repository";
	}

	public CGDirectoryTemplateSet getCommonTemplates() {
		return commonTemplates;
	}

	public Enumeration<TargetSpecificCGTemplateSet> getTargetSpecificTemplates() {
		return targetSpecificTemplates.elements();
	}

	public List<TargetSpecificCGTemplateSet> getTargetSpecificTemplateSets() {
		List<TargetSpecificCGTemplateSet> returned = new ArrayList<TargetSpecificCGTemplateSet>();
		returned.addAll(targetSpecificTemplates.values());
		return returned;
	}

	public CGTemplate getTemplateWithRelativePath(String relativePath) {
		if (relativePath == null) {
			return null;
		}
		CGTemplateSet set = null;
		String templateName = null;
		if (relativePath.indexOf('/') > 0) {
			String setName = relativePath.substring(0, relativePath.indexOf('/'));
			templateName = relativePath.substring(relativePath.indexOf('/') + 1);
			set = getTemplateSetForName(setName);
			if (set == null) {
				set = getCommonTemplates();
				templateName = relativePath;
			}
		} else {
			set = getCommonTemplates();
			templateName = relativePath;
		}
		return set.getTemplate(templateName);
	}

	public TargetSpecificCGTemplateSet getTemplateSetForTarget(TargetType target, boolean createIfNonExistent) {
		TargetSpecificCGTemplateSet returned = targetSpecificTemplates.get(target);
		if (returned == null) {
			File targetDir = new File(_directory, target.getTemplateFolderName());
			if (createIfNonExistent && !isApplicationRepository()) {
				if (!targetDir.exists()) {
					targetDir.mkdirs();
				}
			}
			if (targetDir.exists()) {
				returned = new TargetSpecificCGTemplateSet(targetDir, this, target, false);
				targetSpecificTemplates.put(target, returned);
				setChanged();
				notifyObservers(new CGDataModification("templateSetForTarget", null, returned));
			}
		}
		return returned;
	}

	public TargetSpecificCGTemplateSet getTemplateSetForTarget(TargetType target) {
		return getTemplateSetForTarget(target, false);
	}

	public TargetSpecificCGTemplateSet getTemplateSetForName(String name) {
		for (TargetSpecificCGTemplateSet set : targetSpecificTemplates.values()) {
			if (set.getTargetType().getName().equals(name)) {
				return set;
			}
		}
		return null;
	}

	public Vector<CGTemplate> getAllTemplateFiles() {
		Vector<CGTemplate> v = new Vector<CGTemplate>();
		Enumeration<CGTemplate> en = commonTemplates.getAllTemplates();
		while (en.hasMoreElements()) {
			v.add(en.nextElement());
		}
		for (TargetSpecificCGTemplateSet set : targetSpecificTemplates.values()) {
			en = set.getAllTemplates();
			while (en.hasMoreElements()) {
				v.add(en.nextElement());
			}
		}
		return v;
	}

	@Override
	public CGTemplates getTemplates() {
		return _templates;
	}

	public abstract boolean readOnly();

	public File getDirectory() {
		return _directory;
	}

	public boolean isApplicationRepository() {
		return false;
	}

	@Override
	public abstract String getName();

}
