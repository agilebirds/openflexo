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
package org.openflexo.foundation.dm;

import java.util.Vector;

import javax.naming.InvalidNameException;
import javax.swing.tree.TreeNode;

import org.openflexo.foundation.Inspectors;
import org.openflexo.localization.FlexoLocalization;

public abstract class DMRepositoryFolder extends DMObject {

    private Vector<DMRepository> _repositories;
    private boolean _internalRepresentationNeedsToBeUpdated;

    /**
     * Default constructor
     */
    public DMRepositoryFolder(DMModel dmModel)
    {
        super(dmModel);
        _repositories = new Vector<DMRepository>();
        _internalRepresentationNeedsToBeUpdated = true;
     }

     public abstract int getRepositoriesCount();

    public abstract DMRepository getRepositoryAtIndex(int index);

    @Override
	public abstract String getName();

    @Override
	public String getLocalizedName()
    {
        return FlexoLocalization.localizedForKey(getName());
    }

    public void setLocalizedName(String aName)
    {
        // avoid exception
    }

    public String getLocalizedDescription()
    {
        return FlexoLocalization.localizedForKey(getName()+"_description");
    }

    public void setLocalizedDescription(String aName)
    {
        // avoid exception
    }

    @Override
	public void setName(String aName) throws InvalidNameException {
    }

    /**
     * Overrides isNameValid
     * @see org.openflexo.foundation.dm.DMObject#isNameValid()
     */
    @Override
    public boolean isNameValid()
    {
        return true;
    }

    @Override
	public abstract String getFullyQualifiedName();

    /**
     * Return String uniquely identifying inspector template which must be
     * applied when trying to inspect this object
     *
     * @return a String value
     */
    @Override
	public String getInspectorName()
    {
        return Inspectors.DM.DM_REPOSITORY_FOLDER_INSPECTOR;
    }


    protected void updateInternalRepresentation()
    {
        _internalRepresentationNeedsToBeUpdated = true;
    }

    private synchronized Vector<DMRepository> getInternalRepresentation()
    {
        if (_internalRepresentationNeedsToBeUpdated) {
            _repositories.clear();
            for (int i=0; i<getRepositoriesCount(); i++) {
                _repositories.add(getRepositoryAtIndex(i));
            }
            _internalRepresentationNeedsToBeUpdated = false;
        }
        return _repositories;
    }

    @Override
	public Vector<DMRepository> getEmbeddedDMObjects()
    {
        return getInternalRepresentation();
    }

    @Override
	public boolean isDeletable()
    {
        return false;
    }

    @Override
	public synchronized Vector<DMRepository> getOrderedChildren()
    {
         return getInternalRepresentation();
    }

    @Override
	public TreeNode getParent()
    {
        return getDMModel();
    }

    @Override
	public boolean getAllowsChildren()
    {
        return true;
    }

    @Override
	public void notifyReordering(DMObject cause)
    {
        _internalRepresentationNeedsToBeUpdated = true;
         super.notifyReordering(cause);
    }


}
