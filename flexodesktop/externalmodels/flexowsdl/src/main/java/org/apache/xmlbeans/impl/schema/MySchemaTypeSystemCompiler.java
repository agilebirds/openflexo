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

/**
 * Copy of SchemaTypeSystemCompiler.
 * The only purpose of this is to compile a schema AND javaize everyType in order
 * to get the exact generated Java Names for every xml Type as if the java files were
 * really generated with xmlbeans compiler.
 * 
 * BRUTAL: we could maybe just have re-written our own "java namer module"
 */

package org.apache.xmlbeans.impl.schema;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.BindingConfig;
import org.apache.xmlbeans.Filer;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlErrorCodes;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.common.XmlErrorWatcher;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument.Schema;

public class MySchemaTypeSystemCompiler {
	public static SchemaTypeSystem compile2(String name, SchemaTypeSystem existingSTS, XmlObject[] schemas, BindingConfig config,
			SchemaTypeLoader linkTo, Filer filer, XmlOptions options) {
		try {
			SchemaTypeSystem sys = XmlBeans.compileXmlBeans(name, existingSTS, schemas, config, linkTo, filer, options);
			return sys;
		} catch (XmlException e) {
			e.printStackTrace();
			return null;
		}

	}

	/*
	SchemaTypeSystem sts = XmlBeans.compileXsd(new XmlObject[]
	                                                         { XmlObject.Factory.parse(myXSDFile) },
	                                                         XmlBeans.getBuiltinTypeSystem(),
	                                                         null);
	                                                      SchemaTypeLoader stl = XmlBeans.typeLoaderUnion(new SchemaTypeLoader[]
	                                                         { sts, XmlBeans.getBuiltinTypeSystem() });
	                                                      XmlObject mydoc = stl.parse(instanceFile, null, null);
	                                                      System.out.println("Document valid: " + mydoc.validate());
	  */

	/**
	 * Please do not invoke this method directly as the signature could change unexpectedly. Use one of
	 * {@link XmlBeans#loadXsd(XmlObject[])}, {@link XmlBeans#compileXsd(XmlObject[], SchemaTypeLoader, XmlOptions)}, or
	 * {@link XmlBeans#compileXmlBeans(String, SchemaTypeSystem, XmlObject[], BindingConfig, SchemaTypeLoader, Filer, XmlOptions)}
	 */
	public static SchemaTypeSystemImpl compile(String name, SchemaTypeSystem existingSTS, XmlObject[] input, BindingConfig config,
			SchemaTypeLoader linkTo, Filer filer, XmlOptions options) throws XmlException {
		options = XmlOptions.maskNull(options);
		ArrayList schemas = new ArrayList();

		if (input != null) {
			for (int i = 0; i < input.length; i++) {
				if (input[i] instanceof Schema) {
					schemas.add(input[i]);
				} else if (input[i] instanceof SchemaDocument && ((SchemaDocument) input[i]).getSchema() != null) {
					schemas.add(((SchemaDocument) input[i]).getSchema());
				} else {
					throw new XmlException("Thread " + Thread.currentThread().getName() + ": The " + i
							+ "th supplied input is not a schema document: its type is " + input[i].schemaType());
				}
			}
		}

		Collection userErrors = (Collection) options.get(XmlOptions.ERROR_LISTENER);
		XmlErrorWatcher errorWatcher = new XmlErrorWatcher(userErrors);

		/**
		 * MODIF FROM ORIGINAL: javaize set to TRUE SchemaTypeSystemImpl stsi = compileImpl(existingSTS, name, (Schema[])schemas.toArray(new
		 * Schema[schemas.size()]), config, linkTo, options, errorWatcher, filer!=null, null, null, null);
		 */
		SchemaTypeSystemImpl stsi = compileImpl(existingSTS, name, (Schema[]) schemas.toArray(new Schema[schemas.size()]), config, linkTo,
				options, errorWatcher, true, null, null, null);

		// if there is an error and compile didn't recover (stsi==null), throw exception
		if (errorWatcher.hasError() && stsi == null) {
			throw new XmlException(errorWatcher.firstError());
		}

		/**
		 * MODIF FROM ORIGINAL: DO NOT WRITE FILES if (stsi != null && !stsi.isIncomplete() && filer != null) { stsi.save(filer);
		 * generateTypes(stsi, filer, options); }
		 */
		return stsi;
	}

	//
	// Compiles a SchemaTypeSystem
	//
	public static SchemaTypeSystemImpl compileImpl(SchemaTypeSystem system, String name, Schema[] schemas, BindingConfig config,
			SchemaTypeLoader linkTo, XmlOptions options, Collection outsideErrors, boolean javaize, URI baseURI, Map sourcesToCopyMap,
			File schemasDir) {
		if (linkTo == null) {
			throw new IllegalArgumentException("Must supply linkTo");
		}

		XmlErrorWatcher errorWatcher = new XmlErrorWatcher(outsideErrors);
		boolean incremental = system != null;

		// construct the state
		StscState state = StscState.start();
		boolean validate = (options == null || !options.hasOption(XmlOptions.COMPILE_NO_VALIDATION));
		try {
			state.setErrorListener(errorWatcher);
			state.setBindingConfig(config);
			state.setOptions(options);
			state.setGivenTypeSystemName(name);
			state.setSchemasDir(schemasDir);
			if (baseURI != null) {
				state.setBaseUri(baseURI);
			}

			// construct the classpath (you always get the builtin types)
			linkTo = SchemaTypeLoaderImpl.build(new SchemaTypeLoader[] { BuiltinSchemaTypeSystem.get(), linkTo }, null, null);
			state.setImportingTypeLoader(linkTo);

			List validSchemas = new ArrayList(schemas.length);

			// load all the xsd files into it
			if (validate) {
				XmlOptions validateOptions = new XmlOptions().setErrorListener(errorWatcher);
				if (options.hasOption(XmlOptions.VALIDATE_TREAT_LAX_AS_SKIP)) {
					validateOptions.setValidateTreatLaxAsSkip();
				}
				for (int i = 0; i < schemas.length; i++) {
					if (schemas[i].validate(validateOptions)) {
						validSchemas.add(schemas[i]);
					}
				}
			} else {
				validSchemas.addAll(Arrays.asList(schemas));
			}

			Schema[] startWith = (Schema[]) validSchemas.toArray(new Schema[validSchemas.size()]);

			if (incremental) {
				Set namespaces = new HashSet();
				startWith = getSchemasToRecompile((SchemaTypeSystemImpl) system, startWith, namespaces);
				state.initFromTypeSystem((SchemaTypeSystemImpl) system, namespaces);
			} else {
				state.setDependencies(new SchemaDependencies());
			}

			// deal with imports and includes
			StscImporter.SchemaToProcess[] schemasAndChameleons = StscImporter.resolveImportsAndIncludes(startWith);

			// call the translator so that it may also perform magic
			StscTranslator.addAllDefinitions(schemasAndChameleons);

			// call the resolver to do its magic
			StscResolver.resolveAll();

			// call the checker to check both restrictions and defaults
			StscChecker.checkAll();

			// call the javaizer to do its magic
			StscJavaizer.javaizeAllTypes(javaize);

			// construct the loader out of the state

			StscState.get().sts().loadFromStscState(state);

			// fill in the source-copy map
			if (sourcesToCopyMap != null) {
				sourcesToCopyMap.putAll(state.sourceCopyMap());
			}

			if (errorWatcher.hasError()) {
				// EXPERIMENTAL: recovery from compilation errors and partial type system
				if (state.allowPartial() && state.getRecovered() == errorWatcher.size()) {
					// if partial type system allowed and all errors were recovered
					StscState.get().sts().setIncomplete(true);
				} else {
					// if any non-recoverable errors, return null
					return null;
				}
			}

			if (system != null) {
				((SchemaTypeSystemImpl) system).setIncomplete(true);
			}

			return StscState.get().sts();
		} finally {
			StscState.end();
		}
	}

	/**
	 * Get the list of Schemas to be recompiled, based on the list of Schemas that were modified. We make use of the depencency information
	 * that we stored in the typesystem and of the entity resolvers that have been set up
	 */
	private static Schema[] getSchemasToRecompile(SchemaTypeSystemImpl system, Schema[] modified, Set namespaces) {
		Set modifiedFiles = new HashSet();
		Map haveFile = new HashMap();
		List result = new ArrayList();
		for (int i = 0; i < modified.length; i++) {
			String fileURL = modified[i].documentProperties().getSourceName();
			if (fileURL == null) {
				throw new IllegalArgumentException("One of the Schema files passed in"
						+ " doesn't have the source set, which prevents it to be incrementally" + " compiled");
			}
			modifiedFiles.add(fileURL);
			haveFile.put(fileURL, modified[i]);
			result.add(modified[i]);
		}
		SchemaDependencies dep = system.getDependencies();
		List nss = dep.getNamespacesTouched(modifiedFiles);
		namespaces.addAll(dep.computeTransitiveClosure(nss));
		List needRecompilation = dep.getFilesTouched(namespaces);
		StscState.get().setDependencies(new SchemaDependencies(dep, namespaces));
		for (int i = 0; i < needRecompilation.size(); i++) {
			String url = (String) needRecompilation.get(i);
			Schema have = (Schema) haveFile.get(url);
			if (have == null) {
				// We have to load the file from the entity resolver
				try {
					XmlObject xdoc = StscImporter.DownloadTable.downloadDocument(StscState.get().getS4SLoader(), null, url);
					XmlOptions voptions = new XmlOptions();
					voptions.setErrorListener(StscState.get().getErrorListener());
					if (!(xdoc instanceof SchemaDocument) || !xdoc.validate(voptions)) {
						StscState.get().error("Referenced document is not a valid schema, URL = " + url,
								XmlErrorCodes.CANNOT_FIND_RESOURCE, null);
						continue;
					}

					SchemaDocument sDoc = (SchemaDocument) xdoc;

					result.add(sDoc.getSchema());
				} catch (java.net.MalformedURLException mfe) {
					StscState.get().error(XmlErrorCodes.EXCEPTION_LOADING_URL,
							new Object[] { "MalformedURLException", url, mfe.getMessage() }, null);
					continue;
				} catch (java.io.IOException ioe) {
					StscState.get().error(XmlErrorCodes.EXCEPTION_LOADING_URL, new Object[] { "IOException", url, ioe.getMessage() }, null);
					continue;
				} catch (XmlException xmle) {
					StscState.get().error(XmlErrorCodes.EXCEPTION_LOADING_URL, new Object[] { "XmlException", url, xmle.getMessage() },
							null);
					continue;
				}
			}
		}
		return (Schema[]) result.toArray(new Schema[result.size()]);
	}

}
