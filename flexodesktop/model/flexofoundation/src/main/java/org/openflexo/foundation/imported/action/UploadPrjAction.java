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
package org.openflexo.foundation.imported.action;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.ws.client.PPMWebService.CLProjectDescriptor;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceClient;

public class UploadPrjAction extends FlexoAction<UploadPrjAction, FlexoProject, FlexoProject> {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(UploadPrjAction.class.getPackage().getName());

	public static final FlexoActionType<UploadPrjAction, FlexoProject, FlexoProject> actionType =
		new FlexoActionType<UploadPrjAction, FlexoProject, FlexoProject>("upload_prj",FlexoActionType.defaultGroup) {

			@Override
			public UploadPrjAction makeNewAction(FlexoProject focusedObject, Vector<FlexoProject> globalSelection, FlexoEditor editor) {
				return new UploadPrjAction(focusedObject,globalSelection,editor);
			}

			@Override
			protected boolean isVisibleForSelection(FlexoProject object, Vector<FlexoProject> globalSelection) {
				return true;
			}

			@Override
			protected boolean isEnabledForSelection(FlexoProject object, Vector<FlexoProject> globalSelection) {
				return true;
			}
	};

	static {
		FlexoModelObject.addActionForClass(actionType, FlexoProject.class);
	}

	private String uploadReport;
	private CLProjectDescriptor target;
	PPMWebServiceClient client;
	private File zippedPrj;
	private String _comments;

	protected UploadPrjAction(FlexoProject focusedObject,
			Vector<FlexoProject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		DataHandler zip = new DataHandler(new FileDataSource(zippedPrj));
		try {
			uploadReport = client.uploadPrj(target, zip, _comments!=null && _comments.trim().length()>0 ?_comments:"direct upload from flexo", client.getLogin());
		} catch (PPMWebServiceAuthentificationException e) {
			e.printStackTrace();
			throw new FlexoException(e.getMessage(),e);
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new FlexoException(e.getMessage(),e);
		}
	}

	public String getUploadReport() {
		return uploadReport;
	}

	public void setTargetProject(CLProjectDescriptor target) {
		this.target = target;
	}

	public void setClientWS(PPMWebServiceClient client){
		this.client = client;
	}
	public void setFile(File zipFile) {
		zippedPrj = zipFile;
	}

	public void setComments(String s) {
		_comments = s;
	}
}
