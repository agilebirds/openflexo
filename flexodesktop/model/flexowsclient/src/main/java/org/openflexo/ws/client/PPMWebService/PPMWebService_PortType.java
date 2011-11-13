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

package org.openflexo.ws.client.PPMWebService;

public interface PPMWebService_PortType extends java.rmi.Remote {
	public org.openflexo.ws.client.PPMWebService.PPMRole[] getRoles(java.lang.String login, java.lang.String md5Password)
			throws java.rmi.RemoteException, org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException;

	public org.openflexo.ws.client.PPMWebService.PPMProcess[] getProcesses(java.lang.String login, java.lang.String md5Password)
			throws java.rmi.RemoteException, org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException;

	public byte[] getScreenshoot(java.lang.String login, java.lang.String md5Password, java.lang.String processVersionURI)
			throws java.rmi.RemoteException, org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException;

	public org.openflexo.ws.client.PPMWebService.PPMProcess[] refreshProcesses(java.lang.String login, java.lang.String md5Password,
			java.lang.String[] uris) throws java.rmi.RemoteException,
			org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException;

	public org.openflexo.ws.client.PPMWebService.PPMRole[] refreshRoles(java.lang.String login, java.lang.String md5Password,
			java.lang.String[] uris) throws java.rmi.RemoteException,
			org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException;

	public org.openflexo.ws.client.PPMWebService.CLProjectDescriptor[] getAvailableProjects(java.lang.String login,
			java.lang.String md5Password) throws java.rmi.RemoteException,
			org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException;

	public java.lang.String uploadPrj(org.openflexo.ws.client.PPMWebService.CLProjectDescriptor targetProject,
			javax.activation.DataHandler zip, java.lang.String uploadComment, java.lang.String login) throws java.rmi.RemoteException,
			org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException;
}
