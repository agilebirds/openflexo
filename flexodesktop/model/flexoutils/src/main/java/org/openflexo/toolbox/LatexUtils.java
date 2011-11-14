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
package org.openflexo.toolbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author gpolet
 * 
 */
public class LatexUtils {
	public static final String TEXIFY = "texify";

	public static final String PDFLATEX = "pdflatex";

	public static final Vector<String> stringsToIgnore = new Vector<String>();

	protected static final String LATEX_BACKSLASH = HTMLUtils.LATEX_BACKSLASH;

	protected static final String JAVA_BACKSLASH = "\\";

	protected static final String LATEX_TAG_REGEXP = "^\\\\[^ {]+(\\{[^}]*\\}\\s*)*";

	protected static final Pattern LATEX_TAG_PATTERN = Pattern.compile(LATEX_TAG_REGEXP);

	protected static final String CHARS_TO_ESCAPE_REGEXP = "[\\\\{}_&%#~]";

	protected static final Pattern CHARS_TO_ESCAPE_PATTERN = Pattern.compile(CHARS_TO_ESCAPE_REGEXP);

	static {
		stringsToIgnore.add("<BR>");
		stringsToIgnore.add("<LI>");
		stringsToIgnore.add("<UL>");
		stringsToIgnore.add("</UL>");
	}

	public static String getDefaultLatex2PDFCommand() {
		return getDefaultLatex2PDFCommand(true);
	}

	public static String getDefaultLatex2PDFCommand(boolean testDefaultCommands) {
		// First we test the default commands (if they are defined in the path, it should work)
		if (testDefaultCommands) {
			if (testLatexCommand(TEXIFY)) {
				return TEXIFY;
			} else if (testLatexCommand(PDFLATEX)) {
				return PDFLATEX;
			}
		}

		// Then we scan the paths stored in the PATH variable
		String string = System.getenv("PATH");
		if (string != null) {
			String[] s = string.split(System.getProperty("path.separator"));
			for (int i = 0; i < s.length; i++) {
				String string2 = s[i];
				if (string2.toLowerCase().indexOf("tex") > 0) {
					File file = new File(string2, "texify");
					if (file.exists()) {
						return file.getAbsolutePath();
					} else {
						file = new File(string2, "pdflatex");
						if (file.exists()) {
							return file.getAbsolutePath();
						}
					}
				}
			}
		}

		// Finally we perform a scan in the typical installation directory
		if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
			File pgFiles = new File(System.getenv("ProgramFiles"));
			File[] f = pgFiles.listFiles();
			for (int i = 0; i < f.length; i++) {
				File file = f[i];
				if (file.getName().toLowerCase().startsWith("miktex")) {
					File cmd = new File(file, "miktex/bin/textify");
					if (cmd.exists()) {
						return cmd.getAbsolutePath();
					}
					cmd = new File(file, "miktex/bin/pdflatex");
					if (cmd.exists()) {
						return cmd.getAbsolutePath();
					}
				}
			}
		} else if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			StringBuilder sb = new StringBuilder("/usr/local/teTeX/bin/");
			sb.append(System.getProperty("os.arch").equals("ppc") ? "powerpc" : "i386");
			sb.append("-apple-darwin-current");
			String path = testCommandsWithPath(sb.toString());
			if (path != null) {
				return path;
			}
		} else if (ToolBox.getPLATFORM() == ToolBox.LINUX) {
			String path = testCommandsWithPath("/usr/bin");
			if (path != null) {
				return path;
			}
			path = testCommandsWithPath("/bin");
			if (path != null) {
				return path;
			}
			path = testCommandsWithPath("/usr/local/bin");
			if (path != null) {
				return path;
			}
		}
		return null;
	}

	/**
	 * @param path
	 */
	private static String testCommandsWithPath(String path) {
		File file = new File(path.toString());
		if (file.exists() && file.isDirectory()) {
			File cmd = new File(file, "texify");
			if (cmd.exists()) {
				return cmd.getAbsolutePath();
			}
			cmd = new File(file, "pdflatex");
			if (cmd.exists()) {
				return cmd.getAbsolutePath();
			}
		}
		return null;
	}

	public static boolean testLatexCommand(String command) {
		final Process p;
		try {
			p = Runtime.getRuntime().exec(command + " --help");
			Thread readThread = new Thread(new Runnable() {
				/**
				 * Overrides run
				 * 
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run() {
					InputStreamReader is = new InputStreamReader(p.getInputStream());
					BufferedReader reader = new BufferedReader(is);
					String line;
					try {
						while ((line = reader.readLine()) != null) {

						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			});
			readThread.start();
			p.waitFor();
			return p.exitValue() == 0;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param javaString
	 * @return
	 */
	private static String removeStringsToIgnore(String javaString) {
		StringBuffer sb = new StringBuffer();
		Iterator i = stringsToIgnore.iterator();
		while (i.hasNext()) {
			String s = (String) i.next();
			sb.append(s);
			if (i.hasNext()) {
				sb.append('|');
			}

		}
		Pattern p = Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(javaString);
		sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, " ");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * This method escapes the characters that LaTeX requires to escape. Such chars are:<br>
	 * {, }, $, \, &, %, # and ~
	 * 
	 * @param javaString
	 * @return
	 */
	public static String prepareJavaStringForLatex(String javaString) {
		if (javaString == null) {
			return ""; // This will avoid any NPE
		}

		javaString = removeStringsToIgnore(javaString);
		try {
			StringBuffer sb = new StringBuffer();
			Matcher m = CHARS_TO_ESCAPE_PATTERN.matcher(javaString);
			while (m.find()) {
				String tmp = javaString.substring(m.start());
				Matcher latex = LATEX_TAG_PATTERN.matcher(tmp);
				if (latex.find()) {// This means it has found a Latex_tag
					// pattern
					if (latex.group(1) == null) {// just a tag \blabla
						m.appendReplacement(sb, "$0");
					} else {// a tag \blabla{bloblo} {}
						String str = latex.group();
						int n = -1;
						Matcher tmpMatcher = CHARS_TO_ESCAPE_PATTERN.matcher(str);
						while (tmpMatcher.find()) {
							n++;
						}
						while (n > 0 && m.find()) {
							n--;
							m.appendReplacement(sb, "$0");
						}
					}
				} else {
					if (m.group().equals(JAVA_BACKSLASH)) {
						m.appendReplacement(sb, "\\" + LATEX_BACKSLASH);
						// The double \ added in front of the replacement are
						// there to escape the begining of a Latex backslash
						// which starts with a backslash too and are interpreted
						// in the replacement method.
					} else {
						m.appendReplacement(sb, "\\\\" + m.group());
					}
				}
			}
			m.appendTail(sb);
			return fixQuotesAndLineReturns(sb.toString().replaceAll("\\$", "\\\\$0"));

		} catch (PatternSyntaxException e) {
			e.printStackTrace();
			return javaString;
		}
	}

	public static String fixQuotesAndLineReturns(String string) {
		String s = string.replaceAll("\"(.*?)\"", "``$1''");// This
		// does
		// something:
		// the
		// first expression is a
		// regular expression in
		// which you have to escape
		// the dollar sign. Since
		// the escape character has
		// to be escaped in Java
		// too, it is escaped twice.
		// In the second expression,
		// it is a litteral string
		// where the escape
		// character is escaped with
		// himself (see Javadoc)
		// s = s.replaceAll("[\n\r]{2,}", "\n"); // aja remove to test paragraph in wysiwyg
		return s;
	}

}
