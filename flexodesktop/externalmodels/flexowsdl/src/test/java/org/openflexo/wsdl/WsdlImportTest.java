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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import junit.framework.TestCase;

import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.schema.MySchemaTypeSystemCompiler;
import org.openflexo.toolbox.FileResource;
import org.openflexo.wsdl.SchemaUtils;
import org.openflexo.wsdl.WSDL2Java;
import org.openflexo.wsdl.XMLTypeMapper;


public class WsdlImportTest extends TestCase {

	public WsdlImportTest(String arg0) {
		super(arg0);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	
	
	public String fileName() throws IOException{
		return new FileResource("I6DocWSimport.wsdl").getCanonicalPath();
		//return "/Users/dvanvyve/Documents/Projects/FlexoWS/I6DocWSNoArray.wsdl";
		//return "/Users/dvanvyve/Documents/Projects/FlexoWS/test.wsdl";
	}
	
	
	public void testImportSoapEncoding(){
		System.out.println("**** TEST testImportSoapEncoding");
	    try{
		    SchemaTypeLoader schemaTypeLoader = null;
		    
		    
		   XmlObject[] schemaArray =  new XmlObject[]{ XmlObject.Factory.parse(new FileResource("Resources/soapEncoding.xml")) };
		   
		    
		
	        XmlOptions opts = new XmlOptions();
	        
	            opts.setCompileDownloadUrls();
	        
	          //  opts.setCompileNoUpaRule();
	       
	         //   opts.setCompileNoPvrRule();
	        
	          //  opts.setCompileNoAnnotations();
	        
	           // opts.setCompileMdefNamespaces(mdefNamespaces);
	        opts.setCompileNoValidation(); // already validated here
	      
	        //
	        WSDL2Java.MyResolver entityResolver = new WSDL2Java.MyResolver();
	        opts.setEntityResolver(entityResolver);
	       
	           // opts.setGenerateJavaVersion(javasource);
		    	
		    /** this is another CORRECT but from modified source way to do it WITH java-ized types**/
		    	 SchemaTypeSystem soapsts = MySchemaTypeSystemCompiler.compile(null,null,schemaArray ,null,XmlBeans.getBuiltinTypeSystem(),null,opts);
		    System.out.println("soap System:"+ soapsts);
		    
		    /** this works but generates sources files...boring*/
		    	//schemaTypeSystem = XmlBeans.compileXmlBeans(null,null, new XmlObject[]{ XmlObject.Factory.parse(schema) },null,XmlBeans.getBuiltinTypeSystem(),new FilerImpl(null,null,null,false,false), null);    	
			
		   
		    
		    	 schemaTypeLoader = XmlBeans.typeLoaderUnion(new SchemaTypeLoader[]{ soapsts, XmlBeans.getBuiltinTypeSystem() });

			      WSDL2Java wsdl2Java = new WSDL2Java();
			      List schemas = wsdl2Java.generate(null,
			                                            null,
			                                            null,
			                                            fileName());
			      System.out.println("Schemas:"+schemas);
			      
			      Iterator it = schemas.iterator();
				    schemaArray = new XmlObject[schemas.size()];
				    int l =0;
				    while (it.hasNext()) {
						XmlObject schema = (XmlObject)it.next();
						schemaArray[l]= schema;
						l++;
					}
				    
				
			         opts = new XmlOptions();
			        
			            opts.setCompileDownloadUrls();

			        opts.setCompileNoValidation(); // already validated here
			      
			        //
			        opts.setEntityResolver(entityResolver);
			       
			           // opts.setGenerateJavaVersion(javasource);
				    	
				    /** this is another CORRECT but from modified source way to do it WITH java-ized types**/
				    	 SchemaTypeSystem i6ts = MySchemaTypeSystemCompiler.compile(null,null,schemaArray ,null,schemaTypeLoader,null,opts);
				    System.out.println("i6Ts:"+i6ts);
				
				    
				    	 schemaTypeLoader = XmlBeans.typeLoaderUnion(new SchemaTypeLoader[]{ i6ts, schemaTypeLoader });
				    	exploreTypeSystem(i6ts, schemaTypeLoader);
	    
	    }
		catch(Exception e){
			System.out.println(e.toString());
			e.printStackTrace();
			fail("Error:"+e.toString());}
		    	  
	}
	
	public void testImportWSDLSchemaWithModifiedSchemaCompiler(){
		System.out.println("**** TEST testImportWSDLSchemaWithModifiedSchemaCompiler");
		try{
		String wsdlFileName=fileName();
		//String wsdlFileName="/Users/dvanvyve/Documents/Projects/FlexoWS/I6DocWSimport.wsdl";
		//String wsdlFileName="/Users/dvanvyve/Documents/Projects/FlexoWS/test.wsdl";
	      
	    SchemaTypeSystem sts = WSDL2Java.loadTypeSystem(null, new File(wsdlFileName), true);
		System.out.println("sts:"+sts);
	    exploreSchema(null,sts);
		}
		catch(Exception e){
			System.out.println(e.toString());
			e.printStackTrace();
			fail("Error:"+e.toString());}
	}
	
	
	public void testImportWSDLSchemaWithJeffHanson() throws IOException{
		System.out.println("**** TEST testImportWSDLSchemaWithJeffHanson");
		String wsdlFileName=fileName();
		//String wsdlFileName="/Users/dvanvyve/Documents/Projects/FlexoWS/I6DocWSimport.wsdl";
		//String wsdlFileName="/Users/dvanvyve/Documents/Projects/FlexoWS/test.wsdl";
	      WSDL2Java wsdl2Java = new WSDL2Java();
	      List schemas = wsdl2Java.generate(null,
	                                            null,
	                                            null,
	                                            wsdlFileName);
	      System.out.println("Schemas:"+schemas);
			//List list = SchemaUtils.getSchemas(wsdl.toURI().toString());
			//System.out.println("list:"+list);
					exploreSchema( schemas,null);
				
	}
	
	
	public void testImport(){
		System.out.println("**** TEST testImport");
		try{
		WSDLFactory wsdlFactory = WSDLFactory.newInstance();
		WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
		//File wsdl = new File("/Users/dvanvyve/Documents/Projects/FlexoWS/I6DocWSimport.wsdl");
		
		File wsdl = new File(fileName());
			Definition webServiceDefinition = wsdlReader.readWSDL(wsdl.getAbsolutePath());
		//FlexoController.notify("A new WSDL definition has been read.\nIt still to include it properly in the Flexo model.\n" +
				//"It's a developper job... and it can be done starting in class : org.openflexo.wsdl.Importer");
		//TODO : explore the webServiceDefinition to insert valuable information in FlexoModel 
		// BMA: I suggest to use xmlBeans (http://xmlbeans.apache.org/)
		//      to transform the <types> section of the wsdl
		//      This will produce some java classes that can be packaged in a jar 
		//      and then importing the jar in Flexo will do the trick
		
		//      Other sections are specific wsdl can be handeled with the wsdl api.
		//      When handeling the other sections keep in mind that we can use entities
		//      we have just create with the importation of the jar.
		
		System.out.println("**** TYPES ******");
		//list of XMLObjects
		List list = SchemaUtils.getSchemas(wsdl.toURI().toString());
System.out.println("list:"+list);
		exploreSchema( list, null);
	
		System.out.println("**** OPERATIONS ******");
		Map portTypes = webServiceDefinition.getPortTypes();
		Iterator it = portTypes.values().iterator();
		while(it.hasNext()){
			PortType pt = (PortType)it.next();
			System.out.println("PortType:"+pt.getQName());
			List operations = pt.getOperations();
			Iterator it2 = operations.iterator();
			while(it2.hasNext()){
				Operation op = (Operation) it2.next();
				System.out.println("Operation"+ op.getName());
				System.out.println("input:"+op.getInput().getName());
				
				Iterator it3 = op.getInput().getMessage().getParts().values().iterator();
				while(it3.hasNext()){
					Part input =(Part) it3.next();
					System.out.println("arg: "+input.getName()+" "+input.getTypeName());
				}
				it3 = op.getOutput().getMessage().getParts().values().iterator();
				while(it3.hasNext()){
					Part output = (Part)it3.next();
					System.out.println("outp:"+output.getName()+" "+output.getTypeName());
				}
			}
		}
		
		
		}
		catch(Exception e){e.printStackTrace();fail("Exception occured:"+e.getMessage());}
	}
	
	private void exploreSchema(List schemas, SchemaTypeSystem schemaTypeSystem){
		   // Create Schema Type System
	   // SchemaTypeSystem schemaTypeSystem = null;
	    try{
		    SchemaTypeLoader schemaTypeLoader = null;
		    
		    if(schemaTypeSystem==null){
		    Iterator it = schemas.iterator();
		    XmlObject[] schemaArray = new XmlObject[schemas.size()];
		    int l =0;
		    while (it.hasNext()) {
				XmlObject schema = (XmlObject)it.next();
				schemaArray[l]= schema;
				l++;
			}
		    
		
	        XmlOptions opts = new XmlOptions();
	        
	            opts.setCompileDownloadUrls();
	        
	          //  opts.setCompileNoUpaRule();
	       
	         //   opts.setCompileNoPvrRule();
	        
	          //  opts.setCompileNoAnnotations();
	        
	           // opts.setCompileMdefNamespaces(mdefNamespaces);
	        opts.setCompileNoValidation(); // already validated here
	      
	        //
	        WSDL2Java.MyResolver entityResolver = new WSDL2Java.MyResolver();
	        opts.setEntityResolver(entityResolver);
	       
	           // opts.setGenerateJavaVersion(javasource);
		    	
		    /** this is another CORRECT but from modified source way to do it WITH java-ized types**/
		    	 schemaTypeSystem = MySchemaTypeSystemCompiler.compile(null,null,schemaArray ,null,XmlBeans.getBuiltinTypeSystem(),null,opts);
		    
		    
		    /** this works but generates sources files...boring*/
		    	//schemaTypeSystem = XmlBeans.compileXmlBeans(null,null, new XmlObject[]{ XmlObject.Factory.parse(schema) },null,XmlBeans.getBuiltinTypeSystem(),new FilerImpl(null,null,null,false,false), null);    	
			
		    }//end IF
		    
		    	 schemaTypeLoader = XmlBeans.typeLoaderUnion(new SchemaTypeLoader[]{ schemaTypeSystem, XmlBeans.getBuiltinTypeSystem() });
		    	
		    	  
	    List allSeenTypes = new ArrayList();
	    allSeenTypes.addAll(Arrays.asList(schemaTypeSystem.documentTypes()));
	    for (int i = 0; i < allSeenTypes.size(); i++)
	    {
	        SchemaType sType = (SchemaType)allSeenTypes.get(i);
	        System.out.println("Visiting " + sType.toString());
	        allSeenTypes.addAll(Arrays.asList(sType.getAnonymousTypes()));
	    }
	    System.out.println("list of Document types:"+allSeenTypes);
	    
	    
	    allSeenTypes = new ArrayList();
	    allSeenTypes.addAll(Arrays.asList(schemaTypeSystem.attributeTypes()));
	    for (int i = 0; i < allSeenTypes.size(); i++)
	    {
	        SchemaType sType = (SchemaType)allSeenTypes.get(i);
	        System.out.println("Visiting " + sType.toString());
	        allSeenTypes.addAll(Arrays.asList(sType.getAnonymousTypes()));
	    }
	    System.out.println("list of attribute types:"+allSeenTypes);
	    
	    allSeenTypes = new ArrayList();
	    allSeenTypes.addAll(Arrays.asList(schemaTypeSystem.globalTypes()));
	    for (int i = 0; i < allSeenTypes.size(); i++)
	    {
	        SchemaType sType = (SchemaType)allSeenTypes.get(i);
	        System.out.println("Visiting " + sType.toString());
	        
	        System.out.println("GlobalType:"+sType.getName() +" java:"+sType.getFullJavaName());
	        //SchemaTypeLoader loader = XmlBeans.getContextTypeLoader();
	        SchemaTypeLoader loader = schemaTypeLoader;
	        SchemaType type = loader.findType(sType.getName());
	        System.out.println("loader:"+loader+" Found type:"+type);
	        SchemaTypeSystem sys = sType.getTypeSystem();
	        System.out.println("Sys:"+sys+" foundtype:"+sys.findType(sType.getName()));
	        
	        System.out.println("Loader Java:"+type.getFullJavaName()+" Java Sys:"+sys.findType(sType.getName()).getFullJavaName());
	        
	        List list = Arrays.asList(sType.getElementProperties());
	        for (int ii=0; ii<list.size(); ii++){
	        		SchemaProperty prop = (SchemaProperty)list.get(ii);
	        		
	        		
	        		// if isAtomicType => getPrimitiveType
	        		
	        		//System.out.println("property:"+prop.getName()+" type:"+prop.getType().getPrimitiveType().getShortJavaName());
	        		//System.out.println("property:"+prop.getName()+" type:"+prop.getType().getPrimitiveType().getName().getLocalPart());
	        		System.out.println("property:"+prop.getName()+" type:"+XMLTypeMapper.getJavaTypeForBuiltInType(prop.getType().getPrimitiveType().getBuiltinTypeCode()));
	        		
	        		//SchemaType primitive = BuiltinSchemaTypeSystem.get().findType(prop.getType().getPrimitiveType().getName());
	        		//System.out.println("primitive:"+ primitive +" typeCode:"+primitive.getBuiltinTypeCode()+" isBuiltin "+primitive.isBuiltinType()+" isPrimitive:"+primitive.isPrimitiveType()+ "isSimple:"+primitive.isSimpleType()+" isDocument"+primitive.isDocumentType());
	        		
	        }
	        
	        allSeenTypes.addAll(Arrays.asList(sType.getAnonymousTypes()));
	    }
	    System.out.println("list of Global types:"+allSeenTypes);
	 
	    
	    
	    
	    
	    
	    System.out.println("schema:"+schemaTypeSystem.toString());
	    List types = new ArrayList();
	    types.addAll(Arrays.asList(schemaTypeSystem.globalTypes()));
	    types.addAll(Arrays.asList(schemaTypeSystem.documentTypes()));
	    types.addAll(Arrays.asList(schemaTypeSystem.attributeTypes()));
	   
	   for (Iterator ig = Arrays.asList(schemaTypeSystem.globalElements()).iterator(); ig.hasNext(); )
	   {
		   SchemaGlobalElement element = (SchemaGlobalElement)ig.next();
		   System.out.println("elemnt:"+element.getName());
	   }
	   System.out.println("globalTypes:"+Arrays.asList(schemaTypeSystem.globalTypes()));
	   System.out.println("globalAttributes:"+Arrays.asList(schemaTypeSystem.globalAttributes()));
	   
	    System.out.println("list:"+types);
	    for (Iterator i = types.iterator(); i.hasNext(); )
	    {
	        SchemaType type = (SchemaType)i.next();
	        
	        //if (type.isBuiltinType())
	          //  continue;
	        //if (type.getFullJavaName() == null)
	          //  continue;

	        String fjn = type.getFullJavaName();
	        System.out.println("type:"+fjn);
	    }
	    
		}catch(Exception e){e.printStackTrace();fail("exception:"+e.toString());}
		
		}
	
	private void exploreTypeSystem(SchemaTypeSystem schemaTypeSystem, SchemaTypeLoader schemaTypeLoader){
		try{   
		List allSeenTypes = new ArrayList();
		    allSeenTypes.addAll(Arrays.asList(schemaTypeSystem.documentTypes()));
		    for (int i = 0; i < allSeenTypes.size(); i++)
		    {
		        SchemaType sType = (SchemaType)allSeenTypes.get(i);
		        allSeenTypes.addAll(Arrays.asList(sType.getAnonymousTypes()));
		    }
		    System.out.println("list of Document types:"+allSeenTypes);
		    
		    
		    allSeenTypes = new ArrayList();
		    allSeenTypes.addAll(Arrays.asList(schemaTypeSystem.attributeTypes()));
		    for (int i = 0; i < allSeenTypes.size(); i++)
		    {
		        SchemaType sType = (SchemaType)allSeenTypes.get(i);
		        allSeenTypes.addAll(Arrays.asList(sType.getAnonymousTypes()));
		    }
		    System.out.println("list of attribute types:"+allSeenTypes);
		    
		    allSeenTypes = new ArrayList();
		    allSeenTypes.addAll(Arrays.asList(schemaTypeSystem.globalTypes()));
		    for (int i = 0; i < allSeenTypes.size(); i++)
		    {
		        SchemaType sType = (SchemaType)allSeenTypes.get(i);
		        System.out.println("Visiting " + sType.toString());
		        
		        System.out.println("GlobalType:"+sType.getName() +" java:"+sType.getFullJavaName());
		      
		        /*
		        SchemaTypeLoader loader = schemaTypeLoader;
		        SchemaType type = loader.findType(sType.getName());
		        
		        System.out.println("Loader Java:"+type.getFullJavaName()+" Java Sys:"+sys.findType(sType.getName()).getFullJavaName());
		        */
		        
		        List list = Arrays.asList(sType.getElementProperties());
		        for (int ii=0; ii<list.size(); ii++){
		        		SchemaProperty prop = (SchemaProperty)list.get(ii);
		        		
		        		
		        		// if isAtomicType => getPrimitiveType
		        		
		        		//System.out.println("property:"+prop.getName()+" type:"+prop.getType().getPrimitiveType().getShortJavaName());
		        		//System.out.println("property:"+prop.getName()+" type:"+prop.getType().getPrimitiveType().getName().getLocalPart());
		        		System.out.println("property:"+prop.getName()+" type:"+XMLTypeMapper.getJavaTypeForBuiltInType(prop.getType().getPrimitiveType().getBuiltinTypeCode()));
		        		
		        		//SchemaType primitive = BuiltinSchemaTypeSystem.get().findType(prop.getType().getPrimitiveType().getName());
		        		//System.out.println("primitive:"+ primitive +" typeCode:"+primitive.getBuiltinTypeCode()+" isBuiltin "+primitive.isBuiltinType()+" isPrimitive:"+primitive.isPrimitiveType()+ "isSimple:"+primitive.isSimpleType()+" isDocument"+primitive.isDocumentType());
		        		
		        }
		        
		        allSeenTypes.addAll(Arrays.asList(sType.getAnonymousTypes()));
		    }
		    System.out.println("list of Global types:"+allSeenTypes);
		 
		    
		    
		    
		    
		   /* 
		    System.out.println("schema:"+schemaTypeSystem.toString());
		    List types = new ArrayList();
		    types.addAll(Arrays.asList(schemaTypeSystem.globalTypes()));
		    types.addAll(Arrays.asList(schemaTypeSystem.documentTypes()));
		    types.addAll(Arrays.asList(schemaTypeSystem.attributeTypes()));
		   
		   for (Iterator ig = Arrays.asList(schemaTypeSystem.globalElements()).iterator(); ig.hasNext(); )
		   {
			   SchemaGlobalElement element = (SchemaGlobalElement)ig.next();
			   System.out.println("elemnt:"+element.getName());
		   }
		   System.out.println("globalTypes:"+Arrays.asList(schemaTypeSystem.globalTypes()));
		   System.out.println("globalAttributes:"+Arrays.asList(schemaTypeSystem.globalAttributes()));
		   
		    System.out.println("list:"+types);
		    for (Iterator i = types.iterator(); i.hasNext(); )
		    {
		        SchemaType type = (SchemaType)i.next();
		        
		        //if (type.isBuiltinType())
		          //  continue;
		        //if (type.getFullJavaName() == null)
		          //  continue;

		        String fjn = type.getFullJavaName();
		        System.out.println("type:"+fjn);
		    }
		    */
			}catch(Exception e){e.printStackTrace();fail("exception:"+e.toString());}
			
			}
		
	

}
