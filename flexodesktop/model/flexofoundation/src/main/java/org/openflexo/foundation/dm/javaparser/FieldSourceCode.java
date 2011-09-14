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

import org.openflexo.foundation.dm.DuplicateMethodSignatureException;
import org.openflexo.localization.FlexoLocalization;

public abstract class FieldSourceCode extends AbstractSourceCode 
{
	   private static final Logger logger = Logger.getLogger(FieldSourceCode.class.getPackage().getName());

	protected FieldSourceCode(
			SourceCodeOwner owner, 
			String propertyName, 
			String hasParseErrorPropertyName, 
			String parseErrorWarningPropertyName)
	{
		super(owner, propertyName, hasParseErrorPropertyName, parseErrorWarningPropertyName);
	}
	
	protected FieldSourceCode(
			SourceCodeOwner owner)
	{
		super(owner);
	}
	
	@Override
	public abstract String makeComputedCode();

	public abstract void interpretEditedJavaField(ParsedJavaField javaField) throws DuplicateMethodSignatureException;

	@Override
	public void interpretEditedCode(ParsedJavaElement javaElement) throws DuplicateMethodSignatureException
	{
		interpretEditedJavaField((ParsedJavaField)javaElement);
	}

	public ParsedJavaField getParsedField() throws ParserNotInstalledException
	{
		return parseCode(getCode());
	}
	
	@Override
	protected ParsedJavaField parseCode (final String qualifiedCode) throws ParserNotInstalledException 
	{
    	if (_javaFieldParser == null) throw new ParserNotInstalledException();
    	
    	try {
    		// Try to parse
    		ParsedJavaField parsedJavaField = _javaFieldParser.parseField(qualifiedCode, getOwner().getDMModel());
       		setHasParseErrors(false);
       		return parsedJavaField;
    	}
    	catch (JavaParseException e) {
    		setHasParseErrors(true);
    		setParseErrorWarning("<html><font color=\"red\">"
        			+FlexoLocalization.localizedForKey("parse_error_warning")
        			//+" field: "+qualifiedCode
        			+"</font></html>");
    		if (logger.isLoggable(Level.FINE)) logger.fine("Parse error while parsing field: "+qualifiedCode);
    		return null;
    	}    	
	}

	@Override
	public ParsedJavadoc parseJavadoc (final String qualifiedCode) throws ParserNotInstalledException
	{
		if (_javaFieldParser == null) throw new ParserNotInstalledException();
		try {
			return _javaFieldParser.parseJavadocForField(qualifiedCode, getOwner().getDMModel());
		} catch (JavaParseException e) {
    		setHasParseErrors(true);
    		setParseErrorWarning("<html><font color=\"red\">"
        			+FlexoLocalization.localizedForKey("parse_error_warning")
        			+"</font></html>");
    		return null;
		}
	}

	public void replaceFieldDeclarationInEditedCode (String newFieldDeclaration)
	{
		
		int beginIndex;
		int endIndex;
		
		//logger.info("Called replaceFieldDeclarationInEditedCode() with "+newFieldDeclaration);
		
		// First look javadoc
		int javadocBeginIndex = _editedCode.indexOf("/**");
		if (javadocBeginIndex > -1) {
			beginIndex = _editedCode.indexOf("*/")+2;
		}
		else {
			beginIndex = 0;
		}
		
		if (_editedCode.indexOf("=") > 0) {
			endIndex = _editedCode.indexOf("=");
		}
		else if (_editedCode.indexOf(";") > 0) {
			endIndex = _editedCode.indexOf(";");
		}
		else endIndex = _editedCode.length();

		if (endIndex > beginIndex) {
			_editedCode 
			= _editedCode.substring(0,beginIndex)
			+ newFieldDeclaration
			+ _editedCode.substring(endIndex);
		}

	}

    private static JavaFieldParser _javaFieldParser;

    public static void setJavaFieldParser(JavaFieldParser javaFieldParser)
    {
    	_javaFieldParser = javaFieldParser;
    }
    
    public static JavaFieldParser getJavaFieldParser()
    {
    	return _javaFieldParser;
    }

    /**
     * Overrides isJavaParserInstalled
     * @see org.openflexo.foundation.dm.javaparser.AbstractSourceCode#isJavaParserInstalled()
     */
    @Override
    protected boolean isJavaParserInstalled()
    {
        return _javaFieldParser!=null;
    }

}
