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
import java.util.Vector;

public abstract class FlexoWizard  {
	
	private Vector<IWizardPage> pages;
	
	private IWizardPage currentPage;
	
	public FlexoWizard() {
		pages = new Vector<IWizardPage>();
	}
	
	public void addPage(IWizardPage page) {
		if (page==null)
			return;
		pages.add(page);
		if (pages.size()==1)
			currentPage = page;
	}
	
	public boolean canFinish() {
		for (IWizardPage page : pages) {
			if (!page.isPageComplete())
				return false;
		}
		return true;
	}
	
	public boolean needsPreviousAndNext() {
		return pages.size()>1;
	}
	
	public boolean isPreviousEnabled() {
		return getPreviousPage(currentPage)!=null;
	}
	
	public IWizardPage getCurrentPage() {
		return currentPage;
	}
	
	public void setCurrentPage(IWizardPage currentPage) {
		this.currentPage = currentPage;
	}
	
	public IWizardPage getPreviousPage(IWizardPage page) {
		if (page.getPreviousPage()!=null)
			return page.getPreviousPage();
		else if (page.isPreviousEnabled() && pages.indexOf(page)>0)
			return pages.get(pages.indexOf(page)-1);
		else
			return null;
	}
	
	public boolean isNextEnabled() {
		return getNextPage(currentPage)!=null && currentPage.isPageComplete();
	}
	
	public IWizardPage getNextPage(IWizardPage page) {
		if (page.getNextPage()!=null)
			return page.getNextPage();
		else if (page.isNextEnabled() && pages.indexOf(page)>-1 && pages.indexOf(page)<pages.size()-1)
			return pages.get(pages.indexOf(page)+1);
		else
			return null;
	}
	
	public Image getPageImage() {
		if (currentPage.getPageImage()!=null)
			return currentPage.getPageImage();
		else
			return getDefaultPageImage();
	}
	
	public abstract String getWizardTitle();
	
	public abstract Image getDefaultPageImage();
	
	public abstract void performFinish();
	
	public abstract void performCancel();

}
