<?xml version="1.0" encoding="UTF-8"?>
<wsdl:definitions targetNamespace="http://phenotypical.com" xmlns="http://schemas.xmlsoap.org/wsdl/" xmlns:apachesoap="http://xml.apache.org/xml-soap" xmlns:impl="http://phenotypical.com" xmlns:intf="http://phenotypical.com" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" xmlns:tns1="http://rpc.xml.coldfusion" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:wsdlsoap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
<!--WSDL created by Macromedia ColdFusion MX version 7,0,1,116466-->
 <wsdl:types>
  <schema targetNamespace="http://rpc.xml.coldfusion" xmlns="http://www.w3.org/2001/XMLSchema">
   <import namespace="http://xml.apache.org/xml-soap"/>
   <import namespace="http://schemas.xmlsoap.org/soap/encoding/"/>
   <complexType name="CFCInvocationException">
    <sequence/>
   </complexType>
  </schema>
 </wsdl:types>

   <wsdl:message name="getProverbOfTheDayResponse">

      <wsdl:part name="getProverbOfTheDayReturn" type="apachesoap:Document"/>

   </wsdl:message>

   <wsdl:message name="getProverbOfTheDayRequest">

   </wsdl:message>

   <wsdl:message name="CFCInvocationException">

      <wsdl:part name="fault" type="tns1:CFCInvocationException"/>

   </wsdl:message>

   <wsdl:portType name="proverbs">

      <wsdl:operation name="getProverbOfTheDay">

         <wsdl:input message="impl:getProverbOfTheDayRequest" name="getProverbOfTheDayRequest"/>

         <wsdl:output message="impl:getProverbOfTheDayResponse" name="getProverbOfTheDayResponse"/>

         <wsdl:fault message="impl:CFCInvocationException" name="CFCInvocationException"/>

      </wsdl:operation>

   </wsdl:portType>

   <wsdl:binding name="proverbs.cfcSoapBinding" type="impl:proverbs">

      <wsdlsoap:binding style="rpc" transport="http://schemas.xmlsoap.org/soap/http"/>

      <wsdl:operation name="getProverbOfTheDay">

         <wsdlsoap:operation soapAction=""/>

         <wsdl:input name="getProverbOfTheDayRequest">

            <wsdlsoap:body encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" namespace="http://phenotypical.com" use="encoded"/>

         </wsdl:input>

         <wsdl:output name="getProverbOfTheDayResponse">

            <wsdlsoap:body encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" namespace="http://phenotypical.com" use="encoded"/>

         </wsdl:output>

         <wsdl:fault name="CFCInvocationException">

            <wsdlsoap:fault encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" name="CFCInvocationException" namespace="http://phenotypical.com" use="encoded"/>

         </wsdl:fault>

      </wsdl:operation>

   </wsdl:binding>

   <wsdl:service name="Hungarian Proverbs">

      <wsdl:port binding="impl:proverbs.cfcSoapBinding" name="proverbs.cfc">

         <wsdlsoap:address location="http://phenotypical.com/proverbs.cfc"/>

      </wsdl:port>

   </wsdl:service>

</wsdl:definitions>