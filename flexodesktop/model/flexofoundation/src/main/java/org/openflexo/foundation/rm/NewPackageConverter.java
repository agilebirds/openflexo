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
		return true;
	}

	private boolean convertFile(File file) {
		try {
			String content = org.openflexo.toolbox.FileUtils.fileContents(file);
			content = filter(content, BE_DENALI_FLEXO); // be.denali.flexo* --> org.openflexo*
			content = filter(content, BE_DENALI); // be.denali --> org.openflexo
			content = filter(content, BE_AB_FLEXO); // be.agilebirds.flexo* --> org.openflexo*
			content = filter(content, BE_AB); // be.agilebirds --> org.openflexo
			org.openflexo.toolbox.FileUtils.copyFileToFile(file, new File(file.getParentFile(),file.getName()+".~~"));
			org.openflexo.toolbox.FileUtils.saveToFile(file, content);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private String filter(String content, String packagePrefix) {
		int lastAppendedIndex = 0;
		// be.denali.flexo* --> org.openflexo*
		StringBuilder sb = new StringBuilder(content.length());
		for (int i = 0; i < content.length(); i++) {
			if (content.regionMatches(i, packagePrefix, 0, packagePrefix.length())) {
				sb.append(content, lastAppendedIndex, i);
				sb.append(ORG_OF);
				if (content.charAt(i + packagePrefix.length() + 1) != '.') {
					sb.append('.');
				}
				i = i + packagePrefix.length() - 1;
			}
		}
		return sb.toString();
	}

}
