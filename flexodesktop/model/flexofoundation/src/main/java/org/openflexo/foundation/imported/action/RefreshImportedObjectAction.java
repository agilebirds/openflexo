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

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.ws.client.PPMWebService.PPMWebService_PortType;

public abstract class RefreshImportedObjectAction<A extends RefreshImportedObjectAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
		extends FlexoAction<A, T1, T2> {

	private PPMWebService_PortType webService;
	private String login;
	private String md5Password;
	private boolean isAutomaticAction = false;

	public RefreshImportedObjectAction(FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection,
			FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public boolean isAutomaticAction() {
		return isAutomaticAction;
	}

	public void setAutomaticAction(boolean isAutomaticAction) {
		this.isAutomaticAction = isAutomaticAction;
	}

	public PPMWebService_PortType getWebService() {
		return webService;
	}

	public void setWebService(PPMWebService_PortType webService) {
		this.webService = webService;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getMd5Password() {
		return md5Password;
	}

	public void setMd5Password(String md5Password) {
		this.md5Password = md5Password;
	}

}
