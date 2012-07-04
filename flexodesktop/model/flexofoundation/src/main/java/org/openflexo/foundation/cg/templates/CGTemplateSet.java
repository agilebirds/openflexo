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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.toolbox.StringUtils;

public abstract class CGTemplateSet extends CGTemplateObject {

	protected static final Logger logger = Logger.getLogger(CGTemplateSet.class.getPackage().getName());

	private Hashtable<String, CGTemplate> templates;
	private CGTemplateRepository _repository;
	private boolean isUpdating = false;
	private CGTemplateFolder rootFolder;

	public CGTemplateSet(CGTemplateRepository repository) {
		super();
		_repository = repository;
		templates = new Hashtable<String, CGTemplate>();
		rootFolder = new CGTemplateFolder(this, getRootFolderName()) {
			@Override
			public String getName() {
				return getRootFolderName();
			}
		};
	}

	public String getRootFolderName() {
		return getName();
	}

	public CGTemplateFolder getRootFolder() {
		return rootFolder;
	}

	protected void registerTemplate(CGTemplate template) {
		CGTemplateFolder folder = retrieveFolder(template.getFolderPath());
		folder.templates.add(template);
	}

	protected void unregisterTemplate(CGTemplate template) {
		CGTemplateFolder folder = retrieveFolder(template.getFolderPath());
		folder.templates.remove(template);
	}

	private CGTemplateFolder retrieveFolder(String folderPath) {
		if (StringUtils.isEmpty(folderPath)) {
			return getRootFolder();
		}
		StringTokenizer st = new StringTokenizer(folderPath, "/" + File.separator);
		CGTemplateFolder current = getRootFolder();
		while (st.hasMoreElements()) {
			String next = st.nextToken();
			if (current.getFolder(next) == null) {
				CGTemplateFolder newFolder = new CGTemplateFolder(this, next);
				current.dirs.add(newFolder);
				current = newFolder;
			} else {
				current = current.getFolder(next);
			}
		}
		return current;
	}

	@Override
	public void update() {
		if (isUpdating) {
			return;
		}
		// logger.info("********** Updating templates for directory "+_directory.getAbsolutePath());
		try {
			Set<String> previousKnownTemplates = new HashSet<String>(templates.keySet());

			CGTemplate[] newTemplates = findAllTemplates();

			if (newTemplates != null) {
				for (CGTemplate template : newTemplates) {
					String relativePath = template.getRelativePathWithoutSetPrefix();
					if (templates.get(relativePath) == null) {
						// logger.info(">>>> Adding file "+file.getAbsolutePath()+" relative path = "+relativePath);
						templates.put(relativePath, template);
						registerTemplate(template);
					}
					if (previousKnownTemplates.contains(relativePath)) {
						previousKnownTemplates.remove(relativePath);
					}
				}
			}

			for (String templateRelativePath : previousKnownTemplates) {
				CGTemplate template = templates.get(templateRelativePath);
				if (template == null) {
					continue; // GPO: see bug 1007011 (this should not happen because I prevented recursion from happening with flag
								// isUpdating, but I leave this just to be really sure that no NPE
				}
				// will occur)
				if (!template.isDeleted()) {
					template.delete();
				}
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Removing " + templateRelativePath);
				}
				templates.remove(templateRelativePath);
				unregisterTemplate(template);
			}
			for (CGTemplate template : templates.values()) {
				if (template.getIsVersionOnDiskSeemsNewer()) {
					logger.fine("Updating file: " + template.getRelativePath());
					template.update(true);
				}
			}
		} finally {
			isUpdating = false;
		}
	}

	/**
	 * Returns all templates associated to this set.
	 * 
	 * @return all templates associated to this set.
	 */
	protected abstract CGTemplate[] findAllTemplates();

	@Override
	public FlexoProject getProject() {
		return _repository.getProject();
	}

	@Override
	public String getClassNameKey() {
		return "template_set";
	}

	public Enumeration<CGTemplate> getAllTemplates() {
		return templates.elements();
	}

	public Enumeration<CGTemplate> getSortedTemplates() {
		Vector<CGTemplate> sortedTemplates = new Vector<CGTemplate>(templates.values());
		Collections.sort(sortedTemplates, new CGTemplate.CGTemplateComparator());
		return sortedTemplates.elements();
	}

	// TODO: optimize this
	public List<CGTemplate> getTemplateList() {
		List<CGTemplate> sortedTemplates = new ArrayList<CGTemplate>(templates.values());
		Collections.sort(sortedTemplates, new CGTemplate.CGTemplateComparator());
		return sortedTemplates;
	}

	public CGTemplateRepository getRepository() {
		return _repository;
	}

	@Override
	public CGTemplates getTemplates() {
		return getRepository().getTemplates();
	}

	public CGTemplate getTemplate(String templateRelativePath) {
		return templates.get(templateRelativePath);
	}

	@Override
	public abstract String getName();
}
