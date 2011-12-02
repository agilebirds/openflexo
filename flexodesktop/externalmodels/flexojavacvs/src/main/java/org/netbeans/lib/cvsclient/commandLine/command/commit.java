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
package org.netbeans.lib.cvsclient.commandLine.command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.netbeans.lib.cvsclient.command.Command;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.commit.CommitCommand;
import org.netbeans.lib.cvsclient.commandLine.GetOpt;

/**
 * Implements the commit command
 * 
 * @author Robert Greig
 */
public class commit extends AbstractCommandProvider {

	@Override
	public String[] getSynonyms() {
		return new String[] { "ci", "com", "put" }; // NOI18N
	}

	/**
	 * @param editor
	 *            The editor passed in via -e global option.
	 */
	private static String getEditorProcess(String editor) {
		if (editor == null) {
			if (System.getProperty("os.name").startsWith("Windows")) {
				editor = "notepad.exe";
			} else {
				editor = null;// "vi"; - do not use 'vi'. It's not executed correctly in the terminal.
			}
			editor = System.getProperty("cvs.editor", editor);
		}
		return editor;
	}

	private static File createTempFile(File[] args, File tmpDir) throws IOException {
		File template = null;
		BufferedReader templateReader = null;
		BufferedWriter writer = null;
		try {
			File tempFile = File.createTempFile("cvsTemplate", "txt", tmpDir);
			writer = new BufferedWriter(new FileWriter(tempFile));

			if (args != null && args.length > 0) {
				// Get the template file from the first argument
				template = new File(args[0].getParentFile(), "CVS" + File.separator + "Template");
				if (template.exists()) {
					templateReader = new BufferedReader(new FileReader(template));

					String line = null;
					while ((line = templateReader.readLine()) != null) {
						writer.write(line);
						writer.newLine();
					}
				}
			}
			writer.write("CVS: ----------------------------------------------------------------------");
			writer.newLine();
			writer.write("CVS: Enter Log.  Lines beginning with `CVS:' are removed automatically");
			writer.newLine();
			writer.write("CVS: ");
			writer.newLine();
			// TODO: fix this bit
			writer.write("CVS: Committing in .");
			writer.newLine();
			writer.write("CVS: ");
			writer.newLine();
			writer.write("CVS: Modified Files:");
			writer.newLine();
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					// TODO: don't write out the full path of files
					writer.write("CVS:  " + args[i].getPath());
				}
			}
			writer.write("CVS: ----------------------------------------------------------------------");
			writer.newLine();
			return tempFile;
		} finally {
			if (templateReader != null) {
				templateReader.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
	}

	private static String createMessage(File[] args, GlobalOptions gopt) {
		File temp = null;
		BufferedReader reader = null;
		try {
			temp = createTempFile(args, gopt.getTempDir());
			// we now have a temp file with the appropriate text in it. Just
			// get the appropriate process to edit it.
			// TODO maybe make this more sophisticated, e.g. the cvs.editor
			// property allows certain fields to specify arguments, where the
			// actual filename goes etc.
			String editorProcess = getEditorProcess(gopt.getEditor());
			if (editorProcess == null) {
				return null;
			}
			final Process proc = Runtime.getRuntime().exec(new String[] { editorProcess, temp.getPath() });
			int returnCode = -1;

			try {
				returnCode = proc.waitFor();
			} catch (InterruptedException e) {
				// So somebody else interrupted us.
			}

			if (returnCode != 0) {
				return null;
			} else {
				// TODO: need to add the bit that tests whether the file
				// has been changed so that we can bring up the abort etc.
				// message just like real CVS.
				reader = new BufferedReader(new FileReader(temp));
				String line;
				StringBuffer message = new StringBuffer((int) temp.length());
				while ((line = reader.readLine()) != null) {
					if (!line.startsWith("CVS:")) {
						message.append(line);
						message.append('\n');
					}
				}
				return message.toString();
			}
		} catch (IOException e) {
			// OK something went wrong so just don't bother returning a
			// message
			System.err.println("Error: " + e);
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (temp != null) {
					temp.delete();
				}
			} catch (Exception e) {
				// we're clearly in real trouble so just dump the
				// exception to standard err and get out of here
				System.err.println("Fatal error: " + e);
				e.printStackTrace();
			}
		}
	}

	@Override
	public Command createCommand(String[] args, int index, GlobalOptions gopt, String workDir) {
		CommitCommand command = new CommitCommand();
		command.setBuilder(null);
		final String getOptString = command.getOptString();
		GetOpt go = new GetOpt(args, getOptString);
		int ch = -1;
		go.optIndexSet(index);
		boolean usagePrint = false;
		while ((ch = go.getopt()) != GetOpt.optEOF) {
			boolean ok = command.setCVSCommand((char) ch, go.optArgGet());
			if (!ok) {
				usagePrint = true;
			}
		}
		if (usagePrint) {
			throw new IllegalArgumentException(getUsage());
		}

		int fileArgsIndex = go.optIndexGet();

		File[] fileArgs = null;

		// test if we have been passed any file arguments
		if (fileArgsIndex < args.length) {
			fileArgs = new File[args.length - fileArgsIndex];
			// send the arguments as absolute paths
			if (workDir == null) {
				workDir = System.getProperty("user.dir");
			}
			File workingDir = new File(workDir);
			for (int i = fileArgsIndex; i < args.length; i++) {
				fileArgs[i - fileArgsIndex] = new File(workingDir, args[i]);
			}
			command.setFiles(fileArgs);
		}

		// now only bring up the editor if the message has not been set using
		// the -m option
		if (command.getMessage() == null && command.getLogMessageFromFile() == null) {
			String message = createMessage(fileArgs, gopt);
			if (message == null) {
				throw new IllegalArgumentException(java.util.ResourceBundle.getBundle(commit.class.getPackage().getName() + ".Bundle")
						.getString("commit.messageNotSpecified"));
			}
			command.setMessage(message);
		}

		return command;
	}

}