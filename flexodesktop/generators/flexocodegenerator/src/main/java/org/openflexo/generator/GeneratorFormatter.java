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

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.generator.exception.JavaFormattingException;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.StringUtils;

import de.hunsicker.jalopy.Jalopy;
import de.hunsicker.jalopy.storage.Convention;
import de.hunsicker.jalopy.storage.ConventionKeys;

/**
 * The class GeneratorFormatter was first created for the FlexoCodeGenerator
 * project.
 * 
 * @author gpolet
 * 
 */
public class GeneratorFormatter
{

    private static final Logger logger = FlexoLogger.getLogger(GeneratorFormatter.class.getPackage().getName());

    private static final String MULTIPLE_NEW_LINE_ALONE_REG_EXP = "([\\s&&[^\n\r]]*[\n\r]{2,})";

    private static final String HTML_OPEN = "<[^/!][^<>]*>";

    private static final String HTML_OPEN_CLOSE = "<[^/!][^<>/]*/\\s*>";

    private static final Pattern HTML_OPEN_PATTERN = Pattern.compile(HTML_OPEN, Pattern.DOTALL);

    private static final String HTML_CLOSE = "</[^>]*>";

    private static final Pattern HTML_CLOSE_PATTERN = Pattern.compile(HTML_CLOSE, Pattern.DOTALL);

    private static final String WOD_HEADER = "\\s*[A-Za-z_0-9]+\\s*:\\s*[A-Za-z_0-9]+\\s*\\{\\s*";

    private static final String WOD_FOOTER = "\\s*\\}\\s*";

    private static long lastRead = -1;
    
    private static class JalopyFactory{
    	private static Jalopy jalopy;
    	public static Jalopy getJalopy(){
    		if(jalopy==null)
    			jalopy = new Jalopy();
    		jalopy.reset();
    		return jalopy;
    	}
    }
    
    public synchronized static String formatJavaCode(String javaCode, String packageName, String className, CGGenerator generator, FlexoProject project)
            throws JavaFormattingException
    {
        if (javaCode == null)
            return null;
        if (javaCode.trim().equals(""))
            return "";
        if (lastRead == -1 || lastRead != project.getJavaFormatterSettings().getFile().lastModified()) {
            if (lastRead != -1)
                if (logger.isLoggable(Level.INFO))
                    logger.info("Update detected on java formatter file");
            try {
                Convention.importSettings(project.getJavaFormatterSettings().getFile());
                lastRead = project.getJavaFormatterSettings().getFile().lastModified();
                String sortOrder = Convention.getInstance().get(ConventionKeys.SORT_ORDER, null);
                if (sortOrder!=null) {
                	StringBuilder sb = new StringBuilder();
                	StringTokenizer st =new StringTokenizer(sortOrder,"|", false);
                	while (st.hasMoreElements()) {
						String s = (String) st.nextElement();
						if (s.equals("enum")){
							if (sb.length()==0 || sb.toString().equals("static")) {
								if (sb.length()!=0)
									sb.append("|");
								sb.append(s);
							} else
								sb.insert(0, "enum|");
						} else {
							if (sb.length()!=0)
								sb.append("|");
							sb.append(s);
						}
					}
                	if (!sb.toString().equals(sortOrder))
                		Convention.getInstance().put(ConventionKeys.SORT_ORDER,sb.toString());
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        javaCode = javaCode.replace("\r", "");
        //javaCode = javaCode.replaceAll(MULTIPLE_NEW_LINE_ALONE_REG_EXP, LINE_SEPARATOR+LINE_SEPARATOR);
        Jalopy jalopy = JalopyFactory.getJalopy();
        try {
            StringBuffer sb = new StringBuffer();
            jalopy.setInspect(false);
            jalopy.setForce(true);
            jalopy.setInput(javaCode, packageName.replace('.', '/') + className + ".java");
            jalopy.setOutput(sb);
            if (jalopy.format()) {//This calls makes the format, but if the parse fails, then the stacktrace of it will be printed.
                return sb.toString();
            }
        } catch (Exception e) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Exception occured while parsing the java code");
        }
        if (jalopy.getState() == Jalopy.State.ERROR) {
            logger.warning("Java code could not be formatted");
            throw new JavaFormattingException(generator,packageName+"."+className);
        }
        if (logger.isLoggable(Level.WARNING))
            logger.warning("Java code could not be formatted: " + jalopy.getState());
        return javaCode;
    }

    public static String formatHTMLCode(String htmlCode)
    {

        if (htmlCode == null || htmlCode.trim().length() == 0)
            return "";
        int indent = 0;
        int indexOpening = 0;
        int indexClosing = 0;
        int index = 0;
        int min = 0;
        Matcher open = HTML_OPEN_PATTERN.matcher(htmlCode);
        Matcher close = HTML_CLOSE_PATTERN.matcher(htmlCode);
        if (open.find())
            indexOpening = open.end();
        else
            indexOpening = -1;
        if (close.find())
            indexClosing = close.end();
        else
            indexClosing = -1;
        StringBuilder sb = new StringBuilder();
        try {
            while ((indexOpening > -1 || indexClosing > -1) && index < htmlCode.length()) {
                if (indexOpening < 0 && indexClosing > 0) {
                    if (htmlCode.substring(index, close.start()).trim().length() > 0) {
                        sb.append(indentation(indent));
                        sb.append(htmlCode.substring(index, close.start()).trim());
                        sb.append(StringUtils.LINE_SEPARATOR);
                    }
                    indent--;
                    sb.append(indentation(indent));
                    sb.append(htmlCode.substring(close.start(), indexClosing).trim());
                    sb.append(StringUtils.LINE_SEPARATOR);
                    index = indexClosing;
                } else if (indexOpening > 0 && indexClosing < 0) {
                    if (htmlCode.substring(index, open.start()).trim().length() > 0) {
                        sb.append(indentation(indent));
                        sb.append(htmlCode.substring(index, open.start()).trim());
                        sb.append(StringUtils.LINE_SEPARATOR);
                    }
                    sb.append(indentation(indent));
                    if (tagRequiresIndentation(open.group()))
                        indent++;
                    sb.append(htmlCode.substring(open.start(), indexOpening).trim());
                    sb.append(StringUtils.LINE_SEPARATOR);
                    index = indexOpening;
                } else if (indexOpening > 0 && indexClosing > 0) {
                    min = Math.min(indexOpening, indexClosing);
                    if (indexOpening == min) {
                        if (htmlCode.substring(index, open.start()).trim().length() > 0) {
                            sb.append(indentation(indent));
                            sb.append(htmlCode.substring(index, open.start()).trim());
                            sb.append(StringUtils.LINE_SEPARATOR);
                        }
                        sb.append(indentation(indent));
                        if (tagRequiresIndentation(open.group()))
                            indent++;
                        else
                            sb.deleteCharAt(sb.length() - 1);
                        sb.append(htmlCode.substring(open.start(), indexOpening).trim());
                        sb.append(StringUtils.LINE_SEPARATOR);
                        index = indexOpening;
                    } else {
                        if (htmlCode.substring(index, close.start()).trim().length() > 0) {
                            sb.append(indentation(indent));
                            sb.append(htmlCode.substring(index, close.start()).trim());
                            sb.append(StringUtils.LINE_SEPARATOR);
                        }
                        indent--;
                        sb.append(indentation(indent));
                        sb.append(htmlCode.substring(close.start(), indexClosing).trim());
                        sb.append(StringUtils.LINE_SEPARATOR);
                        index = indexClosing;
                    }
                } else {
                    sb.append(htmlCode.substring(index).trim());
                    index = htmlCode.length();
                }
                if (index == indexOpening) {
                    if (open.find())
                        indexOpening = open.end();
                    else
                        indexOpening = -1;
                } else if (index == indexClosing) {
                    if (close.find()) {
                        indexClosing = close.end();
                    }
                    else
                        indexClosing = -1;
                }
            }
        } catch (RuntimeException re) {
            if (index > 0 && index < htmlCode.length())
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Failed to format at index "+index+" text: "+htmlCode.substring(index));
                throw re;
        }
        return sb.toString();
    }

    private static boolean tagRequiresIndentation(String tag)
    {
        return !tag.toLowerCase().equals("<br>") && !tag.toLowerCase().startsWith("<img") && !tag.matches(HTML_OPEN_CLOSE);
    }

    /**
     * @param indent
     * @return
     */
    private static String indentation(int indent)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

    public static String formatWodCode(String wodCode)
    {
        if (wodCode == null)
            return null;
        if (wodCode.trim().equals(""))
            return "";
        wodCode = wodCode.replace("\r", "");
        wodCode = wodCode.replaceAll(MULTIPLE_NEW_LINE_ALONE_REG_EXP, StringUtils.LINE_SEPARATOR+StringUtils.LINE_SEPARATOR);
        boolean insideHeaderFooter = false;
        if (wodCode.indexOf('\n') > -1) {
            StringTokenizer st = new StringTokenizer(wodCode.trim(), StringUtils.LINE_SEPARATOR);
            StringBuilder sb = new StringBuilder();
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                token = token.trim();
                if (token.length() == 0)
                    continue;
                if (token.matches(WOD_HEADER)) {
                    sb.append(token.substring(0, token.indexOf(':')).trim());
                    sb.append(": ");
                    sb.append(token.substring(token.indexOf(':') + 1, token.indexOf('{')).trim());
                    sb.append(" {"+StringUtils.LINE_SEPARATOR);
                    insideHeaderFooter = true;
                } else if (token.matches(WOD_FOOTER)) {
                    sb.append(token);
                    sb.append(StringUtils.LINE_SEPARATOR+StringUtils.LINE_SEPARATOR);
                    insideHeaderFooter = false;
                } else {
                    if (insideHeaderFooter) {
                        if (token.indexOf('=') > -1) {
                            sb.append("\t").append(token.substring(0, token.indexOf('=')).trim());
                            sb.append(" = ");
                            sb.append(token.substring(token.indexOf('=') + 1).trim());
                        } else {
                            sb.append("\t").append(token);
                            if (logger.isLoggable(Level.WARNING))
                                logger.warning("Inside WOD Header footer but not equal (=) sign could be found: " + token);
                        }
                    } else {
                        if (logger.isLoggable(Level.WARNING))
                            logger.warning("Not inside WOD Header footer: " + token);
                        sb.append(token);
                    }
                    sb.append(StringUtils.LINE_SEPARATOR);
                }
            }
            return sb.toString();
        } else
            return wodCode.trim() + StringUtils.LINE_SEPARATOR+StringUtils.LINE_SEPARATOR;
    }

    public static void main(String[] args)
    {
        String s = "<HTML><BODY><BR><A HREF /><IMG><A ANCHOR=\"blabla\"/><A HREF /   ></BODY></HTML>";
        System.out.println(formatHTMLCode(s));
    }
}
