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
package org.openflexo.inspector.fibconverter;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Vector;

import org.openflexo.antar.binding.BindingValue;
import org.openflexo.antar.binding.KeyValueLibrary;
import org.openflexo.antar.binding.KeyValueProperty;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.DefaultExpressionParser;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.ExpressionTransformer;
import org.openflexo.antar.expr.TransformException;
import org.openflexo.antar.expr.Variable;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.BorderLayoutConstraints;
import org.openflexo.fib.model.BorderLayoutConstraints.BorderLayoutLocation;
import org.openflexo.fib.model.DataBinding;
import org.openflexo.fib.model.FIBCheckBox;
import org.openflexo.fib.model.FIBCheckBoxColumn;
import org.openflexo.fib.model.FIBColor;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomAssignment;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.fib.model.FIBCustomColumn;
import org.openflexo.fib.model.FIBDropDown;
import org.openflexo.fib.model.FIBDropDownColumn;
import org.openflexo.fib.model.FIBFile;
import org.openflexo.fib.model.FIBFile.FileMode;
import org.openflexo.fib.model.FIBFont;
import org.openflexo.fib.model.FIBHtmlEditor;
import org.openflexo.fib.model.FIBIconColumn;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBLabel.Align;
import org.openflexo.fib.model.FIBLabelColumn;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBNumber.NumberType;
import org.openflexo.fib.model.FIBNumberColumn;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.FIBParameter;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTableAction;
import org.openflexo.fib.model.FIBTableAction.FIBAddAction;
import org.openflexo.fib.model.FIBTableAction.FIBCustomAction;
import org.openflexo.fib.model.FIBTableAction.FIBRemoveAction;
import org.openflexo.fib.model.FIBTableColumn;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.FIBTextFieldColumn;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.model.validation.ValidationError;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.inspector.model.InspectorModel;
import org.openflexo.inspector.model.PropertyListAction;
import org.openflexo.inspector.model.PropertyListColumn;
import org.openflexo.inspector.model.PropertyListModel;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.model.TabModel;
import org.openflexo.inspector.widget.DenaliWidget;
import org.openflexo.inspector.widget.DoubleWidget;
import org.openflexo.inspector.widget.DropDownWidget;
import org.openflexo.inspector.widget.IntegerWidget;
import org.openflexo.inspector.widget.LabelWidget;
import org.openflexo.inspector.widget.TextAreaWidget;
import org.openflexo.inspector.widget.TextFieldWidget;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.DuplicateSerializationIdentifierException;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;

public class FIBConverter {

	private static int errors = 0;
	private static String inspectorName;

	private static String[] remaindingArgs(String[] args, int from) {
		String[] returned = new String[args.length - from];
		for (int i = from; i < args.length; i++) {
			returned[i - from] = args[i];
		}
		return returned;
	}

	/**
	 * Usage FIBConverter -f file [-o output_file] -fib fib_class_name [package_prefix 1 [package_prefix 2] ... ] FIBConverter -d input_dir
	 * -o output_dir -fib fib_class_name [package_prefix 1 [package_prefix 2] ... ]
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			if (args[0].equals("-f")) {
				String inputFilePath = null;
				String outputFilePath = null;
				String fibClassName = null;
				inputFilePath = args[1];
				if (args[2].equals("-o")) {
					outputFilePath = args[3];
					fibClassName = args[5];
				} else {
					fibClassName = args[3];
				}
				File inputFile = new FileResource(inputFilePath);
				File outputFile = (outputFilePath != null ? new FileResource(outputFilePath) : null);
				convert(inputFile, outputFile, fibClassName, remaindingArgs(args, args[2].equals("-o") ? 6 : 4));
				exit(true);
			} else if (args[0].equals("-d")) {
				String inputDirPath = args[1];
				String outputDirPath = args[3];
				String fibClassName = args[5];
				String[] packagePrefixes = remaindingArgs(args, 6);
				File inputDir = new FileResource(inputDirPath);
				File outputDir = new FileResource(outputDirPath);
				System.out.println("Convert directory " + inputDir.getAbsolutePath() + " to " + outputDir.getAbsolutePath());
				;
				for (File inputFile : inputDir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".inspector");
					}
				})) {
					File outputFile = new File(outputDir, inputFile.getName());
					// System.out.println("Convert "+inputFile.getAbsolutePath()+" to "+outputFile.getAbsolutePath());
					convert(inputFile, outputFile, fibClassName, packagePrefixes);
				}
				exit(true);
			}
		}
		System.out.println("Usage FIBConverter -f file [-o output_file] -fib fib_class_name [package_prefix 1 [package_prefix 2] ... ]");
		System.out.println("      FIBConverter -d input_dir -o output_dir -fib fib_class_name [package_prefix 1 [package_prefix 2] ... ]");
	}

	// static int prout = 0;

	public static void convert(File inputFile, File outputFile, String fibClassName, String... packagePrefixes) {

		FIBPanel newInspector = null;

		try {
			/*
			 * prout++; if (prout > 10) { System.out.println("On s'arrete la");
			 * exit(-1); }
			 */
			InspectorModel im = importInspectorFile(inputFile);
			Class dataClass = null;
			if (im.inspectedClassName != null && im.inspectedClassName.equals("ignore")) {
				System.out.println("Ignore inspector " + inputFile.getName());
				return;
			} else if (im.inspectedClassName != null && !im.inspectedClassName.equals("ignore")) {
				try {
					dataClass = Class.forName(im.inspectedClassName);
				} catch (ClassNotFoundException e) {
					System.out.println("Convert " + inputFile.getAbsolutePath() + " to "
							+ (outputFile != null ? outputFile.getAbsolutePath() : "console"));
					error("Not found class: " + im.inspectedClassName);
					return;
				}
			} else {
				dataClass = searchClass(inputFile, packagePrefixes);
				if (dataClass == null) {
					System.out.println("Convert " + inputFile.getAbsolutePath() + " to "
							+ (outputFile != null ? outputFile.getAbsolutePath() : "console"));
					error("Not found class for file: " + inputFile.getName());
					return;
				}
			}
			System.out.println("Convert " + inputFile.getAbsolutePath() + " to "
					+ (outputFile != null ? outputFile.getAbsolutePath() : "console") + " for class " + dataClass);
			inspectorName = inputFile.getName();
			newInspector = makeFIB(im, fibClassName, dataClass);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (outputFile != null) {
			try {
				FileOutputStream fos = new FileOutputStream(outputFile);
				XMLCoder.encodeObjectWithMapping(newInspector, FIBLibrary.getFIBMapping(), fos, StringEncoder.getDefaultInstance());
				fos.close();
			} catch (InvalidObjectSpecificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AccessorInvocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DuplicateSerializationIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else {
			try {
				System.out.println(XMLCoder.encodeObjectWithMapping(newInspector, FIBLibrary.getFIBMapping(),
						StringEncoder.getDefaultInstance()));
			} catch (InvalidObjectSpecificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AccessorInvocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DuplicateSerializationIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static Class searchClass(File inputFile, String... packagePrefixes) {
		String className = inputFile.getName().substring(0, inputFile.getName().indexOf(".inspector"));
		// System.out.println("Search "+className);
		for (String packagePrefix : packagePrefixes) {
			// System.out.println("Prefix: "+packagePrefix);
			try {
				return Class.forName(packagePrefix + "." + className);
			} catch (ClassNotFoundException e) {
				// Not this one
			}
		}
		error("Not found class: " + className);
		return null;
	}

	private static FIBPanel makeFIB(InspectorModel im, String fibClassName, final Class dataClass) {
		Vector<DataBinding> bindings = new Vector<DataBinding>();

		Class fibClass;
		FIBPanel newInspector;
		try {
			fibClass = Class.forName(fibClassName);
			newInspector = (FIBPanel) fibClass.getDeclaredConstructors()[0].newInstance(null);
		} catch (ClassNotFoundException e1) {
			error("Not found class: " + fibClassName);
			exit(false);
			return null;
		} catch (IllegalArgumentException e) {
			error("IllegalArgumentException while instanciating class: " + fibClassName);
			return null;
		} catch (SecurityException e) {
			error("SecurityException while instanciating class: " + fibClassName);
			return null;
		} catch (InstantiationException e) {
			error("InstantiationException while instanciating class: " + fibClassName);
			return null;
		} catch (IllegalAccessException e) {
			error("IllegalAccessException while instanciating class: " + fibClassName);
			return null;
		} catch (InvocationTargetException e) {
			error("InvocationTargetException while instanciating class: " + fibClassName);
			return null;
		}

		newInspector.setName("Inspector");
		newInspector.setDataClass(dataClass);
		newInspector.setLayout(Layout.border);
		newInspector.setFont(new Font("SansSerif", Font.PLAIN, 12));
		newInspector.addToParameters(new FIBParameter("title", im.title));
		try {
			newInspector.setControllerClass(Class.forName("org.openflexo.inspector.FIBInspectorController"));
		} catch (ClassNotFoundException e1) {
			error("Not found class: org.openflexo.inspector.FIBInspectorController");
			// exit(false);
		}

		// Retrieve all localized
		FlexoLocalization.localizedForKey("flexo");
		newInspector.retrieveFIBLocalizedDictionary().beginSearchNewLocalizationEntries();

		FIBTabPanel tabPane = new FIBTabPanel();
		tabPane.setName("Tab");
		// tabPane.setConstraints(new BorderLayoutConstraints(BorderLayoutLocation.center));
		newInspector.addToSubComponents(tabPane, new BorderLayoutConstraints(BorderLayoutLocation.center));

		for (int i : im.getTabs().keySet()) {
			final TabModel tm = im.getTabs().get(i);
			FIBTab tab = new FIBTab();
			tab.setFont(new Font("SansSerif", Font.PLAIN, 11));
			tab.setTitle(tm.name);
			tab.setName(makeName(tm.name) + "Tab");
			tab.setLayout(Layout.twocols);
			tab.setIndex(i);
			tabPane.addToSubComponents(tab);
			searchLocalized(newInspector, tm.name);
			if (StringUtils.isNotEmpty(tm.visibilityContext)) {
				tab.setVisible(new DataBinding("controller.displayInspectorTabForContext('" + tm.visibilityContext + "')"));
				bindings.add(tab.getVisible());
			}
			int index = 0;
			for (PropertyModel pm : tm.getOrderedProperties()) {

				FIBWidget widget = null;

				if (pm instanceof PropertyListModel) {

					// Build table
					widget = makeTable((PropertyListModel) pm, dataClass, bindings);

				} else {
					// Build "normal" widget
					widget = makeWidget(pm, bindings, dataClass);
				}

				// When not null, proceed to common conversions
				if (widget != null) {

					// Handle formatter
					if (pm.hasValueForParameter("formatter")) {
						KeyValueProperty kvp = KeyValueLibrary.getKeyValueProperty(dataClass, pm.name);
						if (kvp != null && kvp.getType() instanceof Class && ((Class) kvp.getType()).isEnum()) {
							// In old inspector, enums were lowered cased and localized
							widget.setFormat(new DataBinding("object." + pm.getValueForParameter("formatter") + ".toLowerCase"));
							bindings.add(widget.getFormat());
							widget.setLocalize(true);
							if (pm.getValueForParameter("formatter").equals("name")) {
								for (Object c : ((Class) kvp.getType()).getEnumConstants()) {
									searchLocalized(newInspector, ((Enum) c).name().toLowerCase());
								}
							}
						} else {
							widget.setFormat(new DataBinding("object." + pm.getValueForParameter("formatter")));
							bindings.add(widget.getFormat());
						}
					}
					// Handle formatter (declared as format in some cases)
					if (pm.hasValueForParameter("format")) {
						KeyValueProperty kvp = KeyValueLibrary.getKeyValueProperty(dataClass, pm.name);
						if (kvp != null && kvp.getType() instanceof Class && ((Class) kvp.getType()).isEnum()) {
							// In old inspector, enums were lowered cased and localized
							widget.setFormat(new DataBinding("object." + pm.getValueForParameter("format") + ".toLowerCase"));
							bindings.add(widget.getFormat());
							widget.setLocalize(true);
							if (pm.getValueForParameter("format").equals("name")) {
								for (Object c : ((Class) kvp.getType()).getEnumConstants()) {
									searchLocalized(newInspector, ((Enum) c).name().toLowerCase());
								}
							}
						} else {
							widget.setFormat(new DataBinding("object." + pm.getValueForParameter("format")));
							bindings.add(widget.getFormat());
						}
					}

					// Handle conditionals
					Expression conditional = null;
					if (pm.conditional != null) {
						DefaultExpressionParser parser = new DefaultExpressionParser();
						String hackDeLaMort = null;
						try {
							hackDeLaMort = ToolBox.replaceStringByStringInString(" OR ", " | ", pm.conditional);
							hackDeLaMort = ToolBox.replaceStringByStringInString(" or ", " | ", hackDeLaMort);
							hackDeLaMort = ToolBox.replaceStringByStringInString(" AND ", " & ", hackDeLaMort);
							hackDeLaMort = ToolBox.replaceStringByStringInString(" and ", " & ", hackDeLaMort);
							// hackDeLaMort = ToolBox.replaceStringByStringInString("!=", " != ", hackDeLaMort);
							// hackDeLaMort = ToolBox.replaceStringByStringInString("=", " = ", hackDeLaMort);
							// System.out.println("Converted "+pm.conditional+" to "+hackDeLaMort);
							Expression condition = parser.parse(hackDeLaMort);
							// System.out.println("Expression="+condition);
							try {
								conditional = condition.transform(new ExpressionTransformer() {
									@Override
									public Expression performTransformation(Expression e) throws TransformException {
										if (e instanceof Variable) {
											Variable value = (Variable) e;
											if (tm.getPropertyNamed(value.getName()) != null)
												return new Variable("data." + value.getName());
											else {
												Type accessedType = getAccessedType(value.getName(), dataClass);
												// KeyValueProperty kvp = KeyValueLibrary.getKeyValueProperty(dataClass,value.getValue());
												// if (kvp != null) {
												if (accessedType != null) {
													return new Variable("data." + value.getName());
												} else {
													return new Variable('"' + value.getName() + '"');
												}
											}
										}
										return e;
									}
								});
							} catch (TransformException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							/*conditional = condition.evaluate(new EvaluationContext(parser.getConstantFactory(),
									new org.openflexo.antar.expr.parser.ExpressionParser.VariableFactory() {
										@Override
										public Expression makeVariable(Word value) {
											if (tm.getPropertyNamed(value.getValue()) != null)
												return new Variable("data." + value.getValue());
											else {
												Type accessedType = getAccessedType(value.getValue(), dataClass);
												// KeyValueProperty kvp = KeyValueLibrary.getKeyValueProperty(dataClass,value.getValue());
												// if (kvp != null) {
												if (accessedType != null) {
													return new Variable("data." + value.getValue());
												} else {
													return new Variable('"' + value.getValue() + '"');
												}
											}
										}
									}, parser.getFunctionFactory()));*/
							// System.out.println("conditional="+conditional);
							// System.out.println("conditional="+conditional);
							widget.setVisible(new DataBinding(conditional.toString()));
							bindings.add(widget.getVisible());
						} catch (org.openflexo.antar.expr.oldparser.ParseException e) {
							error("Cound not parse: " + hackDeLaMort);
							e.printStackTrace();
						}/* catch (TypeMismatchException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							}*/
					}

					// Handle display label
					boolean displayLabel = true;
					if (pm.hasValueForParameter(DenaliWidget.DISPLAY_LABEL)) {
						displayLabel = pm.getBooleanValueForParameter(DenaliWidget.DISPLAY_LABEL);
					}
					if (displayLabel && !(widget instanceof FIBLabel)) {
						// Build label
						FIBLabel label = new FIBLabel();
						label.setLabel(pm.label);
						label.setLocalize(true);
						label.setName(makeName(pm.name) + "Label");
						// Search localized
						searchLocalized(newInspector, pm.label);
						if (conditional != null) {
							label.setVisible(new DataBinding(conditional.toString()));
							bindings.add(label.getVisible());
						}

						if (pm instanceof PropertyListModel) {
							label.setAlign(org.openflexo.fib.model.FIBLabel.Align.center);
							tab.addToSubComponents(label, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false, index++));
						} else {
							tab.addToSubComponents(label, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false, index++));
						}
						label.getConstraints().setIndex(pm.constraint * 2);
						label.setIndex(pm.constraint * 2);
					}

					// Handle horizontal and vertical expansions
					boolean expandHorizontally = false;
					if (pm.hasValueForParameter(DenaliWidget.EXPAND_HORIZONTALLY)) {
						expandHorizontally = pm.getBooleanValueForParameter(DenaliWidget.EXPAND_HORIZONTALLY);
					} else {
						// Default widget to expand horizontally
						if (pm instanceof PropertyListModel) {
							expandHorizontally = true;
						} else if (pm.getWidget().equalsIgnoreCase(DenaliWidget.TEXT_FIELD)
								|| pm.getWidget().equalsIgnoreCase(DenaliWidget.READ_ONLY_TEXT_FIELD)
								|| pm.getWidget().equalsIgnoreCase(DenaliWidget.TEXT_AREA)
								|| pm.getWidget().equalsIgnoreCase(DenaliWidget.READ_ONLY_TEXT_AREA)
								|| pm.getWidget().equalsIgnoreCase(DenaliWidget.LABEL)
								|| pm.getWidget().equalsIgnoreCase(DenaliWidget.CUSTOM)
								&& !((FIBCustom) widget).getComponentClass().getName()
										.equals("org.openflexo.fge.view.widget.FIBForegroundStyleSelector")
								&& !((FIBCustom) widget).getComponentClass().getName()
										.equals("org.openflexo.fge.view.widget.FIBBackgroundStyleSelector")
								&& !((FIBCustom) widget).getComponentClass().getName()
										.equals("org.openflexo.fge.view.widget.FIBTextStyleSelector")
								&& !((FIBCustom) widget).getComponentClass().getName()
										.equals("org.openflexo.fge.view.widget.FIBShadowStyleSelector")) {
							expandHorizontally = true;
						}
					}
					boolean expandVertically = false;
					if (pm.hasValueForParameter(DenaliWidget.EXPAND_VERTICALLY)) {
						expandVertically = pm.getBooleanValueForParameter(DenaliWidget.EXPAND_VERTICALLY);
					} else {
						// Default widget to expand vertically
						if (pm instanceof PropertyListModel) {
							expandVertically = true;
						} else if (pm.getWidget().equalsIgnoreCase(DenaliWidget.TEXT_AREA)
								|| pm.getWidget().equalsIgnoreCase(DenaliWidget.READ_ONLY_TEXT_AREA)) {
							expandVertically = true;
						}
					}

					// Finally append widget
					tab.addToSubComponents(widget, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, expandHorizontally,
							expandVertically, index++));

					widget.getConstraints().setIndex(pm.constraint * 2 + 1);
					widget.setIndex(pm.constraint * 2 + 1);
				}
			}
		}

		// Retrieve all localized
		newInspector.retrieveFIBLocalizedDictionary().endSearchNewLocalizationEntries();

		// Check all widgets
		for (FIBComponent component : newInspector.retrieveAllSubComponents()) {
			if (component instanceof FIBCustom) {
				FIBCustom custom = (FIBCustom) component;
				Vector<FIBCustomAssignment> toRemove = new Vector<FIBCustomAssignment>();
				for (FIBCustomAssignment a : custom.getAssignments()) {
					if (a.isMandatory() && !a.getValue().isValid()) {
						error("Custom component " + custom.getComponentClass().getSimpleName() + " does not define mandatory "
								+ a.getVariable() + " assignment"
								+ (!a.getValue().isSet() ? " (value not defined) " : " (invalid binding " + a.getValue() + ")"));
						if (a.getValue().isSet()) {
							a.getValue().getBinding().debugIsBindingValid();
						}
					}
					if (!a.isMandatory() && !a.getValue().isSet()) {
						toRemove.add(a);
					}
				}
				for (FIBCustomAssignment a : toRemove) {
					custom.removeFromAssignments(a);
				}
			} else if (component instanceof FIBTable) {
				FIBTable table = (FIBTable) component;
				for (FIBTableColumn column : table.getColumns()) {
					if (column instanceof FIBCustomColumn) {
						FIBCustomColumn c = (FIBCustomColumn) column;
						Vector<FIBCustomColumn.FIBCustomAssignment> toRemove = new Vector<FIBCustomColumn.FIBCustomAssignment>();
						for (FIBCustomColumn.FIBCustomAssignment a : c.getAssignments()) {
							if (a.isMandatory() && !a.getValue().isValid()) {
								error("Custom column component " + c.getComponentClass().getSimpleName() + " does not define mandatory "
										+ a.getVariable() + " assignment");
							}
							if (!a.isMandatory() && !a.getValue().isSet()) {
								toRemove.add(a);
							}
						}
						for (FIBCustomColumn.FIBCustomAssignment a : toRemove) {
							c.removeFromAssignments(a);
						}
					}
				}
			}

		}

		// Check all bindings
		newInspector.updateBindingModel();
		for (DataBinding binding : bindings) {
			// Following cases are well known (inspector
			// ShapeGraphicalRepresentation
			// and ConnectorGraphicalRepresentation have to be redesigned
			if (binding.toString().startsWith("data.shape")) {
				continue;
			}
			if (binding.toString().startsWith("data.connector")) {
				continue;
			}

			// Those bindings are not correct either, but this wil be fixed later
			if (inspectorName.equals("Artefact.inspector")) {
				if (binding.toString().equals("data.textAlignment")) {
					continue;
				}
			}
			if (inspectorName.equals("BoundingBox.inspector")) {
				if (binding.toString().equals("data.textAlignment")) {
					continue;
				}
				if (binding.toString().equals("data.dashStyle")) {
					continue;
				}
			}
			if (inspectorName.equals("FlexoProcess.inspector")) {
				if (binding.toString().equals("data.preferredRepresentation")) {
					continue;
				}
			}

			if (!binding.isValid(true)) {
				if (binding.getBinding(true) instanceof BindingValue && binding.getBindingDefinition() != null
						&& binding.getBindingDefinition().getType().equals(String.class)
						&& !binding.getBinding().getAccessedType().equals(String.class)) {
					// Try to repair by adding a .toString
					binding.setUnparsedBinding(binding.getBinding() + ".toString");
				}
			}
			if (!binding.isValid()) {
				error("INVALID binding " + binding.getBindingDefinition().getVariableName() + " values: " + binding + " owner="
						+ binding.getOwner());
				// binding.getBinding().debugIsBindingValid();
			}
		}

		/*
		 * newInspector.retrieveFIBLocalizedDictionary(); Language
		 * currentLanguage = FlexoLocalization.getCurrentLanguage();
		 * newInspector
		 * .retrieveFIBLocalizedDictionary().beginSearchNewLocalizationEntries
		 * (); for (Language language : Language.availableValues()) {
		 * FlexoLocalization.setCurrentLanguage(language); }
		 * newInspector.retrieveFIBLocalizedDictionary
		 * ().endSearchNewLocalizationEntries();
		 * newInspector.retrieveFIBLocalizedDictionary().refresh();
		 * FlexoLocalization.setCurrentLanguage(currentLanguage);
		 */

		ValidationReport report = newInspector.validate();
		for (ValidationError error : report.getErrors()) {
			error("Validation error: " + error.getLocalizedMessage());
		}

		return newInspector;

	}

	private static void searchLocalized(FIBPanel inspector, String key) {
		Language currentLanguage = FlexoLocalization.getCurrentLanguage();
		for (Language language : Language.availableValues()) {
			FlexoLocalization.setCurrentLanguage(language);
			FlexoLocalization.localizedForKey(inspector.retrieveFIBLocalizedDictionary(), key);
		}
		FlexoLocalization.setCurrentLanguage(currentLanguage);
	}

	private static FIBWidget makeWidget(PropertyModel pm, Vector<DataBinding> bindings, Class dataClass) {
		FIBWidget returned = buildWidget(pm, bindings, dataClass);
		if (returned == null) {
			return null;
		}
		if (!returned.getData().isSet()) {
			if (returned instanceof FIBFont) {
				returned.setData(new DataBinding("data." + pm.name + ".font"));
			} else {
				returned.setData(new DataBinding("data." + pm.name));
			}
			bindings.add(returned.getData());
		}

		if (pm.hasValueForParameter("visibleFor")) {
			returned.addToParameters(new FIBParameter("visibleFor", pm.getValueForParameter("visibleFor")));
		}
		if (StringUtils.isNotEmpty(pm.help)) {
			returned.setTooltipText(pm.help);
		}
		returned.setName(makeName(pm.name));
		// returned.setDescription(pm.toString());
		return returned;
	}

	private static String makeName(String aString) {
		return JavaUtils.getClassName(aString);
	}

	private static FIBWidget buildWidget(PropertyModel pm, Vector<DataBinding> bindings, Class dataClass) {
		Vector<String> unhandledParams = new Vector<String>();

		for (String s : pm.parameters.keySet()) {
			unhandledParams.add(s);
		}
		if (pm.hasValueForParameter("format")) {
			handleParam("format", unhandledParams);
		}
		if (pm.hasValueForParameter("formatter")) {
			handleParam("formatter", unhandledParams);
		}
		if (pm.hasValueForParameter("visibleFor")) {
			handleParam("visibleFor", unhandledParams);
		}
		if (pm.hasValueForParameter("width")) {
			handleParam("width", unhandledParams);
		}
		if (pm.hasValueForParameter("height")) {
			handleParam("height", unhandledParams);
		}
		if (pm.hasValueForParameter(DenaliWidget.EXPAND_HORIZONTALLY)) {
			handleParam(DenaliWidget.EXPAND_HORIZONTALLY, unhandledParams);
		}
		if (pm.hasValueForParameter(DenaliWidget.EXPAND_VERTICALLY)) {
			handleParam(DenaliWidget.EXPAND_VERTICALLY, unhandledParams);
		}

		if (pm.getWidget().equalsIgnoreCase(DenaliWidget.TEXT_FIELD) || pm.getWidget().equalsIgnoreCase(DenaliWidget.READ_ONLY_TEXT_FIELD)) {
			FIBTextField tf = new FIBTextField();
			if (pm.hasValueForParameter(TextFieldWidget.COLUMNS_PARAM)) {
				handleParam(TextFieldWidget.COLUMNS_PARAM, unhandledParams);
				tf.columns = pm.getIntValueForParameter(TextFieldWidget.COLUMNS_PARAM);
			}
			if (pm.hasValueForParameter(TextFieldWidget.PASSWORD_PARAM)) {
				handleParam(TextFieldWidget.PASSWORD_PARAM, unhandledParams);
				tf.passwd = pm.getBooleanValueForParameter(TextFieldWidget.PASSWORD_PARAM);
			}
			if (pm.hasValueForParameter(TextFieldWidget.VALIDATE_ON_RETURN)) {
				handleParam(TextFieldWidget.VALIDATE_ON_RETURN, unhandledParams);
				tf.validateOnReturn = pm.getBooleanValueForParameter(TextAreaWidget.VALIDATE_ON_RETURN);
			}
			if (pm.getWidget().equalsIgnoreCase(DenaliWidget.READ_ONLY_TEXT_FIELD)) {
				tf.setReadOnly(true);
			}
			checkUnhandledParams(pm, unhandledParams);
			return tf;
		} else if (pm.getWidget().equalsIgnoreCase(DenaliWidget.TEXT_AREA)
				|| pm.getWidget().equalsIgnoreCase(DenaliWidget.READ_ONLY_TEXT_AREA)) {
			FIBTextArea ta = new FIBTextArea();
			ta.setUseScrollBar(true);
			if (pm.hasValueForParameter(TextAreaWidget.COLUMNS)) {
				handleParam(TextAreaWidget.COLUMNS, unhandledParams);
				ta.columns = pm.getIntValueForParameter(TextAreaWidget.COLUMNS);
			}
			if (pm.hasValueForParameter(TextAreaWidget.ROWS)) {
				handleParam(TextAreaWidget.ROWS, unhandledParams);
				ta.rows = pm.getIntValueForParameter(TextAreaWidget.ROWS);
			}
			if (pm.hasValueForParameter(TextAreaWidget.VALIDATE_ON_RETURN)) {
				handleParam(TextAreaWidget.VALIDATE_ON_RETURN, unhandledParams);
				ta.validateOnReturn = pm.getBooleanValueForParameter(TextAreaWidget.VALIDATE_ON_RETURN);
			}
			if (pm.getWidget().equalsIgnoreCase(DenaliWidget.READ_ONLY_TEXT_AREA)) {
				ta.setReadOnly(true);
			}
			checkUnhandledParams(pm, unhandledParams);
			return ta;
		} else if (pm.getWidget().equalsIgnoreCase(DenaliWidget.LABEL)) {
			FIBLabel label = new FIBLabel();
			if (pm.hasValueForParameter(LabelWidget.ALIGN)) {
				handleParam(LabelWidget.ALIGN, unhandledParams);
				if (pm.getValueForParameter(LabelWidget.ALIGN).equalsIgnoreCase("center")) {
					label.setAlign(Align.center);
				} else if (pm.getValueForParameter(LabelWidget.ALIGN).equalsIgnoreCase("left")) {
					label.setAlign(Align.left);
				} else if (pm.getValueForParameter(LabelWidget.ALIGN).equalsIgnoreCase("right")) {
					label.setAlign(Align.right);
				}
			}
			if (pm.hasValueForParameter(LabelWidget.WIDTH)) {
				handleParam(LabelWidget.WIDTH, unhandledParams);
				label.setWidth(pm.getIntValueForParameter(LabelWidget.WIDTH));
			}
			if (pm.hasValueForParameter(LabelWidget.HEIGHT)) {
				handleParam(LabelWidget.HEIGHT, unhandledParams);
				label.setHeight(pm.getIntValueForParameter(LabelWidget.HEIGHT));
			}
			checkUnhandledParams(pm, unhandledParams);
			return label;
		} else if (pm.getWidget().equalsIgnoreCase(DenaliWidget.INFOLABEL)) {
			FIBLabel label = new FIBLabel();
			label.setLabel("InfoLabel");
			checkUnhandledParams(pm, unhandledParams);
			return label;
		} else if (pm.getWidget().equalsIgnoreCase(DenaliWidget.CHECKBOX)
				|| pm.getWidget().equalsIgnoreCase(DenaliWidget.READ_ONLY_CHECKBOX)) {
			FIBCheckBox cb = new FIBCheckBox();
			if (pm.getWidget().equalsIgnoreCase(DenaliWidget.READ_ONLY_CHECKBOX)) {
				cb.setReadOnly(true);
			}
			if (pm.hasValueForParameter("negate")) {
				handleParam("negate", unhandledParams);
				cb.setNegate(pm.getBooleanValueForParameter("negate"));
			}
			handleParam("columns", unhandledParams); // Ignore this
			checkUnhandledParams(pm, unhandledParams);
			return cb;
		} else if (pm.getWidget().equalsIgnoreCase(DenaliWidget.DROPDOWN)) {
			FIBDropDown dd = new FIBDropDown();
			dd.showReset = true;
			if (pm.hasValueForParameter("type")) {
				handleParam("type", unhandledParams);
				try {
					dd.setIteratorClass(Class.forName(pm.getValueForParameter("type")));
				} catch (ClassNotFoundException e) {
					error("Cannot find class " + pm.getValueForParameter("type"));
				}
			} else {
				Type accessedType = getAccessedType(pm.name, dataClass);
				if (accessedType != null) {
					dd.setIteratorClass(TypeUtils.getBaseClass(accessedType));
				}
			}
			if (pm.hasValueForParameter(DropDownWidget.WIDTH_PARAM)) {
				handleParam(DropDownWidget.WIDTH_PARAM, unhandledParams);
				dd.setWidth(pm.getIntValueForParameter(DropDownWidget.WIDTH_PARAM));
			}
			if (pm.hasValueForParameter(DropDownWidget.HEIGHT_PARAM)) {
				handleParam(DropDownWidget.HEIGHT_PARAM, unhandledParams);
				dd.setHeight(pm.getIntValueForParameter(DropDownWidget.HEIGHT_PARAM));
			}
			if (pm.hasValueForParameter("showReset")) {
				handleParam("showReset", unhandledParams);
				dd.showReset = pm.getBooleanValueForParameter("showReset");
			}
			if (pm.hasValueForParameter("showIcon")) {
				handleParam("showIcon", unhandledParams);
				dd.setShowIcon(pm.getBooleanValueForParameter("showIcon"));
			}
			if (pm.hasValueForParameter("columns")) {
				handleParam("columns", unhandledParams);
				dd.setWidth(15 * pm.getIntValueForParameter("columns"));
			}
			if (pm.hasValueForParameter("dynamiclist")) {
				handleParam("dynamiclist", unhandledParams);
				dd.setList(new DataBinding("data." + pm.getValueForParameter("dynamiclist")));
				bindings.add(dd.getList());
			} else {
				KeyValueProperty kvp = KeyValueLibrary.getKeyValueProperty(dataClass, pm.name);
				if (kvp != null && kvp.getType() instanceof Class && TypeUtils.isClassAncestorOf(ChoiceList.class, (Class) kvp.getType())) {
					dd.setList(new DataBinding("data." + pm.name + ".availableValues"));
					bindings.add(dd.getList());
				}
			}
			/*
			 * if (pm.hasValueForParameter("columns")) {
			 * handleParam("columns",unhandledParams); dd.setD }
			 */
			checkUnhandledParams(pm, unhandledParams);
			return dd;
		} else if (pm.getWidget().equalsIgnoreCase(DenaliWidget.INTEGER) || pm.getWidget().equalsIgnoreCase(DenaliWidget.READ_ONLY_INTEGER)) {
			FIBNumber n = new FIBNumber();
			n.setNumberType(NumberType.IntegerType);
			if (pm.getWidget().equalsIgnoreCase(DenaliWidget.READ_ONLY_INTEGER)) {
				n.setReadOnly(true);
			}
			if (pm.hasValueForParameter(IntegerWidget.MIN_VALUE_PARAM)) {
				handleParam(IntegerWidget.MIN_VALUE_PARAM, unhandledParams);
				n.setMinValue(pm.getIntValueForParameter(IntegerWidget.MIN_VALUE_PARAM));
			}
			if (pm.hasValueForParameter(IntegerWidget.MAX_VALUE_PARAM)) {
				handleParam(IntegerWidget.MAX_VALUE_PARAM, unhandledParams);
				n.setMaxValue(pm.getIntValueForParameter(IntegerWidget.MAX_VALUE_PARAM));
			}
			if (pm.hasValueForParameter(IntegerWidget.INCREMENT_VALUE_PARAM)) {
				handleParam(IntegerWidget.INCREMENT_VALUE_PARAM, unhandledParams);
				n.setIncrement(pm.getIntValueForParameter(IntegerWidget.INCREMENT_VALUE_PARAM));
			}
			if (pm.hasValueForParameter("columns")) {
				handleParam(TextAreaWidget.COLUMNS, unhandledParams);
				n.setColumns(pm.getIntValueForParameter("columns"));
			}
			checkUnhandledParams(pm, unhandledParams);
			return n;
		} else if (pm.getWidget().equalsIgnoreCase(DenaliWidget.DOUBLE)) {
			FIBNumber n = new FIBNumber();
			n.setNumberType(NumberType.DoubleType);
			if (pm.hasValueForParameter(IntegerWidget.MIN_VALUE_PARAM)) {
				handleParam(DoubleWidget.MIN_VALUE_PARAM, unhandledParams);
				n.setMinValue(pm.getDoubleValueForParameter(DoubleWidget.MIN_VALUE_PARAM));
			}
			if (pm.hasValueForParameter(IntegerWidget.MAX_VALUE_PARAM)) {
				handleParam(DoubleWidget.MAX_VALUE_PARAM, unhandledParams);
				n.setMaxValue(pm.getDoubleValueForParameter(DoubleWidget.MAX_VALUE_PARAM));
			}
			if (pm.hasValueForParameter(IntegerWidget.INCREMENT_VALUE_PARAM)) {
				handleParam(DoubleWidget.INCREMENT_VALUE_PARAM, unhandledParams);
				n.setIncrement(pm.getDoubleValueForParameter(DoubleWidget.INCREMENT_VALUE_PARAM));
			}
			if (pm.hasValueForParameter("columns")) {
				handleParam(TextAreaWidget.COLUMNS, unhandledParams);
				n.setColumns(pm.getIntValueForParameter("columns"));
			}
			checkUnhandledParams(pm, unhandledParams);
			return n;
		} else if (pm.getWidget().equalsIgnoreCase(DenaliWidget.COLOR)) {
			FIBColor c = new FIBColor();
			handleParam("columns", unhandledParams); // Ignore this
			checkUnhandledParams(pm, unhandledParams);
			return c;
		} else if (pm.getWidget().equalsIgnoreCase(DenaliWidget.FONT)) {
			FIBFont c = new FIBFont();
			handleParam("columns", unhandledParams); // Ignore this
			if (pm.hasValueForParameter("sampleText")) {
				handleParam("sampleText", unhandledParams);
				c.sampleText = pm.getValueForParameter("sampleText");
			}
			checkUnhandledParams(pm, unhandledParams);
			return c;
		} else if (pm.getWidget().equalsIgnoreCase(DenaliWidget.FILE)) {
			FIBFile fileSelector = new FIBFile();
			fileSelector.isDirectory = false;
			fileSelector.mode = FileMode.SaveMode;
			checkUnhandledParams(pm, unhandledParams);
			return fileSelector;
		} else if (pm.getWidget().equalsIgnoreCase(DenaliWidget.DIRECTORY)) {
			FIBFile fileSelector = new FIBFile();
			fileSelector.isDirectory = true;
			fileSelector.mode = FileMode.SaveMode;
			checkUnhandledParams(pm, unhandledParams);
			return fileSelector;
		} else if (pm.getWidget().equalsIgnoreCase(DenaliWidget.CUSTOM)) {
			return makeCustom(pm, bindings, dataClass, unhandledParams);
		} else if (pm.getWidget().equalsIgnoreCase(DenaliWidget.WYSIWYG_ULTRA_LIGHT)) {
			FIBHtmlEditor htmlEditor = new FIBHtmlEditor();
			htmlEditor.makeUltraLightHtmlEditor();
			if (pm.hasValueForParameter("readOnly")) {
				// Ignore it
				handleParam("readOnly", unhandledParams);
			}
			if (pm.hasValueForParameter("widgetLayout")) {
				// Ignore it
				handleParam("widgetLayout", unhandledParams);
			}
			if (pm.hasValueForParameter("align")) {
				// Ignore it
				handleParam("align", unhandledParams);
			}
			checkUnhandledParams(pm, unhandledParams);
			return htmlEditor;
		} else if (pm.getWidget().equalsIgnoreCase(DenaliWidget.WYSIWYG_LIGHT)) {
			FIBHtmlEditor htmlEditor = new FIBHtmlEditor();
			htmlEditor.makeLightHtmlEditor();
			if (pm.hasValueForParameter("readOnly")) {
				// Ignore it
				handleParam("readOnly", unhandledParams);
			}
			checkUnhandledParams(pm, unhandledParams);
			return htmlEditor;
		} else {
			error("Not handled: widget " + pm.getWidget());
			return null;
		}
	}

	private static FIBCustom makeCustom(PropertyModel pm, Vector<DataBinding> bindings, Class dataClass, Vector<String> unhandledParams) {
		FIBCustom c = new FIBCustom();
		if (pm.hasValueForParameter("className")) {
			handleParam("className", unhandledParams);
			String className = pm.getValueForParameter("className");
			try {
				if (className.equals("org.openflexo.fge.view.widget.ForegroundStyleInspectorWidget")) {
					c.setComponentClass(Class.forName("org.openflexo.fge.view.widget.FIBForegroundStyleSelector"));
					checkUnhandledParams(pm, unhandledParams);
					return c;
				} else if (className.equals("org.openflexo.fge.view.widget.BackgroundStyleInspectorWidget")) {
					c.setComponentClass(Class.forName("org.openflexo.fge.view.widget.FIBBackgroundStyleSelector"));
					checkUnhandledParams(pm, unhandledParams);
					return c;
				} else if (className.equals("org.openflexo.fge.view.widget.TextStyleInspectorWidget")) {
					c.setComponentClass(Class.forName("org.openflexo.fge.view.widget.FIBTextStyleSelector"));
					checkUnhandledParams(pm, unhandledParams);
					return c;
				} else if (className.equals("org.openflexo.fge.view.widget.ShadowStyleInspectorWidget")) {
					c.setComponentClass(Class.forName("org.openflexo.fge.view.widget.FIBShadowStyleSelector"));
					checkUnhandledParams(pm, unhandledParams);
					return c;
				} else if (className.equals("org.openflexo.components.widget.RoleInspectorWidget")) {
					c.setComponentClass(Class.forName("org.openflexo.components.widget.FIBRoleSelector"));
					DataBinding variable = new DataBinding("component.project");
					DataBinding value = new DataBinding("data.project");
					c.addToAssignments(new FIBCustomAssignment(c, variable, value, true));
					bindings.add(variable);
					bindings.add(value);
					checkUnhandledParams(pm, unhandledParams);
					return c;
				} else if (className.equals("org.openflexo.components.widget.ProcessInspectorWidget")) {
					c.setComponentClass(Class.forName("org.openflexo.components.widget.FIBProcessSelector"));
					if (pm.hasValueForParameter("isSelectable")) {
						handleParam("isSelectable", unhandledParams);
						DataBinding variable = new DataBinding("component.selectableCondition");
						DataBinding value = new DataBinding('"' + "data." + pm.getValueForParameter("isSelectable") + "("
								+ makeName(pm.name) + ".customComponent.candidateValue)" + '"');
						c.addToAssignments(new FIBCustomAssignment(c, variable, value, true));
						bindings.add(variable);
						bindings.add(value);
					}
					DataBinding variable = new DataBinding("component.project");
					DataBinding value = new DataBinding("data.project");
					c.addToAssignments(new FIBCustomAssignment(c, variable, value, true));
					bindings.add(variable);
					bindings.add(value);
					checkUnhandledParams(pm, unhandledParams);
					return c;
				} else if (className.equals("org.openflexo.components.widget.BindingSelectorInspectorWidget")) {
					c.setComponentClass(Class.forName("org.openflexo.components.widget.binding.BindingSelector"));
					if (pm.hasValueForParameter("binding_definition")) {
						handleParam("binding_definition", unhandledParams);
						DataBinding variable = new DataBinding("component.bindingDefinition");
						DataBinding value = new DataBinding("data." + pm.getValueForParameter("binding_definition"));
						c.addToAssignments(new FIBCustomAssignment(c, variable, value, true));
						bindings.add(variable);
						bindings.add(value);
					}
					if (pm.hasValueForParameter("activate_compound_bindings")) {
						handleParam("activate_compound_bindings", unhandledParams);
						DataBinding variable = new DataBinding("component.allowsCompoundBindings");
						DataBinding value = new DataBinding("" + pm.getBooleanValueForParameter("activate_compound_bindings"));
						c.addToAssignments(new FIBCustomAssignment(c, variable, value, true));
						bindings.add(variable);
						bindings.add(value);
					}
					DataBinding variable = new DataBinding("component.bindable");
					DataBinding value = new DataBinding("data");
					c.addToAssignments(new FIBCustomAssignment(c, variable, value, true));
					bindings.add(variable);
					bindings.add(value);
					checkUnhandledParams(pm, unhandledParams);
					return c;
				} else if (className.equals("org.openflexo.components.widget.DMTypeInspectorWidget")) {
					c.setComponentClass(Class.forName("org.openflexo.components.widget.DMTypeSelector"));
					if (pm.hasValueForParameter("project")) {
						handleParam("project", unhandledParams);
						DataBinding variable = new DataBinding("component.project");
						DataBinding value = new DataBinding("data." + pm.getValueForParameter("project"));
						c.addToAssignments(new FIBCustomAssignment(c, variable, value, true));
						bindings.add(variable);
						bindings.add(value);
					} else {
						DataBinding variable = new DataBinding("component.project");
						DataBinding value = new DataBinding("data.project");
						c.addToAssignments(new FIBCustomAssignment(c, variable, value, true));
						bindings.add(variable);
						bindings.add(value);
					}
					DataBinding variable = new DataBinding("component.owner");
					DataBinding value = new DataBinding("data");
					c.addToAssignments(new FIBCustomAssignment(c, variable, value, true));
					bindings.add(variable);
					bindings.add(value);
					checkUnhandledParams(pm, unhandledParams);
					return c;
				} else if (className.equals("org.openflexo.components.widget.DescriptionInspectorWidget")) {
					c.setComponentClass(Class.forName("org.openflexo.components.widget.FIBDescriptionWidget"));
					c.setData(new DataBinding("data"));
					bindings.add(c.getData());

					// Ignore those params
					handleParam("displayLabel", unhandledParams);
					handleParam("widgetLayout", unhandledParams);
					handleParam("useUltraLightWysiwyg", unhandledParams);
					handleParam("rows", unhandledParams);
					handleParam("align", unhandledParams);
					checkUnhandledParams(pm, unhandledParams);
					return c;
				} else if (className.equals("org.openflexo.components.widget.DurationInspectorWidget")) {
					c.setComponentClass(Class.forName("org.openflexo.fib.utils.DurationSelector"));
					checkUnhandledParams(pm, unhandledParams);
					return c;
				}

				else {
					try {
						Class foundClass = Class.forName(className);
						if (FIBCustomComponent.class.isAssignableFrom(foundClass)) {
							c.setComponentClass(foundClass);
							return c;
						}
						error("Found component class " + className + " but does not implement FIBCustomComponent");
						return null;
					} catch (ClassNotFoundException e) {
						error("Not found: component class " + className);
						return null;
					}
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		error("Not present: component class ");
		return null;

	}

	private static FIBTable makeTable(PropertyListModel pl, Class dataClass, Vector<DataBinding> bindings) {
		FIBTable returned = buildTable(pl, dataClass, bindings);
		if (returned == null) {
			return null;
		}
		returned.setData(new DataBinding("data." + pl.name));
		bindings.add(returned.getData());

		if (pl.hasValueForParameter("visibleFor")) {
			returned.addToParameters(new FIBParameter("visibleFor", pl.getValueForParameter("visibleFor")));
		}
		if (StringUtils.isNotEmpty(pl.help)) {
			returned.setTooltipText(pl.help);
		}
		returned.setName(makeName(pl.name));
		return returned;
	}

	private static FIBTable buildTable(PropertyListModel pl, Class dataClass, Vector<DataBinding> bindings) {
		Vector<String> unhandledParams = new Vector<String>();

		for (String s : pl.parameters.keySet()) {
			unhandledParams.add(s);
		}
		if (pl.hasValueForParameter("format")) {
			handleParam("format", unhandledParams);
		}
		if (pl.hasValueForParameter("formatter")) {
			handleParam("formatter", unhandledParams);
		}
		if (pl.hasValueForParameter(DenaliWidget.DISPLAY_LABEL)) {
			handleParam(DenaliWidget.DISPLAY_LABEL, unhandledParams);
		}
		if (pl.hasValueForParameter("visibleFor")) {
			handleParam("visibleFor", unhandledParams);
		}
		if (pl.hasValueForParameter("widgetLayout")) {
			handleParam("widgetLayout", unhandledParams);
		}

		FIBTable table = new FIBTable();

		if (pl.hasValueForParameter(PropertyListModel.ROW_HEIGHT)) {
			handleParam(PropertyListModel.ROW_HEIGHT, unhandledParams);
			table.setRowHeight(pl.getIntValueForParameter(PropertyListModel.ROW_HEIGHT));
		}

		if (pl.hasValueForParameter(PropertyListModel.VISIBLE_ROW_COUNT)) {
			handleParam(PropertyListModel.VISIBLE_ROW_COUNT, unhandledParams);
			table.setVisibleRowCount(pl.getIntValueForParameter(PropertyListModel.VISIBLE_ROW_COUNT));
		}

		Class iteratorClass = null;
		Type accessedType = getAccessedType(pl.name, dataClass);
		if (accessedType instanceof ParameterizedType && ((ParameterizedType) accessedType).getActualTypeArguments().length > 0) {
			iteratorClass = TypeUtils.getBaseClass(((ParameterizedType) accessedType).getActualTypeArguments()[0]);
			table.setIteratorClass(iteratorClass);
		}

		// System.out.println("Inspector "+inspectorName+" table "+pl.name+" has type: "+table.getIteratorClass());

		for (PropertyListColumn plColumn : pl.getColumns()) {
			FIBTableColumn column = buildTableColumn(plColumn, iteratorClass, bindings);
			if (column != null) {
				table.addToColumns(column);
			}
		}

		for (PropertyListAction plAction : pl.getActions()) {
			FIBTableAction action = buildTableAction(plAction, bindings);
			if (action != null) {
				table.addToActions(action);
			}
		}

		checkUnhandledParams(pl, unhandledParams);
		return table;

	}

	private static Type getAccessedType(String fullPath, Class dataClass) {
		Class currentClass = dataClass;
		String path = fullPath;
		while (path.indexOf(".") > -1) {
			String kv = path.substring(0, path.indexOf("."));
			path = path.substring(path.indexOf(".") + 1);
			KeyValueProperty kvp = KeyValueLibrary.getKeyValueProperty(currentClass, kv);
			if (kvp != null) {
				currentClass = TypeUtils.getBaseClass(kvp.getType());
			} else {
				error("Could not access KeyValueProperty " + kv + " for class " + currentClass);
				currentClass = null;
			}
		}
		if (currentClass != null) {
			KeyValueProperty kvp = KeyValueLibrary.getKeyValueProperty(currentClass, path);
			if (kvp != null) {
				return kvp.getType();
			}
		}
		// error("Cannot access "+path+" from "+currentClass);
		return null;
	}

	private static FIBTableColumn buildTableColumn(PropertyListColumn plColumn, Class dataClass, Vector<DataBinding> bindings) {
		Vector<String> unhandledParams = new Vector<String>();
		for (String s : plColumn.parameters.keySet()) {
			unhandledParams.add(s);
		}

		FIBTableColumn returned = null;

		if (plColumn.getWidget().equals(PropertyListColumn.READ_ONLY_TEXT_FIELD)) {
			returned = new FIBLabelColumn();
		} else if (plColumn.getWidget().equals(PropertyListColumn.TEXT_FIELD)) {
			returned = new FIBTextFieldColumn();
			if (plColumn.hasValueForParameter("isEditable")) {
				handleParam("isEditable", unhandledParams);
				((FIBTextFieldColumn) returned).setIsEditable(new DataBinding("iterator." + plColumn.getValueForParameter("isEditable")));
				bindings.add(((FIBTextFieldColumn) returned).getIsEditable());
			}
		} else if (plColumn.getWidget().equals(PropertyListColumn.CHECKBOX)) {
			returned = new FIBCheckBoxColumn();
		} else if (plColumn.getWidget().equals(PropertyListColumn.INTEGER)) {
			returned = new FIBNumberColumn();
			((FIBNumberColumn) returned).setNumberType(NumberType.IntegerType);
		} else if (plColumn.getWidget().equals(PropertyListColumn.DROPDOWN)) {
			returned = new FIBDropDownColumn();
			if (plColumn.getDynamicList() != null) {
				Type accessedColumnType = getAccessedType(plColumn.name, dataClass);
				Type accessedType = getAccessedType(plColumn.getDynamicList(), TypeUtils.getBaseClass(accessedColumnType));
				if (accessedType instanceof Class && ((Class) accessedType).isArray()) {
					((FIBDropDownColumn) returned).setArray(new DataBinding("iterator." + plColumn.name + "." + plColumn.getDynamicList()));
					bindings.add(((FIBDropDownColumn) returned).getArray());
				} else {
					((FIBDropDownColumn) returned).setList(new DataBinding("iterator." + plColumn.name + "." + plColumn.getDynamicList()));
					bindings.add(((FIBDropDownColumn) returned).getList());
				}
			}
		} else if (plColumn.getWidget().equals(PropertyListColumn.COLOR)) {
			error("Not handled: column widget for COLOR " + plColumn.getWidget());
			return null;
		} else if (plColumn.getWidget().equals(PropertyListColumn.ICON)) {
			returned = new FIBIconColumn();
		} else if (plColumn.getWidget().equals(PropertyListColumn.CUSTOM)) {
			returned = makeCustomColumn(plColumn, bindings, unhandledParams);
			if (returned == null) {
				return null;
			}
			if (plColumn.hasValueForParameter("customRendering")) {
				handleParam("customRendering", unhandledParams);
				((FIBCustomColumn) returned).customRendering = plColumn.getBooleanValueForParameter("customRendering");
			}

		} else {
			error("Not handled: column widget " + plColumn.getWidget());
			return null;
		}
		returned.setData(new DataBinding("iterator." + plColumn.name));
		bindings.add(returned.getData());
		returned.setTitle(plColumn.label);
		if (plColumn.hasValueForParameter(PropertyListColumn.DISPLAY_TITLE)) {
			handleParam(PropertyListColumn.DISPLAY_TITLE, unhandledParams);
			returned.setDisplayTitle(plColumn.getBooleanValueForParameter(PropertyListColumn.DISPLAY_TITLE));
		}
		if (plColumn.hasValueForParameter(PropertyListColumn.COLUMN_WIDTH)) {
			handleParam(PropertyListColumn.COLUMN_WIDTH, unhandledParams);
			returned.setColumnWidth(plColumn.getIntValueForParameter(PropertyListColumn.COLUMN_WIDTH));
		}
		if (plColumn.hasValueForParameter(PropertyListColumn.RESIZABLE)) {
			handleParam(PropertyListColumn.RESIZABLE, unhandledParams);
			returned.setResizable(plColumn.getBooleanValueForParameter(PropertyListColumn.RESIZABLE));
		}
		if (plColumn.hasValueForParameter(PropertyListColumn.FONT)) {
			handleParam(PropertyListColumn.FONT, unhandledParams);
			error("Tiens, regarde moi ce qui se passe ici, on a un parametre FONT pour " + plColumn);
		}
		// Handle formatter
		if (plColumn.hasValueForParameter(PropertyListColumn.FORMAT)) {
			handleParam(PropertyListColumn.FORMAT, unhandledParams);
			returned.setFormat(new DataBinding("object." + plColumn.getValueForParameter(PropertyListColumn.FORMAT)));
			bindings.add(returned.getFormat());
		}
		if (plColumn.hasValueForParameter("tooltip")) {
			handleParam("tooltip", unhandledParams);
			returned.setTooltip(new DataBinding("iterator." + plColumn.getValueForParameter("tooltip")));
			bindings.add(returned.getTooltip());
		}

		checkUnhandledParamsForColumn(plColumn, unhandledParams);
		return returned;
	}

	private static FIBCustomColumn makeCustomColumn(PropertyListColumn plColumn, Vector<DataBinding> bindings,
			Vector<String> unhandledParams) {
		String className = plColumn.getValueForParameter("className");
		handleParam("className", unhandledParams);
		try {
			FIBCustomColumn c = new FIBCustomColumn();
			if (className.equals("org.openflexo.fge.view.widget.ForegroundStyleInspectorWidget")) {
				c.setComponentClass(Class.forName("org.openflexo.fge.view.widget.FIBForegroundStyleSelector"));
				return c;
			} else if (className.equals("org.openflexo.fge.view.widget.BackgroundStyleInspectorWidget")) {
				c.setComponentClass(Class.forName("org.openflexo.fge.view.widget.FIBBackgroundStyleSelector"));
				return c;
			} else if (className.equals("org.openflexo.fge.view.widget.TextStyleInspectorWidget")) {
				c.setComponentClass(Class.forName("org.openflexo.fge.view.widget.FIBTextStyleSelector"));
				return c;
			} else if (className.equals("org.openflexo.fge.view.widget.ShadowStyleInspectorWidget")) {
				c.setComponentClass(Class.forName("org.openflexo.fge.view.widget.FIBShadowStyleSelector"));
				return c;
			} else if (className.equals("org.openflexo.components.widget.RoleInspectorWidget")) {
				c.setComponentClass(Class.forName("org.openflexo.components.widget.FIBRoleSelector"));
				DataBinding variable = new DataBinding("component.project");
				DataBinding value = new DataBinding("iterator.project");
				c.addToAssignments(new FIBCustomColumn.FIBCustomAssignment(c, variable, value, true));
				bindings.add(variable);
				bindings.add(value);
				return c;
			} else if (className.equals("org.openflexo.components.widget.BindingSelectorInspectorWidget")) {
				c.setComponentClass(Class.forName("org.openflexo.components.widget.binding.BindingSelector"));
				if (plColumn.hasValueForParameter("binding_definition")) {
					handleParam("binding_definition", unhandledParams);
					DataBinding variable = new DataBinding("component.bindingDefinition");
					DataBinding value = new DataBinding("iterator." + plColumn.getValueForParameter("binding_definition"));
					c.addToAssignments(new FIBCustomColumn.FIBCustomAssignment(c, variable, value, true));
					bindings.add(variable);
					bindings.add(value);
				}
				if (plColumn.hasValueForParameter("activate_compound_bindings")) {
					handleParam("activate_compound_bindings", unhandledParams);
					DataBinding variable = new DataBinding("component.allowsCompoundBindings");
					DataBinding value = new DataBinding("" + plColumn.getBooleanValueForParameter("activate_compound_bindings"));
					c.addToAssignments(new FIBCustomColumn.FIBCustomAssignment(c, variable, value, true));
					bindings.add(variable);
					bindings.add(value);
				}
				DataBinding variable = new DataBinding("component.bindable");
				DataBinding value = new DataBinding("iterator");
				c.addToAssignments(new FIBCustomColumn.FIBCustomAssignment(c, variable, value, true));
				bindings.add(variable);
				bindings.add(value);
				return c;
			} else if (className.equals("org.openflexo.components.widget.DMTypeInspectorWidget")) {
				c.setComponentClass(Class.forName("org.openflexo.components.widget.DMTypeSelector"));
				if (plColumn.hasValueForParameter("project")) {
					handleParam("project", unhandledParams);
					DataBinding variable = new DataBinding("component.project");
					DataBinding value = new DataBinding("iterator." + plColumn.getValueForParameter("project"));
					c.addToAssignments(new FIBCustomColumn.FIBCustomAssignment(c, variable, value, true));
					bindings.add(variable);
					bindings.add(value);
				}
				DataBinding variable = new DataBinding("component.owner");
				DataBinding value = new DataBinding("iterator");
				c.addToAssignments(new FIBCustomColumn.FIBCustomAssignment(c, variable, value, true));
				bindings.add(variable);
				bindings.add(value);
				return c;
			} else if (className.equals("org.openflexo.components.widget.MetricsValueInspectorWidget")) {
				c.setComponentClass(Class.forName("org.openflexo.components.widget.MetricsValueInspectorWidget"));
				return c;
			}

			else {
				try {
					Class foundClass = Class.forName(className);
					if (FIBCustomComponent.class.isAssignableFrom(foundClass)) {
						c.setComponentClass(foundClass);
						return c;
					}
					error("Found component class " + className + " but does not implement FIBCustomComponent");
					return null;
				} catch (ClassNotFoundException e) {
					error("Not found: component class " + className);
					return null;
				}
			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	private static FIBTableAction buildTableAction(PropertyListAction plAction, Vector<DataBinding> bindings) {
		Vector<String> unhandledParams = new Vector<String>();
		for (String s : plAction.parameters.keySet()) {
			unhandledParams.add(s);
		}

		FIBTableAction returned;

		if (plAction.type.equals(PropertyListAction.ADD_TYPE)) {
			returned = new FIBAddAction();
			if (plAction._getMethod() != null) {
				returned.setMethod(new DataBinding("data." + plAction._getMethod()));
				bindings.add(returned.getMethod());
			}
			if (plAction._getIsAvailable() != null) {
				returned.setIsAvailable(new DataBinding("data." + plAction._getIsAvailable()));
				bindings.add(returned.getIsAvailable());
			}
		} else if (plAction.type.equals(PropertyListAction.DELETE_TYPE)) {
			returned = new FIBRemoveAction();
			if (plAction._getMethod() != null) {
				if (plAction._getMethod().indexOf("(this)") > -1) {
					returned.setMethod(new DataBinding("data."
							+ ToolBox.replaceStringByStringInString("(this)", "(selected)", plAction._getMethod())));
				} else {
					returned.setMethod(new DataBinding("data." + plAction._getMethod() + "(selected)"));
				}
				bindings.add(returned.getMethod());
			}
			if (plAction._getIsAvailable() != null) {
				if (plAction._getIsAvailable().indexOf("(this)") > -1) {
					returned.setIsAvailable(new DataBinding("data."
							+ ToolBox.replaceStringByStringInString("(this)", "(selected)", plAction._getIsAvailable())));
				} else {
					returned.setIsAvailable(new DataBinding("data." + plAction._getIsAvailable() + "(selected)"));
				}
				bindings.add(returned.getIsAvailable());
			}
		} else if (plAction.type.equals(PropertyListAction.ACTION_TYPE)) {
			returned = new FIBCustomAction();
			if (plAction._getMethod() != null) {
				if (plAction._getMethod().indexOf("(this)") > -1) {
					returned.setMethod(new DataBinding("data."
							+ ToolBox.replaceStringByStringInString("(this)", "(selected)", plAction._getMethod())));
				} else {
					returned.setMethod(new DataBinding("data." + plAction._getMethod() + "(selected)"));
				}
				bindings.add(returned.getMethod());
			}
			if (plAction._getIsAvailable() != null) {
				if (plAction._getIsAvailable().indexOf("(this)") > -1) {
					returned.setIsAvailable(new DataBinding("data."
							+ ToolBox.replaceStringByStringInString("(this)", "(selected)", plAction._getIsAvailable())));
				} else {
					returned.setIsAvailable(new DataBinding("data." + plAction._getIsAvailable() + "(selected)"));
				}
				bindings.add(returned.getIsAvailable());
			}
		} else if (plAction.type.equals(PropertyListAction.STATIC_ACTION_TYPE)) {
			returned = new FIBCustomAction();
			if (plAction._getMethod() != null) {
				if (plAction._getMethod().indexOf("(this)") > -1) {
					returned.setMethod(new DataBinding("data."
							+ ToolBox.replaceStringByStringInString("(this)", "(selected)", plAction._getMethod())));
				} else {
					returned.setMethod(new DataBinding("data." + plAction._getMethod()));
				}
				bindings.add(returned.getMethod());
			}
			if (plAction._getIsAvailable() != null) {
				if (plAction._getIsAvailable().indexOf("(this)") > -1) {
					returned.setIsAvailable(new DataBinding("data."
							+ ToolBox.replaceStringByStringInString("(this)", "(selected)", plAction._getIsAvailable())));
				} else {
					returned.setIsAvailable(new DataBinding("data." + plAction._getIsAvailable()));
				}
				bindings.add(returned.getIsAvailable());
			}
		} else {
			error("Not handled: column action " + plAction.type);
			return null;
		}

		returned.setName(plAction.name);

		checkUnhandledParamsForAction(plAction, unhandledParams);
		return returned;
	}

	private static void checkUnhandledParams(PropertyModel pm, Vector<String> unhandledParams) {
		for (String param : unhandledParams) {
			error("Not handled: parameter " + param + " for property " + pm.name + " widget " + pm.getWidget());
		}
	}

	private static void checkUnhandledParamsForColumn(PropertyListColumn pm, Vector<String> unhandledParams) {
		for (String param : unhandledParams) {
			error("Not handled: parameter " + param + " for column " + pm.name + " widget " + pm.getWidget());
		}
	}

	private static void checkUnhandledParamsForAction(PropertyListAction pm, Vector<String> unhandledParams) {
		for (String param : unhandledParams) {
			error("Not handled: parameter " + param + " for action " + pm.name);
		}
	}

	private static void handleParam(String param, Vector<String> unhandledParams) {
		unhandledParams.remove(param);
	}

	private static InspectorModel importInspectorFile(File inspectorFile) throws FileNotFoundException {
		InputStream inputStream = null;
		try {

			inputStream = new FileInputStream(inspectorFile);

			return importInspector(inspectorFile.getName(), inputStream);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (Exception e) {
			error("Exception raised during inspector import: " + e + "\nFile path is: " + inspectorFile.getAbsolutePath());
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				error("Cannot close inspector input stream '" + inspectorFile.getAbsolutePath() + "'");
				e.printStackTrace();
			}
		}

		return null;
	}

	public static InspectorModel importInspector(String name, InputStream stream) {
		try {
			if (getInspectorMapping() != null) {
				InspectorModel inspectorModel = (InspectorModel) XMLDecoder.decodeObjectWithMapping(stream, getInspectorMapping());
				// error("Getting this " +
				// XMLCoder.encodeObjectWithMapping(inspectorModel,
				// getInspectorMapping(),StringEncoder.getDefaultInstance()));
				return inspectorModel;
			}
		} catch (Exception e) {
			error("Exception raised during inspector import '" + name + "': " + e);
			e.printStackTrace();
		}

		return null;
	}

	protected static XMLMapping _inspectorMapping;

	public static XMLMapping getInspectorMapping() {
		if (_inspectorMapping == null) {
			// File mappingFile = new File
			// ("../FlexoInspector/Models/InspectorModel.xml");
			File mappingFile = new FileResource("Models/InspectorModel.xml");
			if (!mappingFile.exists()) {
				error("Could not find file: " + mappingFile.getAbsolutePath());
			}
			try {
				_inspectorMapping = new XMLMapping(mappingFile);
			} catch (InvalidModelException e) {
				// Warns about the exception
				error("Exception raised: " + e + " for file " + mappingFile.getAbsolutePath() + ". See console for details.");
				e.printStackTrace();
			} catch (Exception e) {
				// Warns about the exception
				error("Exception raised: " + e.getClass().getName() + " for file " + mappingFile.getAbsolutePath()
						+ ". See console for details.");
				e.printStackTrace();
			}
		}
		return _inspectorMapping;
	}

	private static void error(String message) {
		errors++;
		System.err.println("*** ERROR " + errors + ": " + inspectorName + " : " + message);
		// if (errors == 5) System.exit(-1);
	}

	private static void exit(boolean normally) {
		System.err.println(normally ? "Conversion exited normally" : "Conversion exited abnormally");
		System.err.println("" + errors + " errors found");
		System.exit(normally ? 0 : -1);
	}
}
