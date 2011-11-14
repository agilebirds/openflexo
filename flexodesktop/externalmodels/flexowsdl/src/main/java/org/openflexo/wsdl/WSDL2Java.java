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
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.ResourceLoader;
import org.apache.xmlbeans.SchemaCodePrinter;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlErrorCodes;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.apache.xmlbeans.impl.common.XmlErrorWatcher;
import org.apache.xmlbeans.impl.schema.PathResourceLoader;
import org.apache.xmlbeans.impl.schema.SchemaTypeLoaderImpl;
import org.apache.xmlbeans.impl.schema.SchemaTypeSystemCompiler;
import org.apache.xmlbeans.impl.schema.SchemaTypeSystemImpl;
import org.apache.xmlbeans.impl.schema.StscState;
import org.apache.xmlbeans.impl.util.FilerImpl;
import org.apache.xmlbeans.impl.xb.substwsdl.DefinitionsDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.xml.sax.EntityResolver;

import repackage.Repackager;

/**
 * inspired from Jeff Hanson which copy-pasted method from XmlBeans' SchemaCompiler Modified with more options from the original class (for
 * example, more params (for download import)
 * 
 * @author <a href="mailto:jeff@jeffhanson.com">Jeff Hanson</a>
 *         <p/>
 *         <p>
 *         <b>Revisions:</b>
 *         <p/>
 *         <p>
 *         <b>Aug 3, 2005 jhanson:</b>
 *         <ul>
 *         <li>Created file.
 *         </ul>
 */

public class WSDL2Java {
	public static class MyResolver implements org.xml.sax.EntityResolver {
		@Override
		public org.xml.sax.InputSource resolveEntity(String publicId, String systemId) {
			System.out.println("System ID: " + systemId);
			return null;
		}
	}

	/**
	 * System.out.println("Options include:"); System.out.println("    -cp [a;b;c] - classpath");
	 * System.out.println("    -d [dir] - target binary directory for .class and .xsb files");
	 * System.out.println("    -src [dir] - target directory for generated .java files");
	 * System.out.println("    -srconly - do not compile .java files or jar the output.");
	 * System.out.println("    -out [xmltypes.jar] - the name of the output jar");
	 * System.out.println("    -dl - permit network downloads for imports and includes (default is off)");
	 * System.out.println("    -noupa - do not enforce the unique particle attribution rule");
	 * System.out.println("    -nopvr - do not enforce the particle valid (restriction) rule");
	 * System.out.println("    -noann - ignore annotations");
	 * System.out.println("    -novdoc - do not validate contents of <documentation>");
	 * System.out.println("    -compiler - path to external java compiler");
	 * System.out.println("    -javasource [version] - generate java source compatible for a Java version (1.4 or 1.5)");
	 * System.out.println("    -ms - initial memory for external java compiler (default '" + CodeGenUtil.DEFAULT_MEM_START + "')");
	 * System.out.println("    -mx - maximum memory for external java compiler (default '" + CodeGenUtil.DEFAULT_MEM_MAX + "')");
	 * System.out.println("    -debug - compile with debug symbols"); System.out.println("    -quiet - print fewer informational messages");
	 * System.out.println("    -verbose - print more informational messages");
	 * System.out.println("    -version - prints version information"); System.out.println("    -license - prints license information");
	 * System.out.println("    -allowmdef \"[ns] [ns] [ns]\" - ignores multiple defs in given namespaces (use ##local for no-namespace)");
	 * System.out.println(
	 * "    -catalog [file] -  catalog file for org.apache.xml.resolver.tools.CatalogResolver. (Note: needs resolver.jar from http://xml.apache.org/commons/components/resolver/index.html)"
	 * );
	 * 
	 * @param name
	 * @param wsdlFile
	 * @param download
	 * @return
	 */

	public static SchemaTypeSystem loadTypeSystem(String name, File wsdlFile, boolean download) throws XmlException {
		File baseDir = null;// params.getBaseDir();
		File[] xsdFiles = null;// params.getXsdFiles();
		File[] wsdlFiles = new File[] { wsdlFile };// params.getWsdlFiles();
		URL[] urlFiles = null;// params.getUrlFiles();
		File[] javaFiles = null;// params.getJavaFiles();
		File[] configFiles = null;// params.getConfigFiles();
		File[] classpath = null;// params.getClasspath();
		File outputJar = null;// params.getOutputJar();
		// String name = null;//params.getName();
		File srcDir = null;// params.getSrcDir();
		File classesDir = null;// params.getClassesDir();
		String compiler = null;// params.getCompiler();
		String javasource = null;// params.getJavaSource();
		String memoryInitialSize = null;// params.getMemoryInitialSize();
		String memoryMaximumSize = null;// params.getMemoryMaximumSize();
		boolean nojavac = true;// params.isNojavac();
		boolean debug = false;// params.isDebug();
		boolean verbose = false;// params.isVerbose();
		boolean quiet = false;// params.isQuiet();
		// boolean download = download;//params.isDownload();
		boolean noUpa = false;// params.isNoUpa();
		boolean noPvr = false;// params.isNoPvr();
		boolean noAnn = true;// params.isNoAnn();
		boolean noVDoc = true;// params.isNoVDoc();
		boolean incrSrcGen = false;// params.isIncrementalSrcGen();

		Collection outerErrorListener = new ArrayList();

		String repackage = null;// params.getRepackage();

		if (repackage != null) {
			SchemaTypeLoaderImpl.METADATA_PACKAGE_LOAD = SchemaTypeSystemImpl.METADATA_PACKAGE_GEN;

			String stsPackage = SchemaTypeSystem.class.getPackage().getName();
			Repackager repackager = new Repackager(repackage);

			SchemaTypeSystemImpl.METADATA_PACKAGE_GEN = repackager.repackage(new StringBuffer(stsPackage)).toString().replace('.', '_');

			System.out.println("\n\n\n" + stsPackage + ".SchemaCompiler  Metadata LOAD:" + SchemaTypeLoaderImpl.METADATA_PACKAGE_LOAD
					+ " GEN:" + SchemaTypeSystemImpl.METADATA_PACKAGE_GEN);
		}

		SchemaCodePrinter codePrinter = null;// params.getSchemaCodePrinter();
		List extensions = null;// params.getExtensions();
		Set mdefNamespaces = null;// params.getMdefNamespaces();

		/*
		 EntityResolver cmdLineEntRes = params.getEntityResolver() == null ?
		     ResolverUtil.resolverForCatalog(params.getCatalogFile()) : params.getEntityResolver();
		*/
		MyResolver entityResolver = new MyResolver();

		/*if (srcDir == null || classesDir == null)
		    throw new IllegalArgumentException("src and class gen directories may not be null.");

		long start = System.currentTimeMillis();
		*/
		// Calculate the usenames based on the relativized filenames on the filesystem
		/*
		if (baseDir == null)
		    baseDir = new File(SystemProperties.getProperty("user.dir"));
		    */
		ResourceLoader cpResourceLoader = null;

		Map sourcesToCopyMap = new HashMap();

		if (classpath != null) {
			cpResourceLoader = new PathResourceLoader(classpath);
		}

		boolean result = true;

		File schemasDir = null;// IOUtil.createDir(classesDir, "schema" + SchemaTypeSystemImpl.METADATA_PACKAGE_GEN + "/src");

		// build the in-memory type system
		XmlErrorWatcher errorListener = new XmlErrorWatcher(outerErrorListener);

		SchemaTypeSystem stsi = loadTypeSystem(name, xsdFiles, wsdlFiles, urlFiles, configFiles, javaFiles, cpResourceLoader, download,
				noUpa, noPvr, noAnn, noVDoc, mdefNamespaces, baseDir, sourcesToCopyMap, outerErrorListener, schemasDir, entityResolver,
				classpath, javasource);
		if (errorListener.hasError() && stsi == null) {

			XmlError error = errorListener.firstError();
			System.out.println("Error:" + error.toString());
			throw new XmlException(errorListener.firstError());
		}
		return stsi;
	}

	private static SchemaTypeSystem loadTypeSystem(String name, File[] xsdFiles, File[] wsdlFiles, URL[] urlFiles, File[] configFiles,
			File[] javaFiles, ResourceLoader cpResourceLoader, boolean download, boolean noUpa, boolean noPvr, boolean noAnn,
			boolean noVDoc, Set mdefNamespaces, File baseDir, Map sourcesToCopyMap, Collection outerErrorListener, File schemasDir,
			EntityResolver entResolver, File[] classpath, String javasource)

	{
		XmlErrorWatcher errorListener = new XmlErrorWatcher(outerErrorListener);

		// construct the state (have to initialize early in case of errors)
		StscState state = StscState.start();
		state.setErrorListener(errorListener);

		// For parsing XSD and WSDL files, we should use the SchemaDocument
		// classloader rather than the thread context classloader. This is
		// because in some situations (such as when being invoked by ant
		// underneath the ide) the context classloader is potentially weird
		// (because of the design of ant).

		SchemaTypeLoader loader = XmlBeans.typeLoaderForClassLoader(SchemaDocument.class.getClassLoader());

		// step 1, parse all the XSD files.
		ArrayList scontentlist = new ArrayList();
		if (xsdFiles != null) {
			System.out.println("CODE DELETED FOR XSD FILES: SEE org.apache.xmlbeans.impl.tool.SchemaCompiler loadTypeSystem method");
		}

		// step 2, parse all WSDL files
		if (wsdlFiles != null) {
			for (int i = 0; i < wsdlFiles.length; i++) {
				try {
					XmlOptions options = new XmlOptions();
					options.setLoadLineNumbers();
					options.setLoadSubstituteNamespaces(Collections.singletonMap("http://schemas.xmlsoap.org/wsdl/",
							"http://www.apache.org/internal/xmlbeans/wsdlsubst"));
					options.setEntityResolver(entResolver);

					XmlObject wsdldoc = loader.parse(wsdlFiles[i], null, options);

					if (!(wsdldoc instanceof org.apache.xmlbeans.impl.xb.substwsdl.DefinitionsDocument)) {
						StscState.addError(errorListener, XmlErrorCodes.INVALID_DOCUMENT_TYPE, new Object[] { wsdlFiles[i], "wsdl" },
								wsdldoc);
					} else {
						addWsdlSchemas(wsdlFiles[i].toString(), (org.apache.xmlbeans.impl.xb.substwsdl.DefinitionsDocument) wsdldoc,
								errorListener, noVDoc, scontentlist);
					}
				} catch (XmlException e) {
					errorListener.add(e.getError());
				} catch (Exception e) {
					StscState.addError(errorListener, XmlErrorCodes.CANNOT_LOAD_FILE,
							new Object[] { "wsdl", wsdlFiles[i], e.getMessage() }, wsdlFiles[i]);
				}
			}
		}

		// step 3, parse all URL files
		// XMLBEANS-58 - Ability to pass URLs instead of Files for Wsdl/Schemas
		if (urlFiles != null) {
			for (int i = 0; i < urlFiles.length; i++) {
				try {
					XmlOptions options = new XmlOptions();
					options.setLoadLineNumbers();
					options.setLoadSubstituteNamespaces(Collections.singletonMap("http://schemas.xmlsoap.org/wsdl/",
							"http://www.apache.org/internal/xmlbeans/wsdlsubst"));
					options.setEntityResolver(entResolver);

					XmlObject urldoc = loader.parse(urlFiles[i], null, options);

					if ((urldoc instanceof org.apache.xmlbeans.impl.xb.substwsdl.DefinitionsDocument)) {
						addWsdlSchemas(urlFiles[i].toString(), (org.apache.xmlbeans.impl.xb.substwsdl.DefinitionsDocument) urldoc,
								errorListener, noVDoc, scontentlist);
					} else if ((urldoc instanceof SchemaDocument)) {
						System.out
								.println("CODE DELETED FOR XSD FILES: SEE org.apache.xmlbeans.impl.tool.SchemaCompiler loadTypeSystem method");
					} else {
						StscState.addError(errorListener, XmlErrorCodes.INVALID_DOCUMENT_TYPE,
								new Object[] { urlFiles[i], "wsdl or schema" }, urldoc);
					}

				} catch (XmlException e) {
					errorListener.add(e.getError());
				} catch (Exception e) {
					StscState.addError(errorListener, XmlErrorCodes.CANNOT_LOAD_FILE, new Object[] { "url", urlFiles[i], e.getMessage() },
							urlFiles[i]);
				}
			}
		}

		SchemaDocument.Schema[] sdocs = (SchemaDocument.Schema[]) scontentlist.toArray(new SchemaDocument.Schema[scontentlist.size()]);

		// now the config files.
		ArrayList cdoclist = new ArrayList();
		if (configFiles != null) {
			System.out.println("CODE DELETED FOR CONFIG FILES: SEE org.apache.xmlbeans.impl.tool.SchemaCompiler loadTypeSystem method");
		}
		// ConfigDocument.Config[] cdocs = (ConfigDocument.Config[])cdoclist.toArray(new ConfigDocument.Config[cdoclist.size()]);

		SchemaTypeLoader linkTo = SchemaTypeLoaderImpl.build(null, cpResourceLoader, null);

		URI baseURI = null;
		if (baseDir != null) {
			baseURI = baseDir.toURI();
		}

		XmlOptions opts = new XmlOptions();
		if (download) {
			opts.setCompileDownloadUrls();
		}
		if (noUpa) {
			opts.setCompileNoUpaRule();
		}
		if (noPvr) {
			opts.setCompileNoPvrRule();
		}
		if (noAnn) {
			opts.setCompileNoAnnotations();
		}
		if (mdefNamespaces != null) {
			opts.setCompileMdefNamespaces(mdefNamespaces);
		}
		opts.setCompileNoValidation(); // already validated here
		opts.setEntityResolver(entResolver);
		if (javasource != null) {
			opts.setGenerateJavaVersion(javasource);
		}

		// now pass it to the main compile function
		SchemaTypeSystemCompiler.Parameters params = new SchemaTypeSystemCompiler.Parameters();
		params.setName(name);
		params.setSchemas(sdocs);
		// params.setConfig(BindingConfigImpl.forConfigDocuments(cdocs, javaFiles, classpath));
		params.setLinkTo(linkTo);
		params.setOptions(opts);
		params.setErrorListener(errorListener);
		params.setJavaize(true);
		params.setBaseURI(baseURI);
		params.setSourcesToCopyMap(sourcesToCopyMap);
		params.setSchemasDir(schemasDir);
		SchemaTypeSystem stsi = SchemaTypeSystemCompiler.compile(params);

		return stsi;

		/*
		 *   return compileImpl(params.getExistingTypeSystem(), params.getName(),
		params.getSchemas(), params.getConfig(), params.getLinkTo(),
		params.getOptions(), params.getErrorListener(), params.isJavaize(),
		params.getBaseURI(), params.getSourcesToCopyMap(), params.getSchemasDir());
		 */
	}

	/**
	 * return the schemas
	 * 
	 * @param schemaFilesDir
	 * @param schemaClassesDir
	 * @param schemaSrcDir
	 * @param wsdlFileName
	 * @return
	 */
	public List generate(String schemaFilesDir, String schemaClassesDir, String schemaSrcDir, String wsdlFileName) {
		boolean doNotValidateContents = false;
		XmlObject wsdlDoc = null;
		MyResolver entityResolver = new MyResolver();
		ArrayList outerErrorListener = new ArrayList();
		XmlErrorWatcher errorListener = new XmlErrorWatcher(outerErrorListener);
		ArrayList scontentlist = new ArrayList();

		try {
			SchemaTypeLoader loader = XmlBeans.typeLoaderForClassLoader(SchemaDocument.class.getClassLoader());

			XmlOptions options = new XmlOptions();
			options.setLoadLineNumbers();
			options.setLoadSubstituteNamespaces(Collections.singletonMap("http://schemas.xmlsoap.org/wsdl/",
					"http://www.apache.org/internal/xmlbeans/wsdlsubst"));
			options.setEntityResolver(entityResolver);

			System.out.println("Parsing WSDL: " + wsdlFileName + "...");

			wsdlDoc = loader.parse(new File(wsdlFileName), null, options);

			if (!(wsdlDoc instanceof DefinitionsDocument)) {
				// Create new definitions doc
				DefinitionsDocument.Definitions definitions = DefinitionsDocument.Definitions.Factory.newInstance();

				// Set types array
				definitions.setTypesArray(wsdlDoc.selectPath("wsdl:types"));
				DefinitionsDocument defDoc = DefinitionsDocument.Factory.newInstance();
				defDoc.setDefinitions(definitions);

				// Adding constructed schema
				addWsdlSchemas(wsdlFileName, defDoc, errorListener, doNotValidateContents, scontentlist);
			} else {
				addWsdlSchemas(wsdlFileName, (DefinitionsDocument) wsdlDoc, errorListener, doNotValidateContents, scontentlist);
			}
		} catch (XmlException e) {
			errorListener.add(e.getError());
		} catch (Exception e) {
			StscState.addError(errorListener, XmlErrorCodes.CANNOT_LOAD_FILE, new Object[] { "wsdl", wsdlFileName, e.getMessage() },
					wsdlDoc);
		}
		/**
		 * SchemaTypeSystem sts = compileSchemas(schemaFilesDir, scontentlist, entityResolver, errorListener);
		 * 
		 * return generateJavaSource(schemaClassesDir, schemaSrcDir, sts);
		 */
		return scontentlist;
	}

	private SchemaTypeSystem compileSchemas(String schemaFilesDir, ArrayList scontentlist, MyResolver entityResolver,
			XmlErrorWatcher errorListener) {
		SchemaDocument.Schema[] sdocs = (SchemaDocument.Schema[]) scontentlist.toArray(new SchemaDocument.Schema[scontentlist.size()]);
		ResourceLoader cpResourceLoader = null;
		SchemaTypeLoader linkTo = SchemaTypeLoaderImpl.build(null, cpResourceLoader, null);

		File baseDir = new File(System.getProperty("user.dir"));
		java.net.URI baseURI = baseDir.toURI();

		XmlOptions opts = new XmlOptions();
		opts.setCompileNoValidation();
		opts.setEntityResolver(entityResolver);
		// DVA download import statement
		opts.setCompileDownloadUrls();

		Map sourcesToCopyMap = new HashMap();
		File schemasDir = IOUtil.createDir(new File("."), schemaFilesDir);

		// create parameters for the main compile function
		SchemaTypeSystemCompiler.Parameters params = new SchemaTypeSystemCompiler.Parameters();
		params.setName(null);
		params.setSchemas(sdocs);
		params.setLinkTo(linkTo);
		params.setOptions(opts);
		params.setErrorListener(errorListener);
		params.setJavaize(true);
		params.setBaseURI(baseURI);
		params.setSourcesToCopyMap(sourcesToCopyMap);
		params.setSchemasDir(schemasDir);

		System.out.println("Compiling schemas...");
		try {
			// create schema files (.xsb's)
			SchemaTypeSystem sts = SchemaTypeSystemCompiler.compile(params);

			// now save .xsb's to disk
			sts.saveToDirectory(schemasDir);
			System.out.println("Schema compilation succeeded");
			return sts;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private List generateJavaSource(String schemaClassesDir, String schemaSrcDir, SchemaTypeSystem sts) {
		File classesDir = new File(schemaClassesDir);
		File srcDir = IOUtil.createDir(new File("."), schemaSrcDir);

		// now, generate the source files
		XmlOptions options = new XmlOptions();
		boolean verbose = false;
		boolean incrSrcGen = false;
		Repackager repackager = null;
		FilerImpl filer = new FilerImpl(classesDir, srcDir, repackager, verbose, incrSrcGen);

		System.out.println("Generating Java source...");
		if (SchemaTypeSystemCompiler.generateTypes(sts, filer, options)) {
			System.out.println("Java source generation succeeded");
			return filer.getSourceFiles();
		} else {
			System.out.println("Java source generation failed");
		}

		return null;
	}

	/**
	 * original version from SchemaCompiler
	 * 
	 * @param name
	 * @param wsdldoc
	 * @param errorListener
	 * @param noVDoc
	 * @param scontentlist
	 */
	private static void addWsdlSchemas(String name, org.apache.xmlbeans.impl.xb.substwsdl.DefinitionsDocument wsdldoc,
			XmlErrorWatcher errorListener, boolean noVDoc, List scontentlist) {
		if (wsdlContainsEncoded(wsdldoc)) {
			StscState
					.addWarning(errorListener, "The WSDL " + name
							+ " uses SOAP encoding. SOAP encoding is not compatible with literal XML Schema.", XmlErrorCodes.GENERIC_ERROR,
							wsdldoc);
		}
		StscState.addInfo(errorListener, "Loading wsdl file " + name);
		XmlOptions opts = new XmlOptions().setErrorListener(errorListener);
		if (noVDoc) {
			opts.setValidateTreatLaxAsSkip();
		}
		XmlObject[] types = wsdldoc.getDefinitions().getTypesArray();
		int count = 0;
		for (int j = 0; j < types.length; j++) {
			XmlObject[] schemas = types[j].selectPath("declare namespace xs=\"http://www.w3.org/2001/XMLSchema\" xs:schema");
			if (schemas.length == 0) {
				StscState.addWarning(errorListener, "The WSDL " + name
						+ " did not have any schema documents in namespace 'http://www.w3.org/2001/XMLSchema'",
						XmlErrorCodes.GENERIC_ERROR, wsdldoc);
				continue;
			}

			for (int k = 0; k < schemas.length; k++) {
				if (schemas[k] instanceof SchemaDocument.Schema && schemas[k].validate(opts)) {
					count++;
					scontentlist.add(schemas[k]);
				}
			}
		}
		StscState.addInfo(errorListener, "Processing " + count + " schema(s) in " + name);
	}

	private static boolean wsdlContainsEncoded(XmlObject wsdldoc) {
		// search for any <soap:body use="encoded"/> etc.
		XmlObject[] useAttrs = wsdldoc.selectPath("declare namespace soap='http://schemas.xmlsoap.org/wsdl/soap/' "
				+ ".//soap:body/@use|.//soap:header/@use|.//soap:fault/@use");
		for (int i = 0; i < useAttrs.length; i++) {
			if ("encoded".equals(((SimpleValue) useAttrs[i]).getStringValue())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * modified version from Jeff Hanson
	 * 
	 * @param wsdlFileName
	 * @param definitionsDocument
	 * @param errorListener
	 * @param doNotValidateContents
	 * @param scontentlist
	 */
	/* private void addWsdlSchemas(String wsdlFileName,
	                             DefinitionsDocument definitionsDocument,
	                             XmlErrorWatcher errorListener,
	                             boolean doNotValidateContents,
	                             ArrayList scontentlist)
	 {
	    XmlOptions opts = new XmlOptions().setErrorListener(errorListener);
	    if (doNotValidateContents)
	    {
	       opts.setValidateTreatLaxAsSkip();
	    }

	    XmlObject[] types =
	       definitionsDocument.getDefinitions().getTypesArray();

	    for (int j = 0; j < types.length; j++)
	    {
	       XmlObject[] schemas =
	          types[j].selectPath("declare namespace"
	                              + " xs=\"http://www.w3.org/2001/XMLSchema\""
	                              + " xs:schema");
	       if (schemas.length == 0)
	       {
	          StscState.addWarning(errorListener,
	                               "The WSDL " + wsdlFileName
	                               + " has no schema documents in namespace "
	                               + "'http://www.w3.org/2001/XMLSchema'",
	                               XmlErrorCodes.GENERIC_ERROR,
	                               definitionsDocument);
	          continue;
	       }

	       for (int k = 0; k < schemas.length; k++)
	       {
	          if (schemas[k] instanceof SchemaDocument.Schema)
	          {
	             SchemaDocumentImpl.SchemaImpl schemaImpl =
	                (SchemaDocumentImpl.SchemaImpl)schemas[k];

	             System.out.println("Validating schema...");
	             if (schemaImpl.validate(opts))
	             {
	                System.out.println("Schema passed validation");
	                scontentlist.add(schemas[k]);
	             }
	             else
	             {
	                System.out.println("Schema failed validation");
	                scontentlist.add(schemas[k]);
	             }
	          }
	       }
	    }
	 }
	 */
}
