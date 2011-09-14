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
package org.openflexo.generator;

import java.util.Stack;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;



import org.openflexo.foundation.dm.DMCardinality;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.javaparser.AbstractSourceCode;
import org.openflexo.foundation.dm.javaparser.FieldSourceCode;
import org.openflexo.foundation.dm.javaparser.JavaParseException;
import org.openflexo.foundation.dm.javaparser.MethodSourceCode;
import org.openflexo.foundation.dm.javaparser.ParsedJavaClass;
import org.openflexo.foundation.dm.javaparser.ParsedJavaElement;
import org.openflexo.foundation.dm.javaparser.ParsedJavaField;
import org.openflexo.foundation.dm.javaparser.ParsedJavaMethod;
import org.openflexo.foundation.dm.javaparser.ParsedJavadoc;
import org.openflexo.foundation.dm.javaparser.ParserNotInstalledException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.cg.JavaFileResource;
import org.openflexo.javaparser.DefaultJavaParser;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.StringUtils;

public class JavaCodeMerger {

	protected static Logger logger = FlexoLogger.getLogger(CGGenerator.class.getPackage().getName());

	private static DefaultJavaParser javaParser = new DefaultJavaParser();
	
	public static String mergeJavaCode(String javaCode, DMEntity entity, JavaFileResource res) throws JavaParseException
	{
		if (entity == null) {
			return javaCode;
		}
		if (!entity.isCodeGenerationApplicable())
			return javaCode;
		long start, end;
		start = System.currentTimeMillis();
		String returned = javaCode;
		
		if (logger.isLoggable(Level.FINE)) logger.fine("Merging for entity :"+entity);
		if (logger.isLoggable(Level.FINER)) logger.finer("Code before merging "+javaCode);

		ParsedJavaClass parsedClass;
		ParsedJavaClass lastGeneratedClass = null;
		ParsedJavaClass lastAcceptedClass = null;

		try {
			parsedClass = javaParser.parseClass(javaCode, entity.getDMModel());
			if (res != null && res.getJavaFile() != null && res.getJavaFile().getLastGeneratedContent() != null)
				try {
					lastGeneratedClass = javaParser.parseClass(res.getJavaFile().getLastGeneratedContent(), entity.getDMModel());
				} catch (RuntimeException e) {
					if (logger.isLoggable(Level.WARNING))
						logger.warning("Could not parse last generated class.");
				}
			if (res != null && res.getJavaFile() != null && res.getJavaFile().getLastAcceptedContent() != null)
				try {
					lastAcceptedClass = javaParser.parseClass(res.getJavaFile().getLastAcceptedContent(), entity.getDMModel());
				} catch (RuntimeException e) {
					if (logger.isLoggable(Level.WARNING))
						logger.warning("Could not parse last accepted class.");
				}

			for (DMProperty p : entity.getDeclaredProperties()) 
			{
				if(!p.mustGenerateCode())continue;
				if (!p.getIsStaticallyDefinedInTemplate()) {
					if (logger.isLoggable(Level.FINE)) logger.fine("Merging property "+p.getName());
					if ((p.getImplementationType() == DMPropertyImplementationType.PUBLIC_FIELD)
							|| (p.getImplementationType() == DMPropertyImplementationType.PROTECTED_FIELD)
							|| (p.getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD)
							|| (p.getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD)
							|| (p.getImplementationType() == DMPropertyImplementationType.PUBLIC_STATIC_FINAL_FIELD)) {
						javaCode = appendSourceCode(
								p.getFieldSourceCode(),
								parsedClass,
								lastGeneratedClass,
								lastAcceptedClass,
								javaCode,
								PreferredLocation.AFTER_LAST_FIELD,
								entity.getProject());
						parsedClass = javaParser.parseClass(javaCode, entity.getDMModel());
					}
					if ((p.getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY)
							|| (p.getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PROTECTED_FIELD)
							|| (p.getImplementationType() == DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD)) {
						javaCode = appendSourceCode(
								p.getGetterSourceCode(),
								parsedClass,
								lastGeneratedClass,
								lastAcceptedClass,
								javaCode,
								PreferredLocation.END_OF_CLASS,
								entity.getProject());
						parsedClass = javaParser.parseClass(javaCode, entity.getDMModel());
						if (p.isSettable()) {
							javaCode = appendSourceCode(
									p.getSetterSourceCode(),
									parsedClass,
									lastGeneratedClass,
									lastAcceptedClass,
									javaCode,
									PreferredLocation.END_OF_CLASS,
									entity.getProject());
							parsedClass = javaParser.parseClass(javaCode, entity.getDMModel());
						}
					       if ((p.getCardinality() == DMCardinality.VECTOR) 
					    		   || (p.getCardinality() == DMCardinality.HASHTABLE)) {
								javaCode = appendSourceCode(
										p.getAdditionSourceCode(),
										parsedClass,
										lastGeneratedClass,
										lastAcceptedClass,
										javaCode,
										PreferredLocation.END_OF_CLASS,
										entity.getProject());
								parsedClass = javaParser.parseClass(javaCode, entity.getDMModel());
								javaCode = appendSourceCode(
										p.getRemovalSourceCode(),
										parsedClass,
										lastGeneratedClass,
										lastAcceptedClass,
										javaCode,
										PreferredLocation.END_OF_CLASS,
										entity.getProject());
								parsedClass = javaParser.parseClass(javaCode, entity.getDMModel());
					        }
					}
				}
			}

			for (DMMethod m : entity.getDeclaredMethods()) 
			{
				if (!m.getIsStaticallyDefinedInTemplate()) {
					if (logger.isLoggable(Level.FINE)) logger.fine("Merging method "+m.getSignature());
					javaCode = appendSourceCode(
							m.getSourceCode(),
							parsedClass,
							lastGeneratedClass,
							lastAcceptedClass,
							javaCode,
							PreferredLocation.END_OF_CLASS,
							entity.getProject());
					parsedClass = javaParser.parseClass(javaCode, entity.getDMModel());
				}
			}

		} catch (ParserNotInstalledException e) {
			logger.warning("ParserNotInstalledException. Escape java merge.");
			return returned;
		} finally {
			entity.getDMModel().getClassLibrary().clearLibrary();
		}
		
		if (logger.isLoggable(Level.FINER)) logger.finer("Returning after merge : \n"+javaCode);

		end = System.currentTimeMillis();
		if (logger.isLoggable(Level.INFO))
			logger.info("Appending Java code for "+entity.getName()+" took "+(end-start)+"ms");
		return javaCode;
	}

	private enum PreferredLocation
	{
		BEGINNING_OF_CLASS,
		END_OF_CLASS,
		AFTER_LAST_FIELD
	}
	
	private static String appendSourceCode (
			AbstractSourceCode sourceCode, 
			ParsedJavaClass parsedClass, 
			ParsedJavaClass lastGeneratedClass, 
			ParsedJavaClass lastAcceptedClass, 
			String initialCode,
			PreferredLocation preferredLocation,
			FlexoProject project) throws ParserNotInstalledException
	{
		//logger.info("Called appendSourceCode() for "+sourceCode.getCode());
		ParsedJavaElement foundExistingElement;
		try {
			foundExistingElement = elementInClass(sourceCode, parsedClass);
		} catch (JavaParseException e) {
			logger.warning("Parse error: "+sourceCode);
			return initialCode;
		}
		if (foundExistingElement != null) {
			if (logger.isLoggable(Level.FINE)) logger.fine("Java element was found. Replace it.");
			return appendSourceCodeByReplacingJavaElement(foundExistingElement, sourceCode, initialCode,project);
		}
		else {
			ParsedJavaElement lowerBound = findLowerBound(sourceCode, parsedClass, lastGeneratedClass, lastAcceptedClass);
			ParsedJavaElement upperBound = null;
			
			if (lowerBound == null) {
				 upperBound = findUpperBound(sourceCode, parsedClass, lastGeneratedClass, lastAcceptedClass);
			}
			
			if (lowerBound == null && upperBound == null && !parsedClass.getMembers().isEmpty()) {
				if (preferredLocation == PreferredLocation.END_OF_CLASS)
					upperBound = parsedClass.getMembers().lastElement();
				else if (preferredLocation == PreferredLocation.BEGINNING_OF_CLASS)
					lowerBound = parsedClass.getMembers().firstElement();
				else if (preferredLocation == PreferredLocation.AFTER_LAST_FIELD) {
					lowerBound = parsedClass.getMembers().firstElement();		
					Vector<? extends ParsedJavaElement> members = parsedClass.getMembers();
					int i=0;
					while (i<members.size()) {
						if (members.elementAt(i) instanceof ParsedJavaField) {
							upperBound = members.elementAt(i);
							lowerBound = null;
						}
						i++;
					}
				}
				else {
					logger.warning("Unexpected: "+preferredLocation);
				}
			}

			if (lowerBound != null) {
				StringBounds lower = boundsForJavaElementInClass(lowerBound, initialCode);
				return initialCode.substring(0,lower.beginIndex)
				+sourceCode.getCode()+StringUtils.LINE_SEPARATOR+StringUtils.LINE_SEPARATOR
				+initialCode.substring(lower.beginIndex);
			}

			if (upperBound != null) {
				StringBounds upper =  boundsForJavaElementInClass(upperBound, initialCode);
				return initialCode.substring(0,upper.endIndex+1)
				+StringUtils.LINE_SEPARATOR+StringUtils.LINE_SEPARATOR
				+sourceCode.getCode()+StringUtils.LINE_SEPARATOR+StringUtils.LINE_SEPARATOR
				+initialCode.substring(upper.endIndex+1);
			}

			//Upper bounds and lower bounds are null, there is no members in the class -> insert before last }
			int lastBrace = initialCode.lastIndexOf('}');
			
			if (lastBrace > -1)
				return initialCode.substring(0, lastBrace) + StringUtils.LINE_SEPARATOR + StringUtils.LINE_SEPARATOR + sourceCode.getCode() + StringUtils.LINE_SEPARATOR + StringUtils.LINE_SEPARATOR + initialCode.substring(lastBrace);
			
			
			logger.warning("I should never come here");
			return initialCode;
		}
	}

	private static String appendSourceCodeByReplacingJavaElement (
			ParsedJavaElement elementToReplace,
			AbstractSourceCode sourceCode, 
			String initialCode,
			FlexoProject project)
	{
		if (sourceCode.isEdited()) {
			StringBounds b = boundsForJavaElementInClass(elementToReplace, initialCode);
			
			/*logger.info("=====================\nJe m'apprete a faire un replace");
			logger.info("Je cherche a remplacer "+elementToReplace);
			logger.info("Que j'ai trouve dans "+b.beginIndex+","+b.endIndex);
			logger.info("Le code en question: ["+sourceCode.getCode()+"]");
			logger.info("Juste avant: ["+initialCode.substring(b.beginIndex-20,b.beginIndex)+"]");
			logger.info("Juste après: ["+initialCode.substring(b.endIndex+1,(b.endIndex+11<initialCode.length()?b.endIndex+11:initialCode.length()-1))+"]");
			
			logger.info("sourceCode.getCode().endsWith(LINE_SEPARATOR)="+sourceCode.getCode().endsWith(LINE_SEPARATOR));
			logger.info("initialCode.substring(b.endIndex+1).indexOf(LINE_SEPARATOR)="+initialCode.substring(b.endIndex+1).indexOf(LINE_SEPARATOR));
			*/
			
			if (sourceCode.getCode().endsWith(StringUtils.LINE_SEPARATOR) 
					&& initialCode.substring(b.endIndex+1).indexOf(StringUtils.LINE_SEPARATOR) == 0) {
				return initialCode.substring(0,b.beginIndex)+sourceCode.getCode()+initialCode.substring(b.endIndex+1+StringUtils.LINE_SEPARATOR.length());
			}
			else {
				return initialCode.substring(0,b.beginIndex)+sourceCode.getCode()+initialCode.substring(b.endIndex+1);
			}
		} else {
			StringBounds b = boundsForJavaElementInClass(elementToReplace, initialCode);
			try {
				ParsedJavadoc doc = javaParser.parseJavadocForMethod(initialCode.substring(b.beginIndex,b.endIndex+1), project.getDataModel());
				if (doc==null) {
					doc = javaParser.parseJavadocForMethod(sourceCode.getCode(),project.getDataModel());
					if (doc!=null)
					    return initialCode.substring(0,b.beginIndex)+doc.getStringRepresentation()+initialCode.substring(b.beginIndex);
				}
			} catch (JavaParseException e) {
				e.printStackTrace();
			}
			return initialCode;
		}
	}
	
	static class StringBounds
	{
		int beginIndex;
		int endIndex;
	}
	
	private static StringBounds boundsForJavaElementInClass (
			ParsedJavaElement element,
			String initialCode)
	{
		StringBounds returned = new StringBounds();
		String remainderCode = StringUtils.extractStringFromLine(initialCode, element.getLineNumber()-1);
		if (element.getModifiers()!=null && element.getModifiers().length>0) {
			int index = remainderCode.indexOf('\n');
			if (index>-1 && remainderCode.substring(0, index).indexOf(element.getModifiers()[0])>-1)
				remainderCode = remainderCode.substring(remainderCode.substring(0, index).indexOf(element.getModifiers()[0]));
		}
		returned.beginIndex = initialCode.indexOf(remainderCode);
		if (returned.beginIndex == -1) {
			logger.warning("Cannot find ["+remainderCode+"] in ["+initialCode+"]");
            int index = 0;
            index = initialCode.indexOf('\n',index);
            while (index>0) {
                if (initialCode.charAt(index-1)!='\r') {
                    if (index-50>0 && index+50<initialCode.length())
                        System.out.println(initialCode.substring(index - 50, index)+" here!!! "+initialCode.substring(index, index+50));
                    else
                        System.out.println("No \r at index "+index);
                }
                index = initialCode.indexOf('\n',index+1);
            }
		}
		if (element instanceof ParsedJavaField) {
			returned.endIndex = returned.beginIndex+StringUtils.indexOfEscapingJava(';',remainderCode);
		}
		else if (element instanceof ParsedJavaMethod) {
			returned.endIndex = returned.beginIndex+StringUtils.indexOfEscapingJava('}',remainderCode);
		}
		
		// Now, take eventual javadoc into account
		if (returned.beginIndex > -1 && element.getJavadoc() != null) {
			String codeBeforeDeclaration = initialCode.substring(0,returned.beginIndex);
			int javadocIndex = codeBeforeDeclaration.lastIndexOf("/**");
			if (javadocIndex > -1) {
				returned.beginIndex = returned.beginIndex-(codeBeforeDeclaration.length()-javadocIndex);
			}
		}
		
		if (logger.isLoggable(Level.FINER)) logger.finer("boundsForJavaElementInClass: "+element.getUniqueIdentifier()+" begin="+returned.beginIndex+" end="+returned.endIndex);
		if (returned.beginIndex>-1 && returned.beginIndex <returned.endIndex)
			if (logger.isLoggable(Level.FINER)) logger.finer("Result: ["+initialCode.substring(returned.beginIndex,returned.endIndex+1)+"]");
		return returned;
	}
	
	private static ParsedJavaElement findUpperBound (
			AbstractSourceCode sourceCode, 
			ParsedJavaClass parsedClass, 
			ParsedJavaClass lastGeneratedClass, 
			ParsedJavaClass lastAcceptedClass) throws ParserNotInstalledException 
	{		
		if (logger.isLoggable(Level.FINE)) logger.fine("Find upper bound");
		
		ParsedJavaElement returned = null;
		
		// First search if 'generatedAfter' matches something known in current generated class
		/*if (sourceCode.getGeneratedAfter() != null) {
			returned = elementInClass(sourceCode.getGeneratedAfter(), parsedClass);
		}*/
		
		// Now, we will look in LAST_ACCEPTED_VERSION
		if (returned == null && lastAcceptedClass != null) {
			ParsedJavaElement elementInLastAccepted;
			try {
				elementInLastAccepted = elementInClass(sourceCode, lastAcceptedClass);
			} catch (JavaParseException e1) {
				logger.warning("Should not happen");
				return null;
			}
			if (elementInLastAccepted != null) {
				if (logger.isLoggable(Level.FINER)) logger.finer("FOUND in LAST_ACCEPTED !!!");
				Stack<ParsedJavaElement> st = new Stack<ParsedJavaElement>();
				int i = 0;
				while (i<lastAcceptedClass.getMembers().size() && lastAcceptedClass.getMembers().elementAt(i) != elementInLastAccepted) {
					st.push(lastAcceptedClass.getMembers().elementAt(i));
					i++;
				}
				while (returned == null && !st.empty()) {
					ParsedJavaElement e = st.pop();
					returned = elementInClass(e.getUniqueIdentifier(), parsedClass);
				}
				if (returned != null) {
					if (logger.isLoggable(Level.FINER)) logger.finer("So choose "+returned.getUniqueIdentifier()+" as upper bound");
				}
			}
		}

		// Now, we will look in LAST_GENERATED_VERSION
		if (returned == null && lastGeneratedClass != null) {
			ParsedJavaElement elementInLastGenerated;
			try {
				elementInLastGenerated = elementInClass(sourceCode, lastGeneratedClass);
			} catch (JavaParseException e1) {
				logger.warning("Should not happen");
				return null;
			}
			if (elementInLastGenerated != null) {
				if (logger.isLoggable(Level.FINER)) logger.finer("FOUND in LAST_GENERATED !!!");
				Stack<ParsedJavaElement> st = new Stack<ParsedJavaElement>();
				int i = 0;
				while (i<lastGeneratedClass.getMembers().size() && lastGeneratedClass.getMembers().elementAt(i) != elementInLastGenerated) {
					st.push(lastGeneratedClass.getMembers().elementAt(i));
					i++;
				}
				while (returned == null && !st.empty()) {
					ParsedJavaElement e = st.pop();
					returned = elementInClass(e.getUniqueIdentifier(), parsedClass);
				}
				if (returned != null) {
					if (logger.isLoggable(Level.FINER)) logger.finer("So choose "+returned.getUniqueIdentifier()+" as upper bound");
				}
			}
		}

		return returned;
	}

	private static ParsedJavaElement findLowerBound (
			AbstractSourceCode sourceCode, 
			ParsedJavaClass parsedClass, 
			ParsedJavaClass lastGeneratedClass, 
			ParsedJavaClass lastAcceptedClass) throws ParserNotInstalledException 
	{
		if (logger.isLoggable(Level.FINE)) logger.fine("Find lower bound");
		
		ParsedJavaElement returned = null;
		
		// First search if 'generatedBefore' matches something known in current generated class
		/*if (sourceCode.getGeneratedBefore() != null) {
			returned = elementInClass(sourceCode.getGeneratedBefore(), parsedClass);
		}*/
		
		// Now, we will look in LAST_ACCEPTED_VERSION
		if (returned == null && lastAcceptedClass != null) {
			ParsedJavaElement elementInLastAccepted;
			try {
				elementInLastAccepted = elementInClass(sourceCode, lastAcceptedClass);
			} catch (JavaParseException e1) {
				logger.warning("Should not happen");
				return null;
			}
			if (elementInLastAccepted != null) {
				if (logger.isLoggable(Level.FINER)) logger.finer("FOUND in LAST_ACCEPTED !!!");
				int i=lastAcceptedClass.getMembers().indexOf(elementInLastAccepted);
				while (i<lastAcceptedClass.getMembers().size() && returned == null) {
					ParsedJavaElement e = lastAcceptedClass.getMembers().elementAt(i);
					returned = elementInClass(e.getUniqueIdentifier(), parsedClass);
					i++;
				}
				if (returned != null) {
					if (logger.isLoggable(Level.FINER)) logger.finer("So choose "+returned.getUniqueIdentifier()+" as lower bound");
				}
			}
		}

		// Now, we will look in LAST_GENERATED_VERSION
		if (returned == null && lastGeneratedClass != null) {
			ParsedJavaElement elementInLastGenerated;
			try {
				elementInLastGenerated = elementInClass(sourceCode, lastGeneratedClass);
			} catch (JavaParseException e1) {
				logger.warning("Should not happen");
				return null;
			}
			if (elementInLastGenerated != null) {
				if (logger.isLoggable(Level.FINER)) logger.finer("FOUND in LAST_GENERATED !!!");
				int i=lastGeneratedClass.getMembers().indexOf(elementInLastGenerated);
				while (i<lastGeneratedClass.getMembers().size() && returned == null) {
					ParsedJavaElement e = lastGeneratedClass.getMembers().elementAt(i);
					returned = elementInClass(e.getUniqueIdentifier(), parsedClass);
					i++;
				}
				if (returned != null) {
					if (logger.isLoggable(Level.FINER)) logger.finer("So choose "+returned.getUniqueIdentifier()+" as lower bound");
				}
			}
		}

		return returned;
	}

	private static ParsedJavaElement elementInClass(AbstractSourceCode sourceCode,ParsedJavaClass aClass) throws ParserNotInstalledException, JavaParseException
	{
			if (sourceCode instanceof FieldSourceCode) {
				ParsedJavaField fieldToAppend = ((FieldSourceCode)sourceCode).getParsedField();
				if (fieldToAppend == null) {
					logger.warning("Could not parse supplied field to append to source code: "+sourceCode.getCode());
					throw new JavaParseException();
				}
				return fieldInClass(fieldToAppend.getName(), aClass);
			}
			else if (sourceCode instanceof MethodSourceCode) {
				ParsedJavaMethod methodToAppend = ((MethodSourceCode)sourceCode).getParsedMethod();
				if (methodToAppend == null) {
					logger.warning("Could not parse supplied method to append to source code: "+sourceCode.getCode());
					throw new JavaParseException();
				}
				return methodInClass(methodToAppend.getCallSignature(),aClass);
			}
		return null;
	}
	
	private static ParsedJavaElement elementInClass(String elementIdentifier,ParsedJavaClass aClass)
	{
		if (elementIdentifier.contains("(")) return methodInClass(elementIdentifier, aClass);
		else return fieldInClass(elementIdentifier, aClass);
	}
	
	private static ParsedJavaMethod methodInClass(String signature,ParsedJavaClass aClass)
	{
		return aClass.getMethodBySignature(signature);
	}
	
	private static ParsedJavaField fieldInClass(String fieldName,ParsedJavaClass aClass)
	{
		return aClass.getFieldByName(fieldName);
	}
	
}
