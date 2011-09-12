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
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.netbeans.lib.cvsclient.Client;
import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;
import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.DefaultFileInfoContainer;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.checkout.CheckoutCommand;
import org.netbeans.lib.cvsclient.command.update.UpdateCommand;
import org.netbeans.lib.cvsclient.connection.AbstractConnection;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.event.CVSAdapter;
import org.netbeans.lib.cvsclient.event.EnhancedMessageEvent;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;
import org.netbeans.lib.cvsclient.event.FileUpdatedEvent;
import org.netbeans.lib.cvsclient.event.MessageEvent;
import org.openflexo.foundation.Inspectors;
import org.openflexo.fps.dm.CVSFileDiscovered;
import org.openflexo.fps.dm.CVSFileForgotten;
import org.openflexo.fps.dm.CVSModuleDiscovered;
import org.openflexo.fps.dm.CVSModuleForgotten;
import org.openflexo.fps.dm.HasCVSExplored;
import org.openflexo.fps.dm.WillCVSExplore;


public class CVSModule extends FPSObject implements CVSExplorable
{
	private static final Logger logger = Logger.getLogger(CVSModule.class.getPackage().getName());

	private String _moduleName;
	private CVSExplorable _parent;
	
	protected CVSModule(String moduleName, CVSExplorable parent)
	{
		super();
		if (logger.isLoggable(Level.FINE))
			logger.fine("Created module "+this+" ("+moduleName+") parent="+parent);
		_moduleName = moduleName;
		_parent = parent;
		_modules = new Vector<CVSModule>();
		_files = new Vector<CVSFile>();
	}

	@Override
	public CVSExplorable getParent()
	{
		return _parent;
	}
	
	public String getModuleName() 
	{
		return _moduleName;
	}
	
	public String getFullQualifiedModuleName() 
	{
		if (getParent() instanceof CVSRepository) return getModuleName();
		else if (getParent() instanceof CVSModule) return ((CVSModule)getParent()).getFullQualifiedModuleName()+'/'+getModuleName();
		return null;
	}
	
	public boolean isSubModule()
	{
		return !(getParent() instanceof CVSRepository);
	}
	
	@Override
	public String getInspectorName() 
	{
		return Inspectors.FPS.CVS_MODULE_INSPECTOR;
	}
	
	@Override
	public String getClassNameKey()
	{
		return "cvs_module";
	}

	@Override
	public CVSRepository getCVSRepository() 
	{
		if (_parent instanceof CVSRepository) return (CVSRepository)_parent;
		else return _parent.getCVSRepository();
	}
	
	@Override
	public boolean isContainedIn(FPSObject obj) 
	{
		if (obj instanceof CVSRepositoryList) {
			return getCVSRepository().isContainedIn(obj);
		}
		else if (obj instanceof CVSRepository) {
			return getCVSRepository() == obj;
		}
		return (obj == this);
	}

	private Vector<CVSModule> _modules;
	
	@Override
	public Vector<CVSModule> getCVSModules() 
	{
		return _modules;
	}

	public void setCVSModules(Vector<CVSModule> modules) 
	{
		_modules = modules;
		setChanged();
	}
	
	public void addToCVSModules(CVSModule module) 
	{
		_modules.add(module);
		setChanged();
		notifyObservers(new CVSModuleDiscovered(module));
	}
	
	public void removeFromCVSModules(CVSModule module) 
	{
		_modules.remove(module);
		setChanged();
		notifyObservers(new CVSModuleForgotten(module));
	}
	
	public CVSModule getModuleNamed(String name)
	{
		if (name.indexOf('/') > -1) {
			String parentModuleName = name.substring(0,name.lastIndexOf('/'));
			CVSModule parentModule = getModuleNamed(parentModuleName);
			return parentModule.getModuleNamed(name.substring(name.lastIndexOf('/')+1));
		}
		for (CVSModule module : _modules) {
			if (module.getModuleName().equals(name)) return module;
		}
		// Not found, create it
		CVSModule returned;
		logger.info ("Create module "+name+" as child of module "+this.getFullQualifiedModuleName());
		addToCVSModules(returned = new CVSModule(name,this));
		return returned;
	}

	private Vector<CVSFile> _files;
	
	public Vector<CVSFile> getCVSFiles() 
	{
		return _files;
	}

	public void setCVSFiles(Vector<CVSFile> modules) 
	{
		_files = modules;
		setChanged();
	}
	
	public void addToCVSFiles(CVSFile module) 
	{
		_files.add(module);
		setChanged();
		notifyObservers(new CVSFileDiscovered(module));
	}
	
	public void removeFromCVSFiles(CVSFile module) 
	{
		_files.remove(module);
		setChanged();
		notifyObservers(new CVSFileForgotten(module));
	}
	
	private CVSExplorer _explorer;
	
	@Override
	public CVSExplorer getCVSExplorer(CVSExplorerListener explorerListener)
	{
		if (_explorer == null) {
			_explorer = new CVSExplorer(this,explorerListener);
		}
		return _explorer;
	}
	
	public CVSExplorer exploreModule(CVSExplorerListener explorerListener)
	{
		_explorer = null;
		CVSExplorer returned = getCVSExplorer(explorerListener);
		returned.explore();
		return returned;
	}
	
	@Override
	public void notifyWillExplore()
	{
		setChanged();
		notifyObservers(new WillCVSExplore());
	}
	
	@Override
	public void notifyHasExplored()
	{
		setChanged();
		notifyObservers(new HasCVSExplored());
	}
	
	private class ModuleRetriever extends CVSAdapter
	{
		private Vector<CVSModule> _knownModules;
		private Vector<CVSFile> _knownFiles;
		private StandardAdminHandler adminHandler;
		
		private ModuleRetriever (Vector<CVSModule> knownModules, Vector<CVSFile> knownFiles)
		{
			_knownModules = knownModules;
			_knownFiles = knownFiles;
			adminHandler = new StandardAdminHandler();
		}
		
		/**
	     * Called when the server wants to send a message to be displayed to
	     * the user. The message is only for information purposes and clients
	     * can choose to ignore these messages if they wish.
	     * @param e the event
	     */
	    @Override
		public void messageSent(MessageEvent e) 
	    {
	    	
	    	//logger.info("**** messageSent(MessageEvent) "+e+" "+e.getMessage());
	    	if (e instanceof EnhancedMessageEvent) {
	    		return;
	    	}
	    	else {
	    		int start = e.getMessage().indexOf("`");
	    		int stop = e.getMessage().indexOf("'");
	    		if (start >=0 && stop >= start) {
	    			String fullQualifiedModule = e.getMessage().substring(start+1,stop);
	    			if (!fullQualifiedModule.equals("CVSROOT")) {
	    				//logger.info("Found "+foundModule+" ! Add it.");
	    				CVSModule existingModule = null;
	    				String foundModule = fullQualifiedModule.substring(fullQualifiedModule.lastIndexOf('/')+1);
	    				for (CVSModule m : _knownModules) {
	    					if (m.getModuleName().equals(foundModule)) existingModule = m;
	    				}
	    				if (existingModule == null) {
	    					addToCVSModules(new CVSModule(foundModule,CVSModule.this));
	    				}
	    				else {
	    					_knownModules.remove(existingModule);
	    				}
	    			}
	    		}
	    	}
	    }
	    
	    /**
	     * Called when a file has been updated
	     * @param e the event
	     */
	    @Override
		public void fileUpdated(FileUpdatedEvent e) 
	    {
	    	if(logger.isLoggable(Level.FINE))
	    		logger.fine("fileUpdated() "+e.getFilePath());
	    }

	    /**
	     * Called when file status information has been received
	     */
	    @Override
		public void fileInfoGenerated(FileInfoEvent e) 
	    {
	    	if(logger.isLoggable(Level.FINE))
	    		logger.fine("fileInfoGenerated()  "+e.getInfoContainer().getClass().getSimpleName()+" "+e.getInfoContainer());
	    	if (e.getInfoContainer() instanceof DefaultFileInfoContainer) {
	    		DefaultFileInfoContainer info = (DefaultFileInfoContainer)e.getInfoContainer();
	    		if (logger.isLoggable(Level.FINE)) 
	    			logger.fine("Added file : "+info.getFile());
				CVSFile existingFile = null;
				String foundFile = info.getFile().getName();
				for (CVSFile f : _knownFiles) {
					if (f.getFileName().equals(foundFile)) existingFile = f;
				}
				Entry entry = null;
				try {
					entry = adminHandler.getEntry(info.getFile());
					if (logger.isLoggable(Level.FINE)) 
		    			logger.fine("Entry for "+info.getFile()+": "+entry);
				} catch (IOException e1) {
					logger.warning("Could not retrieve entry for "+info.getFile());
				}
				if (existingFile == null) {
					addToCVSFiles(new CVSFile(info.getFile(),entry, null));
				}
				else {
					existingFile.setEntry(entry);
					_knownFiles.remove(existingFile);
				}
	    	}
	    }

	}
	
	private File _repositoryExploringDirectory;
	
	@Override
	public File getRepositoryExploringDirectory() 
	{
		return _repositoryExploringDirectory;
	}
	
	private void _retrieveLocalFiles(ModuleRetriever retriever) throws IOException, CommandAbortedException, CommandException, AuthenticationException
	{
		if (logger.isLoggable(Level.FINE)) 
			logger.fine("_retrieveLocalFiles() on directory "+getParent().getRepositoryExploringDirectory());
	
	    // Run "cvs checkout -l" command on parent directory
        CheckoutCommand checkout = new CheckoutCommand();
        checkout.setRecursive(false);
        checkout.setModule(getFullQualifiedModuleName());
        
		AbstractConnection connection = CVSConnection.initConnection(getCVSRepository());

		GlobalOptions globalOptions = new GlobalOptions();
		globalOptions.setCVSRoot(connection.getRepository());

 		Client client = new Client(connection, retriever.adminHandler);
		client.setLocalPath(getCVSRepository().getRepositoryExploringDirectory().getCanonicalPath());
		
		client.getEventManager().addCVSListener(retriever);
		client.getEventManager().addCVSListener(CVSConsole.getCVSConsole());

		CVSConsole.getCVSConsole().commandLog("cvs "+checkout.getCVSCommand());

		logger.info("Command "+checkout.getCVSCommand());        
		try {
			client.executeCommand(checkout, globalOptions);
		} finally {
			try {
				client.getConnection().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		_repositoryExploringDirectory = new File(getParent().getRepositoryExploringDirectory(),getModuleName());
	}
	
	private void _retrieveLocalDirectories(ModuleRetriever retriever) throws IOException, CommandAbortedException, CommandException, AuthenticationException
	{
		if (logger.isLoggable(Level.FINE)) 
			logger.fine("_retrieveLocalDirectories() on directory "+getParent().getRepositoryExploringDirectory());
	
	       // Run "cvs -n update -d" command on parent directory
        UpdateCommand updt = new UpdateCommand();
        updt.setBuildDirectories( true ); // build directories '-d'
        File[] files = new File[1];
        files[0] = _repositoryExploringDirectory.getCanonicalFile();
        updt.setFiles(files);       
        
		AbstractConnection connection = CVSConnection.initConnection(getCVSRepository());

		GlobalOptions globalOptions = new GlobalOptions();
		globalOptions.setDoNoChanges( true ); // no changes on files '-n'
		globalOptions.setCVSRoot(connection.getRepository()); 

		Client client = new Client(connection, retriever.adminHandler);
		client.setLocalPath(getParent().getRepositoryExploringDirectory().getCanonicalPath());

		client.getEventManager().addCVSListener(retriever);
		client.getEventManager().addCVSListener(CVSConsole.getCVSConsole());

		CVSConsole.getCVSConsole().commandLog("cvs "+updt.getCVSCommand());

		logger.info("Command "+updt.getCVSCommand());        
		try {
			client.executeCommand(updt, globalOptions);
		} finally {
			try {
				client.getConnection().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void _retrieveSubModules() throws IOException, CommandAbortedException, CommandException, AuthenticationException
	{
		Vector<CVSModule> knownModules = (Vector<CVSModule>)getCVSModules().clone();
		Vector<CVSFile> knownFiles = (Vector<CVSFile>)getCVSFiles().clone();
		ModuleRetriever retriever = new ModuleRetriever(knownModules,knownFiles);
		
		_retrieveLocalFiles(retriever);
		_retrieveLocalDirectories(retriever);
		
	}
	
    @Override
	public String getFullyQualifiedName() 
    {
        return getFullQualifiedModuleName();
    }

	@Override
	public CVSExplorer explore(CVSExplorerListener explorerListener) {
		return exploreModule(explorerListener);
	}

}
