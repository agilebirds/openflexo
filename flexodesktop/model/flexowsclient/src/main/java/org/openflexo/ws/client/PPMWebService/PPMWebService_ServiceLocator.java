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

public class PPMWebService_ServiceLocator extends org.apache.axis.client.Service implements org.openflexo.ws.client.PPMWebService.PPMWebService_Service {

    public PPMWebService_ServiceLocator() {
    }


    public PPMWebService_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PPMWebService_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PPMWebService
    private java.lang.String PPMWebService_address = "http://BenMacBook.local:3001/cgi-bin/WebObjects/DNLServerApplication.woa/ws/PPMWebService";

    @Override
	public java.lang.String getPPMWebServiceAddress() {
        return PPMWebService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PPMWebServiceWSDDServiceName = "PPMWebService";

    public java.lang.String getPPMWebServiceWSDDServiceName() {
        return PPMWebServiceWSDDServiceName;
    }

    public void setPPMWebServiceWSDDServiceName(java.lang.String name) {
        PPMWebServiceWSDDServiceName = name;
    }

    @Override
	public org.openflexo.ws.client.PPMWebService.PPMWebService_PortType getPPMWebService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PPMWebService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPPMWebService(endpoint);
    }

    @Override
	public org.openflexo.ws.client.PPMWebService.PPMWebService_PortType getPPMWebService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub _stub = new org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getPPMWebServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPPMWebServiceEndpointAddress(java.lang.String address) {
        PPMWebService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.openflexo.ws.client.PPMWebService.PPMWebService_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub _stub = new org.openflexo.ws.client.PPMWebService.PPMWebServiceSoapBindingStub(new java.net.URL(PPMWebService_address), this);
                _stub.setPortName(getPPMWebServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("PPMWebService".equals(inputPortName)) {
            return getPPMWebService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    @Override
	public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://BenMacBook.local:3001/cgi-bin/WebObjects/DNLServerApplication.woa/ws/PPMWebService", "PPMWebService");
    }

    private java.util.HashSet ports = null;

    @Override
	public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://BenMacBook.local:3001/cgi-bin/WebObjects/DNLServerApplication.woa/ws/PPMWebService", "PPMWebService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("PPMWebService".equals(portName)) {
            setPPMWebServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
