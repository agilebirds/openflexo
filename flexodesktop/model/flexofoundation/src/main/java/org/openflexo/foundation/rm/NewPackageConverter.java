package org.openflexo.foundation.rm;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

public class NewPackageConverter {

	private static final String BE_DENALI_FLEXO = "be.denali.flexo";
	private static final String BE_DENALI = "be.denali";
	private static final String BE_AB_FLEXO = "be.agilebirds.flexo";
	private static final String BE_AB = "be.agilebirds";

	private static final String ORG_OF = "org.openflexo";

	private final File projectDirectory;

	public NewPackageConverter(File projectDirectory) {
		this.projectDirectory = projectDirectory;

	}

	public boolean convert() {
		Collection<File> files = FileUtils.listFiles(projectDirectory, ProjectRestructuration.FILE_EXTENSIONS, true);
		for (File file : files) {
			if (!convertFile(file)) {
				return false;
			}
		}
		Collection<File> templates = FileUtils.listFiles(projectDirectory, new String[] { "vm" }, true);
		for (File template : templates) {
			if (!convertTemplate(template)) {
				return false;
			}
		}
		return true;
	}

	private boolean convertFile(File file) {
		try {
			String content = org.openflexo.toolbox.FileUtils.fileContents(file);
			org.openflexo.toolbox.FileUtils.saveToFile(new File(file.getParentFile(), file.getName() + ".~~"), content);
			if (file.getName().endsWith(ProjectRestructuration.DM_EXTENSION)) {
				content = convertDMContentToNewPackage(content);
			} else {
				content = convertContentToNewPackage(content);
			}
			org.openflexo.toolbox.FileUtils.saveToFile(file, content);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean convertTemplate(File template) {
		try {
			String content = org.openflexo.toolbox.FileUtils.fileContents(template);
			content = filter(content, BE_DENALI_FLEXO); // be.denali.flexo* --> org.openflexo*
			content = filter(content, BE_AB_FLEXO); // be.agilebirds.flexo* --> org.openflexo*
			content = filter(content, BE_AB); // be.agilebirds --> org.openflexo
			content = content.replace("$velocityCount", "$foreach.count");
			content = content.replace("${velocityCount}", "${foreach.count}");
			if (template.getName().equals("LocalizedFile.vm")) {
				content = content.replace("\\\"", "${quote}");
			}
			org.openflexo.toolbox.FileUtils.copyFileToFile(template, new File(template.getParentFile(), template.getName() + ".~~"));
			org.openflexo.toolbox.FileUtils.saveToFile(template, content);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static String convertDMContentToNewPackage(String content) {
		String JDK = "JDKRepository";
		String COMPONENT="ComponentRepository";
		String PROCESS_BUSINESS_DATA="ProcessBusinessDataRepository";
		String PROCESS_INSTANCE="ProcessInstanceRepository";
		content = convertDMContentToNewPackage(content, JDK);
		content = convertDMContentToNewPackage(content, COMPONENT);
		content = convertDMContentToNewPackage(content, PROCESS_BUSINESS_DATA);
		content = convertDMContentToNewPackage(content, PROCESS_INSTANCE);
		return content;
	}

	private static String convertDMContentToNewPackage(String content, String repository_tag_name) {
		StringBuilder sb = new StringBuilder(content.length());
		int lastAppended = 0;
		int index = content.indexOf(repository_tag_name);
		while (index > -1) {
			int endIndex = content.indexOf(repository_tag_name, index + repository_tag_name.length());
			sb.append(content, lastAppended, index);
			sb.append(convertContentToNewPackage(content.substring(index, endIndex)));
			lastAppended = endIndex;
			index = content.indexOf(repository_tag_name, endIndex + repository_tag_name.length());
		}
		return sb.append(content, lastAppended, content.length()).toString();
	}

	public static String convertContentToNewPackage(String content) {
		content = filter(content, BE_DENALI_FLEXO); // be.denali.flexo* --> org.openflexo*
		content = filter(content, BE_DENALI); // be.denali --> org.openflexo
		content = filter(content, BE_AB_FLEXO); // be.agilebirds.flexo* --> org.openflexo*
		content = filter(content, BE_AB); // be.agilebirds --> org.openflexo
		return content;
	}


	private static String filter(String content, String packagePrefix) {
		int lastAppendedIndex = 0;
		// be.denali.flexo* --> org.openflexo*
		StringBuilder sb = new StringBuilder(content.length());
		for (int i = 0; i < content.length(); i++) {
			if (content.regionMatches(i, packagePrefix, 0, packagePrefix.length())
					&& !content.regionMatches(i + packagePrefix.length(), ".engine", 0, 7)
					&& !content.regionMatches(i + packagePrefix.length(), ".commons", 0, 8)) {
				sb.append(content, lastAppendedIndex, i);
				sb.append(ORG_OF);
				char c = content.charAt(i + packagePrefix.length());
				switch (c) {
				case '"': // in DM when this is the end of the package attribute we don't need to put a '.'
					break;
				case '.': // If there is already a dot, then don't add one.
					break;
				default:
					sb.append('.');
				}
				i = i + packagePrefix.length() - 1;
				lastAppendedIndex = i + 1;
			}
		}
		sb.append(content, lastAppendedIndex, content.length());
		return sb.toString();
	}

	public static void main(String[] args) {
		String s = "coucou be.denali.flexo.zut\nmachin be.denali.flexobrol bidule\nbe.denali.flexo.engine\n<JDKRepository id=\"12354\">\nblabla be.denali.coucou\nbe.denali.engine.db.ProcessInstance\n</JDKRepository>";
		System.err.println(s);
		System.err.println(convertDMContentToNewPackage(s));
	}

}
