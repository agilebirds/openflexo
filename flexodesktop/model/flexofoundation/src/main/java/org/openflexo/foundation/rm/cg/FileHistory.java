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
package org.openflexo.foundation.rm.cg;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.cg.version.AbstractCGFileVersion;
import org.openflexo.foundation.cg.version.CGFileIntermediateVersion;
import org.openflexo.foundation.cg.version.CGFileReleaseVersion;
import org.openflexo.foundation.cg.version.CGRelease;
import org.openflexo.foundation.cg.version.CGVersionIdentifier;
import org.openflexo.foundation.cg.version.CGVersionIdentifier.InvalidVersionFormatException;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;


public class FileHistory 
{
	private static final Logger logger = Logger.getLogger(FileHistory.class.getPackage().getName());

	private CGRepositoryFileResource _resource;
	//private int lastWritingIndex = 0;

	public static final String HISTORY_DIR = ".history";
	
	private Vector<CGFileReleaseVersion> _releasesVersion; 
	private Hashtable<CGVersionIdentifier,AbstractCGFileVersion> _versions; 
	
	public FileHistory(CGRepositoryFileResource resource)
	{
		_resource = resource;
		_releasesVersion = new Vector<CGFileReleaseVersion>();
		_versions = new Hashtable<CGVersionIdentifier,AbstractCGFileVersion>();
		update();
	}
	
	public void update()
	{
		for (AbstractCGFileVersion fileVersion : _versions.values()) {
			fileVersion.delete();
		}
		_releasesVersion.clear();
		_versions.clear();
		
		if (_resource == null
				|| _resource.getResourceFile() == null
				|| _resource.getResourceFile().getFile() == null) return;
				
		
		final File file = _resource.getResourceFile().getFile();
		File directory = new File(file.getParentFile(),HISTORY_DIR);
		if (directory.exists()) {
			File[] candidates = directory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) 
				{
					return ((!name.equals(file.getName())) && (name.indexOf(file.getName()) == 0));
				}
			});
			for (File candidate : candidates) {
				String versionAsString = candidate.getName().substring(file.getName().length());
				CGVersionIdentifier version;
				try {
					version = new CGVersionIdentifier(versionAsString);
					addVersion(candidate,version);
				} catch (InvalidVersionFormatException e) {
					// Forget this file
				}
			}
			Collections.sort(_releasesVersion,new Comparator<CGFileReleaseVersion>() {
				@Override
				public int compare(CGFileReleaseVersion o1, CGFileReleaseVersion o2) {
					return CGVersionIdentifier.COMPARATOR.compare(o1.getVersionId(), o2.getVersionId());
				}
			});
		}
		else {
			logger.fine("Directory "+directory.getAbsolutePath()+" does not exist");
		}
	}
	
	private CGRelease releaseForVersion (CGVersionIdentifier versionId)
	{
		for (CGRelease release : _resource.getCGFile().getRepository().getReleases()) {
			if ((release.getVersionIdentifier().major == versionId.major)
					&& (release.getVersionIdentifier().minor == versionId.minor))
				return release;
		}
 		return null;
	}
	
	private CGFileReleaseVersion releaseVersionForRelease (CGRelease release)
	{
		for (CGFileReleaseVersion releaseVersion : _releasesVersion) {
			if ((release.getVersionIdentifier().major == releaseVersion.getVersionId().major)
					&& (release.getVersionIdentifier().minor == releaseVersion.getVersionId().minor))
				return releaseVersion;
		}
      	return null;
	}
	
	private CGFileReleaseVersion releaseVersionForBeforeFirstRelease ()
	{
		for (CGFileReleaseVersion releaseVersion : _releasesVersion) {
			if ((releaseVersion.getVersionId().major == 0)
					&& (releaseVersion.getVersionId().minor == 0))
				return releaseVersion;
		}
		CGFileReleaseVersion returned = new CGFileReleaseVersion.BeforeFirstRelease(_resource.getCGFile());
		_releasesVersion.add(returned);		
      	return returned;
	}
	
	private void addVersion (File file, CGVersionIdentifier versionId)
	{
		if (logger.isLoggable(Level.FINE))
			logger.fine("Found "+file.getAbsolutePath()+" for version "+versionId);
		
		CGRelease release = null;
		CGFileReleaseVersion fileReleaseVersion = null;
		if ((versionId.major == 0) && (versionId.minor == 0)) {
			fileReleaseVersion = releaseVersionForBeforeFirstRelease();
		}
		else {
			release = releaseForVersion(versionId);
			if (release != null) {
				fileReleaseVersion = releaseVersionForRelease(release);
				if (fileReleaseVersion == null) {
					fileReleaseVersion = new CGFileReleaseVersion(_resource.getCGFile(), release, null);
					_releasesVersion.add(fileReleaseVersion);
				}
			}
		}
		
		if (fileReleaseVersion == null) {
			logger.warning("Inconsistant data in versionning while handling "+versionId);
			return;
		}
 		
		if (versionId.patch == 0) {
			fileReleaseVersion.setFile(file);
			_versions.put(versionId,fileReleaseVersion);
		}
		else {
			CGFileIntermediateVersion intermediateVersion = new CGFileIntermediateVersion(
					_resource.getCGFile(),
					versionId,
					file);
			fileReleaseVersion.addToIntermediateVersions(intermediateVersion);
			_versions.put(versionId,intermediateVersion);
		}
		
	}

	public AbstractCGFileVersion versionWithId(CGVersionIdentifier versionId)
	{
		if (versionId == null) return null;
		AbstractCGFileVersion returned = _versions.get(versionId);
		/*if (returned == null) {
			logger.info("I have:");
			for (CGVersionIdentifier v : _versions.keySet()) {
				logger.info(" > "+v);
			}
		}*/
		return returned;
	}

	public int getNextWritingIndex() 
	{
		CGRelease release = _resource.getCGFile().getRepository().getLastRelease();
		CGFileReleaseVersion fileReleaseVersion = null;
		if (release == null) {
			fileReleaseVersion = releaseVersionForBeforeFirstRelease();
		}
		else {
			fileReleaseVersion = releaseVersionForRelease(release);
			if (fileReleaseVersion == null) {
				logger.warning("Some history files seems to have disappeared. Processing anyway.");
				return 1;
			}
		}
		if (fileReleaseVersion.getIntermediateVersions().size() == 0) return 1;
		return fileReleaseVersion.getIntermediateVersions().lastElement().getVersionId().patch+1;
	}
	
	public void storeCurrentFileInHistory(CGVersionIdentifier.VersionType type)
	{
		if (_resource.getResourceFile().getFile().exists()) {
			CGVersionIdentifier newVersion = _resource.getCGFile().getRepository().getLastReleaseVersionIdentifier().clone();
			newVersion.patch = getNextWritingIndex();
			newVersion.type = type;
			storeCurrentFileInHistoryAs(newVersion);
		}
		else {
			logger.warning("File "+_resource.getResourceFile().getFile().getAbsolutePath()+" does not exist");
		}
	}
	
	public void storeCurrentFileInHistoryAs(CGVersionIdentifier newVersion)
	{
		if (_resource.getResourceFile().getFile().exists()) {
			String newFileName = _resource.getResourceFile().getFile().getName()+"."+newVersion.toString();
			File historyDir = new File(_resource.getResourceFile().getFile().getParentFile(),HISTORY_DIR);
			if (!historyDir.exists())
                historyDir.mkdirs();
			verifyAndCreateCVSIgnoreIfRequired();
			File toCopy = _resource.getResourceFile().getFile();
			File theCopy = new File(historyDir,newFileName);
			try {
				if (_resource instanceof WOFileResource) {
					String baseName = toCopy.getName().substring(0, toCopy.getName().indexOf(".wo"));
					theCopy.mkdirs();
					File originalHTML = new File(toCopy,baseName+".html");
					File copiedHTML = new File(theCopy,baseName+".html"+"."+newVersion.toString());
					FileUtils.copyFileToFile(originalHTML, copiedHTML);
					File originalWOD = new File(toCopy,baseName+".wod");
					File copiedWOD = new File(theCopy,baseName+".wod"+"."+newVersion.toString());
					FileUtils.copyFileToFile(originalWOD, copiedWOD);
					File originalWOO = new File(toCopy,baseName+".woo");
					File copiedWOO = new File(theCopy,baseName+".woo"+"."+newVersion.toString());
					FileUtils.copyFileToFile(originalWOO, copiedWOO);
				}
				else {
					FileUtils.copyFileToFile(toCopy, theCopy);
				}
				addVersion(theCopy,newVersion);
				_resource.getCGFile().notifyHistoryRefreshed();
			} 
			catch (IOException e) {
				logger.warning("File "+theCopy.getAbsolutePath()+" cound not be written on disk");
				e.printStackTrace();
			}
		}
		else {
			logger.warning("File "+_resource.getResourceFile().getFile().getAbsolutePath()+" does not exist");
		}
	}

    /**
     * 
     */
    private void verifyAndCreateCVSIgnoreIfRequired()
    {
        File cvsIgnore = new File(_resource.getResourceFile().getFile().getParentFile(),".cvsignore");
        if (!cvsIgnore.exists()) {
            try {
                FileUtils.copyFileToFile(new FileResource("Resources/dotCVSignore"), cvsIgnore);
                if (logger.isLoggable(Level.INFO))
                    logger.info(".cvsignore file created at: "+cvsIgnore.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                if (logger.isLoggable(Level.WARNING))
                    logger.warning(".cvsignore file could not be generated");
            }
        }
    }

    public Vector<CGFileReleaseVersion> getReleasesVersion() 
	{
		return _releasesVersion;
	}

	public void clean(boolean cleanBeforeFirstRelease, Vector<CGRelease> releasesToClean) 
	{
		if (cleanBeforeFirstRelease)
			releaseVersionForBeforeFirstRelease().clean();
		for (CGRelease release : releasesToClean) {
			CGFileReleaseVersion releaseVersion = releaseVersionForRelease(release);
			if (releaseVersion != null)
				releaseVersion.clean();
		}
	}
	
	public void refresh()
	{
		update();
		_resource.getCGFile().notifyHistoryRefreshed();
	}
}
