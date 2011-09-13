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
package org.openflexo.wsdl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.openflexo.toolbox.FileResource;
import org.openflexo.wsdl.SchemaTypeExtractor;
import org.openflexo.wsdl.XMLTypeMapper;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


public class SchemaImportTest extends TestCase {
	
	public void testImportArray(){
		try{
		//File wsdl =	new File("/Users/dvanvyve/Documents/Projects/FlexoWS/I6DocWSimportElementTest.wsdl");
	
			String fileName = new FileResource("I6DocWSimport.wsdl").getCanonicalPath();	
			//String fileName = "/Users/dvanvyve/Documents/Projects/FlexoWS/I6DocWSimportElementTest.wsdl";
			
		SchemaTypeExtractor extractor = new SchemaTypeExtractor(fileName);
		
		
        Hashtable answer = new Hashtable();
        
        
        List allSeenTypes = new ArrayList();
	    allSeenTypes.addAll(Arrays.asList(extractor.schemaTypeSystem().globalTypes()));
	    System.out.println(Arrays.asList(extractor.schemaTypeSystem().globalTypes()));
	    
	    /**We need to add the ElementTypes: ie Anonymous complex types of */
		List documentTypes = new ArrayList();
		documentTypes.addAll(Arrays.asList(extractor.schemaTypeSystem().documentTypes()));
	    for (int i = 0; i < documentTypes.size(); i++)
	    {
	        SchemaType sType = (SchemaType)documentTypes.get(i);
	        allSeenTypes.addAll(Arrays.asList(sType.getAnonymousTypes()));
	    }
	    for (int i = 0; i < allSeenTypes.size(); i++)
	    {
	        SchemaType sType = (SchemaType)allSeenTypes.get(i);
	        System.out.println("Visiting " + sType.toString());
	        System.out.println("Global/Anonymous Element Type:"+sType.getName() +" java:"+sType.getFullJavaName());
	        
	        //String className = XMLTypeMapper.getFullJavaNameForType(sType, extractor);
     		//System.out.println("className:"+  className);
          //  String packName = sType.getFullJavaName().substring(0,sType.getFullJavaName().lastIndexOf(className)-1);
           // System.out.println("Package Name:"+packName);
           
	        System.out.println("Type:"+sType);
			//System.out.println("attributeModel:"+ type.getAttributeModel());
			System.out.println("base Type: "+sType.getBaseType());
			SchemaType baseType = sType.getBaseType();
			if(baseType!=null&&baseType.getName().getLocalPart().equals("Array")&&
					baseType.getName().getNamespaceURI().equals("http://schemas.xmlsoap.org/soap/encoding/")){
				System.out.println("This is an array");
			 
				XMLTypeMapper.getFullJavaNameForType(sType, extractor,new Hashtable());
				//XMLTypeMapper.getFullJavaNameForType("/Users/dvanvyve/Documents/Projects/FlexoWS/I6DocWSimportElementTest.wsdl", sType, extractor);
				  //  	getSchemas("/Users/dvanvyve/Documents/Projects/FlexoWS/I6DocWSimportElementTest.wsdl", sType, extractor);	
			}
     /*       
        //    entity.setDescription(currentClass.getDocumentation()+"\n"+EOModelExtractor.getOperationsDescription(currentClass));
      
            List list = Arrays.asList(sType.getElementProperties());
	        for (int ii=0; ii<list.size(); ii++){
	        		SchemaProperty prop = (SchemaProperty)list.get(ii);
	        		
	        		
	        		// if isAtomicType => getPrimitiveType
	       		
	        	    String attributeName = prop.getName().getLocalPart();
	        	      String attributeType = XMLTypeMapper.getFullJavaNameForType(prop.getType(), extractor);//getJavaTypeForBuiltInType(prop.getType().getPrimitiveType().getBuiltinTypeCode());
	      	   		System.out.println("property:"+prop.getName()+" type"+prop.getType()+" Java type:"+attributeType);
		   	  
	   	      
	   	       //prop.setDescription(attrib.getDocumentation());
	        }
           
            
         */   
            
            
        }
	
	}catch(Exception e){e.printStackTrace();fail();}
}
	
	
	 public static List getSchemas( String wsdlUrl, SchemaType type, SchemaTypeExtractor extractor) throws Exception
	   {
	      System.out.println( "loading schema types from " + wsdlUrl );
	      XmlOptions options = new XmlOptions();
	      options.setCompileNoValidation();
	      options.setCompileDownloadUrls();
	      options.setCompileNoUpaRule();
	      options.setSaveUseOpenFrag();
	      options.setSaveSyntheticDocumentElement( new QName( "http://www.w3.org/2001/XMLSchema", "schema" ));

	      XmlObject xmlObject = XmlObject.Factory.parse(new File(wsdlUrl)/*new URL(wsdlUrl)*/,options);

	      XmlObject[] schemas = xmlObject
	            .selectPath("declare namespace s='http://www.w3.org/2001/XMLSchema' .//s:schema");
	      
	     
	      for (int i = 0; i < schemas.length; i++)
	      {
	      
	    	  	XmlObject schema = schemas[i];
	         XmlObject[] elements = schema.selectPath("declare namespace s='http://www.w3.org/2001/XMLSchema' .//s:element[@name='ArrayOf_tns1_Book']");
		     for(int j = 0;j<elements.length;j++){
		    	 	XmlObject element = elements[j];
		    	 	XmlObject[] attributes = element.selectPath("declare namespace wsdl='http://schemas.xmlsoap.org/wsdl/' declare namespace s='http://www.w3.org/2001/XMLSchema' .//s:attribute[@wsdl:arrayType]");
		    	 	System.out.println("hello");
		    	 	XmlObject attribut = attributes[0];
		    	 	NamedNodeMap map = attribut.getDomNode().getAttributes();
		    	 	Node s = map.getNamedItemNS("http://schemas.xmlsoap.org/wsdl/","arrayType");
		    	 	System.out.println("s:"+s.getNodeValue());
		     }
	         //String path = XmlBeans.compilePath("declare namespace s='http://www.w3.org/2001/XMLSchema' .//s:element[@s:name='ArrayOf_tns1_Book']");
		    
		      	XmlObject[] attr = xmlObject.selectPath("declare namespace s='http://www.w3.org/2001/XMLSchema' .//s:element[@name='ArrayOf_tns1_Book']");///s:attribute[@ref]");
		   
	      }
	      
	      	System.out.println("qname:"+type.getName());
	       //if(type.isDocumentType()){
      		//	SchemaGlobalElement element =extractor.schemaTypeSystem().findElement(elementName);
      		//	SchemaType elementType = element.getType();
      			//on a recupere le bon complexType
      	      //1. Je cherche les elements qui ont le bon nom.
	      	
	      	
	      String path = XmlBeans.compilePath("declare namespace s='http://www.w3.org/2001/XMLSchema' .//s:element[@name='ArrayOf_tns1_Book']");
	      	XmlObject[] restrictions = xmlObject.selectPath(path);///s:attribute[@ref]");
	        
	      	///s:attribute[@ref]");
	        	      
      	      
      	      for (int i=0; i<restrictions.length; i++){
      	    	  	XmlObject obj = restrictions[i];
      	    	  XmlObject[] attributes = xmlObject.selectPath("declare namespace s='http://www.w3.org/2001/XMLSchema' .//s:attribute[@ref]");
      	        
      	    	  	
      	    	  	Node node = obj.getDomNode();
      	    	  	System.out.println(node.getNodeName());
      	    	  System.out.println(node.getLocalName());
      	    	  System.out.println(node.getAttributes());
      	    	  	//obj.
      	      }
         
   
	      

	      
	      XmlObject[] attributes2 = xmlObject.selectPath("declare namespace s='http://www.w3.org/2001/XMLSchema' .//s:restriction");
	      
	      for (int i = 0; i < schemas.length; i++)
	      {
	         XmlCursor xmlCursor = schemas[i].newCursor();
	         String xmlText = xmlCursor.getObject().xmlText( options );
	         schemas[i] = XmlObject.Factory.parse(xmlText, options);
	         
	         schemas[i].documentProperties().setSourceName(wsdlUrl);
	         if( wsdlUrl.startsWith( "file:"))
	         	;//fixRelativeFileImports( schemas[i] );
	      }
	      
	      List result = new ArrayList( Arrays.asList( schemas ));
	      
	      XmlObject [] imports = xmlObject.selectPath("declare namespace s='http://schemas.xmlsoap.org/wsdl/' .//s:import");
	      for (int i = 0; i < imports.length; i++)
	      {
	         String location = ((Element)imports[i].newDomNode()).getAttribute( "location" );
	         if( location != null )
	         {
	            if( location.indexOf( "://") > 0 )
	            {
	              // result.addAll( getSchemas( location, null, null ));
	            }
	            else
	            {
	              // result.addAll( getSchemas( joinRelativeUrl( wsdlUrl, location ) ));
	            }
	         }
	      }
	      
	      return result;
	   }
	
}
