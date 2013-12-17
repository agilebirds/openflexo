package org.openflexo.xmlcode.pamelagenerator;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.ModelEntity;
import org.openflexo.xmlcode.XMLMapping;
import org.xml.sax.SAXException;

public class PAMELAGenerator {

	private XMLMapping xmlMapping;
	private final File sourceCode;
	private final File outputDir;

	public PAMELAGenerator(File xmlMappingFile, File sourceCode, File outputDir) {

		this.sourceCode = sourceCode;
		this.outputDir = outputDir;

		System.out.println("xmlMappingFile=" + xmlMappingFile);
		System.out.println("sourceCode=" + sourceCode);
		System.out.println("outputDir=" + outputDir);

		try {
			xmlMapping = new XMLMapping(xmlMappingFile, true);
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Iterator<ModelEntity> it = xmlMapping.allModelEntities();

		while (it.hasNext()) {
			ModelEntity entity = it.next();
			System.out.println("entity: " + entity);
			File sourceFile = getSourceFile(entity);
			System.out.println("sourceFile: " + sourceFile);
		}

		Class builderClass = xmlMapping.builderClass();
		System.out.println("Builder=" + builderClass);
		if (builderClass != null) {
			System.out.println("Builder Source file = " + getSourceFile(builderClass, sourceCode));
		}

	}

	private File getSourceFile(ModelEntity entity) {
		Class entityClass = entity.getRelatedClass();
		System.out.println("Searching source file for " + entityClass);
		return getSourceFile(entityClass, sourceCode);
	}

	private File getSourceFile(Class clazz, File dir) {
		StringTokenizer st = new StringTokenizer(clazz.getName(), ".");
		File current = dir;
		while (st.hasMoreElements()) {
			String path = st.nextToken();
			File tryThis;
			if (st.hasMoreElements()) {
				tryThis = new File(current, path);
			} else {
				if (path.contains("$")) {
					tryThis = new File(current, path.substring(0, path.indexOf("$")) + ".java");
				} else {
					tryThis = new File(current, path + ".java");
				}
			}
			if (tryThis.exists()) {
				current = tryThis;
			} else {
				System.out.println("Ben non pour " + tryThis + " class=" + clazz);
				return null;
			}
			// if (!st.hasMoreElements()) {
			// System.out.println("Du coup, je regarde si c'est pas le bon path: " + current + " pour " + clazz);
			// }
		}
		if (current.exists()) {
			System.out.println("Found: " + current + " for " + clazz);
			return current;
		}
		return null;
	}
}
