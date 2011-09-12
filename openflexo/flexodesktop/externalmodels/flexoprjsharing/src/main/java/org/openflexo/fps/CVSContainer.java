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
package org.openflexo.fps;

import java.io.File;
import java.util.Vector;

public interface CVSContainer {

	public SharedProject getSharedProject();
	
	public abstract CVSStatus getDerivedStatus();

	public abstract void notifyStatusChanged(CVSAbstractFile file);

	public Vector<CVSDirectory> getDirectories();

	public void setDirectories(Vector<CVSDirectory> directories);
	
	public void addToDirectories(CVSDirectory aDirectory);
	
	public void removeFromDirectories(CVSDirectory aDirectory);
	
	public Vector<CVSFile> getFiles();

	public void setFiles(Vector<CVSFile> files);
	
	public void addToFiles(CVSFile aFile);
	
	public void removeFromFiles(CVSFile aFile);

	public boolean isRegistered(File aFile);
	
	public CVSAbstractFile getCVSAbstractFile(File aFile);
}
