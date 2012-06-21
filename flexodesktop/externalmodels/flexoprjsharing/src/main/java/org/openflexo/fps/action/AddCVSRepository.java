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
package org.openflexo.fps.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.CVSRepository.ConnectionType;
import org.openflexo.fps.CVSRepositoryList;
import org.openflexo.fps.FPSObject;
import org.openflexo.fps.FlexoAuthentificationException;

public class AddCVSRepository extends CVSAction<AddCVSRepository, CVSRepositoryList> {

	private static final Logger logger = Logger.getLogger(AddCVSRepository.class.getPackage().getName());

	public static FlexoActionType<AddCVSRepository, CVSRepositoryList, FPSObject> actionType = new FlexoActionType<AddCVSRepository, CVSRepositoryList, FPSObject>(
			"add_CVS_repository", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddCVSRepository makeNewAction(CVSRepositoryList focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) {
			return new AddCVSRepository(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(CVSRepositoryList object, Vector<FPSObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(CVSRepositoryList object, Vector<FPSObject> globalSelection) {
			return true;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, CVSRepositoryList.class);
	}

	private CVSRepository _newCVSRepository;

	private String name = "Sylvain sur localhost";
	private String userName = "sylvain";
	private String hostName = "localhost";
	private String repository = "/usr/local/CVS";
	private int port = 119;
	private String passwd = "";
	private ConnectionType _connectionType = ConnectionType.PServer;
	private boolean storePasswd;

	AddCVSRepository(CVSRepositoryList focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws IOFlexoException, FlexoAuthentificationException {
		logger.info("AddCVSRepository");
		_newCVSRepository = new CVSRepository();
		_newCVSRepository.setName(name);
		_newCVSRepository.setHostName(hostName);
		_newCVSRepository.setRepository(repository);
		_newCVSRepository.setConnectionType(_connectionType);
		_newCVSRepository.setPort(port);
		_newCVSRepository.setUserName(userName);
		if (_connectionType == ConnectionType.PServer) {
			_newCVSRepository.setPassword(passwd, true);
		} else {
			_newCVSRepository.setPassword(passwd, false);
		}
		_newCVSRepository.setStorePassword(storePasswd);
		getFocusedObject().addToCVSRepositories(_newCVSRepository);
		/*try {
			_newCVSRepository._retrieveModules();
		} catch (IOException e) {
			throw new IOFlexoException(e);
		} catch (CommandAbortedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationException e) {
			throw new FlexoAuthentificationException(_newCVSRepository);
		}*/
	}

	public CVSRepository getNewCVSRepository() {
		return _newCVSRepository;
	}

	public ConnectionType getConnectionType() {
		return _connectionType;
	}

	public void setConnectionType(ConnectionType connectionType) {
		_connectionType = connectionType;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPassword(String passwd) {
		this.passwd = passwd;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean getStorePasswd() {
		return storePasswd;
	}

	public void setStorePasswd(boolean storePasswd) {
		this.storePasswd = storePasswd;
	}

}
