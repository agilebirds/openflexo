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
package org.openflexo.inspector;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import org.openflexo.inspector.model.InspectorMapping;
import org.openflexo.inspector.model.InspectorModel;
import org.openflexo.inspector.model.TabModel;
import org.openflexo.inspector.selection.InspectorSelection;
import org.openflexo.inspector.selection.UniqueSelection;
import org.openflexo.inspector.widget.DenaliWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Controller dedicated to inspector: there is only one instance of this
 * controller
 *
 * @author bmangez,sguerin
 */
public class InspectorController implements Observer, AbstractController
{

	private final Logger logger = Logger.getLogger(InspectorController.class.getPackage().getName());

	private Hashtable<String,InspectorModel> _inspectors;

	private Vector<InspectingWidget> _inspectingWidgets;


	public final LabelFontMetrics labelFontMetrics = new LabelFontMetrics(DenaliWidget.DEFAULT_LABEL_FONT);

	private InspectorNotFoundHandler _inspectorNotFoundHandler;

	private Vector<InspectorExceptionHandler> _exceptionHandlers;

	protected XMLMapping _inspectorMapping;

	private InspectorDelegate delegate;
	private HelpDelegate helpDelegate;
	private InspectorWidgetConfiguration inspectorConfiguration;

	private InspectorController _instance;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	protected InspectorController(InspectorDelegate inspectorDelegate, HelpDelegate helpDelegate) {
		this(inspectorDelegate, helpDelegate, null);
	}

	protected InspectorController(InspectorDelegate inspectorDelegate, HelpDelegate helpDelegate, InspectorWidgetConfiguration inspectorConfiguration)
	{
		super();
		this.delegate = inspectorDelegate;
		this.helpDelegate = helpDelegate;
		this.inspectorConfiguration = inspectorConfiguration;
		_inspectors = new Hashtable<String,InspectorModel>();
		_inspectingWidgets = new Vector<InspectingWidget>();

		//_inspectorWindows = new Hashtable<JFrame,InspectorWindow>();

		_exceptionHandlers = new Vector<InspectorExceptionHandler>();
	}

	public InspectorController instance()
	{
		return _instance;
	}

	public void registerAsInstance(InspectorController instance)
	{
		_instance = instance;
	}

	public boolean hasInstance()
	{
		return _instance != null;
	}

	public InspectorWindow createInspectorWindow(JFrame frame)
	{
		InspectorWindow inspectorWindow = new InspectorWindow(frame,this);
		_inspectingWidgets.add(inspectorWindow);
		return inspectorWindow;
	}

	public InspectorTabbedPanel createInspectorTabbedPanel()
	{
		InspectorTabbedPanel inspectorTabbedPanel = new InspectorTabbedPanel(this);
		_inspectingWidgets.add(inspectorTabbedPanel);
		return inspectorTabbedPanel;
	}

	public InspectorSinglePanel createInspectorSinglePanel(TabModel tabModel)
	{
		InspectorSinglePanel inspectorSinglePanel = new InspectorSinglePanel(this,tabModel);
		_inspectingWidgets.add(inspectorSinglePanel);
		return inspectorSinglePanel;
	}

	public InspectorWindow createInspectorWindow(JFrame frame, JMenuBar menuBar)
	{
		return createInspectorWindow(frame);
	}

	public XMLMapping getInspectorMapping()
	{
		return InspectorMapping.getInstance();
	}

	public InspectorModel importInspectorFile(File inspectorFile) throws FileNotFoundException
	{
		InputStream inputStream = null;
		try {

			inputStream = new FileInputStream(inspectorFile);

			return importInspector(inspectorFile.getName(), inputStream);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised during inspector import: " + e+"\nFile path is: "+inspectorFile.getAbsolutePath());
			}
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot close inspector input stream '" + inspectorFile.getAbsolutePath() + "'");
				}
				e.printStackTrace();
			}
		}

		return null;
	}

	public InspectorModel importInspector(String name, InputStream stream)
	{
		try {
			if (getInspectorMapping() != null) {
				InspectorModel inspectorModel = (InspectorModel) XMLDecoder.decodeObjectWithMapping(stream, getInspectorMapping(),
						this);
				importInspector(name, inspectorModel);
				if (logger.isLoggable(Level.FINE)) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Getting this " + XMLCoder.encodeObjectWithMapping(inspectorModel, getInspectorMapping(),StringEncoder.getDefaultInstance()));
					}
				}
				return inspectorModel;
			}
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised during inspector import '" + name + "': " + e);
			}
			e.printStackTrace();
		}

		return null;
	}

	public boolean importInspector(String name, InspectorModel inspector)
	{
		_inspectors.put(name, inspector);
		return true;
	}

	public boolean updateSuperInspectors()
	{
		Iterator it = _inspectors.values().iterator();
		while (it.hasNext()) {
			InspectorModel inspector = (InspectorModel) it.next();
			if (inspector.superInspectorName != null && inspector.superInspectorName != "") {
				InspectorModel superInspector = _inspectors.get(inspector.superInspectorName);
				if (superInspector == null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("WARNING : can't find super inspector named :" + inspector.superInspectorName);
					}
				} else {
					inspector.setSuperInspector(superInspector);
				}
			}
		}
		return true;
	}

	public Enumeration getInspectors()
	{
		return _inspectors.elements();
	}

	// ==========================================================================
	// ============================= Observer ===================================
	// ==========================================================================

	@Override
	public void update(Observable observable, Object selection)
	{
		if (selection instanceof InspectorSelection) {
			InspectorSelection inspectorSelection = (InspectorSelection)selection;
			if (inspectorSelection instanceof UniqueSelection) {
				currentInspectionContext = ((UniqueSelection)inspectorSelection).getInspectionContext();
			}
			for (InspectingWidget i : _inspectingWidgets) {
				i.newSelection(inspectorSelection);
			}
		}
	}

	private Hashtable<String,Object> currentInspectionContext;

	protected Hashtable<String,Object> getCurrentInspectionContext()
	{
		return currentInspectionContext;
	}

	// ==========================================================================
	// ============================= Class Methods
	// ==============================
	// ==========================================================================

	public Element elementFromFilePath(String xmlFilePath) throws Exception
	{
		return parseXMLFile(xmlFilePath).getDocumentElement();
	}

	public Element elementFromFile(File xmlFile) throws Exception
	{
		return parseXMLFile(xmlFile).getDocumentElement();
	}

	public Document parseXMLFile(String xmlFilePath) throws Exception
	{
		File f = new File(xmlFilePath);
		FileInputStream fis = new FileInputStream(f);
		org.xml.sax.InputSource is = new org.xml.sax.InputSource(fis);
		Document d = parseXMLFile(is);
		fis.close();
		fis = null;
		return d;
	}

	public Document parseXMLFile(File f) throws Exception
	{
		FileInputStream fis = new FileInputStream(f);
		org.xml.sax.InputSource is = new org.xml.sax.InputSource(fis);
		Document d = parseXMLFile(is);
		fis.close();
		fis = null;
		return d;
	}

	private Document parseXMLFile(org.xml.sax.InputSource is) throws Exception
	{
		org.apache.xerces.parsers.DOMParser parser = new org.apache.xerces.parsers.DOMParser();
		parser.parse(is);
		Document d = parser.getDocument();
		parser.reset();
		parser = null;
		return d;
	}

	public Element elementFromString(String s) throws Exception
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(s.getBytes());
		org.xml.sax.InputSource is = new org.xml.sax.InputSource(bis);
		Element e = parseXMLFile(is).getDocumentElement();
		bis.close();
		bis = null;
		return e;
	}

	public Hashtable fillParameters(Hashtable<String, String> hash, NodeList list)
	{
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				if (list.item(i).getLocalName().equals("param")) {
					String name = ((Element) list.item(i)).getAttribute("name");
					String val = ((Element) list.item(i)).getAttribute("value");
					if (hash.get(name) != null) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("WARNING those parameters have the same name :" + name);
						}
					}
					hash.put(name, val);
				}
			}
		}
		return hash;
	}

	// ==========================================================================
	// ============================= Instance Methods
	// ===========================
	// ==========================================================================

	public InspectorModel getInspectorModel(String inspectorName)
	{
		if (inspectorName == null) {
			return null;
		}
		InspectorModel answer = _inspectors.get(inspectorName);
		if (answer == null) {
			if (getInspectorNotFoundHandler() != null) {
				getInspectorNotFoundHandler().inspectorNotFound(inspectorName);
				answer = _inspectors.get(inspectorName);
			}
		}
		if (answer == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Sorry, I can't find inspector named : " + inspectorName);
			}
		}
		return answer;
	}

	public InspectorNotFoundHandler getInspectorNotFoundHandler()
	{
		return _inspectorNotFoundHandler;
	}

	public void setInspectorNotFoundHandler(InspectorNotFoundHandler notFoundHandler)
	{
		_inspectorNotFoundHandler = notFoundHandler;
	}

	/**
	 * Adds an InspectorExceptionHandler
	 *
	 * @param exceptionHandler
	 */
	public void addInspectorExceptionHandler(InspectorExceptionHandler exceptionHandler)
	{
		_exceptionHandlers.add(exceptionHandler);
	}

	/**
	 * Remove an InspectorExceptionHandler
	 *
	 * @param exceptionHandler
	 */
	public void removeInspectorExceptionHandler(InspectorExceptionHandler exceptionHandler)
	{
		_exceptionHandlers.remove(exceptionHandler);
	}

	/**
	 * Tries to handle an exception raised during object inspection
	 *
	 * @param inspectable
	 *            the object on which exception was raised
	 * @param propertyName
	 *            the concerned property name
	 * @param value
	 *            the value that raised an exception
	 * @param exception
	 *            the exception that was raised
	 * @return a boolean indicating exception was correctely handled, or not
	 */
	@Override
	public boolean handleException(InspectableObject inspectable, String propertyName, Object value, Throwable exception)
	{
		for (Enumeration en = _exceptionHandlers.elements(); en.hasMoreElements();) {
			InspectorExceptionHandler next = (InspectorExceptionHandler) en.nextElement();
			if (next.handleException(inspectable, propertyName, value, exception)) {
				return true;
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Unexpected exception raised in Inspector. See console for details.");
		}
		exception.printStackTrace();
		return false;
	}

	public void setInspectorWindowsAlwaysOnTop(boolean alwaysOnTop)
	{
		for (InspectingWidget i : _inspectingWidgets) {
			if (i instanceof InspectorWindow) {
				((InspectorWindow)i).setAlwaysOnTop(alwaysOnTop);
			}
		}
	}

	@Override
	public InspectorDelegate getDelegate()
	{
		return delegate;
	}

	@Override
	public HelpDelegate getHelpDelegate()
	{
		return helpDelegate;
	}

	@Override
	public void notifiedInspectedObjectChange(InspectableObject newInspectedObject)
	{
		// Dont care
	}

	// At this level, we only use inspector name as supplied in InspectableObject interface
	// But we may override this method to return a different value given a other context
	public String getInspectorName(InspectableObject object, Hashtable<String, Object> inspectionContext)
	{
		return object.getInspectorName();
	}

	public String getWindowTitle(InspectorModelView currentTabPanel)
	{

		if (currentTabPanel != null) {
			if (currentTabPanel.getInspectedObject() != null
					&& currentTabPanel.getInspectedObject().getInspectorTitle() != null) {
				return currentTabPanel.getInspectedObject().getInspectorTitle();
			}
			return currentTabPanel.getName();
		} else {
			return FlexoLocalization.localizedForKey("inspector");
		}
	}

	public String getNothingToInspectLabel()
	{
		return FlexoLocalization.localizedForKey("nothing_to_inspect");
	}

	public String getMultipleSelectionLabel()
	{
		return FlexoLocalization.localizedForKey("multiple_selection");
	}

	public String getNonApplicableLabel()
	{
		return FlexoLocalization.localizedForKey("non_applicable");
	}

	// Override when required
	@Override
	public boolean isTabPanelVisible(TabModel tab, InspectableObject inspectable)
	{
		if (tab.visibilityContext != null && getCurrentInspectionContext() != null) {
			//System.out.println("isTabPanelVisible() for tab "+tab.name+" visibilityContext="+tab.visibilityContext+" with "+getCurrentInspectionContext());
			StringTokenizer st = new StringTokenizer(tab.visibilityContext,",");
			while (st.hasMoreTokens()) {
				if (getCurrentInspectionContext().get(st.nextToken()) != null) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	@Override
	public final InspectorWidgetConfiguration getConfiguration()
	{
		/*if(inspectorConfiguration == null)
			inspectorConfiguration = new InspectorWidgetConfiguration() {

				public boolean showViewSourceButtonInWysiwyg()
				{
					return false;
				}
			};*/

		return inspectorConfiguration;
	}

}
