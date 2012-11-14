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

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Java wrapper above diff
 * 
 * @author sylvain
 * 
 */
public class ComputeDiff {

	public static class Base {
		protected Base(String[] a, String[] b) {
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
		private String[] file0, file1;

		/**
		 * Divide SCRIPT into pieces by calling HUNKFUN and print each piece with PRINTFUN. Both functions take one arg, an edit script.
		 * 
		 * PRINTFUN takes a subscript which belongs together (with a null link at the end) and prints it.
		 */
		protected DiffReport buildDiffReport(Diff.change script, DiffSource source1, DiffSource source2) {
			DiffReport returned = new DiffReport(source1, source2);

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

				addChange(returned, t);

				/* Reconnect the script so it will all be freed properly.  */
				end.link = next;
			}
			// outfile.flush();
			return returned;
		}

		/**
		 * Called with the tail of the script and returns the last link that belongs together with the start of the tail.
		 */
		private Diff.change hunkfun(Diff.change hunk) {
			return hunk;
		}

		private int first0, last0, first1, last1, deletes, inserts;

		// protected PrintWriter outfile;
		// protected StringBuffer diffResult;

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

		private void analyze_hunk(Diff.change hunk) {
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

		/**
		 * Print a hunk of a normal diff. This is a contiguous portion of a complete edit script, describing changes in consecutive lines.
		 */

		private void addChange(DiffReport report, Diff.change hunk) {

			DiffChange change;

			/* Determine range of line numbers involved in each file.  */
			analyze_hunk(hunk);
			if (deletes == 0 && inserts == 0) {
				return;
			}

			if (inserts == 0) {
				change = new RemovalChange();
			} else if (deletes == 0) {
				change = new AdditionChange();
			} else {
				change = new ModificationChange();
			}

			change.first0 = first0;
			change.first1 = first1;
			change.last0 = last0;
			change.last1 = last1;

			/* Print the lines that the first file has.  */
			if (deletes != 0) {
				StringBuffer removedString = new StringBuffer();
				for (int i = first0; i <= last0; i++) {
					removedString.append(file0[i] + "\n");
				}
				change.removedString = removedString.toString();
			}

			/* Print the lines that the second file has.  */
			if (inserts != 0) {
				StringBuffer addedString = new StringBuffer();
				for (int i = first1; i <= last1; i++) {
					addedString.append(file1[i] + "\n");
				}
				change.addedString = addedString.toString();
			}

			report.addChange(change);
		}

	}

	public static DiffReport diff(DiffSource source, DiffSource anOtherSource) {
		return computeDiff(source, anOtherSource);
	}

	public static DiffReport diff(File aFile, String aString) throws IOException {
		DiffSource source0 = new DiffSource(aFile);
		DiffSource source1 = new DiffSource(aString);
		return computeDiff(source0, source1);
	}

	public static DiffReport diff(String aString, String anOtherString) {
		DiffSource source0 = new DiffSource(aString);
		DiffSource source1 = new DiffSource(anOtherString);
		return computeDiff(source0, source1);
	}

	public static DiffReport diff(File aFile, File anOtherFile) throws IOException {
		DiffSource source0 = new DiffSource(aFile);
		DiffSource source1 = new DiffSource(anOtherFile);
		return computeDiff(source0, source1);
	}

	public static DiffReport diff(File aFile, String aString, int ignoredCols) throws IOException {
		DiffSource source0 = new DiffSource(aFile, ignoredCols);
		DiffSource source1 = new DiffSource(aString, ignoredCols);
		return computeDiff(source0, source1);
	}

	public static DiffReport diff(String aString, String anOtherString, int ignoredCols) throws IOException {
		DiffSource source0 = new DiffSource(aString, ignoredCols);
		DiffSource source1 = new DiffSource(anOtherString, ignoredCols);
		return computeDiff(source0, source1);
	}

	public static DiffReport diff(File aFile, File anOtherFile, int ignoredCols) throws IOException {
		DiffSource source0 = new DiffSource(aFile, ignoredCols);
		DiffSource source1 = new DiffSource(anOtherFile, ignoredCols);
		return computeDiff(source0, source1);
	}

	public static DiffReport diff(DiffSource source, DiffSource anOtherSource, DelimitingMethod method) {
		return computeDiff(source, anOtherSource);
	}

	public static DiffReport diff(File aFile, String aString, DelimitingMethod method) throws IOException {
		DiffSource source0 = new DiffSource(aFile, method);
		DiffSource source1 = new DiffSource(aString, method);
		return computeDiff(source0, source1);
	}

	public static DiffReport diff(String aString, String anOtherString, DelimitingMethod method) {
		DiffSource source0 = new DiffSource(aString, method);
		DiffSource source1 = new DiffSource(anOtherString, method);
		return computeDiff(source0, source1);
	}

	public static DiffReport diff(File aFile, File anOtherFile, DelimitingMethod method) throws IOException {
		DiffSource source0 = new DiffSource(aFile, method);
		DiffSource source1 = new DiffSource(anOtherFile, method);
		return computeDiff(source0, source1);
	}

	public static DiffReport diff(File aFile, String aString, DelimitingMethod method, int ignoredCols) throws IOException {
		DiffSource source0 = new DiffSource(aFile, method, ignoredCols);
		DiffSource source1 = new DiffSource(aString, method, ignoredCols);
		return computeDiff(source0, source1);
	}

	public static DiffReport diff(String aString, String anOtherString, DelimitingMethod method, int ignoredCols) throws IOException {
		DiffSource source0 = new DiffSource(aString, method, ignoredCols);
		DiffSource source1 = new DiffSource(anOtherString, method, ignoredCols);
		return computeDiff(source0, source1);
	}

	public static DiffReport diff(File aFile, File anOtherFile, DelimitingMethod method, int ignoredCols) throws IOException {
		DiffSource source0 = new DiffSource(aFile, method, ignoredCols);
		DiffSource source1 = new DiffSource(anOtherFile, method, ignoredCols);
		return computeDiff(source0, source1);
	}

	private static DiffReport computeDiff(DiffSource source0, DiffSource source1) {
		Diff d = new Diff(source0.getSignificativeTokens(), source1.getSignificativeTokens());
		Diff.change script = d.diff_2(false);
		if (script == null) {
			// No differences
			return new DiffReport(source0, source1);
		} else {
			Base p = null;
			p = new Base(source0.getSignificativeTokens(), source1.getSignificativeTokens());
			return p.buildDiffReport(script, source0, source1);
		}
	}

	public static class DiffReport {
		private Vector<DiffChange> changes;
		private DiffSource source0;
		private DiffSource source1;

		public void print(boolean isLeftOriented) {
			Enumeration<DiffChange> en = getChanges().elements();
			while (en.hasMoreElements()) {
				DiffChange change = en.nextElement();
				System.out.println(change.toNiceString(isLeftOriented));
				System.out.println("\tADDED:" + change.addedString);
				System.out.println("\tREMOVED:" + change.removedString);
			}
		}

		protected DiffReport(DiffSource source0, DiffSource source1) {
			changes = new Vector<DiffChange>();
			this.source0 = source0;
			this.source1 = source1;
		}

		protected void addChange(DiffChange change) {
			changes.add(change);
		}

		public Vector<DiffChange> getChanges() {
			return changes;
		}

		@Override
		public String toString() {
			if (changes.size() == 0) {
				return "DiffReport: no changes";
			} else {
				StringBuffer returned = new StringBuffer();
				for (DiffChange c : getChanges()) {
					returned.append(c + "\n");
				}
				return returned.toString();
			}
		}

		public String[] getInput0() {
			return source0.getSignificativeTokens();
		}

		public String[] getInput1() {
			return source1.getSignificativeTokens();
		}

		public DiffChange changeBefore(DiffChange change) {
			int index = changes.indexOf(change);
			if (index >= 1) {
				return changes.get(index - 1);
			}
			return null;
		}

		public int getAdditionChangeCount() {
			int returned = 0;
			for (DiffChange change : getChanges()) {
				if (change instanceof AdditionChange) {
					returned++;
				}
			}
			return returned;
		}

		public int getRemovalChangeCount() {
			int returned = 0;
			for (DiffChange change : getChanges()) {
				if (change instanceof RemovalChange) {
					returned++;
				}
			}
			return returned;
		}

		public int getModificationChangeCount() {
			int returned = 0;
			for (DiffChange change : getChanges()) {
				if (change instanceof ModificationChange) {
					returned++;
				}
			}
			return returned;
		}

	}

	public abstract static class DiffChange {
		protected int first0, last0, first1, last1;
		protected String addedString;
		protected String removedString;

		public int getFirst0() {
			return first0;
		}

		public int getFirst1() {
			return first1;
		}

		public int getLast0() {
			return last0;
		}

		public int getLast1() {
			return last1;
		}

		public abstract String toNiceString(boolean isLeftOriented);

		public void setFirst0(int first0) {
			this.first0 = first0;
		}

		public void setFirst1(int first1) {
			this.first1 = first1;
		}

		public void setLast0(int last0) {
			this.last0 = last0;
		}

		public void setLast1(int last1) {
			this.last1 = last1;
		}

	}

	public static class AdditionChange extends DiffChange {
		@Override
		public String toString() {
			return ">>>>>> ADDITION " + first0 + "," + last0 + " " + first1 + "," + last1/*+" of TEXT:\n"+addedString*/;
		}

		@Override
		public String toNiceString(boolean isLeftOriented) {
			return (isLeftOriented ? "REMOVAL" : "ADDITION") + " " + first0 + "," + last0 + " " + first1 + "," + last1;
		}

	}

	public static class RemovalChange extends DiffChange {
		@Override
		public String toString() {
			return ">>>>>> REMOVAL " + first0 + "," + last0 + " " + first1 + "," + last1/*+" of TEXT:\n"+removedString*/;
		}

		@Override
		public String toNiceString(boolean isLeftOriented) {
			return (isLeftOriented ? "ADDITION" : "REMOVAL") + " " + first0 + "," + last0 + " " + first1 + "," + last1;
		}
	}

	public static class ModificationChange extends DiffChange {
		@Override
		public String toString() {
			return ">>>>>> MODIFICATION " + first0 + "," + last0 + " " + first1 + "," + last1/*+" of TEXT:\n"+removedString+"BY:\n"+addedString*/;
		}

		@Override
		public String toNiceString(boolean isLeftOriented) {
			return "MODIFICATION " + first0 + "," + last0 + " " + first1 + "," + last1;
		}

		public String toNiceStringDebugVersion(boolean isLeftOriented) {
			StringBuffer removedStringBuffer = new StringBuffer();
			removedStringBuffer.append("[");
			for (int i = 0; i < removedString.length(); i++) {
				removedStringBuffer.append(" " + (int) removedString.charAt(i));
			}
			removedStringBuffer.append("]" + "(" + removedString.length() + ")");
			StringBuffer addedStringBuffer = new StringBuffer();
			addedStringBuffer.append("[");
			for (int i = 0; i < addedString.length(); i++) {
				addedStringBuffer.append(" " + (int) addedString.charAt(i));
			}
			addedStringBuffer.append("]" + "(" + addedString.length() + ")");

			return "MODIFICATION " + first0 + "," + last0 + " " + first1 + "," + last1 + " of TEXT:\n" + removedStringBuffer + "BY:\n"
					+ addedStringBuffer;
		}
	}

}
