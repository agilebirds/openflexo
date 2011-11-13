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

public class PPMWebService_ServiceTestCase extends junit.framework.TestCase {
	public PPMWebService_ServiceTestCase(java.lang.String name) {
		super(name);
	}

	public void testPPMWebServiceWSDL() throws Exception {
		javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
		java.net.URL url = new java.net.URL(
				new org.openflexo.ws.client.PPMWebService.PPMWebService_ServiceLocator().getPPMWebServiceAddress() + "?WSDL");
		javax.xml.rpc.Service service = serviceFactory.createService(url,
				new org.openflexo.ws.client.PPMWebService.PPMWebService_ServiceLocator().getServiceName());
		assertTrue(service != null);
	}

	public void test1PPMWebServiceGetRoles() throws Exception {
		org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub binding;
		try {
			binding = (org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub) new org.openflexo.ws.client.PPMWebService.PPMWebService_ServiceLocator()
					.getPPMWebService();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		try {
			org.openflexo.ws.client.PPMWebService.PPMRole[] value = null;
			value = binding.getRoles(new java.lang.String(), new java.lang.String());
		} catch (org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException e1) {
			throw new junit.framework.AssertionFailedError("PPMWebServiceAuthentificationException Exception caught: " + e1);
		}
		// TBD - validate results
	}

	public void test2PPMWebServiceGetProcesses() throws Exception {
		org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub binding;
		try {
			binding = (org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub) new org.openflexo.ws.client.PPMWebService.PPMWebService_ServiceLocator()
					.getPPMWebService();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		try {
			org.openflexo.ws.client.PPMWebService.PPMProcess[] value = null;
			value = binding.getProcesses(new java.lang.String(), new java.lang.String());
		} catch (org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException e1) {
			throw new junit.framework.AssertionFailedError("PPMWebServiceAuthentificationException Exception caught: " + e1);
		}
		// TBD - validate results
	}

	public void test3PPMWebServiceGetScreenshoot() throws Exception {
		org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub binding;
		try {
			binding = (org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub) new org.openflexo.ws.client.PPMWebService.PPMWebService_ServiceLocator()
					.getPPMWebService();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		try {
			byte[] value = null;
			value = binding.getScreenshoot(new java.lang.String(), new java.lang.String(), new java.lang.String());
		} catch (org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException e1) {
			throw new junit.framework.AssertionFailedError("PPMWebServiceAuthentificationException Exception caught: " + e1);
		}
		// TBD - validate results
	}

	public void test4PPMWebServiceRefreshProcesses() throws Exception {
		org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub binding;
		try {
			binding = (org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub) new org.openflexo.ws.client.PPMWebService.PPMWebService_ServiceLocator()
					.getPPMWebService();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		try {
			org.openflexo.ws.client.PPMWebService.PPMProcess[] value = null;
			value = binding.refreshProcesses(new java.lang.String(), new java.lang.String(), new java.lang.String[0]);
		} catch (org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException e1) {
			throw new junit.framework.AssertionFailedError("PPMWebServiceAuthentificationException Exception caught: " + e1);
		}
		// TBD - validate results
	}

	public void test5PPMWebServiceRefreshRoles() throws Exception {
		org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub binding;
		try {
			binding = (org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub) new org.openflexo.ws.client.PPMWebService.PPMWebService_ServiceLocator()
					.getPPMWebService();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		try {
			org.openflexo.ws.client.PPMWebService.PPMRole[] value = null;
			value = binding.refreshRoles(new java.lang.String(), new java.lang.String(), new java.lang.String[0]);
		} catch (org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException e1) {
			throw new junit.framework.AssertionFailedError("PPMWebServiceAuthentificationException Exception caught: " + e1);
		}
		// TBD - validate results
	}

	public void test6PPMWebServiceGetAvailableProjects() throws Exception {
		org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub binding;
		try {
			binding = (org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub) new org.openflexo.ws.client.PPMWebService.PPMWebService_ServiceLocator()
					.getPPMWebService();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		try {
			org.openflexo.ws.client.PPMWebService.CLProjectDescriptor[] value = null;
			value = binding.getAvailableProjects(new java.lang.String(), new java.lang.String());
		} catch (org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException e1) {
			throw new junit.framework.AssertionFailedError("PPMWebServiceAuthentificationException Exception caught: " + e1);
		}
		// TBD - validate results
	}

	public void test7PPMWebServiceUploadPrj() throws Exception {
		org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub binding;
		try {
			binding = (org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub) new org.openflexo.ws.client.PPMWebService.PPMWebService_ServiceLocator()
					.getPPMWebService();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		try {
			java.lang.String value = null;
			value = binding.uploadPrj(new org.openflexo.ws.client.PPMWebService.CLProjectDescriptor(), null, new java.lang.String(),
					new java.lang.String());
		} catch (org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException e1) {
			throw new junit.framework.AssertionFailedError("PPMWebServiceAuthentificationException Exception caught: " + e1);
		}
		// TBD - validate results
	}

}
