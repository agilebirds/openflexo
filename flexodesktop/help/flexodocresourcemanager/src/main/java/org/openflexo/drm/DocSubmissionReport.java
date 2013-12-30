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
package org.openflexo.drm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;
import org.openflexo.xmlcode.XMLSerializable;
import org.xml.sax.SAXException;

public class DocSubmissionReport extends DRMObject implements XMLSerializable {

	private static final Logger logger = Logger.getLogger(DocSubmissionReport.class.getPackage().getName());

	private Vector<DocItemAction> _submissionActions;

	public DocSubmissionReport(DocResourceCenter docResourceCenter) {
		super(docResourceCenter);
		_submissionActions = new Vector<DocItemAction>();
	}

	public DocSubmissionReport(DRMBuilder builder) {
		this(builder.docResourceCenter);
		initializeDeserialization(builder);
	}

	public Vector getSubmissionActions() {
		return _submissionActions;
	}

	public void setSubmissionActions(Vector<DocItemAction> submissionActions) {
		_submissionActions = submissionActions;
	}

	public void addToSubmissionActions(DocItemAction action) {
		_submissionActions.add(action);
	}

	public void removeFromSubmissionActions(DocItemAction action) {
		_submissionActions.remove(action);
	}

	public int size() {
		return _submissionActions.size();
	}

	private static XMLMapping _dsrMapping;

	public static XMLMapping getDSRMapping() {
		if (_dsrMapping == null) {
			StringEncoder.getDefaultInstance()._addConverter(DocItemVersion.Version.converter);
			File dsrModelFile;
			dsrModelFile = new FileResource("Models/DSRModel.xml");
			if (!dsrModelFile.exists()) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("File " + dsrModelFile.getAbsolutePath() + " doesn't exist. Maybe you have to check your paths ?");
				}
				return null;
			} else {
				try {
					_dsrMapping = new XMLMapping(dsrModelFile);
				} catch (InvalidModelException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				} catch (IOException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				} catch (SAXException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				}
			}
		}
		return _dsrMapping;
	}

	public static DocSubmissionReport load(DocResourceCenter drc, File docSubmissionReportFile) {
		DocSubmissionReport report = null;
		StringEncoder.getDefaultInstance()._addConverter(ActionType.actionTypeConverter);
		try {
			FileInputStream in = new FileInputStream(docSubmissionReportFile);
			report = (DocSubmissionReport) XMLDecoder.decodeObjectWithMapping(in, getDSRMapping(), new DRMBuilder(drc));
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return report;
	}

	public boolean save(File docSubmissionReportFile) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(docSubmissionReportFile);
			XMLCoder.encodeObjectWithMapping(this, getDSRMapping(), out);
			out.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void clear() {
		getSubmissionActions().clear();
	}

	/**
	 * Overrides getIdentifier
	 * 
	 * @see org.openflexo.drm.DRMObject#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return "DocSubmissionReport";
	}

}
