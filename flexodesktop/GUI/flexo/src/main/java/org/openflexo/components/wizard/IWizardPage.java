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
package org.openflexo.components.wizard;

import java.awt.Image;

import javax.swing.JComponent;

public interface IWizardPage {

	/**
	 * The title of this page.
	 * 
	 * @return
	 */
	public String getTitle();

	/**
	 * The current wizard that is using this page
	 * 
	 * @param wizard
	 */
	public void setWizard(FlexoWizard wizard);

	/**
	 * The next page of this wizard page. Can be null.
	 * 
	 * @return
	 */
	public IWizardPage getNextPage();

	/**
	 * The previous page of this wizard page. Can be null.
	 * 
	 * @return
	 */
	public IWizardPage getPreviousPage();

	public Image getPageImage();

	public boolean isPageComplete();

	public boolean isNextEnabled();

	public boolean isFinishEnabled();

	public boolean isPreviousEnabled();

	public JComponent initUserInterface(JComponent parent);

	public JComponent getUserInterface();

}
