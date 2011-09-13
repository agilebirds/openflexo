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
package org.openflexo.foundation.cg.version;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.rm.cg.ASCIIFile;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.foundation.rm.cg.WOFile;
import org.openflexo.toolbox.FileUtils;


public abstract class AbstractCGFileVersion extends CGObject {

	private static final Logger logger = Logger.getLogger(AbstractCGFileVersion.class.getPackage().getName());

	private CGFile _cgFile;
	private CGVersionIdentifier _versionId;
	private File _file;
	
	
	/**
     * Default constructor
     */
    public AbstractCGFileVersion(CGFile cgFile, CGVersionIdentifier versionId, File file)
    {
        super(cgFile.getGeneratedCode());
        _cgFile = cgFile;
        _file = file;
        _versionId = versionId;
    }
	
	@Override
	public GenerationStatus getGenerationStatus()
	{
		return GenerationStatus.UpToDate;
	}

	@Override
	public boolean hasGenerationErrors() 
	{
		return false;
	}

	@Override
	public boolean needsModelReinjection()
	{
		return false;
	}

	@Override
	public boolean isContainedIn(CGObject obj)
	{
		if (obj == getCGFile()) return true;
		return getCGFile().isContainedIn(obj);
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

	@Override
	public boolean needsRegeneration()
	{
		return false;
	}

	@Override
	public String getFullyQualifiedName()
	{
		return getCGFile().getFullyQualifiedName()+"."+getVersionId();
	}

	public CGFile getCGFile()
	{
		return _cgFile;
	}


	public CGVersionIdentifier getVersionId()
	{
		return _versionId;
	}

	public File getFile() 
	{
		return _file;
	}

	public void setFile(File file) 
	{
		_file = file;
	}

	@Override
	public abstract String getName();

	@Override
	public abstract String getDescription();

	public abstract Date getDate();

	public abstract String getUserId();

	public String getDateAsString()
	{
		if (getDate() != null) {
			return (new SimpleDateFormat("dd/MM HH:mm:ss")).format(getDate());
		}
		return "";
	}
	
	public abstract String getStringRepresentation();

	private String _content;
	
	public String getContent() throws IOFlexoException
	{
		if (_cgFile.getResource().getGeneratedResourceData() instanceof ASCIIFile) {
			if (_content == null) {
				if ((_file != null) && (_file.exists())) {
					try {
						_content = FileUtils.fileContents(_file);
					} catch (IOException e) {
						throw new IOFlexoException(e);
					}
				}
				else {
					logger.warning("Unable to access file "+_file);
					return "Unable to access file "+_file;
				}
			}
			return _content;
		}
		return "???";
	}

	public String getHTMLContent() throws IOFlexoException
	{
		File htmlFile = null;
		if (_cgFile.getResource().getGeneratedResourceData() instanceof WOFile) {
			if (_content == null) {
				if ((_file != null) && (_file.exists())) {
					String name = getFile().getName().substring(0, getFile().getName().indexOf(".wo"));
					try {	
						 htmlFile = new File(getFile(),name+".html"+"."+_versionId.toString());
						_content = FileUtils.fileContents(htmlFile);
					} catch (IOException e) {
						try {	
							 htmlFile = new File(getFile(),name+".html");
							_content = FileUtils.fileContents(htmlFile);
						} catch (IOException e2) {
							throw new IOFlexoException(e);
						}
					}
				}
				else {
					logger.warning("Unable to access file "+htmlFile+" for "+_file);
					return "Unable to access file "+htmlFile+" for "+_file;
				}
			}
			return _content;
		}
		return "???";
	}

	public String getWODContent() throws IOFlexoException
	{
		File wodFile = null;
		if (_cgFile.getResource().getGeneratedResourceData() instanceof WOFile) {
			if (_content == null) {
				if ((_file != null) && (_file.exists())) {
					String name = getFile().getName().substring(0, getFile().getName().indexOf(".wo"));
					try {	
						wodFile = new File(getFile(),name+".wod"+"."+_versionId.toString());
						_content = FileUtils.fileContents(wodFile);
					} catch (IOException e) {
						try {	
							wodFile = new File(getFile(),name+".wod");
							_content = FileUtils.fileContents(wodFile);
						} catch (IOException e2) {
							throw new IOFlexoException(e);
						}
					}
				}
				else {
					logger.warning("Unable to access file "+wodFile+" for "+_file);
					return "Unable to access file "+wodFile+" for "+_file;
				}
			}
			return _content;
		}
		return "???";
	}

	public String getWOOContent() throws IOFlexoException
	{
		File wooFile = null;
		if (_cgFile.getResource().getGeneratedResourceData() instanceof WOFile) {
			if (_content == null) {
				if ((_file != null) && (_file.exists())) {
					String name = getFile().getName().substring(0, getFile().getName().indexOf(".wo"));
					try {	
						wooFile = new File(getFile(),name+".woo"+"."+_versionId.toString());
						_content = FileUtils.fileContents(wooFile);
					} catch (IOException e) {
						try {	
							wooFile = new File(getFile(),name+".woo");
							_content = FileUtils.fileContents(wooFile);
						} catch (IOException e2) {
							throw new IOFlexoException(e);
						}
					}
				}
				else {
					logger.warning("Unable to access file "+wooFile+" for "+_file);
					return "Unable to access file "+wooFile+" for "+_file;
				}
			}
			return _content;
		}
		return "???";
	}

	@Override
	public void delete()
	{
		super.delete();
		deleteObservers();
	}

}
