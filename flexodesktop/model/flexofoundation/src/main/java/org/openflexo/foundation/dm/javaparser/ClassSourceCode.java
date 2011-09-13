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
package org.openflexo.foundation.dm.javaparser;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.localization.FlexoLocalization;

public abstract class ClassSourceCode extends AbstractSourceCode 
{
	   private static final Logger logger = Logger.getLogger(ClassSourceCode.class.getPackage().getName());

	   private ParsedJavaClass _parsedClass;
	   
	protected ClassSourceCode(
			SourceCodeOwner owner, 
			String propertyName, 
			String hasParseErrorPropertyName, 
			String parseErrorWarningPropertyName)
	{
		super(owner, propertyName, hasParseErrorPropertyName, parseErrorWarningPropertyName);
	}
	
	protected ClassSourceCode(
			SourceCodeOwner owner)
	{
		super(owner);
	}
	
	@Override
	public abstract String makeComputedCode();

	public abstract void interpretEditedJavaClass(ParsedJavaClass javaClass);

	@Override
	public void interpretEditedCode(ParsedJavaElement javaElement)
	{
		interpretEditedJavaClass((ParsedJavaClass)javaElement);
	}

	public ParsedJavaClass getParsedClass() throws ParserNotInstalledException
	{
		if (_parsedClass==null) {
			return parseCode(getCode());
		}
		return _parsedClass;
	}
	
	@Override
	protected ParsedJavaClass parseCode (final String qualifiedCode) throws ParserNotInstalledException 
	{
    	if (_javaClassParser == null) {
			throw new ParserNotInstalledException();
		}
    	
    	try {
    		// Try to parse
    		if (qualifiedCode == null) {
				throw new JavaParseException();
			}
    		_parsedClass = _javaClassParser.parseClass(qualifiedCode, getOwner().getDMModel());
       		setHasParseErrors(false);
       		return _parsedClass;
    	}
    	catch (JavaParseException e) {
    		setHasParseErrors(true);
    		setParseErrorWarning("<html><font color=\"red\">"
        			+FlexoLocalization.localizedForKey("parse_error_warning")
         			+"</font></html>");
       		if (logger.isLoggable(Level.FINE)) {
				logger.fine("Parse error while parsing class: "+qualifiedCode);
			}
       		return null;
    	}    	
	}

	@Override
	public ParsedJavadoc parseJavadoc (final String qualifiedCode) throws ParserNotInstalledException
	{
		if (_javaClassParser == null) {
			throw new ParserNotInstalledException();
		}
		try {
			return _javaClassParser.parseJavadocForClass(qualifiedCode, getOwner().getDMModel());
		} catch (JavaParseException e) {
    		setHasParseErrors(true);
    		setParseErrorWarning("<html><font color=\"red\">"
        			+FlexoLocalization.localizedForKey("parse_error_warning")
        			+"</font></html>");
    		return null;
		}
	}

    private static JavaClassParser _javaClassParser;

    public static void setJavaClassParser(JavaClassParser javaClassParser)
    {
    	_javaClassParser = javaClassParser;
    }
    
    public static JavaClassParser getJavaClassParser()
    {
    	return _javaClassParser;
    }

    /**
     * Overrides isJavaParserInstalled
     * @see org.openflexo.foundation.dm.javaparser.AbstractSourceCode#isJavaParserInstalled()
     */
    @Override
    protected boolean isJavaParserInstalled()
    {
        return _javaClassParser!=null;
    }

	public void setParsedClass(ParsedJavaClass parsedClass) {
		_parsedClass = parsedClass;
	}

}
