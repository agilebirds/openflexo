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

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;

public class PPMWebServiceClient {

	private PPMWebService_ServiceLocator ppmServiceLocator;
	private PPMWebService_PortType ppmService;
	private String login;
	private String encriptedPWD;

	public PPMWebServiceClient(String webServiceUrl, String login, String encriptedPWD) throws ServiceException {
		this.ppmServiceLocator = new PPMWebService_ServiceLocator();
		this.ppmServiceLocator.setPPMWebServiceEndpointAddress(webServiceUrl);
		this.ppmService = this.ppmServiceLocator.getPPMWebService();
		if (this.ppmService == null)
			throw new ServiceException("Cannot obtain service client ! Axis has returned an unknow error.");
		org.apache.axis.client.Stub s = (Stub) this.ppmService;
		s.setTimeout(30000);
		this.login = login;
		this.encriptedPWD = encriptedPWD;
	}

	public PPMWebService_PortType getWebService_PortType() {
		return ppmService;
	}

	public PPMRole[] getRoles() throws ServiceException, PPMWebServiceAuthentificationException, RemoteException {
		return ppmService.getRoles(login, encriptedPWD);
	}

	public PPMProcess[] getProcesses() throws ServiceException, PPMWebServiceAuthentificationException, RemoteException {
		return ppmService.getProcesses(login, encriptedPWD);
	}

	public byte[] getScreenshot(String processVersionURI) throws ServiceException, PPMWebServiceAuthentificationException, RemoteException {
		return ppmService.getScreenshoot(login, encriptedPWD, processVersionURI);
	}

	public PPMRole[] refreshRoles(String[] uris) throws ServiceException, PPMWebServiceAuthentificationException, RemoteException {
		return ppmService.refreshRoles(login, encriptedPWD, uris);
	}

	public PPMProcess[] refreshProcesses(String[] uris) throws ServiceException, PPMWebServiceAuthentificationException, RemoteException {
		return ppmService.refreshProcesses(login, encriptedPWD, uris);
	}

	public CLProjectDescriptor[] getAvailableProjects() throws java.rmi.RemoteException,
			org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException {
		return ppmService.getAvailableProjects(login, encriptedPWD);
	}

	public String uploadPrj(org.openflexo.ws.client.PPMWebService.CLProjectDescriptor targetProject, javax.activation.DataHandler zip,
			java.lang.String uploadComment, java.lang.String login) throws java.rmi.RemoteException,
			org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException {
		return ppmService.uploadPrj(targetProject, zip, uploadComment, login);
	}

	public String getLogin() {
		return login;
	}

	public String getEncriptedPWD() {
		return encriptedPWD;
	}
}
