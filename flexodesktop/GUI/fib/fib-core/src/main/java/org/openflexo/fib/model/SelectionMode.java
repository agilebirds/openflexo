package org.openflexo.fib.model;

import javax.swing.ListSelectionModel;
import javax.swing.tree.TreeSelectionModel;

public enum SelectionMode {
	SingleSelection {
		@Override
		public int getMode() {
			return ListSelectionModel.SINGLE_SELECTION;
		}
	},
	SingleIntervalSelection {
		@Override
		public int getMode() {
			return ListSelectionModel.SINGLE_INTERVAL_SELECTION;
		}
	},
	MultipleIntervalSelection {
		@Override
		public int getMode() {
			return ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
		}
	},
	DiscontiguousTreeSelection {
		@Override
		public int getMode() {
			return TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION;
		}
	};
	;

	public abstract int getMode();
}