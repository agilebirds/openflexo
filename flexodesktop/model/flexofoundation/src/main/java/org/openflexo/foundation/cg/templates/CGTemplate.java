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

import java.util.Comparator;
import java.util.Date;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.toolbox.FileFormat;


/**
 * Abstract class representing a template.
 * 
 * @author Nicolas Daniels
 */
public abstract class CGTemplate extends CGTemplateObject {

	protected CGTemplateSet set;

	public CGTemplate(CGTemplateSet set) {
		super();
		this.set = set;
	}

	/**
	 * Return the template name (without its eventual folder).
	 * 
	 * @return the template name (without its eventual folder).
	 */
	@Override
	public String getName() {
		return getTemplateName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getClassNameKey() {
		return "template_file";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFullyQualifiedName() {
		return set.getFullyQualifiedName() + "." + getName();
	}

	public String getNiceQualifiedName() {
		return getRepository().getName() + ": " + set.getName() + " - " + getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FlexoProject getProject() {
		return set.getProject();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CGTemplates getTemplates() {
		return getRepository().getTemplates();
	}

	/**
	 * Retrieve the Template set associated to this template.
	 * 
	 * @return the retrieved Template set.
	 */
	public CGTemplateSet getSet() {
		return set;
	}

	/**
	 * Retrieve the Template repository associated to this template.
	 * 
	 * @return the retrieved Template repository.
	 */
	public CGTemplateRepository getRepository() {
		return set.getRepository();
	}

	public boolean isCustomTemplate() {
		return !getRepository().isApplicationRepository();
	}

	public boolean isApplicationTemplate() {
		return getRepository().isApplicationRepository();
	}

	/**
	 * Return the template folder path. The returned path is relative to the template 'principal' parent. <br>
	 * The returned path doesn't start with a '/' but ends with a '/'. <br>
	 * Basically, it will return the result of getRelativePath() without the template name.
	 * 
	 * @return the template folder path.
	 */
	public String getFolderPath() {
		return getRelativePath().substring(0, getRelativePath().length() - getTemplateName().length());
	}

	/**
	 * Trying to guess file format (not very important, just for syntax coloring)
	 * 
	 * @return the calculated format.
	 */
	public FileFormat getFileFormat() {
		String name = getTemplateName().toLowerCase();

		if(name.endsWith(".vm")) {
			name = name.substring(0, name.length() - 3);
		}

		String extension = name.substring(name.lastIndexOf(".")+1);
		FileFormat returned = FileFormat.getDefaultFileFormatByExtension(extension);
		if (returned == FileFormat.UNKNOWN) {
			return FileFormat.UNKNOWN_ASCII_FILE;
		}
		return returned;
	}

	@Override
	public String toString() {
		return getTemplateName() + "/" + getRepository().getName() + "/" + getSet().getName();
	}

	/**
	 * Reload the cached template content if necessary.
	 * 
	 */
	@Override
	public void update() {
		update(false);
	}

	/**
	 * Reload the cached template content if necessary or if forceUpdate is set to true.
	 * 
	 * @param forceUpdate
	 */
	public abstract void update(boolean forceUpdate);

	/**
	 * Return true if the template version on disk (or other storage device) is newer than the last loaded one.
	 * 
	 * @return true if the template version on disk (or other storage device) is newer than the last loaded one.
	 */
	public abstract boolean getIsVersionOnDiskSeemsNewer();

	/**
	 * Return the template name (without its eventual folder, with its extension).
	 * 
	 * @return the template name (without its eventual folder, with its extension).
	 */
	public abstract String getTemplateName();

	/**
	 * Return the template path relative to its principal parent. <br>
	 * The returned path doesn't start with a '/' and contains the template name. This can be used for example to redefine the template in another directory while keeping its uniqueness. <br>
	 * i.e. /home/flexo/flexocodegenerator/resources/templates/java/test.vm will return java/test.vm.
	 * 
	 * @return the template path relative to its principal parent.
	 */
	public abstract String getRelativePath();

	/**
	 * Returns the template content as string
	 * 
	 * @return the template content as string
	 */
	public abstract String getContent();

	/**
	 * This date is use to perform fine tuning resource dependencies computing
	 * 
	 * @return the date when the template has been modified last time.
	 */
	@Override
	public abstract Date getLastUpdate();

	public static class CGTemplateComparator implements Comparator<CGTemplate> {

		@Override
		public int compare(CGTemplate o1, CGTemplate o2) {
			return o1.getTemplateName().compareToIgnoreCase(o2.getTemplateName());
		}
	}

	public abstract String getRelativePathWithoutSetPrefix();

}
