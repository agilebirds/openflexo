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

package org.netbeans.lib.cvsclient.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;

import org.netbeans.lib.cvsclient.util.BugLog;
import org.netbeans.lib.cvsclient.util.ByteArray;

/**
 * File Processor that handles processing of rcs-diff response. (aka diff -n format) The original file is read and a merge is created. At
 * the same time the correct line-ending is processed. It's possible to set the processor the preffered line-ending. The default is
 * dependant on the platform.
 * 
 * @author Milos Kleint
 */
public class WriteRcsDiffFilePreprocessor implements WriteTextFilePreprocessor {

	private static final int CHUNK_SIZE = 32768;

	private static final int READ_REMAINING = -2;

	/**
	 * line ending that will be used when writing the final result file.
	 */
	private String lineEnding = System.getProperty("line.separator");

	public WriteRcsDiffFilePreprocessor() {
	}

	/**
	 * Gets the value of lineending used when writing the resulting file.
	 * 
	 * @return Value of property lineEnding.
	 */
	public String getLineEnding() {
		return lineEnding;
	}

	/**
	 * Setter for lineEnding used to write the file.
	 * 
	 * @param lineEnding
	 *            New value of property lineEnding.
	 */
	public void setLineEnding(String lineEnding) {
		this.lineEnding = lineEnding;
	}

	/**
	 * Processes the diff and merges it with the original file.
	 * 
	 * @param processedInput
	 *            the stored diff.
	 * @param fileToWrite
	 *            the resulting file and the original that is merged as the same time.
	 * @param customOutput
	 * @throws IOException
	 *             if any IO operation fails. The original file is replaced by the merge only if no excpetion is thrown.
	 */
	@Override
	public void copyTextFileToLocation(InputStream processedInput, File fileToWrite, OutputStreamProvider customOutput) throws IOException {
		// Here we read the temp file in again, doing any processing required
		// (for example, unzipping). We must not convert the bytes to characters
		// because this would corrupt files, that were written in an encoding
		// different from the current encoding.
		ReadInfo tempFileReader = null;
		OutputStream out = null;
		ReadInfo tempDiffReader = null;
		File tempFile = null;
		// BUGLOG.. assert the fileToWrite Exists.. otherwise it has no sense.

		try {
			tempDiffReader = new ReadInfo(new BufferedInputStream(processedInput));
			tempFileReader = new ReadInfo(new BufferedInputStream(new FileInputStream(fileToWrite)));
			tempFile = File.createTempFile(".#merg", "cvs"); // NOI18N

			out = new BufferedOutputStream(new FileOutputStream(tempFile));

			int fileStart = 0;
			int diffCount = 0;
			byte[] diff = tempDiffReader.readLine();
			while (diff != null && diff.length > 0) {
				// System.out.println("diffline=" + diff);
				if (diff[0] == 'd') {
					// now do delete..
					int startLine = getStart(diff);
					int count = getLength(diff);
					// System.out.println("deleting. start =" + startLine + "  count=" + count);
					if (startLine >= 0 && count > 0) {
						// read from file only if deletion is not some
						// from the beginning..
						readToLine(startLine - 1, tempFileReader, out);
						readToLine(startLine - 1 + count, tempFileReader, null);
						// null skips the lines..
					} else {
						// BugLog..
						BugLog.getInstance().bug("wrong parsing.." + new String(diff));
						throw new IOException(); // Interrupt the merging process so that the file is not corrupted.
					}
				} else if (diff[0] == 'a') {
					// now add lines from the diff..
					int startLine = getStart(diff);
					int count = getLength(diff);
					// System.out.println("adding.. start =" + startLine + "  count=" + count);
					if (startLine >= 0 && count > 0) {
						readToLine(startLine, tempFileReader, out);
						tempDiffReader.setLineNumber(0);
						readToLine(count, tempDiffReader, out);
					} else {
						// BugLog..
						BugLog.getInstance().bug("wrong parsing.." + new String(diff));
						throw new IOException(); // Interrupt the merging process so that the file is not corrupted.
					}
				}
				// now process next difference.
				diff = tempDiffReader.readLine();
			}
			// read what's remaining..
			readToLine(READ_REMAINING, tempFileReader, out);

			if (tempFile != null) {
				tempFileReader.close();
				out.close();
				InputStream in = null;
				OutputStream customOutputStream = customOutput.createOutputStream();
				try {
					in = new BufferedInputStream(new FileInputStream(tempFile));
					while (true) {
						int ch = in.read();
						if (ch == -1) {
							break;
						}
						customOutputStream.write(ch);
					}
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException ex) {
						}
					}
					try {
						customOutputStream.close();
					} catch (IOException ex) {
					}
				}
			}
		} catch (Exception exc) {
			BugLog.getInstance().showException(exc);
		} finally {
			if (tempDiffReader != null) {
				try {
					tempDiffReader.close();
				} catch (IOException ex) {
					// ignore
				}
			}
			if (tempFileReader != null) {
				try {
					tempFileReader.close();
				} catch (IOException ex) {
					// ignore
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
					// ignore
				}
			}
			if (tempFile != null) {
				tempFile.delete();
			}
		}
	}

	/**
	 * Reads lines from the reader until the specified line number is reached. Writes the lines to the writer
	 * 
	 * @param finalLine
	 *            the last line to read/write. Special value -2 reads all that is remaining in the reader.
	 * @param reader
	 *            - does not accept null.
	 * @param writer
	 *            the writer that the read lines are written to. Accepts null. In such a case the read lines are discarded.
	 */
	private void readToLine(int finalLine, final ReadInfo reader, final OutputStream out) throws IOException {
		byte[] line;
		while (reader.getLineNumber() < finalLine || finalLine == READ_REMAINING) {
			line = reader.readLine();
			if (line == null) {
				// end of file..
				return;
			}
			if (out != null) {
				out.write(line);
				out.write(getLineEnding().getBytes());
			}
		}
	}

	private static int indexOf(byte[] bytes, byte b) {
		return indexOf(bytes, b, 0);
	}

	private static int indexOf(byte[] bytes, byte b, int start) {
		int index = -1;
		for (int i = start; i < bytes.length; i++) {
			if (bytes[i] == b) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * Extracts the line where the diff starts.
	 */
	private static int getStart(byte[] diffLine) {
		int spacePos = indexOf(diffLine, (byte) ' ');
		if (spacePos > 0) {
			String number = new String(diffLine, 1, spacePos - 1);
			try {
				int toReturn = Integer.parseInt(number);
				return toReturn;
			} catch (NumberFormatException exc) {
				// System.out.println("diffLine=" + diffLine);
				return -1;
			}
		}
		// System.out.println("no space in diffline=" + diffLine);
		return -1;
	}

	/**
	 * Extracts the length of the diff. For "delete" it means how many lines to delete from original. For "add" it means how many lines are
	 * added from the diff to original file.
	 */
	private static int getLength(byte[] diffLine) {
		// String trimmed = diffLine.trim();
		int spacePos = indexOf(diffLine, (byte) ' ');
		if (spacePos > 0) {
			int end = indexOf(diffLine, (byte) ' ', spacePos + 1);
			if (end < 0) {
				end = diffLine.length;
			}
			String number = new String(diffLine, spacePos + 1, end - spacePos - 1);
			try {
				int toReturn = Integer.parseInt(number);
				return toReturn;
			} catch (NumberFormatException exc) {
				// System.out.println("numformat exception..=" + diffLine);
				return -1;
			}
		}
		// System.out.println("no space in diffline=" + diffLine);
		return -1;
	}

	private static class ReadInfo {

		private static final boolean crLines = "\r".equals(System.getProperty("line.separator"));

		private PushbackInputStream in;
		private int readLength;
		private int startIndex;
		private int lineNumber;
		private ByteArray line;

		public ReadInfo(InputStream in) {
			this.in = new PushbackInputStream(in, 1);
			readLength = -1;
			startIndex = 0;
			lineNumber = 0;
			line = new ByteArray();
		}

		public int getLineNumber() {
			return lineNumber;
		}

		public void setLineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
		}

		/**
		 * Reads a line. The line-termination bytes are not added.
		 * 
		 * @return null When the end of the stream is reached.
		 */
		public byte[] readLine() throws IOException {
			line.reset();
			boolean end = false;
			do {
				int b = in.read();
				if (b == -1) {
					end = true;
					break;
				}
				if (b == '\n') {
					lineNumber++;
					break;
				}
				if (b == '\r') {
					int next = in.read();
					if (next == '\n') {
						lineNumber++;
						break;
					}
					in.unread(next);
					if (crLines) {
						lineNumber++;
						break;
					}
				}
				line.add((byte) b);
			} while (true);
			byte[] bytes = line.getBytes();
			if (end && bytes.length == 0) {
				bytes = null;
			}
			return bytes;
			/*            
			            StringBuffer toReturn = new StringBuffer();
			            while (true) {
			                if (startIndex > readLength) {
			//                    System.out.println("reading..");
			                    readLength = reader.read(cchunk);
			//                    System.out.println("read=" + readLength);
			                    startIndex = 0;
			                }
			                if (startIndex >= readLength) {
			                    readLength = -1;
			                    if (toReturn.length() == 0) {
			                        return null;
			                    }
			                    else {
			                        lineNumber = lineNumber + 1;
			                        return toReturn.toString();
			                    }
			                }
			//                System.out.println("startindex = " + startIndex + "   length=" + readLength);
			                for (int i = startIndex; i < readLength; i++) {
			                    if (cchunk[i] == '\n') {
			                        toReturn.append(WriteRcsDiffFilePreprocessor.this.getLineEnding());
			                        startIndex = i + 1;
			                        lineNumber = lineNumber + 1;
			//                        System.out.println("linenum=" + lineNumber);
			//                        System.out.println("line=" + toReturn.toString() + "--");
			                        return toReturn.toString();
			                    }
			                    else {
			                        // could be maybe made faster by appending the whole array when
			                        // read all or encountering the newline..
			                        toReturn.append(cchunk[i]);
			                    }
			                }
			                startIndex = readLength;
			            }
			 */
		}

		public void close() throws IOException {
			if (in != null) {
				in.close();
			}
		}
	} // end of inner class..

}
