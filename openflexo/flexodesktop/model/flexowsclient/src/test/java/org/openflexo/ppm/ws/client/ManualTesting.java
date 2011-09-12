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
package org.openflexo.ppm.ws.client;

import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

import javax.xml.rpc.ServiceException;

import org.openflexo.ws.client.PPMWebService.PPMProcess;
import org.openflexo.ws.client.PPMWebService.PPMRole;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceClient;


public class ManualTesting {

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws ServiceException 
	 * @throws RemoteException 
	 * @throws PPMWebServiceAuthentificationException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, PPMWebServiceAuthentificationException, RemoteException, ServiceException {
		String encriptedPWD = getMd5Hash("wstest");
		String login = "wstest";
		PPMWebServiceClient ws = new PPMWebServiceClient("http://BenMacBook.local:3301/Flexo/WebObjects/DNLServerApplication.woa/ws/PPMWebService",login,encriptedPWD);
		PPMRole[] roles = ws.getRoles();
		PPMProcess[] processes = ws.getProcesses();
		printRoleArray(roles);
		System.out.println();
		printProcessArray(processes,0);
		System.out.println("NOW let's refresh");
		
		String roleUris[] = new String[roles.length-1];
		for(int i=0;i<roleUris.length;i++){
			roleUris[i] = roles[i].getUri();
		}
		
		String processUris[] = new String[processes.length-1];
		for(int i=0;i<processUris.length;i++){
			processUris[i] = processes[i].getUri();
		}
		
		PPMRole[] rolesRefreshed = ws.refreshRoles(roleUris);
		PPMProcess[] processesRefreshed = ws.refreshProcesses(processUris);
		
		printRoleArray(rolesRefreshed);
		System.out.println();
		printProcessArray(processesRefreshed,0);
	}

	private static void printRoleArray(PPMRole[] roles) {
		for(int i=0;i<roles.length;i++){
			PPMRole role = roles[i];
			System.out.println("ROLE : "+role.getName());
		}
	}

	private static void printProcessArray(PPMProcess[] processes, int indent){
		
		for(int i=0;i<processes.length;i++){
			PPMProcess process = processes[i];
			for(int j=0;j<indent;j++)System.out.print("\t");
			System.out.println("PROCESS : "+process.getName());
			if(process.getSubProcesses().length>0)
				printProcessArray(process.getSubProcesses(), indent+1);
		}
	}
	
	
	
	public static String getMd5Hash(String toHash) throws NoSuchAlgorithmException 
    {
    	if(toHash==null) return null;
   		java.security.MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");
		byte dataBytes[] = toHash.getBytes();
	    md5.update(dataBytes);
	    byte digest[] = md5.digest();
    	
    	StringBuffer hashString = new StringBuffer();

	    for (int i = 0; i < digest.length; ++i) 
	    {
	    	String hex = Integer.toHexString(digest[i]);

	    	if (hex.length() == 1) 
	    	{
	    		hashString.append('0');
	    		hashString.append(hex.charAt(hex.length() - 1));
	    	}
	    	else 
	    	{
	    		hashString.append(hex.substring(hex.length() - 2));
	    	}
	    }
	    return hashString.toString();

    }
}
