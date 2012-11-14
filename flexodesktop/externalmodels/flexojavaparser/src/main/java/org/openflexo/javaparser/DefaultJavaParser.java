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
package org.openflexo.javaparser;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DuplicateMethodSignatureException;
import org.openflexo.foundation.dm.javaparser.JavaClassParser;
import org.openflexo.foundation.dm.javaparser.JavaFieldParser;
import org.openflexo.foundation.dm.javaparser.JavaMethodParser;
import org.openflexo.foundation.dm.javaparser.JavaParseException;
import org.openflexo.foundation.dm.javaparser.ParsedJavaClass;
import org.openflexo.foundation.dm.javaparser.ParsedJavaField;
import org.openflexo.foundation.dm.javaparser.ParsedJavaMethod;
import org.openflexo.foundation.dm.javaparser.ParsedJavaMethod.ParsedJavaMethodParameter;
import org.openflexo.foundation.dm.javaparser.ParsedJavadoc;
import org.openflexo.foundation.dm.javaparser.ParsedJavadocItem;
import org.openflexo.javaparser.FJPTypeResolver.CrossReferencedEntitiesException;
import org.openflexo.toolbox.StringUtils;

import com.thoughtworks.qdox.parser.ParseException;

public class DefaultJavaParser implements JavaClassParser, JavaMethodParser, JavaFieldParser {

	private static final Logger logger = Logger.getLogger(DefaultJavaParser.class.getPackage().getName());

	@Override
	public ParsedJavadoc parseJavadocForClass(String classCode, DMModel dataModel) throws JavaParseException {
		try {
			String sourceName = "TemporaryClass";
			FJPJavaSource source = new FJPJavaSource(sourceName, classCode, dataModel.getClassLibrary());
			FJPJavaClass parsedClass = source.getRootClass();
			return parsedClass.getJavadoc();
		}

		catch (ParseException e) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Parse error");
			}
			throw new JavaParseException();
		}

	}

	@Override
	public ParsedJavaClass parseClass(String classCode, DMModel dataModel) throws JavaParseException {
		try {
			String sourceName = "TemporaryClass";
			FJPJavaSource source = new FJPJavaSource(sourceName, classCode, dataModel.getClassLibrary());
			FJPJavaClass parsedClass = source.getRootClass();
			return parsedClass;
		}

		catch (ParseException e) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Parse error");
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Code: " + classCode);
			}
			throw new JavaParseException();
		}
	}

	@Override
	public FJPJavaMethod parseMethod(String methodCode, DMModel dataModel) throws JavaParseException {
		try {
			String parsedString = "public class TemporaryClass {" + StringUtils.LINE_SEPARATOR + methodCode + StringUtils.LINE_SEPARATOR
					+ "}" + StringUtils.LINE_SEPARATOR;

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Parsing " + parsedString);
			}

			String sourceName = "TemporaryClass";
			FJPJavaSource source = new FJPJavaSource(sourceName, parsedString, dataModel.getClassLibrary());
			FJPJavaClass parsedClass = source.getRootClass();
			if (parsedClass.getMethods().length == 0) {
				return null;
			}
			FJPJavaMethod parsedMethod = parsedClass.getMethods()[0];

			return parsedMethod;
		}

		catch (ParseException e) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Parse error: " + methodCode);
			}
			throw new JavaParseException();
		}

	}

	@Override
	public ParsedJavaField parseField(String fieldCode, DMModel dataModel) throws JavaParseException {
		String parsedString = null;
		try {
			parsedString = "public class TemporaryClass {" + StringUtils.LINE_SEPARATOR + fieldCode + StringUtils.LINE_SEPARATOR + "}"
					+ StringUtils.LINE_SEPARATOR;

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Parsing " + parsedString);
			}

			String sourceName = "TemporaryClass";
			FJPJavaSource source = new FJPJavaSource(sourceName, parsedString, dataModel.getClassLibrary());
			FJPJavaClass parsedClass = source.getRootClass();
			if (parsedClass.getFields().length == 0) {
				return null;
			}
			FJPJavaField parsedField = parsedClass.getFields()[0];

			return parsedField;
		}

		catch (ParseException e) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Parse error: " + fieldCode);
			}
			throw new JavaParseException();
		}

	}

	@Override
	public ParsedJavadoc parseJavadocForMethod(String methodCode, DMModel dataModel) throws JavaParseException {
		try {
			String parsedString = "public class TemporaryClass {" + StringUtils.LINE_SEPARATOR + methodCode + StringUtils.LINE_SEPARATOR
					+ "}" + StringUtils.LINE_SEPARATOR;

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Parsing " + parsedString);
			}

			String sourceName = "TemporaryClass";
			FJPJavaSource source = new FJPJavaSource(sourceName, parsedString, dataModel.getClassLibrary());
			FJPJavaClass parsedClass = source.getRootClass();
			if (parsedClass.getMethods().length == 0) {
				return null;
			}
			FJPJavaMethod parsedMethod = parsedClass.getMethods()[0];

			return parsedMethod.getJavadoc();
		}

		catch (ParseException e) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Parse error");
			}
			throw new JavaParseException();
		}
	}

	@Override
	public ParsedJavadoc parseJavadocForField(String fieldCode, DMModel dataModel) throws JavaParseException {
		try {
			String parsedString = "public class TemporaryClass {" + StringUtils.LINE_SEPARATOR + fieldCode + StringUtils.LINE_SEPARATOR
					+ "}" + StringUtils.LINE_SEPARATOR;

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Parsing " + parsedString);
			}

			String sourceName = "TemporaryClass";
			FJPJavaSource source = new FJPJavaSource(sourceName, parsedString, dataModel.getClassLibrary());
			FJPJavaClass parsedClass = source.getRootClass();
			if (parsedClass.getFields().length == 0) {
				return null;
			}
			FJPJavaField parsedField = parsedClass.getFields()[0];

			return parsedField.getJavadoc();
		}

		catch (ParseException e) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Parse error");
			}
			throw new JavaParseException();
		}

	}

	public static void main(String[] args) {
		DefaultJavaParser parser = new DefaultJavaParser();
		String codeToParse = "/**" + StringUtils.LINE_SEPARATOR + " * description first line" + StringUtils.LINE_SEPARATOR
				+ " * description second line" + StringUtils.LINE_SEPARATOR + " * description third line" + StringUtils.LINE_SEPARATOR
				+ " * " + StringUtils.LINE_SEPARATOR + " * @doc UserManual a description for user manual" + StringUtils.LINE_SEPARATOR
				+ " * @doc Technical a technical description" + StringUtils.LINE_SEPARATOR + " * @param param1 param1 description"
				+ StringUtils.LINE_SEPARATOR + " * @param param2 param2 description" + StringUtils.LINE_SEPARATOR
				+ " * @param param3 param3 description" + StringUtils.LINE_SEPARATOR + " * @return foo" + StringUtils.LINE_SEPARATOR
				+ " */" + StringUtils.LINE_SEPARATOR + "public a() {}";
		try {
			ParsedJavadoc jd = parser.parseJavadocForMethod(codeToParse, null);
			if (logger.isLoggable(Level.INFO)) {
				for (ParsedJavadocItem d : jd.getDocletTags()) {
					logger.info("tag " + d.getTag() + " name=[" + d.getParameterName() + "] value=(" + d.getParameterValue() + ")");
				}
				logger.info("jd comment=" + jd.getComment());
			}

		} catch (JavaParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void updateWith(DMMethod method, ParsedJavaMethod javaMethod) throws DuplicateMethodSignatureException {
		FJPJavaMethod parsedMethod = (FJPJavaMethod) javaMethod;
		FJPJavaClass parsedClass = parsedMethod.getParentClass();
		FJPJavaSource source = parsedMethod.getJavaSource();

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Try to update method " + method + " from parsed method");
		}
		try {
			DMMethod updatedMethod = FJPDMMapper.makeMethod(parsedClass, parsedMethod.getCallSignature(), method.getDMModel(), null,
					source, false);
			if (updatedMethod == null) {
				logger.warning("Could not lookup method " + updatedMethod);
				return;
			} else {
				method.update(updatedMethod, true);
			}
		} catch (CrossReferencedEntitiesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Try to update method " + method + " from parsed method: DONE ");
		}
	}

	@Override
	public void updateGetterWith(DMProperty property, ParsedJavaMethod javaMethod) throws DuplicateMethodSignatureException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Try to update property " + property + " from parsed getter method");
		}

		if (javaMethod.getJavadoc() != null) {
			property.setDescription(javaMethod.getJavadoc().getComment());
		}

		// TODO: we can handle type reinjection here
		// TODO: handle 'static' here
	}

	@Override
	public void updateSetterWith(DMProperty property, ParsedJavaMethod javaMethod) throws DuplicateMethodSignatureException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Try to update property " + property + " from parsed setter method");
		}

		if (javaMethod.getMethodParameters().size() == 1) {
			ParsedJavaMethodParameter setterParam = javaMethod.getMethodParameters().firstElement();
			property.setSetterParamName(setterParam.getName());
		}

		// TODO: we can handle type reinjection here
		// TODO: handle 'static' here
	}

	@Override
	public void updateAdditionAccessorWith(DMProperty property, ParsedJavaMethod javaMethod) throws DuplicateMethodSignatureException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Try to update property " + property + " from parsed addition method");
		}

		if (javaMethod.getMethodParameters().size() == 1) {
			ParsedJavaMethodParameter additionMethodParam = javaMethod.getMethodParameters().firstElement();
			property.setAdditionAccessorParamName(additionMethodParam.getName());
		}

		// TODO: we can handle type reinjection here
		// TODO: we can also handle key type reinjection here
		// TODO: handle 'static' here
	}

	@Override
	public void updateRemovalAccessorWith(DMProperty property, ParsedJavaMethod javaMethod) throws DuplicateMethodSignatureException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Try to update property " + property + " from parsed removal method");
		}

		if (javaMethod.getMethodParameters().size() == 1) {
			ParsedJavaMethodParameter removalMethodParam = javaMethod.getMethodParameters().firstElement();
			property.setRemovalAccessorParamName(removalMethodParam.getName());
		}

		// TODO: we can handle key type reinjection here
		// TODO: handle 'static' here
	}

	@Override
	public void updatePropertyFromJavaField(DMProperty property, ParsedJavaField javaField) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Try to update property " + property + " from parsed field");
			// TODO implement this
			// TODO: handle 'static' here
		}
	}

}
