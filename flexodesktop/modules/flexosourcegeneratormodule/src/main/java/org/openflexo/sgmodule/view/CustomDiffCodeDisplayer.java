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
package org.openflexo.sgmodule.view;

import java.awt.BorderLayout;
import java.util.logging.Logger;

import org.openflexo.diff.ComputeDiff;
import org.openflexo.foundation.rm.cg.ASCIIFile;
import org.openflexo.foundation.rm.cg.ASCIIFileResource;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.rm.cg.ContentSource.ContentSourceType;
import org.openflexo.foundation.rm.cg.WOFile;
import org.openflexo.foundation.rm.cg.WOFileResource;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.swing.diff.DiffPanel;
import org.openflexo.toolbox.TokenMarkerStyle;

public class CustomDiffCodeDisplayer extends DiffCodeDisplayer {

	private static final Logger logger = Logger.getLogger(CustomDiffCodeDisplayer.class.getPackage().getName());

	private ContentSource _left;
	private ContentSource _right;

	public CustomDiffCodeDisplayer(GenerationAvailableFileResource resource, ContentSource left, ContentSource right,
			SGController controller) {
		super(resource, controller);
		_left = left;
		_right = right;
		update();
	}

	@Override
	protected DiffCodeDisplayerComponent buildComponent() {
		_component = null;

		if (getResource() instanceof ASCIIFileResource) {
			_component = new CustomASCIIFileDiffCodeDisplayer();
		} else if (getResource() instanceof WOFileResource) {
			_component = new CustomWOFileDiffCodeDisplayer();
		}

		if (_component == null) {
			_component = new ErrorPanel();
		}

		return (DiffCodeDisplayerComponent) _component;
	}

	protected class CustomASCIIFileDiffCodeDisplayer extends ASCIIFileDiffCodeDisplayer {
		@Override
		public void update() {
			removeAll();
			if (getLeft() == null || getRight() == null) {
				return;
			}
			_diffReport = ComputeDiff.diff(getLeft(), getRight());
			String leftLabel = _left != null ? _left.getStringRepresentation() : "null";
			String rightLabel = _right != null ? _right.getStringRepresentation() : "null";
			boolean isLeftOriented = false;
			_diffPanel = new DiffPanel(_diffReport, getTokenMarkerStyle(), leftLabel, rightLabel,
					FlexoLocalization.localizedForKey("no_structural_changes"), isLeftOriented);
			add(_diffPanel, BorderLayout.CENTER);
			validate();
		}

		public String getLeft() {
			if (_left == null) {
				return "";
			}
			if (getResource() instanceof ASCIIFileResource) {
				if (_left.getType() == ContentSourceType.PureGeneration) {
					return ((ASCIIFileResource) getResource()).getCurrentGeneration();
				}
				return ((ASCIIFile) getResourceData()).getContent(_left);
			}
			return "Inconsistent data";
		}

		public String getRight() {
			if (_right == null) {
				return "";
			}
			if (getResource() instanceof ASCIIFileResource) {
				if (_right.getType() == ContentSourceType.PureGeneration) {
					return ((ASCIIFileResource) getResource()).getCurrentGeneration();
				}
				return ((ASCIIFile) getResourceData()).getContent(_right);
			}
			return "Inconsistent data";
		}
	}

	protected class CustomWOFileDiffCodeDisplayer extends WOFileDiffCodeDisplayer {
		@Override
		public void update() {
			removeAll();
			htmlDisplayer = new CustomASCIIFileDiffCodeDisplayer() {
				@Override
				protected TokenMarkerStyle getTokenMarkerStyle() {
					return TokenMarkerStyle.HTML;
				}

				@Override
				public String getLeft() {
					if (_left == null) {
						return "";
					}
					if (getResource() instanceof WOFileResource) {
						if (_left.getType() == ContentSourceType.PureGeneration) {
							return ((WOFileResource) getResource()).getCurrentHTMLGeneration();
						}
						return ((WOFile) getResourceData()).getHTMLFile().getContent(_left);
					}
					return "Inconsistent data";
				}

				@Override
				public String getRight() {
					if (_right == null) {
						return "";
					}
					if (getResource() instanceof WOFileResource) {
						if (_right.getType() == ContentSourceType.PureGeneration) {
							return ((WOFileResource) getResource()).getCurrentHTMLGeneration();
						}
						return ((WOFile) getResourceData()).getHTMLFile().getContent(_right);
					}
					return "Inconsistent data";
				}
			};
			wodDisplayer = new CustomASCIIFileDiffCodeDisplayer() {
				@Override
				protected TokenMarkerStyle getTokenMarkerStyle() {
					return TokenMarkerStyle.WOD;
				}

				@Override
				public String getLeft() {
					if (_left == null) {
						return "";
					}
					if (getResource() instanceof WOFileResource) {
						if (_left.getType() == ContentSourceType.PureGeneration) {
							return ((WOFileResource) getResource()).getCurrentWODGeneration();
						}
						return ((WOFile) getResourceData()).getWODFile().getContent(_left);
					}
					return "Inconsistent data";
				}

				@Override
				public String getRight() {
					if (_right == null) {
						return "";
					}
					if (getResource() instanceof WOFileResource) {
						if (_right.getType() == ContentSourceType.PureGeneration) {
							return ((WOFileResource) getResource()).getCurrentWODGeneration();
						}
						return ((WOFile) getResourceData()).getWODFile().getContent(_right);
					}
					return "Inconsistent data";
				}
			};
			wooDisplayer = new CustomASCIIFileDiffCodeDisplayer() {
				@Override
				protected TokenMarkerStyle getTokenMarkerStyle() {
					return TokenMarkerStyle.WOD;
				}

				@Override
				public String getLeft() {
					if (_left == null) {
						return "";
					}
					if (getResource() instanceof WOFileResource) {
						if (_left.getType() == ContentSourceType.PureGeneration) {
							return ((WOFileResource) getResource()).getCurrentWOOGeneration();
						}
						return ((WOFile) getResourceData()).getWOOFile().getContent(_left);
					}
					return "Inconsistent data";
				}

				@Override
				public String getRight() {
					if (_right == null) {
						return "";
					}
					if (getResource() instanceof WOFileResource) {
						if (_right.getType() == ContentSourceType.PureGeneration) {
							return ((WOFileResource) getResource()).getCurrentWOOGeneration();
						}
						return ((WOFile) getResourceData()).getWOOFile().getContent(_right);
					}
					return "Inconsistent data";
				}
			};
			add(".html", htmlDisplayer);
			add(".wod", wodDisplayer);
			add(".woo", wooDisplayer);
			validate();
		}

	}

}
