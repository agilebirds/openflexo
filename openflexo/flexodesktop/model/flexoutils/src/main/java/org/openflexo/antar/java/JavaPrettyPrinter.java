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
package org.openflexo.antar.java;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.Assignment;
import org.openflexo.antar.Class;
import org.openflexo.antar.Conditional;
import org.openflexo.antar.ControlGraph;
import org.openflexo.antar.Declaration;
import org.openflexo.antar.Flow;
import org.openflexo.antar.Instruction;
import org.openflexo.antar.Loop;
import org.openflexo.antar.Nop;
import org.openflexo.antar.Procedure;
import org.openflexo.antar.Procedure.ProcedureParameter;
import org.openflexo.antar.ProcedureCall;
import org.openflexo.antar.Sequence;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.pp.PrettyPrinter;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

import de.hunsicker.jalopy.Jalopy;

public class JavaPrettyPrinter extends PrettyPrinter {

	private static final Logger logger = FlexoLogger.getLogger(JavaPrettyPrinter.class.getPackage().getName());

	private static final String MULTIPLE_NEW_LINE_ALONE_REG_EXP = "([\\s&&[^\n\r]]*[\n\r]{2,})";

	public JavaPrettyPrinter() {
		super(new JavaExpressionPrettyPrinter());
	}

	protected String makeJavadocComment(String javadocComment) {
		return "/** " + StringUtils.LINE_SEPARATOR + "  * " + ToolBox.getJavaDocString(javadocComment, "  ") + "  */" + StringUtils.LINE_SEPARATOR;
	}

	protected String makeInlineComment(String inlineComment) {
		return "/* " + inlineComment + " */";
	}

	protected String makeHeaderComment(String headerComment) {
		StringBuffer returned = new StringBuffer();
		StringTokenizer st = new StringTokenizer(headerComment, StringUtils.LINE_SEPARATOR);
		while (st.hasMoreTokens()) {
			String next = st.nextToken();
			if (!next.trim().equals("")) {
				returned.append("// " + next + StringUtils.LINE_SEPARATOR);
			}
		}
		return returned.toString();
	}

	@Override
	public String makeStringRepresentation(Conditional conditional) {
		String headerComment = conditional.getHeaderComment() != null ? makeHeaderComment(conditional.getHeaderComment()) : "";
		String inlineComment = conditional.getInlineComment() != null ? makeInlineComment(conditional.getInlineComment()) : "";

		StringBuffer sb = new StringBuffer();

		sb.append(headerComment);
		sb.append("if ");
		sb.append(getStringRepresentation(conditional.getCondition()));
		sb.append(inlineComment);
		sb.append(" {" + StringUtils.LINE_SEPARATOR);
		sb.append(getStringRepresentation(conditional.getThenStatement()));
		sb.append(StringUtils.LINE_SEPARATOR + "}");

		if (conditional.getElseStatement() != null) {
			if (conditional.getElseStatement() instanceof Conditional) {
				// This special case handles "else if" patterns
				ControlGraph elseStatement = conditional.getElseStatement();
				while (elseStatement instanceof Conditional) {
					Conditional currentConditional = (Conditional) elseStatement;
					String headerComment2 = currentConditional.getHeaderComment() != null ? StringUtils.LINE_SEPARATOR + makeHeaderComment(currentConditional
							.getHeaderComment()) : "";
					String inlineComment2 = currentConditional.getInlineComment() != null ? makeInlineComment(currentConditional
							.getInlineComment()) : "";
					sb.append(headerComment2);
					sb.append("else if ");
					sb.append(getStringRepresentation(currentConditional.getCondition()));
					sb.append(inlineComment2);
					sb.append(" {" + StringUtils.LINE_SEPARATOR);
					sb.append(getStringRepresentation(currentConditional.getThenStatement()));
					sb.append(StringUtils.LINE_SEPARATOR + "}");
					elseStatement = currentConditional.getElseStatement();
				}
				if (elseStatement != null) {
					sb.append(" else {" + StringUtils.LINE_SEPARATOR);
					sb.append(getStringRepresentation(elseStatement));
					sb.append(StringUtils.LINE_SEPARATOR + "}");
				}
			} else {
				sb.append(" else {" + StringUtils.LINE_SEPARATOR);
				sb.append(getStringRepresentation(conditional.getElseStatement()));
				sb.append(StringUtils.LINE_SEPARATOR + "}");
			}
		}

		return sb.toString();
	}

	@Override
	public String makeStringRepresentation(Instruction instruction) {
		String headerComment = instruction.getHeaderComment() != null ? makeHeaderComment(instruction.getHeaderComment()) : "";
		String inlineComment = instruction.getInlineComment() != null ? makeInlineComment(instruction.getInlineComment()) : "";

		if (instruction instanceof Nop) {
			return headerComment + (instruction.getInlineComment() == null ? "/* Nothing to do */" : inlineComment) + ";";
		}

		if (instruction instanceof Assignment) {
			return headerComment + makeStringRepresentation((Assignment) instruction) + inlineComment;
		}

		if (instruction instanceof ProcedureCall) {
			return headerComment + makeStringRepresentation((ProcedureCall) instruction) + inlineComment;
		}

		if (instruction instanceof Declaration) {
			return headerComment + makeStringRepresentation((Declaration) instruction) + inlineComment;
		}

		if (instruction instanceof JavaPrettyPrintable) {
			return headerComment + ((JavaPrettyPrintable) instruction).getJavaStringRepresentation() + inlineComment;
		}

		return "???";
	}

	public String makeStringRepresentation(Assignment assignment) {
		return assignment.getReceiver().getName() + " = " + getStringRepresentation(assignment.getAssignmentValue()) + ";";
	}

	public String makeStringRepresentation(ProcedureCall procedureCall) {
		StringBuffer sb = new StringBuffer();
		StringBuffer args = new StringBuffer();
		if (procedureCall.getArguments().size() > 0) {
			boolean isFirst = true;
			for (Expression e : procedureCall.getArguments()) {
				args.append((isFirst ? "" : ",") + getStringRepresentation(e));
				isFirst = false;
			}
		}
		sb.append(procedureCall.getProcedure().getProcedureName());
		sb.append("(" + args.toString() + ");");
		return sb.toString();
	}

	public String makeStringRepresentation(Declaration declaration) {
		StringBuffer sb = new StringBuffer();
		sb.append(declaration.getType().getTypeName() + " ");
		sb.append(declaration.getVariable().getName());
		if (declaration.getInitializationValue() != null) {
			sb.append(" = " + getStringRepresentation(declaration.getInitializationValue()));
		}
		sb.append(";");
		return sb.toString();
	}

	@Override
	public String makeStringRepresentation(Procedure procedure) {
		StringBuffer sb = new StringBuffer();
		if (procedure.getComment() != null && !procedure.getComment().trim().equals("")) {
			sb.append(makeJavadocComment(procedure.getComment()));
		}
		StringBuffer params = new StringBuffer();
		if (procedure.getParameters().size() > 0) {
			boolean isFirst = true;
			for (ProcedureParameter p : procedure.getParameters()) {
				params.append((isFirst ? "" : ",") + p.getType().getTypeName() + " " + p.getVariable().getName());
				isFirst = false;
			}
		}
		sb.append("public ");
		sb.append(procedure.getReturnType() == null ? "void" : procedure.getReturnType());
		sb.append(" " + procedure.getProcedureName());
		sb.append("(" + params.toString() + ")" + StringUtils.LINE_SEPARATOR + "{" + StringUtils.LINE_SEPARATOR);
		sb.append(getStringRepresentation(procedure.getControlGraph()) + StringUtils.LINE_SEPARATOR);
		sb.append("}");
		return sb.toString();
	}

	@Override
	public String makeStringRepresentation(Class aClass) {
		StringBuffer sb = new StringBuffer();
		if (aClass.getGroupName() != null) {
			sb.append("package ").append(aClass.getGroupName()).append(";").append(StringUtils.LINE_SEPARATOR)
					.append(StringUtils.LINE_SEPARATOR);
		}
		sb.append("import org.openflexo.engine.*;").append(StringUtils.LINE_SEPARATOR).append(StringUtils.LINE_SEPARATOR);

		if (aClass.getComment() != null && !aClass.getComment().trim().equals("")) {
			sb.append(makeJavadocComment(aClass.getComment()));
		}

		sb.append("public class");
		sb.append(" " + aClass.getClassName());
		sb.append(" extends ProcessProcessor {" + StringUtils.LINE_SEPARATOR + StringUtils.LINE_SEPARATOR);

		sb.append("public ");
		sb.append(" " + aClass.getClassName());
		sb.append("(IProcessInstance processInstance){").append(StringUtils.LINE_SEPARATOR);
		sb.append("super(processInstance);").append(StringUtils.LINE_SEPARATOR);
		sb.append("}").append(StringUtils.LINE_SEPARATOR).append(StringUtils.LINE_SEPARATOR);

		String processInstanceClassName = aClass.getClassName().substring(0, aClass.getClassName().length() - "Processor".length());

		sb.append("@Override" + StringUtils.LINE_SEPARATOR);
		sb.append("public " + processInstanceClassName);
		sb.append(" getProcessInstance(){");
		sb.append("return (" + processInstanceClassName + ")processInstance;").append(StringUtils.LINE_SEPARATOR);
		sb.append("}").append(StringUtils.LINE_SEPARATOR).append(StringUtils.LINE_SEPARATOR);

		if (aClass.getProcedures().size() > 0) {
			for (Procedure p : aClass.getProcedures()) {
				sb.append(getStringRepresentation(p));
				sb.append(StringUtils.LINE_SEPARATOR);
			}
		}

		sb.append("}");
		sb.append(StringUtils.LINE_SEPARATOR);
		return sb.toString();
	}

	@Override
	public String makeStringRepresentation(Loop loop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String makeStringRepresentation(Sequence sequence) {
		StringBuffer sb = new StringBuffer();

		String headerComment = sequence.getHeaderComment() != null ? makeHeaderComment(sequence.getHeaderComment()) : "";
		sb.append(headerComment);

		for (ControlGraph statement : sequence.getStatements()) {
			sb.append(getStringRepresentation(statement) + (statement != sequence.getStatements().lastElement() ? StringUtils.LINE_SEPARATOR : ""));
		}

		return sb.toString();
	}

	@Override
	public String makeStringRepresentation(Flow sequence) {
		StringBuffer sb = new StringBuffer();

		String headerComment = sequence.getHeaderComment() != null ? makeHeaderComment(sequence.getHeaderComment()) : "";
		sb.append(headerComment);

		for (ControlGraph statement : sequence.getStatements()) {
			sb.append("getExecutorService().submit(new Runnable(){" + StringUtils.LINE_SEPARATOR);
			sb.append("public void run(){" + StringUtils.LINE_SEPARATOR);
			sb.append(getStringRepresentation(statement));
			sb.append("}" + StringUtils.LINE_SEPARATOR);
			sb.append("});" + StringUtils.LINE_SEPARATOR);
			sb.append(statement != sequence.getStatements().lastElement() ? StringUtils.LINE_SEPARATOR : "");
		}

		return sb.toString();
	}

	private static class JalopyFactory {
		private static Jalopy jalopy;

		public static Jalopy getJalopy() {
			if (jalopy == null) {
				jalopy = new Jalopy();
			}
			jalopy.reset();
			return jalopy;
		}
	}

	public static String formatJavaCodeAsClassCode(String someCode) throws JavaFormattingException {
		if (someCode == null) {
			return null;
		}
		if (someCode.trim().equals("")) {
			return "";
		}

		String javaCode = someCode;

		javaCode = javaCode.replace("\r", "");
		javaCode = javaCode.replaceAll(MULTIPLE_NEW_LINE_ALONE_REG_EXP, StringUtils.LINE_SEPARATOR + StringUtils.LINE_SEPARATOR);
		Jalopy jalopy = JalopyFactory.getJalopy();
		try {
			StringBuffer sb = new StringBuffer();
			jalopy.setInspect(false);
			jalopy.setForce(true);
			jalopy.setInput(javaCode, "Format.java");
			jalopy.setOutput(sb);
			if (jalopy.format()) {
				String formatted = sb.toString();
				return removeExtraIndentation(formatted);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception occured while parsing the java code");
			}
		}
		if (jalopy.getState() == Jalopy.State.ERROR) {
			logger.warning("Java code could not be formatted");
			throw new JavaFormattingException();
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Java code could not be formatted: " + jalopy.getState());
		}
		return someCode;
	}

	public static String formatJavaCodeAsMethodCode(String someCode) throws JavaFormattingException {
		if (someCode == null) {
			return null;
		}
		if (someCode.trim().equals("")) {
			return "";
		}

		String javaCode = "public class Format {" + StringUtils.LINE_SEPARATOR + someCode + StringUtils.LINE_SEPARATOR + "}";

		javaCode = javaCode.replace("\r", "");
		javaCode = javaCode.replaceAll(MULTIPLE_NEW_LINE_ALONE_REG_EXP, StringUtils.LINE_SEPARATOR + StringUtils.LINE_SEPARATOR);
		Jalopy jalopy = JalopyFactory.getJalopy();
		try {
			StringBuffer sb = new StringBuffer();
			jalopy.setInspect(false);
			jalopy.setForce(true);
			jalopy.setInput(javaCode, "Format.java");
			jalopy.setOutput(sb);
			if (jalopy.format()) {
				String formatted = sb.toString();
				// System.out.println("Unformatted:["+someCode+"]");
				return removeExtraIndentation(formatted.substring(21 + StringUtils.LINE_SEPARATOR.length(),
						formatted.length() - 1 - StringUtils.LINE_SEPARATOR.length()));
				// return formatted.substring(53,formatted.length()-4);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception occured while parsing the java code");
			}
		}
		if (jalopy.getState() == Jalopy.State.ERROR) {
			logger.warning("Java code could not be formatted");
			throw new JavaFormattingException();
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Java code could not be formatted: " + jalopy.getState());
		}
		return someCode;
	}

	public static String formatJavaCodeAsInlineCode(String someCode) throws JavaFormattingException {
		if (someCode == null) {
			return null;
		}
		if (someCode.trim().equals("")) {
			return "";
		}

		String javaCode = "public class Format {" + StringUtils.LINE_SEPARATOR + "public void formatThis() {" + StringUtils.LINE_SEPARATOR + "" + someCode + "" + StringUtils.LINE_SEPARATOR + "}" + StringUtils.LINE_SEPARATOR + "}";

		javaCode = javaCode.replace("\r", "");
		javaCode = javaCode.replaceAll(MULTIPLE_NEW_LINE_ALONE_REG_EXP, StringUtils.LINE_SEPARATOR + StringUtils.LINE_SEPARATOR);
		Jalopy jalopy = JalopyFactory.getJalopy();
		try {
			StringBuffer sb = new StringBuffer();
			jalopy.setInspect(false);
			jalopy.setForce(true);
			jalopy.setInput(javaCode, "Format.java");
			jalopy.setOutput(sb);
			if (jalopy.format()) {
				String formatted = sb.toString();
				// System.out.println("Unformatted:["+someCode+"]");
				return removeExtraIndentation(formatted.substring(51 + 2 * StringUtils.LINE_SEPARATOR.length(),
						formatted.length() - 2 - 2 * StringUtils.LINE_SEPARATOR.length()));
				// return formatted.substring(53,formatted.length()-4);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception occured while parsing the java code");
			}
		}
		if (jalopy.getState() == Jalopy.State.ERROR) {
			logger.warning("Java code could not be formatted");
			throw new JavaFormattingException();
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Java code could not be formatted: " + jalopy.getState());
		}
		return someCode;
	}

	public static String removeExtraIndentation(String someCode) {
		StringTokenizer st = new StringTokenizer(someCode, StringUtils.LINE_SEPARATOR);
		Vector<String> lines = new Vector<String>();

		while (st.hasMoreTokens()) {
			String next = st.nextToken();
			if (!next.trim().equals("")) {
				lines.add(next);
			}
		}
		boolean charIsUnsignificantAndMatchesEverywhere = true;
		int firstSignificantCharIndex = 0;
		while (charIsUnsignificantAndMatchesEverywhere) {
			char c = lines.firstElement().charAt(firstSignificantCharIndex);
			if (c < 32 || c == ' ' || c == '\t') {
				for (int i = 1; i < lines.size(); i++) {
					if (lines.elementAt(i).length() <= firstSignificantCharIndex || lines.elementAt(i).charAt(firstSignificantCharIndex) != c) {
						charIsUnsignificantAndMatchesEverywhere = false;
					}
				}
				if (charIsUnsignificantAndMatchesEverywhere) {
					// System.out.println("Found char "+c+" of "+((int)c));
					firstSignificantCharIndex++;
				}
			} else {
				charIsUnsignificantAndMatchesEverywhere = false;
			}
		}

		StringBuffer returned = new StringBuffer();

		st = new StringTokenizer(someCode, StringUtils.LINE_SEPARATOR, true);
		while (st.hasMoreTokens()) {
			String next = st.nextToken();
			if (firstSignificantCharIndex < next.length()) {
				returned.append(next.substring(firstSignificantCharIndex));
			} else {
				returned.append(next);
			}
		}

		return returned.toString();
	}

}
