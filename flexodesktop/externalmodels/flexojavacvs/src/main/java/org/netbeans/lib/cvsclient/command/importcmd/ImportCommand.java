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
package org.netbeans.lib.cvsclient.command.importcmd;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.netbeans.lib.cvsclient.ClientServices;
import org.netbeans.lib.cvsclient.command.BuildableCommand;
import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.KeywordSubstitutionOptions;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.request.ArgumentRequest;
import org.netbeans.lib.cvsclient.request.ArgumentxRequest;
import org.netbeans.lib.cvsclient.request.CommandRequest;
import org.netbeans.lib.cvsclient.request.DirectoryRequest;
import org.netbeans.lib.cvsclient.request.ModifiedRequest;
import org.netbeans.lib.cvsclient.response.WrapperSendResponse;
import org.netbeans.lib.cvsclient.util.SimpleStringPattern;
import org.netbeans.lib.cvsclient.util.StringPattern;

/**
 * The import command imports local directory structures into the repository.
 * 
 * @author Thomas Singer
 */
public class ImportCommand extends BuildableCommand {
	private Map wrapperMap = new HashMap();
	private String logMessage;
	private String module;
	private String releaseTag;
	private String vendorBranch;
	private String vendorTag;
	private String importDirectory;
	private KeywordSubstitutionOptions keywordSubstitutionOptions;
	private boolean useFileModifTime;
	private List ignoreList = new LinkedList();

	public ImportCommand() {
		resetCVSCommand();
	}

	public void addWrapper(String filenamePattern, KeywordSubstitutionOptions keywordSubstitutionOptions) {
		if (keywordSubstitutionOptions == null) {
			throw new IllegalArgumentException("keywordSubstitutionOptions must not be null");
		}

		wrapperMap.put(new SimpleStringPattern(filenamePattern), keywordSubstitutionOptions);
	}

	public void addWrapper(StringPattern filenamePattern, KeywordSubstitutionOptions keywordSubstitutionOptions) {
		if (keywordSubstitutionOptions == null) {
			throw new IllegalArgumentException("keywordSubstitutionOptions must not be null");
		}

		wrapperMap.put(filenamePattern, keywordSubstitutionOptions);
	}

	/**
	 * Compliant method to addWrapper. It replaces the whole list of cvswrappers. The Map's structure should be following: Key: instance of
	 * StringPattern(fileName wildpattern) Value: instance of KeywordSubstitutionOptions
	 */
	public void setWrappers(Map wrapperMap) {
		this.wrapperMap = wrapperMap;
	}

	/**
	 * Returns a map with all wrappers. For map descriptions see setWrapper()
	 */
	public Map getWrappers() {
		return wrapperMap;
	}

	/**
	 * Returns the keyword substitution option.
	 */
	public KeywordSubstitutionOptions getKeywordSubstitutionOptions() {
		return keywordSubstitutionOptions;
	}

	/**
	 * Sets the keywords substitution option.
	 */
	public void setKeywordSubstitutionOptions(KeywordSubstitutionOptions keywordSubstitutionOptions) {
		this.keywordSubstitutionOptions = keywordSubstitutionOptions;
	}

	/**
	 * Returns the release tag.
	 */
	public String getReleaseTag() {
		return releaseTag;
	}

	/**
	 * Sets the necessary release tag.
	 */
	public void setReleaseTag(String releaseTag) {
		this.releaseTag = getTrimmedString(releaseTag);
	}

	/**
	 * Returns the log message.
	 */
	public String getLogMessage() {
		return logMessage;
	}

	/**
	 * Sets the log message.
	 */
	public void setLogMessage(String logMessage) {
		this.logMessage = getTrimmedString(logMessage);
	}

	/**
	 * Returns the module (the in-repository path, where the files should be stored.
	 */
	public String getModule() {
		return module;
	}

	/**
	 * Sets the module (the in-repository path, where the files should be stored).
	 */
	public void setModule(String module) {
		this.module = getTrimmedString(module);
	}

	/**
	 * Pints to directoty to import.
	 */
	public void setImportDirectory(String directory) {
		importDirectory = directory;
	}

	public String getImportDirectory() {
		return importDirectory;
	}

	/**
	 * Returns the vendor branch.
	 */
	public String getVendorBranch() {
		return vendorBranch;
	}

	/**
	 * Returns the vendor branch. If not set, then 1.1.1 is returned.
	 */
	private String getVendorBranchNotNull() {
		if (vendorBranch == null) {
			return "1.1.1"; // NOI18N
		}

		return vendorBranch;
	}

	/**
	 * Sets the vendor branch. If null is set, the default branch 1.1.1 is used automatically.
	 */
	public void setVendorBranch(String vendorBranch) {
		this.vendorBranch = getTrimmedString(vendorBranch);
	}

	/**
	 * Returns the vendor tag.
	 */
	public String getVendorTag() {
		return vendorTag;
	}

	/**
	 * Sets the necessary vendor tag.
	 */
	public void setVendorTag(String vendorTag) {
		this.vendorTag = getTrimmedString(vendorTag);
	}

	/**
	 * Tells, whether the file modification time is to be used as the time of the import.
	 */
	public boolean isUseFileModifTime() {
		return useFileModifTime;
	}

	/**
	 * Sets whether the file modification time is to be used as the time of the import.
	 */
	public void setUseFileModifTime(boolean useFileModifTime) {
		this.useFileModifTime = useFileModifTime;
	}

	/**
	 * Get a list of files that are ignored by import.
	 */
	public List getIgnoreFiles() {
		return Collections.unmodifiableList(ignoreList);
	}

	/**
	 * Add a file name that is to be ignored by the import.
	 */
	public void addIgnoredFile(String ignoredFileName) {
		ignoreList.add(ignoredFileName);
	}

	/**
	 * Executes thiz command using the set options.
	 */
	@Override
	public void execute(ClientServices client, EventManager eventManager) throws CommandException, AuthenticationException {
		// check necessary fields
		if (getLogMessage() == null) {
			String localizedMsg = CommandException.getLocalMessage("ImportCommand.messageEmpty"); // NOI18N
			throw new CommandException("message may not be null nor empty", // NOI18N
					localizedMsg);
		}
		if (getModule() == null) {
			String localizedMsg = CommandException.getLocalMessage("ImportCommand.moduleEmpty"); // NOI18N
			throw new CommandException("module may not be null nor empty", // NOI18N
					localizedMsg);
		}
		if (getReleaseTag() == null) {
			String localizedMsg = CommandException.getLocalMessage("ImportCommand.releaseTagEmpty"); // NOI18N
			throw new CommandException("release tag may not be null nor empty", // NOI18N
					localizedMsg);
		}
		if (getVendorTag() == null) {
			String localizedMsg = CommandException.getLocalMessage("ImportCommand.vendorTagEmpty"); // NOI18N
			throw new CommandException("vendor tag may not be null nor empty", // NOI18N
					localizedMsg);
		}

		client.ensureConnection();

		// get the connection wrappers here
		Map allWrappersMap = new HashMap(client.getWrappersMap());
		allWrappersMap.putAll(getWrappers());
		setWrappers(allWrappersMap);

		// start working
		super.execute(client, eventManager);
		assert getLocalDirectory() != null : "local directory may not be null";

		List requestList = new ArrayList();

		try {
			// add requests
			requestList.add(new ArgumentRequest("-b")); // NOI18N
			requestList.add(new ArgumentRequest(getVendorBranchNotNull()));

			if (getKeywordSubstitutionOptions() != null) {
				requestList.add(new ArgumentRequest("-k")); // NOI18N
				requestList.add(new ArgumentRequest(getKeywordSubstitutionOptions().toString()));
			}

			addMessageRequests(requestList, getLogMessage());

			addWrapperRequests(requestList, this.wrapperMap);

			if (isUseFileModifTime()) {
				requestList.add(new ArgumentRequest("-d")); // NOI18N
			}

			for (int i = 0; i < ignoreList.size(); i++) {
				requestList.add(new ArgumentRequest("-I")); // NOI18N
				requestList.add(new ArgumentRequest((String) ignoreList.get(i)));
			}

			requestList.add(new ArgumentRequest(getModule()));
			requestList.add(new ArgumentRequest(getVendorTag()));
			requestList.add(new ArgumentRequest(getReleaseTag()));

			addFileRequests(new File(getLocalDirectory()), requestList, client);

			requestList.add(new DirectoryRequest(".", getRepositoryRoot(client))); // NOI18N

			requestList.add(CommandRequest.IMPORT);

			// process the requests
			client.processRequests(requestList);
		} catch (CommandException ex) {
			throw ex;
		} catch (EOFException ex) {
			String localizedMsg = CommandException.getLocalMessage("CommandException.EndOfFile", null); // NOI18N
			throw new CommandException(ex, localizedMsg);
		} catch (Exception ex) {
			throw new CommandException(ex, ex.getLocalizedMessage());
		}
	}

	@Override
	public String getCVSCommand() {
		StringBuffer toReturn = new StringBuffer("import "); // NOI18N
		toReturn.append(getCVSArguments());
		if (getModule() != null) {
			toReturn.append(" "); // NOI18N
			toReturn.append(getModule());
		} else {
			String localizedMsg = CommandException.getLocalMessage("ImportCommand.moduleEmpty.text"); // NOI18N
			toReturn.append(" "); // NOI18N
			toReturn.append(localizedMsg);
		}
		if (getVendorTag() != null) {
			toReturn.append(" "); // NOI18N
			toReturn.append(getVendorTag());
		} else {
			String localizedMsg = CommandException.getLocalMessage("ImportCommand.vendorTagEmpty.text"); // NOI18N
			toReturn.append(" "); // NOI18N
			toReturn.append(localizedMsg);
		}
		if (getReleaseTag() != null) {
			toReturn.append(" "); // NOI18N
			toReturn.append(getReleaseTag());
		} else {
			String localizedMsg = CommandException.getLocalMessage("ImportCommand.releaseTagEmpty.text"); // NOI18N
			toReturn.append(" "); // NOI18N
			toReturn.append(localizedMsg);
		}
		return toReturn.toString();
	}

	@Override
	public String getCVSArguments() {
		StringBuffer toReturn = new StringBuffer(""); // NOI18N
		if (getLogMessage() != null) {
			toReturn.append("-m \""); // NOI18N
			toReturn.append(getLogMessage());
			toReturn.append("\" "); // NOI18N
		}
		if (getKeywordSubstitutionOptions() != null) {
			toReturn.append("-k"); // NOI18N
			toReturn.append(getKeywordSubstitutionOptions().toString());
			toReturn.append(" "); // NOI18N
		}
		if (getVendorBranch() != null) {
			toReturn.append("-b "); // NOI18N
			toReturn.append(getVendorBranch());
			toReturn.append(" "); // NOI18N
		}
		if (isUseFileModifTime()) {
			toReturn.append("-d "); // NOI18N
		}
		if (wrapperMap.size() > 0) {
			Iterator it = wrapperMap.keySet().iterator();
			while (it.hasNext()) {
				StringPattern pattern = (StringPattern) it.next();
				KeywordSubstitutionOptions keywordSubstitutionOptions = (KeywordSubstitutionOptions) wrapperMap.get(pattern);
				toReturn.append("-W "); // NOI18N
				toReturn.append(pattern.toString());
				toReturn.append(" -k '"); // NOI18N
				toReturn.append(keywordSubstitutionOptions.toString());
				toReturn.append("' "); // NOI18N
			}
		}
		for (Iterator it = ignoreList.iterator(); it.hasNext();) {
			toReturn.append("-I "); // NOI18N
			toReturn.append((String) it.next());
			toReturn.append(" "); // NOI18N
		}
		return toReturn.toString();
	}

	@Override
	public boolean setCVSCommand(char opt, String optArg) {
		if (opt == 'b') {
			setVendorBranch(optArg);
		} else if (opt == 'm') {
			setLogMessage(optArg);
		} else if (opt == 'k') {
			setKeywordSubstitutionOptions(KeywordSubstitutionOptions.findKeywordSubstOption(optArg));
		} else if (opt == 'W') {
			Map wrappers = WrapperSendResponse.parseWrappers(optArg);
			for (Iterator it = wrappers.keySet().iterator(); it.hasNext();) {
				StringPattern pattern = (StringPattern) it.next();
				KeywordSubstitutionOptions keywordOption = (KeywordSubstitutionOptions) wrappers.get(pattern);
				addWrapper(pattern, keywordOption);
			}
		} else if (opt == 'd') {
			setUseFileModifTime(true);
		} else if (opt == 'I') {
			addIgnoredFile(optArg);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public void resetCVSCommand() {
		setLogMessage(null);
		setModule(null);
		setReleaseTag(null);
		setVendorTag(null);
		setVendorBranch(null);
		setUseFileModifTime(false);
		ignoreList.clear();
		wrapperMap.clear();
	}

	@Override
	public String getOptString() {
		return "m:W:b:k:dI:"; // NOI18N
	}

	/**
	 * Adds requests for the specified logMessage to the specified requestList.
	 */
	private void addMessageRequests(List requestList, String logMessage) {
		requestList.add(new ArgumentRequest("-m")); // NOI18N

		StringTokenizer token = new StringTokenizer(logMessage, "\n", false); // NOI18N
		boolean first = true;
		while (token.hasMoreTokens()) {
			if (first) {
				requestList.add(new ArgumentRequest(token.nextToken()));
				first = false;
			} else {
				requestList.add(new ArgumentxRequest(token.nextToken()));
			}
		}
	}

	/**
	 * Adds requests for specified wrappers to the specified requestList.
	 */
	private void addWrapperRequests(List requestList, Map wrapperMap) {
		for (Iterator it = wrapperMap.keySet().iterator(); it.hasNext();) {
			StringPattern pattern = (StringPattern) it.next();
			KeywordSubstitutionOptions keywordSubstitutionOptions = (KeywordSubstitutionOptions) wrapperMap.get(pattern);

			StringBuffer buffer = new StringBuffer();
			buffer.append(pattern.toString());
			buffer.append(" -k '"); // NOI18N
			buffer.append(keywordSubstitutionOptions.toString());
			buffer.append("'"); // NOI18N

			requestList.add(new ArgumentRequest("-W")); // NOI18N
			requestList.add(new ArgumentRequest(buffer.toString()));
		}
	}

	/**
	 * Adds recursively all request for files and directories in the specified directory to the specified requestList.
	 */
	private void addFileRequests(File directory, List requestList, ClientServices clientServices) throws IOException {
		String relativePath = getRelativeToLocalPathInUnixStyle(directory);
		String repository = getRepositoryRoot(clientServices);
		if (!relativePath.equals(".")) { // NOI18N
			repository += '/' + relativePath;
		}
		requestList.add(new DirectoryRequest(relativePath, repository));

		File[] files = directory.listFiles();
		if (files == null) {
			return;
		}

		List subdirectories = null;

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			String filename = file.getName();

			if (clientServices.shouldBeIgnored(directory, filename)) {
				continue;
			}

			if (file.isDirectory()) {
				if (subdirectories == null) {
					subdirectories = new LinkedList();
				}
				subdirectories.add(file);
			} else {
				boolean isBinary = isBinary(filename);
				requestList.add(new ModifiedRequest(file, isBinary));
			}
		}

		if (subdirectories != null) {
			for (Iterator it = subdirectories.iterator(); it.hasNext();) {
				File subdirectory = (File) it.next();
				addFileRequests(subdirectory, requestList, clientServices);
			}
		}
	}

	/**
	 * Returns the used root path in the repository. It's built from the repository stored in the clientService and the module.
	 */
	private String getRepositoryRoot(ClientServices clientServices) {
		String repository = clientServices.getRepository() + '/' + getModule();
		return repository;
	}

	/**
	 * Returns true, if the file for the specified filename should be treated as a binary file.
	 * 
	 * The information comes from the wrapper map and the set keywordsubstitution.
	 */
	private boolean isBinary(String filename) {
		KeywordSubstitutionOptions keywordSubstitutionOptions = getKeywordSubstitutionOptions();

		for (Iterator it = wrapperMap.keySet().iterator(); it.hasNext();) {
			StringPattern pattern = (StringPattern) it.next();
			if (pattern.doesMatch(filename)) {
				keywordSubstitutionOptions = (KeywordSubstitutionOptions) wrapperMap.get(pattern);
				break;
			}
		}

		return keywordSubstitutionOptions == KeywordSubstitutionOptions.BINARY;
	}

	/**
	 * Creates the ImportBuilder.
	 */
	@Override
	public Builder createBuilder(EventManager eventManager) {
		return new ImportBuilder(eventManager, this);
	}
}
