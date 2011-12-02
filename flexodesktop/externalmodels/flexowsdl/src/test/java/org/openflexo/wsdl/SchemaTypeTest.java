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
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.MySchemaTypeSystemCompiler;
import org.openflexo.toolbox.FileResource;

public class SchemaTypeTest extends TestCase {

	/*
		 private static final Method _compilationMethod = buildCompilationMethod();
		private static final Method buildCompilationMethod()
		    {
		        return
		            buildMethod(
		                "org.apache.xmlbeans.impl.schema.MySchemaTypeSystemCompiler", "compile",
		                new Class[] { String.class, SchemaTypeSystem.class, XmlObject[].class, BindingConfig.class, SchemaTypeLoader.class, Filer.class, XmlOptions.class } );
		    }

		   private static final Method buildMethod ( String className, String methodName, Class[] args )
		    {
		        try
		        {
		            return
		                Class.forName(
		                    className, false, XmlBeans.class.getClassLoader() ).
		                        getMethod( methodName, args );
		        }
		        catch ( Exception e )
		        {
		            throw 
		                new IllegalStateException(
		                    "Cannot load " + methodName +
		                        ": verify that xbean.jar is on the classpath");
		        }
		    }
		
		*/

	public void testImportSchema() {
		// Create Schema Type System
		System.out.println("Using the xml schema, ");
		// File schema = new File("/Users/dvanvyve/Documents/Projects/FlexoWS/easypo.xsd");
		File schema = new FileResource("I6DocWS.wsdl");
		// XmlBeans
		SchemaTypeSystem schemaTypeSystem = null;
		SchemaTypeLoader schemaTypeLoader = null;
		try {
			/* schemaTypeSystem = XmlBeans.compileXsd(
			         new XmlObject[] { XmlObject.Factory.parse(schema) }, XmlBeans.getBuiltinTypeSystem(),
			         null);
			*/
			/**
			 * this shit does not work... schemaTypeSystem =(SchemaTypeSystem) XmlBeans.loadXsd(new XmlObject[] {
			 * XmlObject.Factory.parse(schema) }); //schemaTypeSystem = (SchemaTypeSystem)schemaTypeLoader;
			 */

			/** this is the API CORRECT WAY... but the types are not java-ized ! */
			// schemaTypeSystem = XmlBeans.compileXsd(new XmlObject[]{ XmlObject.Factory.parse(schema)
			// },XmlBeans.getBuiltinTypeSystem(),null);

			/** this is another CORRECT but from modified source way to do it WITH java-ized types **/
			schemaTypeSystem = MySchemaTypeSystemCompiler.compile(null, null, new XmlObject[] { XmlObject.Factory.parse(schema) }, null,
					XmlBeans.getBuiltinTypeSystem(), null, null);

			// schemaTypeSystem = (SchemaTypeSystem)_compilationMethod.invoke(null, new Object[]{null,null,new XmlObject[]{
			// XmlObject.Factory.parse(schema) },null,XmlBeans.getBuiltinTypeSystem(),null,null});

			/** this works but generates sources files...boring */
			// schemaTypeSystem = XmlBeans.compileXmlBeans(null,null, new XmlObject[]{ XmlObject.Factory.parse(schema)
			// },null,XmlBeans.getBuiltinTypeSystem(),new FilerImpl(null,null,null,false,false), null);

			schemaTypeLoader = XmlBeans.typeLoaderUnion(new SchemaTypeLoader[] { schemaTypeSystem, XmlBeans.getBuiltinTypeSystem() });
			// XmlObject mydoc = stl.parse(instanceFile, null, null);

			/**
			 * read from SchemaTypeSystemCompiler
			 * 
			 * hope it gives java names to types.
			 */
			// StscJavaizer.javaizeAllTypes(true);

			List allSeenTypes = new ArrayList();
			allSeenTypes.addAll(Arrays.asList(schemaTypeSystem.documentTypes()));
			for (int i = 0; i < allSeenTypes.size(); i++) {
				SchemaType sType = (SchemaType) allSeenTypes.get(i);
				System.out.println("Visiting " + sType.toString());
				allSeenTypes.addAll(Arrays.asList(sType.getAnonymousTypes()));
			}
			System.out.println("list of Document types:" + allSeenTypes);

			allSeenTypes = new ArrayList();
			allSeenTypes.addAll(Arrays.asList(schemaTypeSystem.attributeTypes()));
			for (int i = 0; i < allSeenTypes.size(); i++) {
				SchemaType sType = (SchemaType) allSeenTypes.get(i);
				System.out.println("Visiting " + sType.toString());
				allSeenTypes.addAll(Arrays.asList(sType.getAnonymousTypes()));
			}
			System.out.println("list of attribute types:" + allSeenTypes);

			allSeenTypes = new ArrayList();
			allSeenTypes.addAll(Arrays.asList(schemaTypeSystem.globalTypes()));
			for (int i = 0; i < allSeenTypes.size(); i++) {
				SchemaType sType = (SchemaType) allSeenTypes.get(i);
				System.out.println("Visiting " + sType.toString());

				System.out.println("GlobalType:" + sType.getName() + " java:" + sType.getFullJavaName());
				// SchemaTypeLoader loader = XmlBeans.getContextTypeLoader();
				SchemaTypeLoader loader = schemaTypeLoader;
				SchemaType type = loader.findType(sType.getName());
				System.out.println("loader:" + loader + " Found type:" + type);
				SchemaTypeSystem sys = sType.getTypeSystem();
				System.out.println("Sys:" + sys + " foundtype:" + sys.findType(sType.getName()));

				System.out
						.println("Loader Java:" + type.getFullJavaName() + " Java Sys:" + sys.findType(sType.getName()).getFullJavaName());

				List list = Arrays.asList(sType.getElementProperties());
				for (int ii = 0; ii < list.size(); ii++) {
					SchemaProperty prop = (SchemaProperty) list.get(ii);

					// if isAtomicType => getPrimitiveType

					// System.out.println("property:"+prop.getName()+" type:"+prop.getType().getPrimitiveType().getShortJavaName());
					// System.out.println("property:"+prop.getName()+" type:"+prop.getType().getPrimitiveType().getName().getLocalPart());
					System.out.println("property:" + prop.getName() + " type:"
							+ XMLTypeMapper.getJavaTypeForBuiltInType(prop.getType().getPrimitiveType().getBuiltinTypeCode()));

					// SchemaType primitive = BuiltinSchemaTypeSystem.get().findType(prop.getType().getPrimitiveType().getName());
					// System.out.println("primitive:"+ primitive
					// +" typeCode:"+primitive.getBuiltinTypeCode()+" isBuiltin "+primitive.isBuiltinType()+" isPrimitive:"+primitive.isPrimitiveType()+
					// "isSimple:"+primitive.isSimpleType()+" isDocument"+primitive.isDocumentType());

				}

				allSeenTypes.addAll(Arrays.asList(sType.getAnonymousTypes()));
			}
			System.out.println("list of Global types:" + allSeenTypes);

			System.out.println("schema:" + schemaTypeSystem.toString());
			List types = new ArrayList();
			types.addAll(Arrays.asList(schemaTypeSystem.globalTypes()));
			types.addAll(Arrays.asList(schemaTypeSystem.documentTypes()));
			types.addAll(Arrays.asList(schemaTypeSystem.attributeTypes()));

			for (Iterator ig = Arrays.asList(schemaTypeSystem.globalElements()).iterator(); ig.hasNext();) {
				SchemaGlobalElement element = (SchemaGlobalElement) ig.next();
				System.out.println("elemnt:" + element.getName());
			}
			System.out.println("globalTypes:" + Arrays.asList(schemaTypeSystem.globalTypes()));
			System.out.println("globalAttributes:" + Arrays.asList(schemaTypeSystem.globalAttributes()));

			System.out.println("list:" + types);
			for (Iterator i = types.iterator(); i.hasNext();) {
				SchemaType type = (SchemaType) i.next();

				// if (type.isBuiltinType())
				// continue;
				// if (type.getFullJavaName() == null)
				// continue;

				String fjn = type.getFullJavaName();
				System.out.println("type:" + fjn);
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail("exception:" + e.toString());
		}

	}

}
