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

package org.openflexo.diff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Vector;

interface UnaryPredicate {
	boolean execute(Object obj);
}

/**
 * A simple framework for printing change lists produced by <code>Diff</code>.
 * 
 * @see bmsi.util.Diff
 * @author Stuart D. Gathman Copyright (C) 2000 Business Management Systems, Inc.
 *         <p>
 *         This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 *         published by the Free Software Foundation; either version 1, or (at your option) any later version.
 *         <p>
 *         This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *         <p>
 *         You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software
 *         Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

public class DiffPrint {
	/**
	 * A Base class for printing edit scripts produced by Diff. This class divides the change list into "hunks", and calls
	 * <code>print_hunk</code> for each hunk. Various utility methods are provided as well.
	 */
	public static abstract class Base {
		protected Base(Object[] a, Object[] b) {
			outfile = new PrintWriter(new OutputStreamWriter(System.out));
			diffResult = new StringBuffer();
			file0 = a;
			file1 = b;
		}

		/**
		 * Set to ignore certain kinds of lines when printing an edit script. For example, ignoring blank lines or comments.
		 */
		protected UnaryPredicate ignore = null;

		/**
		 * Set to the lines of the files being compared.
		 */
		protected Object[] file0, file1;

		/**
		 * Divide SCRIPT into pieces by calling HUNKFUN and print each piece with PRINTFUN. Both functions take one arg, an edit script.
		 * 
		 * PRINTFUN takes a subscript which belongs together (with a null link at the end) and prints it.
		 */
		public void print_script(Diff.change script) {
			Diff.change next = script;

			while (next != null) {
				Diff.change t, end;

				/* Find a set of changes that belong together.  */
				t = next;
				end = hunkfun(next);

				/* Disconnect them from the rest of the changes,
				 making them a hunk, and remember the rest for next iteration.  */
				next = end.link;
				end.link = null;
				// if (DEBUG)

				// debug_script(t);

				/* Print this hunk.  */
				print_hunk(t);

				/* Reconnect the script so it will all be freed properly.  */
				end.link = next;
			}
			outfile.flush();
		}

		/**
		 * If using LinePrint class results are returned as string
		 */
		public String getDiffResult() {
			return diffResult != null ? diffResult.toString() : null;
		}

		/**
		 * Called with the tail of the script and returns the last link that belongs together with the start of the tail.
		 */
		protected Diff.change hunkfun(Diff.change hunk) {
			return hunk;
		}

		protected int first0, last0, first1, last1, deletes, inserts;
		protected PrintWriter outfile;
		protected StringBuffer diffResult;

		/**
		 * Look at a hunk of edit script and report the range of lines in each file that it applies to. HUNK is the start of the hunk, which
		 * is a chain of `struct change'. The first and last line numbers of file 0 are stored in *FIRST0 and *LAST0, and likewise for file
		 * 1 in *FIRST1 and *LAST1. Note that these are internal line numbers that count from 0.
		 * 
		 * If no lines from file 0 are deleted, then FIRST0 is LAST0+1.
		 * 
		 * Also set *DELETES nonzero if any lines of file 0 are deleted and set *INSERTS nonzero if any lines of file 1 are inserted. If
		 * only ignorable lines are inserted or deleted, both are set to 0.
		 */

		protected void analyze_hunk(Diff.change hunk) {
			int f0, l0 = 0, f1, l1 = 0, show_from = 0, show_to = 0;
			int i;
			Diff.change next;
			boolean nontrivial = ignore == null;

			show_from = show_to = 0;

			f0 = hunk.line0;
			f1 = hunk.line1;

			for (next = hunk; next != null; next = next.link) {
				l0 = next.line0 + next.deleted - 1;
				l1 = next.line1 + next.inserted - 1;
				show_from += next.deleted;
				show_to += next.inserted;
				for (i = next.line0; i <= l0 && !nontrivial; i++) {
					if (!ignore.execute(file0[i])) {
						nontrivial = true;
					}
				}
				for (i = next.line1; i <= l1 && !nontrivial; i++) {
					if (!ignore.execute(file1[i])) {
						nontrivial = true;
					}
				}
			}

			first0 = f0;
			last0 = l0;
			first1 = f1;
			last1 = l1;

			/* If all inserted or deleted lines are ignorable,
			 tell the caller to ignore this hunk.  */

			if (!nontrivial) {
				show_from = show_to = 0;
			}

			deletes = show_from;
			inserts = show_to;
		}

		protected abstract void print_hunk(Diff.change hunk);

		protected void print_1_line(String pre, Object linbuf) {
			outfile.println(pre + linbuf.toString());
			diffResult.append(pre + linbuf.toString() + "\n");
		}

		/**
		 * Print a pair of line numbers with SEPCHAR, translated for file FILE. If the two numbers are identical, print just one number.
		 * 
		 * Args A and B are internal line numbers. We print the translated (real) line numbers.
		 */

		protected void print_number_range(char sepchar, int a, int b) {
			/* Note: we can have B < A in the case of a range of no lines.
			 In this case, we should print the line number before the range,
			 which is B.  */
			if (++b > ++a) {
				outfile.print("" + a + sepchar + b);
				diffResult.append("" + a + sepchar + b);
			} else {
				outfile.print(b);
				diffResult.append(b);
			}
		}

		public static char change_letter(int inserts, int deletes) {
			if (inserts == 0) {
				return 'd';
			} else if (deletes == 0) {
				return 'a';
			} else {
				return 'c';
			}
		}
	}

	/**
	 * Print a change list in the Verse format used for Jscripture
	 * 
	 * @author Patrick Lacson (patrick@lacson.net)
	 */
	public static class LinePrint extends Base {

		public LinePrint(Object[] a, Object[] b) {
			super(a, b);
		}

		/**
		 * Print a hunk of a normal diff. This is a contiguous portion of a complete edit script, describing changes in consecutive lines.
		 */

		@Override
		protected void print_hunk(Diff.change hunk) {

			/* Determine range of line numbers involved in each file.  */
			analyze_hunk(hunk);
			if (deletes == 0 && inserts == 0) {
				return;
			}

			/* Print out the line number header for this hunk */
			print_number_range(',', first0, last0);
			outfile.print(change_letter(inserts, deletes));
			diffResult.append(change_letter(inserts, deletes));
			print_number_range(',', first1, last1);
			outfile.println();
			diffResult.append("\n");

			/* Print the lines that the first file has.  */
			if (deletes != 0) {
				for (int i = first0; i <= last0; i++) {
					print_1_line("< ", file0[i]);
				}
			}

			if (inserts != 0 && deletes != 0) {
				outfile.println("---");
				diffResult.append("---\n");
			}

			/* Print the lines that the second file has.  */
			if (inserts != 0) {
				for (int i = first1; i <= last1; i++) {
					print_1_line("> ", file1[i]);
				}
			}
		}
	}

	/**
	 * Print a change list in the standard diff format.
	 */
	public static class NormalPrint extends Base {

		public NormalPrint(Object[] a, Object[] b) {
			super(a, b);
		}

		/**
		 * Print a hunk of a normal diff. This is a contiguous portion of a complete edit script, describing changes in consecutive lines.
		 */

		@Override
		protected void print_hunk(Diff.change hunk) {

			/* Determine range of line numbers involved in each file.  */
			analyze_hunk(hunk);
			if (deletes == 0 && inserts == 0) {
				return;
			}

			/* Print out the line number header for this hunk */
			print_number_range(',', first0, last0);
			outfile.print(change_letter(inserts, deletes));
			print_number_range(',', first1, last1);
			outfile.println();

			/* Print the lines that the first file has.  */
			if (deletes != 0) {
				for (int i = first0; i <= last0; i++) {
					print_1_line("< ", file0[i]);
				}
			}

			if (inserts != 0 && deletes != 0) {
				outfile.println("---");
			}

			/* Print the lines that the second file has.  */
			if (inserts != 0) {
				for (int i = first1; i <= last1; i++) {
					print_1_line("> ", file1[i]);
				}
			}
		}
	}

	/**
	 * Prints an edit script in a format suitable for input to <code>ed</code>. The edit script must be generated with the reverse option to
	 * be useful as actual <code>ed</code> input.
	 */
	public static class EdPrint extends Base {

		public EdPrint(Object[] a, Object[] b) {
			super(a, b);
		}

		/** Print a hunk of an ed diff */
		@Override
		protected void print_hunk(Diff.change hunk) {

			/* Determine range of line numbers involved in each file.  */
			analyze_hunk(hunk);
			if (deletes == 0 && inserts == 0) {
				return;
			}

			/* Print out the line number header for this hunk */
			print_number_range(',', first0, last0);
			outfile.println(change_letter(inserts, deletes));

			/* Print new/changed lines from second file, if needed */
			if (inserts != 0) {
				boolean inserting = true;
				for (int i = first1; i <= last1; i++) {
					/* Resume the insert, if we stopped.  */
					if (!inserting) {
						outfile.println(i - first1 + first0 + "a");
					}
					inserting = true;

					/* If the file's line is just a dot, it would confuse `ed'.
					 So output it with a double dot, and set the flag LEADING_DOT
					 so that we will output another ed-command later
					 to change the double dot into a single dot.  */

					if (".".equals(file1[i])) {
						outfile.println("..");
						outfile.println(".");
						/* Now change that double dot to the desired single dot.  */
						outfile.println(i - first1 + first0 + 1 + "s/^\\.\\././");
						inserting = false;
					} else {
						/* Line is not `.', so output it unmodified.  */
						print_1_line("", file1[i]);
					}
				}

				/* End insert mode, if we are still in it.  */
				if (inserting) {
					outfile.println(".");
				}
			}
		}
	}

	/**
	 * Read a text file into an array of String. This provides basic diff functionality. A more advanced diff utility will use specialized
	 * objects to represent the text lines, with options to, for example, convert sequences of whitespace to a single space for comparison
	 * purposes.
	 */
	public static String[] slurpFile(File file) throws IOException {
		BufferedReader rdr = new BufferedReader(new FileReader(file));
		Vector<String> s = new Vector<String>();
		for (;;) {
			String line = rdr.readLine();
			if (line == null) {
				break;
			}
			s.addElement(line);
			// System.out.println("File: add line "+line);
		}
		String[] a = new String[s.size()];
		s.copyInto(a);
		return a;
	}

	/**
	 * Same as above, but ignore first ignoredCols cols
	 */
	public static String[] slurpFile(File file, int ignoredCols) throws IOException {
		BufferedReader rdr = new BufferedReader(new FileReader(file));
		Vector<String> s = new Vector<String>();
		for (;;) {
			String line = rdr.readLine();
			if (line == null) {
				break;
			}
			s.addElement(line.substring(ignoredCols));
			// System.out.println("File: add line "+line);
		}
		String[] a = new String[s.size()];
		s.copyInto(a);
		return a;
	}

	/**
	 * Read a text file into an array of String. This provides basic diff functionality. A more advanced diff utility will use specialized
	 * objects to represent the text lines, with options to, for example, convert sequences of whitespace to a single space for comparison
	 * purposes.
	 */
	public static String[] slurpFile(String file) throws IOException {
		return slurpFile(new File(file));
	}

	/**
	 * Convert a String to an array of String (use \n)
	 */
	public static String[] slurpString(String aString) throws IOException {
		BufferedReader rdr = new BufferedReader(new StringReader(aString));
		Vector<String> s = new Vector<String>();
		for (;;) {
			String line = rdr.readLine();
			if (line == null) {
				break;
			}
			s.addElement(line);
			// System.out.println("String: add line "+line);
		}
		String[] a = new String[s.size()];
		s.copyInto(a);
		return a;
	}

	/**
	 * Same as above, but ignore first ignoredCols cols
	 */
	public static String[] slurpString(String aString, int ignoredCols) throws IOException {
		BufferedReader rdr = new BufferedReader(new StringReader(aString));
		Vector<String> s = new Vector<String>();
		for (;;) {
			String line = rdr.readLine();
			if (line == null) {
				break;
			}
			s.addElement(line.substring(ignoredCols));
		}
		String[] a = new String[s.size()];
		s.copyInto(a);
		return a;
	}

	public static void main(String[] argv) throws IOException {
		if (argv.length <= 1) {
			System.out.println("Usage: DiffPrint <file1> <file2>");
			System.exit(0);
		}
		String[] a = slurpFile(argv[argv.length - 2]);
		String[] b = slurpFile(argv[argv.length - 1]);
		Diff d = new Diff(a, b);
		boolean edstyle = "-e".equals(argv[0]);
		Diff.change script = d.diff_2(edstyle);
		if (script == null) {
			System.err.println("No differences");
		} else {
			Base p = null;
			if (edstyle) {
				p = new EdPrint(a, b);
			} else {
				p = new NormalPrint(a, b);
			}
			p.print_script(script);
		}
	} // end main

	public static boolean diff(File aFile, String aString) throws IOException {
		String[] a = slurpFile(aFile);
		String[] b = slurpString(aString);
		Diff d = new Diff(a, b);
		Diff.change script = d.diff_2(false);
		if (script == null) {
			// No differences
			return false;
		} else {
			Base p = null;
			p = new NormalPrint(a, b);
			p.print_script(script);
			return true;
		}
	}

	public static boolean diff(String aString, String anOtherString) throws IOException {
		String[] a = slurpString(aString);
		String[] b = slurpString(anOtherString);
		Diff d = new Diff(a, b);
		Diff.change script = d.diff_2(false);
		if (script == null) {
			// No differences
			return false;
		} else {
			Base p = null;
			p = new NormalPrint(a, b);
			p.print_script(script);
			return true;
		}
	}

	public static boolean diff(File aFile, String aString, int ignoredCols) throws IOException {
		String[] a = slurpFile(aFile, ignoredCols);
		String[] b = slurpString(aString, ignoredCols);
		Diff d = new Diff(a, b);
		Diff.change script = d.diff_2(false);
		if (script == null) {
			// No differences
			return false;
		} else {
			Base p = null;
			p = new NormalPrint(a, b);
			p.print_script(script);
			return true;
		}
	}

}
