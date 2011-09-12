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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.netbeans.lib.cvsclient.Client;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;
import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.update.UpdateCommand;
import org.netbeans.lib.cvsclient.connection.AbstractConnection;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.connection.StandardScrambler;
import org.netbeans.lib.cvsclient.event.CVSAdapter;
import org.netbeans.lib.cvsclient.event.EnhancedMessageEvent;
import org.netbeans.lib.cvsclient.event.MessageEvent;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.Inspectors;
import org.openflexo.fps.dm.CVSModuleDiscovered;
import org.openflexo.fps.dm.CVSModuleForgotten;
import org.openflexo.fps.dm.HasCVSExplored;
import org.openflexo.fps.dm.WillCVSExplore;

public class CVSRepository extends FPSObject implements CVSExplorable {

	private static final Logger logger = Logger.getLogger(CVSRepository.class.getPackage().getName());

	private String name;
	private String userName;
	private String hostName;
    private String repository;
    private int port = -1;    
    private String passwd;
    private String encodedPasswd;
    private boolean storePassword = false;
    private ConnectionType _connectionType;
   
    private static final transient String NAME = "Name";
    private static final transient String USER_NAME = "UserName";
    private static final transient String HOST_NAME = "HostName";
    private static final transient String REPOSITORY = "Repository";
    private static final transient String PORT = "Port";
    private static final transient String CONNECTION_TYPE = "ConnectionType";
    private static final transient String ENCODED_PASSWD = "EncodedPassword";
    private static final transient String PASSWD = "Password";
   
    public CVSRepository()
    {
        _modules = new Vector<CVSModule>();
    }
    
     public CVSRepository(Properties props)
     {
    	 this();
    	 name = props.getProperty(NAME);
    	 userName = props.getProperty(USER_NAME);
    	 hostName = props.getProperty(HOST_NAME);
    	 repository = props.getProperty(REPOSITORY);
    	 if (props.getProperty(PORT) != null) {
    		 try {
    		 port = Integer.parseInt(props.getProperty(PORT));
    		 }
    		 catch(NumberFormatException e) {
    			 logger.warning("Could not decode "+props.getProperty(PORT)+" as port number");
    		 }
    	 }
    	 if (props.getProperty(PASSWD) != null) {
    		 passwd = props.getProperty(PASSWD);
    		 storePassword = true;
    	 }
        if (props.getProperty(ENCODED_PASSWD) != null) {
            encodedPasswd = props.getProperty(ENCODED_PASSWD);
            storePassword = true;
        }
        _connectionType = ConnectionType.get(props.getProperty(CONNECTION_TYPE));
    }

    public CVSRepository(File propFile)
     {
    	 this(getProperties(propFile));
     }
     
 	@Override
	public CVSExplorable getParent() 
 	{
		// A repository has a 'null' parent
		return null;
	}

 	@Override
	public CVSRepository getCVSRepository()
 	{
 		return this;
 	}
 	
    private static Properties getProperties (File propFile)
     {
    	 Properties returned = new Properties();
         try {
			returned.loadFromXML(new FileInputStream(propFile));
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
			logger.warning("Could not load "+propFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.warning("Could not load "+propFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			logger.warning("Could not load "+propFile.getAbsolutePath());
		}
		return returned;
     }
     
     public Properties getProperties()
     {
    	Properties properties = new Properties();
    	properties.setProperty(NAME, name!=null?name:"");
    	properties.setProperty(USER_NAME, userName!=null?userName:"");
    	properties.setProperty(HOST_NAME, hostName!=null?hostName:"");
    	properties.setProperty(REPOSITORY, repository!=null?repository:"");
    	properties.setProperty(PORT, ""+port);
       	properties.setProperty(CONNECTION_TYPE, _connectionType.getUnlocalizedStringRepresentation());
       	if (storePassword) {
       		if (_connectionType == ConnectionType.PServer && encodedPasswd != null) 
       			properties.setProperty(ENCODED_PASSWD, encodedPasswd);
       		if (_connectionType == ConnectionType.SSH && passwd != null) 
       			properties.setProperty(PASSWD, passwd);
       	}
       	return properties;
    }
    
    public String getCVSRoot() {
 		return ":"+getConnectionType().getUnlocalizedStringRepresentation()+":"+(getUserName()!=null?getUserName():"")+"@"+getHostName()+":"+getRepository();
 	}
     
	public ConnectionType getConnectionType() 
	{
		return _connectionType;
	}

	public void setConnectionType(ConnectionType connectionType) 
	{
		_connectionType = connectionType;
	}

	public String getEncodedPasswd() 
	{
		return encodedPasswd;
	}

	public void setEncodedPasswd(String encodedPasswd) 
	{
		this.encodedPasswd = encodedPasswd;
		setChanged();
	}

	public void setPassword(String passwd, boolean scramble)
	{
		if (scramble) {
			this.passwd = passwd;
			this.encodedPasswd = StandardScrambler.getInstance().scramble(passwd);
		}
		else {
			this.passwd = passwd;
		}
		setChanged();
		notifyObservers(new DataModification(DataModification.ATTRIBUTE,"password",null,passwd));
	}

	public void setPassword(String passwd)
	{
		setPassword(passwd,(getConnectionType()==ConnectionType.PServer));
	}

	public String getPassword()
	{
		if (passwd == null) return encodedPasswd;
		return passwd;
	}
	
	public String getHostName() 
	{
		return hostName;
	}

	public void setHostName(String hostName) 
	{
		this.hostName = hostName;
		setChanged();
	}

	public String getRepository() 
	{
		return repository;
	}

	public void setRepository(String repository) 
	{
		this.repository = repository;
		setChanged();
	}

	public String getUserName() 
	{
		return userName;
	}

	public void setUserName(String userName) 
	{
		this.userName = userName;
		setChanged();
	}

	@Override
	public String getName() 
	{
		return name;
	}

	@Override
	public void setName(String aName) 
	{
		if (name == null || !name.equals(aName)) {
			if (name != null && getCVSRepositoryLocationFile().exists()) 
				getCVSRepositoryLocationFile().delete();
			name = aName;
			setChanged();
		}
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
	

	private class ModuleRetriever extends CVSAdapter
	{
		Vector<CVSModule> _knownModules;
		
		private ModuleRetriever (Vector<CVSModule> knownModules)
		{
			_knownModules = knownModules;
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
	    	
	    	//logger.info("messageSent(MessageEvent) "+e+" "+e.getMessage());
	    	if (e instanceof EnhancedMessageEvent) {
	    		return;
	    	}
	    	else {
	    		int start = e.getMessage().indexOf("`");
	    		int stop = e.getMessage().indexOf("'");
	    		if (start >=0 && stop >= start) {
	    			String foundModule = e.getMessage().substring(start+1,stop);
	    			if (!foundModule.equals("CVSROOT")) {
	    				//logger.info("Found "+foundModule+" ! Add it.");
	    				CVSModule existingModule = null;
	    				for (CVSModule m : _knownModules) {
	    					if (m.getModuleName().equals(foundModule)) existingModule = m;
	    				}
	    				if (existingModule == null) {
	    					addToCVSModules(new CVSModule(foundModule,CVSRepository.this));
	    				} else {
	    					_knownModules.remove(existingModule);
	    				}
	    			}
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
	
	/**
	 * Little hack described on http://www.netbeans.org/issues/show_bug.cgi?id=35239
	 * @throws IOException
	 * @throws AuthenticationException 
	 * @throws CommandException 
	 * @throws CommandAbortedException 
	 */
	protected void _retrieveModules() throws IOException, CommandAbortedException, CommandException, AuthenticationException
	{
		logger.info("retrieveModules");
	
		// Delete existing directory
		if (_repositoryExploringDirectory != null && _repositoryExploringDirectory.exists()) {
			FileUtils.recursiveDeleteFile(_repositoryExploringDirectory);
		}
		
        // Create an empty temporary folder
        File aTempFile = null;
        aTempFile = File.createTempFile("CVSRepositoryExploring."+Integer.toHexString(hashCode())+".", "");
        String aTempFileName = aTempFile.getAbsolutePath();
        aTempFile.delete();
        
        _repositoryExploringDirectory = new File(aTempFileName);
        _repositoryExploringDirectory.mkdir();
 
        // makes a CVS sub-folder
        File cvsDir = new File( _repositoryExploringDirectory, "CVS" );
        cvsDir.mkdir();

        // Create CVS/Entries file with "D\n" content.
        File cvsEntries = new File( cvsDir, "Entries" );
        try {
			FileUtils.saveToFile(cvsEntries, "D\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        // Create CVS/Root file with repository content.
        File cvsRoot = new File( cvsDir, "Root" );
		FileUtils.saveToFile(cvsRoot, getCVSRoot()+"\n");
        
        // Create CVS/Repository file with repository content.
        File cvsRepository = new File( cvsDir, "Repository" );
		FileUtils.saveToFile(cvsRepository, ".\n");
       
        // Run "cvs -n update -d" command. It will list the
        // files and folders in the trunk on the server.
        UpdateCommand updt = new UpdateCommand();
        updt.setBuildDirectories( true ); // build directories '-d'

		AbstractConnection connection = CVSConnection.initConnection(this);

		GlobalOptions globalOptions = new GlobalOptions();
		globalOptions.setDoNoChanges( true ); // no changes on files '-n'
		globalOptions.setCVSRoot(getCVSRoot()); // Je sais pas ????

 		StandardAdminHandler adminHandler = new StandardAdminHandler();
		Client client = new Client(connection, adminHandler);
		client.setLocalPath(_repositoryExploringDirectory.getCanonicalPath());
		Vector<CVSModule> knownModules = (Vector<CVSModule>)getCVSModules().clone();
		ModuleRetriever retriever = new ModuleRetriever(knownModules);
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
				logger.warning("Fermeture de la connexion impossible"+e.getMessage());
			}
			//FileUtils.recursiveDeleteFile(aTempDir);
		}
		
		for (CVSModule m : knownModules) {
			removeFromCVSModules(m);
		}
		
		logger.info("Retrieve modules DONE");     
		_isConnected = true;
	}
	
	private boolean _isConnected = false;
	
	public boolean isConnected()
	{
		return _isConnected;
	}
	
	@Override
	public boolean isEnabled() 
	{
		return (isConnected() || (_explorer != null && !_explorer.isError()));
	}

	public void disconnect()
	{
		for (CVSModule m : (Vector<CVSModule>)getCVSModules().clone())  {
			removeFromCVSModules(m);
		}
		_isConnected = false;
	}
	
	public enum ConnectionType implements StringConvertable, ChoiceList
	{
		PServer,
		SSH;

		public String getUnlocalizedStringRepresentation() 
		{
			if (this == PServer) return "pserver";
			else if (this == SSH) return "ssh";
			return "???";
		}
		
		public String getStringRepresentation()
		{
			return FlexoLocalization.localizedForKey(getUnlocalizedStringRepresentation());
		}

		@Override
		public StringEncoder.Converter getConverter()
		{
			return connectionTypeConverter;
		}
		public static StringEncoder.Converter connectionTypeConverter = new Converter<ConnectionType>(ConnectionType.class) {

			@Override
			public ConnectionType convertFromString(String value)
			{
				for (ConnectionType cs : values()) {
					if (cs.getStringRepresentation().equals(value)) return cs;
				}
				return null;
			}

			@Override
			public String convertToString(ConnectionType value)
			{
				return value.getStringRepresentation();
			}

		};

	    public static ConnectionType get(String connectionTypeName)
	    {
	        for (Enumeration e = availableValues().elements(); e.hasMoreElements();) {
	        	ConnectionType temp = (ConnectionType) e.nextElement();
	            if (temp.getUnlocalizedStringRepresentation().equals(connectionTypeName)) {
	                return temp;
	            }
	        }

	        if (logger.isLoggable(Level.WARNING))
	            logger.warning("Could not find ConnectionType named " + connectionTypeName);
	        return null;
	    }

	    private static Vector<ConnectionType> _availableValues = null;

	    public static Vector<ConnectionType> availableValues()
	    {
	        if (_availableValues == null) {
	            _availableValues = new Vector<ConnectionType>();
	            for (ConnectionType o : values()) {
	            	_availableValues.add(o);
	            }
	        }
	        return _availableValues;
	    }

	    @Override
		public Vector<ConnectionType> getAvailableValues() 
	    {
	    	return availableValues();
	    }

	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.FPS.CVS_REPOSITORY_INSPECTOR;
	}

	@Override
	public String getClassNameKey()
	{
		return "cvs_repository";
	}

	public boolean getStorePassword()
	{
		return storePassword;
	}

	public void setStorePassword(boolean storePassword)
	{
		this.storePassword = storePassword;
		setChanged();
	}
	
	private File getCVSRepositoryLocationFile()
	{
		if (_cvsRepositoryList == null) return null;
		if (_cvsRepositoryList.getStoredRepositoryDirectory() == null) return null;
		return new File(_cvsRepositoryList.getStoredRepositoryDirectory(),getName()+".cvs");
	}
	
	protected void saveCVSRepositoryLocation()
	{
		if (_cvsRepositoryList == null) return;
		if (_cvsRepositoryList.getStoredRepositoryDirectory() == null) return;
		if (!_cvsRepositoryList.getStoredRepositoryDirectory().exists()) _cvsRepositoryList.getStoredRepositoryDirectory().mkdirs();
		saveCVSRepositoryLocation(getCVSRepositoryLocationFile());
	}

	protected void saveCVSRepositoryLocation(File fileToSave)
	{
		if (!fileToSave.exists()) {
			boolean b = false;
			try {
				b = fileToSave.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (!b)
				try {
					fileToSave.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
		try {
			getProperties().storeToXML(new FileOutputStream(fileToSave), "CVS repository location stored on "+(new Date()));
		} catch (IOException e) {
			e.printStackTrace();
			logger.warning("Could not save "+fileToSave.getAbsolutePath());
		}
	}

	private CVSRepositoryList _cvsRepositoryList;

	public CVSRepositoryList getCVSRepositoryList() 
	{
		return _cvsRepositoryList;
	}

	public void setCVSRepositoryList(CVSRepositoryList cvsRepositoryList) 
	{
		_cvsRepositoryList = cvsRepositoryList;
	}
	
	@Override
	public void setChanged()
	{
		saveCVSRepositoryLocation();
		super.setChanged();
	}
	
	@Override
	public final void delete()
	{
		super.delete();		
		getCVSRepositoryLocationFile().delete();
		getCVSRepositoryList().removeFromCVSRepositories(this);
		deleteObservers();
	}
	
	public CVSModule getModuleNamed(String name)
	{
		if (name.lastIndexOf('/') > -1) {
			String parentModuleName = name.substring(0,name.lastIndexOf('/'));
			CVSModule parentModule = getModuleNamed(parentModuleName);
			logger.info("Parent module: "+parentModule.getFullQualifiedModuleName());
			return parentModule.getModuleNamed(name.substring(name.lastIndexOf('/')+1));
		}
		for (CVSModule module : _modules) {
			if (module.getModuleName().equals(name)) return module;
		}
		// Not found, create it
		CVSModule returned;
		addToCVSModules(returned = new CVSModule(name,CVSRepository.this));
		return returned;
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof CVSRepository) {
			CVSRepository r = (CVSRepository)object;
			return (getName().equals(r.getName())
					&& getHostName().equals(r.getHostName())
					&& getConnectionType() == r.getConnectionType()
					&& getPort() == r.getPort()
					&& getUserName().equals(r.getUserName()));
		}
		else return super.equals(object);
	}
	
	@Override
	public boolean isContainedIn(FPSObject obj) 
	{
		if (obj instanceof CVSRepositoryList) {
			return ((CVSRepositoryList)obj).getCVSRepositories().contains(this);
		}
		return (obj == this);
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
	
	public CVSExplorer exploreRepository(CVSExplorerListener explorerListener)
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

	@Override
	public CVSExplorer explore(CVSExplorerListener explorerListener) {
		return exploreRepository(explorerListener);
	}

 }
