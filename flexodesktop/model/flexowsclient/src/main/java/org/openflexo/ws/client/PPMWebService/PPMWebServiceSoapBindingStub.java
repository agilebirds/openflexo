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

public class PPMWebServiceSoapBindingStub extends org.apache.axis.client.Stub implements org.openflexo.ws.client.PPMWebService.PPMWebService_PortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[7];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRoles");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "md5Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "ArrayOf_tns1_PPMRole"));
        oper.setReturnClass(org.openflexo.ws.client.PPMWebService.PPMRole[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getRolesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://BenMacBook.local:3001/cgi-bin/WebObjects/DNLServerApplication.woa/ws/PPMWebService", "fault"),
                      "org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException",
                      new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMWebServiceAuthentificationException"), 
                      true
                     ));
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getProcesses");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "md5Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "ArrayOf_tns1_PPMProcess"));
        oper.setReturnClass(org.openflexo.ws.client.PPMWebService.PPMProcess[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getProcessesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://BenMacBook.local:3001/cgi-bin/WebObjects/DNLServerApplication.woa/ws/PPMWebService", "fault"),
                      "org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException",
                      new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMWebServiceAuthentificationException"), 
                      true
                     ));
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getScreenshoot");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "md5Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "processVersionURI"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "base64Binary"));
        oper.setReturnClass(byte[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getScreenshootReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://BenMacBook.local:3001/cgi-bin/WebObjects/DNLServerApplication.woa/ws/PPMWebService", "fault"),
                      "org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException",
                      new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMWebServiceAuthentificationException"), 
                      true
                     ));
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("refreshProcesses");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "md5Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "uris"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://BenMacBook.local:3001/cgi-bin/WebObjects/DNLServerApplication.woa/ws/PPMWebService", "ArrayOf_soapenc_string"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "ArrayOf_tns1_PPMProcess"));
        oper.setReturnClass(org.openflexo.ws.client.PPMWebService.PPMProcess[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "refreshProcessesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://BenMacBook.local:3001/cgi-bin/WebObjects/DNLServerApplication.woa/ws/PPMWebService", "fault"),
                      "org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException",
                      new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMWebServiceAuthentificationException"), 
                      true
                     ));
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("refreshRoles");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "md5Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "uris"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://BenMacBook.local:3001/cgi-bin/WebObjects/DNLServerApplication.woa/ws/PPMWebService", "ArrayOf_soapenc_string"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "ArrayOf_tns1_PPMRole"));
        oper.setReturnClass(org.openflexo.ws.client.PPMWebService.PPMRole[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "refreshRolesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://BenMacBook.local:3001/cgi-bin/WebObjects/DNLServerApplication.woa/ws/PPMWebService", "fault"),
                      "org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException",
                      new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMWebServiceAuthentificationException"), 
                      true
                     ));
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAvailableProjects");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "md5Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "ArrayOf_tns3_CLProjectDescriptor"));
        oper.setReturnClass(org.openflexo.ws.client.PPMWebService.CLProjectDescriptor[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getAvailableProjectsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://BenMacBook.local:3001/cgi-bin/WebObjects/DNLServerApplication.woa/ws/PPMWebService", "fault"),
                      "org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException",
                      new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMWebServiceAuthentificationException"), 
                      true
                     ));
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("uploadPrj");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "targetProject"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "CLProjectDescriptor"), org.openflexo.ws.client.PPMWebService.CLProjectDescriptor.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "zip"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "DataHandler"), javax.activation.DataHandler.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "uploadComment"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "login"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "uploadPrjReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://BenMacBook.local:3001/cgi-bin/WebObjects/DNLServerApplication.woa/ws/PPMWebService", "fault"),
                      "org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException",
                      new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMWebServiceAuthentificationException"), 
                      true
                     ));
        _operations[6] = oper;

    }

    public PPMWebServiceSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public PPMWebServiceSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public PPMWebServiceSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://BenMacBook.local:3001/cgi-bin/WebObjects/DNLServerApplication.woa/ws/PPMWebService", "ArrayOf_soapenc_string");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "ArrayOf_tns1_PPMProcess");
            cachedSerQNames.add(qName);
            cls = org.openflexo.ws.client.PPMWebService.PPMProcess[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMProcess");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "ArrayOf_tns1_PPMRole");
            cachedSerQNames.add(qName);
            cls = org.openflexo.ws.client.PPMWebService.PPMRole[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMRole");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "ArrayOf_tns3_CLProjectDescriptor");
            cachedSerQNames.add(qName);
            cls = org.openflexo.ws.client.PPMWebService.CLProjectDescriptor[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "CLProjectDescriptor");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "CLProjectDescriptor");
            cachedSerQNames.add(qName);
            cls = org.openflexo.ws.client.PPMWebService.CLProjectDescriptor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMProcess");
            cachedSerQNames.add(qName);
            cls = org.openflexo.ws.client.PPMWebService.PPMProcess.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMRole");
            cachedSerQNames.add(qName);
            cls = org.openflexo.ws.client.PPMWebService.PPMRole.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMWebServiceAuthentificationException");
            cachedSerQNames.add(qName);
            cls = org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public org.openflexo.ws.client.PPMWebService.PPMRole[] getRoles(java.lang.String login, java.lang.String md5Password) throws java.rmi.RemoteException, org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.ppm.denali.be", "getRoles"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, md5Password});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.openflexo.ws.client.PPMWebService.PPMRole[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.openflexo.ws.client.PPMWebService.PPMRole[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.openflexo.ws.client.PPMWebService.PPMRole[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException) {
              throw (org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.openflexo.ws.client.PPMWebService.PPMProcess[] getProcesses(java.lang.String login, java.lang.String md5Password) throws java.rmi.RemoteException, org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.ppm.denali.be", "getProcesses"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, md5Password});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.openflexo.ws.client.PPMWebService.PPMProcess[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.openflexo.ws.client.PPMWebService.PPMProcess[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.openflexo.ws.client.PPMWebService.PPMProcess[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException) {
              throw (org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public byte[] getScreenshoot(java.lang.String login, java.lang.String md5Password, java.lang.String processVersionURI) throws java.rmi.RemoteException, org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.ppm.denali.be", "getScreenshoot"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, md5Password, processVersionURI});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (byte[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (byte[]) org.apache.axis.utils.JavaUtils.convert(_resp, byte[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException) {
              throw (org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.openflexo.ws.client.PPMWebService.PPMProcess[] refreshProcesses(java.lang.String login, java.lang.String md5Password, java.lang.String[] uris) throws java.rmi.RemoteException, org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.ppm.denali.be", "refreshProcesses"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, md5Password, uris});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.openflexo.ws.client.PPMWebService.PPMProcess[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.openflexo.ws.client.PPMWebService.PPMProcess[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.openflexo.ws.client.PPMWebService.PPMProcess[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException) {
              throw (org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.openflexo.ws.client.PPMWebService.PPMRole[] refreshRoles(java.lang.String login, java.lang.String md5Password, java.lang.String[] uris) throws java.rmi.RemoteException, org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.ppm.denali.be", "refreshRoles"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, md5Password, uris});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.openflexo.ws.client.PPMWebService.PPMRole[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.openflexo.ws.client.PPMWebService.PPMRole[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.openflexo.ws.client.PPMWebService.PPMRole[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException) {
              throw (org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.openflexo.ws.client.PPMWebService.CLProjectDescriptor[] getAvailableProjects(java.lang.String login, java.lang.String md5Password) throws java.rmi.RemoteException, org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.ppm.denali.be", "getAvailableProjects"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {login, md5Password});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.openflexo.ws.client.PPMWebService.CLProjectDescriptor[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.openflexo.ws.client.PPMWebService.CLProjectDescriptor[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.openflexo.ws.client.PPMWebService.CLProjectDescriptor[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException) {
              throw (org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String uploadPrj(org.openflexo.ws.client.PPMWebService.CLProjectDescriptor targetProject, javax.activation.DataHandler zip, java.lang.String uploadComment, java.lang.String login) throws java.rmi.RemoteException, org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ws.ppm.denali.be", "uploadPrj"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {targetProject, zip, uploadComment, login});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException) {
              throw (org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

}
