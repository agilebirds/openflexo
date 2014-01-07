package org.openflexo.xmlcode.pamelagenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.xmlcode.HashtableKeyValueProperty;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.ModelEntity;
import org.openflexo.xmlcode.ModelProperty;
import org.openflexo.xmlcode.VectorKeyValueProperty;
import org.openflexo.xmlcode.XMLMapping;
import org.xml.sax.SAXException;

public class PAMELAGenerator {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

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

		Iterator<ModelEntity> it = xmlMapping.allModelEntitiesStoredByClassNames();

		while (it.hasNext()) {
			ModelEntity entity = it.next();
			// System.out.println("entity: " + entity.getName());
			File sourceFile = getSourceFile(entity);
			if (sourceFile == null) {
				System.err.println("ERROR: cannot lookup source file for " + entity.getName());
			} else {
				System.out.println("Processing source file: " + sourceFile);
				try {
					generateEntity(entity, sourceFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		/*Class builderClass = xmlMapping.builderClass();
		System.out.println("Builder=" + builderClass);
		if (builderClass != null) {
			System.out.println("Builder Source file = " + getSourceFile(builderClass, sourceCode));
		}*/

	}

	private File getSourceFile(ModelEntity entity) {
		Class entityClass = entity.getRelatedClass();
		// System.out.println("Searching source file for " + entityClass);
		return getSourceFile(entityClass, sourceCode);
	}

	private File getSourceFile(Class clazz, File dir) {
		StringTokenizer st = new StringTokenizer(clazz.getName(), ".");
		File current = dir;
		boolean stopSearch = false;
		while (st.hasMoreElements() && !stopSearch) {
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
				stopSearch = true;
			}
		}
		if (current.exists() && !stopSearch) {
			System.out.println("Found: " + current + " for " + clazz);
			return current;
		}

		// System.out.println("Cannot find " + clazz + " in " + dir + ". Looking inside");

		if (dir.isDirectory()) {
			for (File d : dir.listFiles()) {
				if (d.isDirectory()) {
					File returned = getSourceFile(clazz, d);
					if (returned != null) {
						return returned;
					}
				}
			}
		}

		return null;
	}

	private void generateEntity(ModelEntity entity, File sourceFile) throws IOException {
		ParsedJavaFile parsedJavaFile = new ParsedJavaFile(sourceFile);

		String interfaceInnerSourceCode = buildInterfaceInnerSourceCode(entity);

		StringBuilder sb = new StringBuilder();

		String interfaceClassName = entity.getRelatedClass().getSimpleName();
		String implementationClassName = entity.getRelatedClass().getSimpleName() + "Impl";

		// Uncomment this
		sb.append(parsedJavaFile.prelude);

		sb.append("@ModelEntity" + (entity.isAbstract() ? "(isAbstract = true)" : "") + LINE_SEPARATOR);
		sb.append("@ImplementationClass(" + interfaceClassName + "." + implementationClassName + ".class)" + LINE_SEPARATOR);
		if (!entity.isAbstract()) {
			if (entity.getDefaultXmlTag().equals(interfaceClassName)) {
				sb.append("@XMLElement" + LINE_SEPARATOR);
			} else {
				sb.append("@XMLElement(xmlTag=\"" + entity.getDefaultXmlTag() + "\")" + LINE_SEPARATOR);
			}
			sb.append("@XMLElement(xmlTag=\"" + entity.getDefaultXmlTag() + "\")" + LINE_SEPARATOR);
		}

		sb.append(parsedJavaFile.header.headerForInterface() + "{" + LINE_SEPARATOR);
		sb.append(interfaceInnerSourceCode);

		sb.append(parsedJavaFile.header.headerForImplementation() + LINE_SEPARATOR);

		String newCode = replaceStringByStringInString("public " + interfaceClassName, "public " + implementationClassName,
				parsedJavaFile.code);

		// Uncomment this
		sb.append(newCode);

		sb.append("}" + LINE_SEPARATOR);

		System.out.println("GENERATED: " + sb.toString());

		// File outputFile = new File(sourceFile.getParent(), entity.getRelatedClass().getSimpleName() + "2.java");
		// saveToFile(outputFile, sb.toString(), null);

		saveToFile(sourceFile, sb.toString(), null);
	}

	private String buildInterfaceInnerSourceCode(ModelEntity entity) {

		StringBuilder sb = new StringBuilder();

		List<PropertyCode> pCodeList = new ArrayList<PropertyCode>();

		Iterator<ModelProperty> it = entity.getModelPropertiesIterator();

		if (it == null) {
			return "// WARNING: could not find any property in this class" + LINE_SEPARATOR;
		}

		sb.append(LINE_SEPARATOR);

		while (it.hasNext()) {
			ModelProperty p = it.next();
			PropertyCode pCode = new PropertyCode(p);
			pCodeList.add(pCode);
		}

		for (PropertyCode pCode : pCodeList) {
			sb.append(pCode.identifierCode + LINE_SEPARATOR);
		}

		sb.append(LINE_SEPARATOR);

		for (PropertyCode pCode : pCodeList) {
			sb.append(pCode.getterCode);
			sb.append(LINE_SEPARATOR);
			sb.append(pCode.setterCode);
			sb.append(LINE_SEPARATOR);
			if (pCode.property.isVector()) {
				if (pCode.adderCode != null) {
					sb.append(pCode.adderCode);
					sb.append(LINE_SEPARATOR);
				}
				if (pCode.removerCode != null) {
					sb.append(pCode.removerCode);
					sb.append(LINE_SEPARATOR);
				}
			}
			if (pCode.property.isHashtable()) {
				if (pCode.adderCode != null) {
					sb.append(pCode.adderCode);
					sb.append(LINE_SEPARATOR);
				}
				if (pCode.removerCode != null) {
					sb.append(pCode.removerCode);
					sb.append(LINE_SEPARATOR);
				}
			}
			sb.append(LINE_SEPARATOR);
		}

		return sb.toString();
	}

	private class PropertyCode {

		private String identifierCode;
		private String getterCode;
		private String setterCode;
		private String adderCode;
		private String removerCode;
		private final ModelProperty property;
		private final String identifier;

		public PropertyCode(ModelProperty property) {
			this.property = property;
			identifier = identifierStringForName(property.getName()) + "_KEY";
			identifierCode = "@PropertyIdentifier(type=" + property.getType().getSimpleName() + ".class)" + LINE_SEPARATOR;
			identifierCode += "public static final String " + identifier + " = \"" + property.getName() + "\";";

			String typeAsString = null;

			if (property.isSingle()) {
				typeAsString = property.getType().getSimpleName();
			} else if (property.isVector()) {
				VectorKeyValueProperty kvProperty = (VectorKeyValueProperty) property.getKeyValueProperty();
				typeAsString = "List<" + kvProperty.getContentType().getSimpleName() + ">";
			} else if (property.isHashtable()) {
				HashtableKeyValueProperty kvProperty = (HashtableKeyValueProperty) property.getKeyValueProperty();
				typeAsString = "Map<" + kvProperty.getKeyType().getSimpleName() + "," + kvProperty.getContentType().getSimpleName() + ">";
			}

			getterCode = "@Getter(value="
					+ identifier
					+ (property.getType().isPrimitive() ? ",defaultValue = \"" + (property.getType().equals(Boolean.TYPE) ? "false" : "0")
							+ "\"" : "") + (property.isVector() ? ",cardinality = Cardinality.LIST" : "") + ")" + LINE_SEPARATOR;
			if (property.getIsAttribute()) {
				if (property.getDefaultXmlTag().equals(property.getName())) {
					getterCode += "@XMLAttribute" + LINE_SEPARATOR;
				} else {
					getterCode += "@XMLAttribute(xmlTag=" + '"' + property.getDefaultXmlTag() + '"' + ")" + LINE_SEPARATOR;
				}
			}

			String getMethodName;
			if (property.getKeyValueProperty().getGetMethod() != null) {
				getMethodName = property.getKeyValueProperty().getGetMethod().getName();
			} else {
				String propertyNameWithFirstCharToUpperCase = property.getName().substring(0, 1).toUpperCase()
						+ property.getName().substring(1, property.getName().length());
				getMethodName = "get" + propertyNameWithFirstCharToUpperCase;
			}
			getterCode += "public " + typeAsString + " " + getMethodName + "();" + LINE_SEPARATOR;

			setterCode = "@Setter(" + identifier + ")" + LINE_SEPARATOR;

			String setMethodName;
			if (property.getKeyValueProperty().getSetMethod() != null) {
				setMethodName = property.getKeyValueProperty().getSetMethod().getName();
			} else {
				String propertyNameWithFirstCharToUpperCase = property.getName().substring(0, 1).toUpperCase()
						+ property.getName().substring(1, property.getName().length());
				setMethodName = "set" + propertyNameWithFirstCharToUpperCase;
			}
			setterCode += "public void " + setMethodName + "(" + typeAsString + " " + property.getName() + ");" + LINE_SEPARATOR;

			if (property.isVector()) {
				VectorKeyValueProperty kvProperty = (VectorKeyValueProperty) property.getKeyValueProperty();
				String contentTypeAsString = kvProperty.getContentType().getSimpleName();
				String paramName;
				if (property.getName().endsWith("s") || property.getName().endsWith("S")) {
					paramName = "a" + property.getName().substring(0, 1).toUpperCase()
							+ property.getName().substring(1, property.getName().length() - 1);
				} else {
					paramName = "a" + property.getName().substring(0, 1).toUpperCase() + property.getName().substring(1);
				} // end of else
				adderCode = "@Adder(" + identifier + ")" + LINE_SEPARATOR;
				adderCode += "public void " + kvProperty.getDefaultAddToMethod().getName() + "(" + contentTypeAsString + " " + paramName
						+ ");" + LINE_SEPARATOR;
				removerCode = "@Remover(" + identifier + ")" + LINE_SEPARATOR;
				removerCode += "public void " + kvProperty.getDefaultRemoveFromMethod().getName() + "(" + contentTypeAsString + " "
						+ paramName + ");" + LINE_SEPARATOR;

			}

		}
	}

	private class ParsedJavaFile {
		public String prelude;
		public String headerAsString;
		public String code;
		public ClassHeader header;

		public ParsedJavaFile(File sourceFile) throws IOException {
			FileInputStream fis = new FileInputStream(sourceFile);

			StringBuilder builder = new StringBuilder();
			int ch;
			while ((ch = fis.read()) != -1) {
				builder.append((char) ch);
			}

			String fileContents = builder.toString();
			// System.out.println("fileContents=" + fileContents);

			int headerIndex = fileContents.indexOf("public ");
			int startCode = fileContents.indexOf("{", headerIndex);

			prelude = fileContents.substring(0, headerIndex);
			headerAsString = fileContents.substring(headerIndex, startCode);
			header = new ClassHeader(headerAsString);
			code = fileContents.substring(startCode);

			// System.out.println("prelude=" + prelude);
			// System.out.println("header=" + headerAsString);
			// System.out.println("code=" + code);

		}
	}

	private class ClassHeader {

		public String modifiers;
		public String className;
		public String extendsDeclaration = null;
		public String implementsDeclaration = null;

		public ClassHeader(String headerAsString) throws IOException {

			int classIndex = headerAsString.indexOf("class ");
			int extendsIndex = headerAsString.indexOf("extends ");
			int implementsIndex = headerAsString.indexOf("implements ");

			modifiers = headerAsString.substring(0, classIndex - 1);
			if (extendsIndex < 0) {
				className = headerAsString.substring(classIndex + 6, headerAsString.length() - 1);
			} else {
				className = headerAsString.substring(classIndex + 6, extendsIndex - 1);
				if (implementsIndex < 0) {
					extendsDeclaration = headerAsString.substring(extendsIndex + 8, headerAsString.length() - 1);
				} else {
					extendsDeclaration = headerAsString.substring(extendsIndex + 8, implementsIndex - 1);
					implementsDeclaration = headerAsString.substring(implementsIndex + 11, headerAsString.length() - 1);
				}
			}

			// System.out.println("modifiers = [" + modifiers + "]");
			// System.out.println("className = [" + className + "]");
			// System.out.println("extendsDeclaration = [" + extendsDeclaration + "]");
			// System.out.println("implementsDeclaration = [" + implementsDeclaration + "]");

		}

		public String headerForInterface() {
			String newExtends = (extendsDeclaration != null ? extendsDeclaration : "");
			if (implementsDeclaration != null) {
				if (newExtends.length() > 0) {
					newExtends += ",";
				}
				newExtends += implementsDeclaration;
			}
			return modifiers + " interface " + className + " extends " + newExtends;
		}

		public String headerForImplementation() {
			String newModifiers = modifiers.substring(0, 6) + " static abstract " + modifiers.substring(6);
			String newClassName = className + "Impl";
			return newModifiers + " class " + newClassName + (extendsDeclaration != null ? " extends " + extendsDeclaration + "Impl" : "")
					+ " implements " + className;
		}
	}

	public static boolean createNewFile(File newFile) throws IOException {
		boolean ret = false;
		if (!newFile.exists()) {
			if (!newFile.getParentFile().exists()) {
				ret = newFile.getParentFile().mkdirs();
				if (!ret) {
					newFile = newFile.getCanonicalFile();
					ret = newFile.getParentFile().mkdirs();
				}
				if (!ret) {
					System.err.println("WARNING: cannot create directory: " + newFile.getParent() + " createNewFile(File)");
				}
			}
			try {
				ret = newFile.createNewFile();
			} catch (IOException e) {
				newFile = newFile.getCanonicalFile();
				ret = newFile.createNewFile();
				if (!ret) {
					System.err.println("WARNING: cannot create file: " + newFile.getAbsolutePath() + " createNewFile(File)");
				}
			}
		}
		return ret;
	}

	public static void saveToFile(File dest, String fileContent, String encoding) throws IOException {
		createNewFile(dest);
		FileOutputStream fos = new FileOutputStream(dest);
		BufferedReader bufferedReader = new BufferedReader(new StringReader(fileContent));
		OutputStreamWriter fw = new OutputStreamWriter(fos, Charset.forName(encoding != null ? encoding : "UTF-8"));
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				fw.write(line);
				fw.write(LINE_SEPARATOR);
			}
			fw.flush();
		} finally {
			if (fos != null) {
				fos.close();
			}
			if (fw != null) {
				fw.close();
			}
		}
	}

	public static String replaceStringByStringInString(String replacedString, String aNewString, String message) {
		if (message == null || message.equals("")) {
			return "";
		}
		if (replacedString == null || replacedString.equals("")) {
			return message;
		}
		if (aNewString == null || aNewString.equals("")) {
			aNewString = "";
		}

		// String newString = "";
		// int replacedStringLength = replacedString.length();
		// int indexOfTag = message.indexOf(replacedString);
		// while (indexOfTag != -1) {
		// newString = newString + message.substring(0, indexOfTag) + aNewString;
		// message = message.substring(indexOfTag + replacedStringLength);
		// indexOfTag = message.indexOf(replacedString);
		// }
		// return newString + message;

		StringBuffer newString = new StringBuffer("");
		int replacedStringLength = replacedString.length();
		int indexOfTag = message.indexOf(replacedString);
		while (indexOfTag != -1) {
			newString.append(message.substring(0, indexOfTag)).append(aNewString);
			message = message.substring(indexOfTag + replacedStringLength);
			indexOfTag = message.indexOf(replacedString);
		}
		return newString.append(message).toString();
	}

	private String identifierStringForName(String propertyName) {
		StringBuffer returned = new StringBuffer();
		boolean changeCase = false;
		for (int i = 0; i < propertyName.length(); i++) {
			char c = propertyName.charAt(i);
			if (i > 0 && c >= 'A' && c <= 'Z') {
				if (!changeCase) {
					returned.append("_");
				}
				changeCase = true;
			} else {
				changeCase = false;
			}
			returned.append(("" + c).toUpperCase());
		}
		return returned.toString();
	}
}
