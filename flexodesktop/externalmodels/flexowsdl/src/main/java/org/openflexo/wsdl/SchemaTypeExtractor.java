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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.BindingConfig;
import org.apache.xmlbeans.Filer;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.schema.MySchemaTypeSystemCompiler;
import org.openflexo.toolbox.FileResource;

public class SchemaTypeExtractor {

	File wsdlFile;
	String wsdlUrl;
	// XMLObjects of schemas
	List schemaList;
	SchemaTypeSystem sts;
	SchemaTypeLoader stl;

	/**
	 * used by xml beans for writing generated files..
	 * 
	 * @author dvanvyve
	 * 
	 */
	public class MyFiler implements Filer {
		/**
		 * Creates a new schema binary file (.xsb) and returns a stream for writing to it.
		 * 
		 * @param typename
		 *            fully qualified type name
		 * @return a stream to write the type to
		 * @throws IOException
		 */
		@Override
		public OutputStream createBinaryFile(String typename) throws IOException {
			return new EmptyFileOutputStream();
		}

		/**
		 * Creates a new binding source file (.java) and returns a writer for it.
		 * 
		 * @param typename
		 *            fully qualified type name
		 * @return a stream to write the type to
		 * @throws IOException
		 */
		@Override
		public Writer createSourceFile(String typename) throws IOException {
			return new OutputStreamWriter(new EmptyFileOutputStream());
		}
	}

	private class EmptyFileOutputStream extends OutputStream {
		EmptyFileOutputStream() {
		}

		@Override
		public void close() throws IOException {
		}

		@Override
		public void write(int arg0) throws IOException {
		}

		@Override
		public void flush() throws IOException {
		}

		@Override
		public void write(byte[] arg0) throws IOException {
		}

		@Override
		public void write(byte[] arg0, int arg1, int arg2) throws IOException {
		}
	}

	public SchemaTypeExtractor(String fileName) {

		System.out.println("**** TEST testImport");
		try {

			wsdlFile = new File(fileName);
			wsdlUrl = fileName;

			SchemaTypeLoader schemaTypeLoader = null;
			XmlObject[] schemaArray = new XmlObject[] { XmlObject.Factory.parse(new FileResource("Resources/soapEncoding.xml")) };
			XmlOptions opts = new XmlOptions();
			opts.setCompileDownloadUrls();
			opts.setCompileNoValidation(); // already validated here
			WSDL2Java.MyResolver entityResolver = new WSDL2Java.MyResolver();
			opts.setEntityResolver(entityResolver);

			// TODO : use my new loadScheam method in WSDL2Java,(working but returns null if an error occurs
			// cannot manage to get the error displayed...
			/** this is another CORRECT but from modified source way to do it WITH java-ized types **/
			// test with xmlBeans normal COMPILE
			SchemaTypeSystem soapsts = MySchemaTypeSystemCompiler.compile2("SoapTypeSystem", null, schemaArray, new BindingConfig(),
					XmlBeans.getBuiltinTypeSystem(), new MyFiler(), opts);

			// SchemaTypeSystem soapsts = MySchemaTypeSystemCompiler.compile("SoapTypeSystem",null,schemaArray
			// ,null,XmlBeans.getBuiltinTypeSystem(),null,opts);
			System.out.println("soap System:" + soapsts);

			stl = XmlBeans.typeLoaderUnion(new SchemaTypeLoader[] { soapsts, XmlBeans.getBuiltinTypeSystem() });

			// / SOLUTION 1: WSDL2Java to get the wsdl schema list.
			WSDL2Java wsdl2Java = new WSDL2Java();
			// get the wsdl schema in a list of XmlObject
			List schemas = wsdl2Java.generate(null, null, null, wsdlUrl);
			schemaList = schemas;
			System.out.println("Schemas:" + schemas);
			Iterator it = schemas.iterator();
			schemaArray = new XmlObject[schemas.size()];
			int l = 0;
			while (it.hasNext()) {
				XmlObject schema = (XmlObject) it.next();
				schemaArray[l] = schema;
				l++;
			}
			// SOLUTION 2: (NOT WORKING) Try directly with xmlbeans
			// schemaArray = new XmlObject[]{ XmlObject.Factory.parse(getWsdlFile()) };

			opts = new XmlOptions();
			opts.setCompileDownloadUrls();
			opts.setCompileNoValidation(); // already validated here
			opts.setEntityResolver(entityResolver);
			/** this is another CORRECT but from modified source way to do it WITH java-ized types **/
			// sts = MySchemaTypeSystemCompiler.compile("WSDLTypeSystem",null,schemaArray ,null,stl,null,opts);

			sts = MySchemaTypeSystemCompiler.compile2("WSDLTypeSystem", null, schemaArray, new BindingConfig(), stl, new MyFiler(), opts);

			stl = XmlBeans.typeLoaderUnion(new SchemaTypeLoader[] { sts, stl });
		} catch (Exception e) {
			e.printStackTrace();
			sts = null;
			stl = null;
		}
	}

	public SchemaTypeSystem schemaTypeSystem() {
		return sts;
	}

	public SchemaTypeLoader schemaTypeLoader() {
		return stl;
	}

	public List getSchemaList() {
		return schemaList;
	}

	public File getWsdlFile() {
		return wsdlFile;
	}

	public String getWsdlUrl() {
		return wsdlUrl;
	}

}
