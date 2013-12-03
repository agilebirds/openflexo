package org.openflexo.fib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBNumber.NumberType;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.GridLayoutConstraints;
import org.openflexo.fib.sampleData.Numbers;
import org.openflexo.fib.view.widget.FIBNumberWidget;
import org.openflexo.localization.FlexoLocalization;

/**
 * Test the structural and behavioural features of FIBNumber widget
 * 
 * @author sylvain
 * 
 */
public class FIBNumberWidgetTest {

	private static GraphicalContextDelegate gcDelegate;

	private static FIBPanel component;

	private static FIBLabel byteLabel;
	private static FIBNumber bytePWidget;
	private static FIBNumber byteOWidget;
	private static FIBNumber byteSWidget;

	private static FIBLabel shortLabel;
	private static FIBNumber shortPWidget;
	private static FIBNumber shortOWidget;
	private static FIBNumber shortSWidget;

	private static FIBLabel integerLabel;
	private static FIBNumber integerPWidget;
	private static FIBNumber integerOWidget;
	private static FIBNumber integerSWidget;

	private static FIBLabel longLabel;
	private static FIBNumber longPWidget;
	private static FIBNumber longOWidget;
	private static FIBNumber longSWidget;

	private static FIBLabel floatLabel;
	private static FIBNumber floatPWidget;
	private static FIBNumber floatOWidget;
	private static FIBNumber floatSWidget;

	private static FIBLabel doubleLabel;
	private static FIBNumber doublePWidget;
	private static FIBNumber doubleOWidget;
	private static FIBNumber doubleSWidget;

	private static FIBController controller;
	private static Numbers numbers;

	/**
	 * Create an initial component
	 */
	@Test
	public void test1CreateComponent() {

		component = new FIBPanel();
		component.setLayout(Layout.grid);
		component.setRows(6);
		component.setCols(4);

		component.setDataClass(Numbers.class);

		byteLabel = new FIBLabel("byte");
		component.addToSubComponents(byteLabel, new GridLayoutConstraints(0, 0));
		bytePWidget = new FIBNumber();
		bytePWidget.setNumberType(NumberType.ByteType);
		bytePWidget.setData(new DataBinding<String>("data.byteP", bytePWidget, Byte.TYPE, BindingDefinitionType.GET_SET));
		component.addToSubComponents(bytePWidget, new GridLayoutConstraints(1, 0));
		byteOWidget = new FIBNumber();
		byteOWidget.setNumberType(NumberType.ByteType);
		byteOWidget.setData(new DataBinding<String>("data.byteO", byteOWidget, Byte.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(byteOWidget, new GridLayoutConstraints(2, 0));
		byteSWidget = new FIBNumber();
		byteSWidget.setNumberType(NumberType.ByteType);
		byteSWidget.setData(new DataBinding<String>("data.byteP+data.byteO", byteSWidget, Byte.class, BindingDefinitionType.GET));
		component.addToSubComponents(byteSWidget, new GridLayoutConstraints(3, 0));

		assertTrue(bytePWidget.getData().isValid());
		assertTrue(byteOWidget.getData().isValid());
		assertTrue(byteSWidget.getData().isValid());

		shortLabel = new FIBLabel("short");
		component.addToSubComponents(shortLabel, new GridLayoutConstraints(0, 1));
		shortPWidget = new FIBNumber();
		shortPWidget.setNumberType(NumberType.ShortType);
		shortPWidget.setData(new DataBinding<String>("data.shortP", shortPWidget, Short.TYPE, BindingDefinitionType.GET_SET));
		component.addToSubComponents(shortPWidget, new GridLayoutConstraints(1, 1));
		shortOWidget = new FIBNumber();
		shortOWidget.setNumberType(NumberType.ShortType);
		shortOWidget.setData(new DataBinding<String>("data.shortO", shortOWidget, Short.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(shortOWidget, new GridLayoutConstraints(2, 1));
		shortSWidget = new FIBNumber();
		shortSWidget.setNumberType(NumberType.ShortType);
		shortSWidget.setData(new DataBinding<String>("data.shortP+data.shortO", shortSWidget, Short.class, BindingDefinitionType.GET));
		component.addToSubComponents(shortSWidget, new GridLayoutConstraints(3, 1));

		assertTrue(shortPWidget.getData().isValid());
		assertTrue(shortOWidget.getData().isValid());
		assertTrue(shortSWidget.getData().isValid());

		integerLabel = new FIBLabel("integer");
		component.addToSubComponents(integerLabel, new GridLayoutConstraints(0, 2));
		integerPWidget = new FIBNumber();
		integerPWidget.setNumberType(NumberType.IntegerType);
		integerPWidget.setData(new DataBinding<String>("data.intP", integerPWidget, Integer.TYPE, BindingDefinitionType.GET_SET));
		component.addToSubComponents(integerPWidget, new GridLayoutConstraints(1, 2));
		integerOWidget = new FIBNumber();
		integerOWidget.setNumberType(NumberType.IntegerType);
		integerOWidget.setData(new DataBinding<String>("data.intO", integerOWidget, Integer.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(integerOWidget, new GridLayoutConstraints(2, 2));
		integerSWidget = new FIBNumber();
		integerSWidget.setNumberType(NumberType.IntegerType);
		integerSWidget.setData(new DataBinding<String>("data.intP+data.intO", integerSWidget, Integer.class, BindingDefinitionType.GET));
		component.addToSubComponents(integerSWidget, new GridLayoutConstraints(3, 2));

		assertTrue(integerPWidget.getData().isValid());
		assertTrue(integerOWidget.getData().isValid());
		assertTrue(integerSWidget.getData().isValid());

		longLabel = new FIBLabel("long");
		component.addToSubComponents(longLabel, new GridLayoutConstraints(0, 3));
		longPWidget = new FIBNumber();
		longPWidget.setNumberType(NumberType.LongType);
		longPWidget.setData(new DataBinding<String>("data.longP", longPWidget, Long.TYPE, BindingDefinitionType.GET_SET));
		component.addToSubComponents(longPWidget, new GridLayoutConstraints(1, 3));
		longOWidget = new FIBNumber();
		longOWidget.setNumberType(NumberType.LongType);
		longOWidget.setData(new DataBinding<String>("data.longO", longOWidget, Long.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(longOWidget, new GridLayoutConstraints(2, 3));
		longSWidget = new FIBNumber();
		longSWidget.setNumberType(NumberType.LongType);
		longSWidget.setData(new DataBinding<String>("data.longP+data.longO", longSWidget, Long.class, BindingDefinitionType.GET));
		component.addToSubComponents(longSWidget, new GridLayoutConstraints(3, 3));

		assertTrue(longPWidget.getData().isValid());
		assertTrue(longOWidget.getData().isValid());
		assertTrue(longSWidget.getData().isValid());

		floatLabel = new FIBLabel("float");
		component.addToSubComponents(floatLabel, new GridLayoutConstraints(0, 4));
		floatPWidget = new FIBNumber();
		floatPWidget.setNumberType(NumberType.FloatType);
		floatPWidget.setData(new DataBinding<String>("data.floatP", floatPWidget, Float.TYPE, BindingDefinitionType.GET_SET));
		component.addToSubComponents(floatPWidget, new GridLayoutConstraints(1, 4));
		floatOWidget = new FIBNumber();
		floatOWidget.setNumberType(NumberType.FloatType);
		floatOWidget.setData(new DataBinding<String>("data.floatO", floatOWidget, Float.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(floatOWidget, new GridLayoutConstraints(2, 4));
		floatSWidget = new FIBNumber();
		floatSWidget.setNumberType(NumberType.FloatType);
		floatSWidget.setData(new DataBinding<String>("data.floatP+data.floatO", floatSWidget, Float.class, BindingDefinitionType.GET));
		component.addToSubComponents(floatSWidget, new GridLayoutConstraints(3, 4));

		assertTrue(floatPWidget.getData().isValid());
		assertTrue(floatOWidget.getData().isValid());
		assertTrue(floatSWidget.getData().isValid());

		doubleLabel = new FIBLabel("double");
		component.addToSubComponents(doubleLabel, new GridLayoutConstraints(0, 5));
		doublePWidget = new FIBNumber();
		doublePWidget.setNumberType(NumberType.DoubleType);
		doublePWidget.setData(new DataBinding<String>("data.doubleP", doublePWidget, Double.TYPE, BindingDefinitionType.GET_SET));
		component.addToSubComponents(doublePWidget, new GridLayoutConstraints(1, 5));
		doubleOWidget = new FIBNumber();
		doubleOWidget.setNumberType(NumberType.DoubleType);
		doubleOWidget.setData(new DataBinding<String>("data.doubleO", doubleOWidget, Double.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(doubleOWidget, new GridLayoutConstraints(2, 5));
		doubleSWidget = new FIBNumber();
		doubleSWidget.setNumberType(NumberType.DoubleType);
		doubleSWidget.setData(new DataBinding<String>("data.doubleP+data.doubleO", doubleSWidget, Double.class, BindingDefinitionType.GET));
		component.addToSubComponents(doubleSWidget, new GridLayoutConstraints(3, 5));

		assertTrue(doublePWidget.getData().isValid());
		assertTrue(doubleOWidget.getData().isValid());
		assertTrue(doubleSWidget.getData().isValid());

	}

	/**
	 * Instanciate component, while instanciating view AFTER data has been set
	 */
	@Test
	public void test2InstanciateComponent() {
		controller = FIBController.instanciateController(component, FlexoLocalization.getMainLocalizer());
		assertNotNull(controller);
		numbers = new Numbers();
		controller.setDataObject(numbers);
		controller.buildView();

		gcDelegate.addTab("Test2", controller);

		assertNotNull(controller.getRootView());

		assertNotNull(controller.viewForComponent(byteLabel));
		assertNotNull(controller.viewForComponent(bytePWidget));
		assertNotNull(controller.viewForComponent(byteOWidget));

		assertNotNull(controller.viewForComponent(shortLabel));
		assertNotNull(controller.viewForComponent(shortPWidget));
		assertNotNull(controller.viewForComponent(shortOWidget));

		assertNotNull(controller.viewForComponent(integerLabel));
		assertNotNull(controller.viewForComponent(integerPWidget));
		assertNotNull(controller.viewForComponent(integerOWidget));

		assertNotNull(controller.viewForComponent(longLabel));
		assertNotNull(controller.viewForComponent(longPWidget));
		assertNotNull(controller.viewForComponent(longOWidget));

		assertNotNull(controller.viewForComponent(floatLabel));
		assertNotNull(controller.viewForComponent(floatPWidget));
		assertNotNull(controller.viewForComponent(floatOWidget));

		assertNotNull(controller.viewForComponent(doubleLabel));
		assertNotNull(controller.viewForComponent(doublePWidget));
		assertNotNull(controller.viewForComponent(doubleOWidget));

		// controller.viewForComponent(firstNameTF).update();

		assertEquals(Byte.valueOf((byte) 1), controller.viewForComponent(bytePWidget).getData());
		assertEquals(Byte.valueOf((byte) 7), controller.viewForComponent(byteOWidget).getData());
		assertEquals(Byte.valueOf((byte) 8), controller.viewForComponent(byteSWidget).getData());

		assertEquals(Short.valueOf((short) 2), controller.viewForComponent(shortPWidget).getData());
		assertEquals(Short.valueOf((short) 8), controller.viewForComponent(shortOWidget).getData());
		assertEquals(Short.valueOf((short) 10), controller.viewForComponent(shortSWidget).getData());

		assertEquals(Integer.valueOf(3), controller.viewForComponent(integerPWidget).getData());
		assertEquals(Integer.valueOf(9), controller.viewForComponent(integerOWidget).getData());
		assertEquals(Integer.valueOf(12), controller.viewForComponent(integerSWidget).getData());

		assertEquals(Long.valueOf(4), controller.viewForComponent(longPWidget).getData());
		assertEquals(Long.valueOf(10), controller.viewForComponent(longOWidget).getData());
		assertEquals(Long.valueOf(14), controller.viewForComponent(longSWidget).getData());

		assertEquals(Float.valueOf(5), controller.viewForComponent(floatPWidget).getData());
		assertEquals(Float.valueOf(11), controller.viewForComponent(floatOWidget).getData());
		assertEquals(Float.valueOf(16), controller.viewForComponent(floatSWidget).getData());

		assertEquals(Double.valueOf(6), controller.viewForComponent(doublePWidget).getData());
		assertEquals(Double.valueOf(12), controller.viewForComponent(doubleOWidget).getData());
		assertEquals(Double.valueOf(18), controller.viewForComponent(doubleSWidget).getData());

	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	public void test3ModifyValueInModel() {
		numbers.setByteP((byte) 101);
		numbers.setByteO(Byte.valueOf((byte) 107));
		assertEquals((byte) 101, controller.viewForComponent(bytePWidget).getData());
		assertEquals((byte) 107, controller.viewForComponent(byteOWidget).getData());
		assertEquals((byte) 208, controller.viewForComponent(byteSWidget).getData());

		numbers.setShortP((short) 102);
		numbers.setShortO(Short.valueOf((short) 108));
		assertEquals((short) 102, controller.viewForComponent(shortPWidget).getData());
		assertEquals((short) 108, controller.viewForComponent(shortOWidget).getData());
		assertEquals((short) 210, controller.viewForComponent(shortSWidget).getData());

		numbers.setIntP((int) 103);
		numbers.setIntO(Integer.valueOf(109));
		assertEquals((int) 103, controller.viewForComponent(integerPWidget).getData());
		assertEquals((int) 109, controller.viewForComponent(integerOWidget).getData());
		assertEquals((int) 212, controller.viewForComponent(integerSWidget).getData());

		numbers.setLongP((long) 104);
		numbers.setLongO(Long.valueOf(110));
		assertEquals((long) 104, controller.viewForComponent(longPWidget).getData());
		assertEquals((long) 110, controller.viewForComponent(longOWidget).getData());
		assertEquals((long) 214, controller.viewForComponent(longSWidget).getData());

		numbers.setFloatP((float) 105);
		numbers.setFloatO(Float.valueOf(111));
		assertEquals((float) 105, controller.viewForComponent(floatPWidget).getData());
		assertEquals((float) 111, controller.viewForComponent(floatOWidget).getData());
		assertEquals((float) 216, controller.viewForComponent(floatSWidget).getData());

		numbers.setDoubleP((double) 106);
		numbers.setDoubleO(Double.valueOf(112));
		assertEquals((double) 106, controller.viewForComponent(doublePWidget).getData());
		assertEquals((double) 112, controller.viewForComponent(doubleOWidget).getData());
		assertEquals((double) 218, controller.viewForComponent(doubleSWidget).getData());

	}

	/**
	 * Update the widget, and check that model has well reacted
	 */
	@Test
	public void test4ModifyValueInWidget() {

		FIBNumberWidget<Byte> bytePWidgetView = (FIBNumberWidget<Byte>) controller.viewForComponent(bytePWidget);
		bytePWidgetView.getDynamicJComponent().setValue((byte) 201);
		assertEquals((byte) 201, numbers.getByteP());

		FIBNumberWidget<Byte> byteOWidgetView = (FIBNumberWidget<Byte>) controller.viewForComponent(byteOWidget);
		byteOWidgetView.getDynamicJComponent().setValue((byte) 207);
		assertEquals(Byte.valueOf((byte) 207), numbers.getByteO());

		assertEquals((byte) 408, controller.viewForComponent(byteSWidget).getData());

		FIBNumberWidget<Short> shortPWidgetView = (FIBNumberWidget<Short>) controller.viewForComponent(shortPWidget);
		shortPWidgetView.getDynamicJComponent().setValue((short) 202);
		assertEquals((short) 202, numbers.getShortP());

		FIBNumberWidget<Short> shortOWidgetView = (FIBNumberWidget<Short>) controller.viewForComponent(shortOWidget);
		shortOWidgetView.getDynamicJComponent().setValue((short) 208);
		assertEquals(Short.valueOf((short) 208), numbers.getShortO());

		assertEquals((short) 410, controller.viewForComponent(shortSWidget).getData());

		FIBNumberWidget<Integer> integerPWidgetView = (FIBNumberWidget<Integer>) controller.viewForComponent(integerPWidget);
		integerPWidgetView.getDynamicJComponent().setValue((int) 203);
		assertEquals((int) 203, numbers.getIntP());

		FIBNumberWidget<Integer> integerOWidgetView = (FIBNumberWidget<Integer>) controller.viewForComponent(integerOWidget);
		integerOWidgetView.getDynamicJComponent().setValue((int) 209);
		assertEquals(Integer.valueOf((int) 209), numbers.getIntO());

		assertEquals((int) 412, controller.viewForComponent(integerSWidget).getData());

		FIBNumberWidget<Long> longPWidgetView = (FIBNumberWidget<Long>) controller.viewForComponent(longPWidget);
		longPWidgetView.getDynamicJComponent().setValue((long) 204);
		assertEquals((long) 204, numbers.getLongP());

		FIBNumberWidget<Long> longOWidgetView = (FIBNumberWidget<Long>) controller.viewForComponent(longOWidget);
		longOWidgetView.getDynamicJComponent().setValue((long) 210);
		assertEquals(Long.valueOf((long) 210), numbers.getLongO());

		assertEquals((long) 414, controller.viewForComponent(longSWidget).getData());

		FIBNumberWidget<Float> floatPWidgetView = (FIBNumberWidget<Float>) controller.viewForComponent(floatPWidget);
		floatPWidgetView.getDynamicJComponent().setValue((float) 205);
		assertEquals(205, numbers.getFloatP(), 0.000001);

		FIBNumberWidget<Float> floatOWidgetView = (FIBNumberWidget<Float>) controller.viewForComponent(floatOWidget);
		floatOWidgetView.getDynamicJComponent().setValue((float) 211);
		assertEquals(Float.valueOf(211), numbers.getFloatO(), 0.000001);

		assertEquals((float) 416, controller.viewForComponent(floatSWidget).getData());

		FIBNumberWidget<Double> doublePWidgetView = (FIBNumberWidget<Double>) controller.viewForComponent(doublePWidget);
		doublePWidgetView.getDynamicJComponent().setValue((double) 205);
		assertEquals(205, numbers.getDoubleP(), 0.000001);

		FIBNumberWidget<Double> doubleOWidgetView = (FIBNumberWidget<Double>) controller.viewForComponent(doubleOWidget);
		doubleOWidgetView.getDynamicJComponent().setValue((double) 211);
		assertEquals(Double.valueOf(211), numbers.getDoubleO(), 0.000001);

		assertEquals((double) 416, controller.viewForComponent(doubleSWidget).getData());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new GraphicalContextDelegate();
	}

	@AfterClass
	public static void waitGUI() {
		gcDelegate.waitGUI();
	}

	@Before
	public void setUp() {
		gcDelegate.setUp();
	}

	@After
	public void tearDown() throws Exception {
		gcDelegate.tearDown();
	}

}
