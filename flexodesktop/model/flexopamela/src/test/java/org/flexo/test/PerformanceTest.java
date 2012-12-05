package org.flexo.test;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openflexo.model.ModelContext;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

/**
 * Class of tests to measure the performance of pamela vs regular written classes.
 * 
 * Tests are made of:
 * <ol>
 * <li>A model interface and a default implementation</li>
 * <li>Test runnables: one kind of operation performed once with a pamela implementation and once with regular classes</li>
 * <li>Tests: which simply executes the different runnables in a given order</li>
 * </ol>
 * The execution time and the used memory are automatically computed and printed to the error console. Theses measures are to be taken with
 * great care since execution time can always be influenced by the workload of the computer on which it is executed, while the memory
 * measure can always be influenced by the GC running in a separate thread.
 * 
 * @author Guillaume
 * 
 */
public class PerformanceTest {

	/**
	 * The result of a TestRunnable.
	 * 
	 * @author Guillaume
	 * 
	 */
	public static class TestRunnableResult {
		public Object returned;
		public long usedMemory;
		public long usedTime;
	}

	/**
	 * An operation that needs to be run with pamela and regular classes. Different operations can provide performance measure to compare
	 * pamela agains traditional classes.
	 * 
	 * @author Guillaume
	 * 
	 */
	public static interface TestRunnable {
		/**
		 * The operation that will be executed.
		 * 
		 * @param factory
		 *            if the factory is not null, then it should be used to build pamela objects, else the default implementation should
		 *            rather be used.
		 * @return the root object of the model (in order to keep a reference to the model and avoid the GC to garbage collect the memory
		 *         before the memory footprint has been computed).
		 */
		public Object run(ModelFactory factory);
	}

	/**
	 * A simple model object which defines a hierarchy.
	 * 
	 * @author Guillaume
	 * 
	 */
	@ModelEntity
	public static interface ModelObject {

		public static final String CHILDREN = "children";
		public static final String PARENT = "parent";

		@Getter(value = PARENT, inverse = CHILDREN)
		public ModelObject getParent();

		@Setter(PARENT)
		public void setParent(ModelObject parent);

		@Getter(value = CHILDREN, cardinality = Cardinality.LIST, inverse = PARENT)
		public List<ModelObject> getChildren();

		@Setter(CHILDREN)
		public void setChildren(List<ModelObject> children);

		@Adder(CHILDREN)
		public void addToChildren(ModelObject child);

		@Remover(CHILDREN)
		public void removeFromChildren(ModelObject child);
	}

	/**
	 * A default implementation.
	 * 
	 * @author Guillaume
	 * 
	 */
	public static class ModelObjectImpl implements ModelObject {

		private List<ModelObject> children;
		private ModelObject parent;

		@Override
		public ModelObject getParent() {
			return parent;
		}

		@Override
		public void setParent(ModelObject parent) {
			if (this.parent != parent) {
				if (this.parent != null) {
					this.parent.removeFromChildren(this);
				}
				this.parent = parent;
				if (this.parent != null) {
					this.parent.addToChildren(this);
				}
			}
		}

		@Override
		public List<ModelObject> getChildren() {
			return children;
		}

		@Override
		public void setChildren(List<ModelObject> children) {
			this.children = children;
		}

		@Override
		public void addToChildren(ModelObject child) {
			if (children == null) {
				children = new ArrayList<PerformanceTest.ModelObject>();
			}
			if (!children.contains(child)) {
				children.add(child);
				child.setParent(this);
			}
		}

		@Override
		public void removeFromChildren(ModelObject child) {
			if (children == null) {
				return;
			}
			if (children.contains(child)) {
				children.remove(child);
				child.setParent(null);
			}
		}

	}

	/**
	 * Simply instantiate a rather big model (about 100 000 objects with a tree depth of 7).
	 * 
	 * @author Guillaume
	 * 
	 */
	public static class BuildBasicModelRunnable implements TestRunnable {
		@Override
		public Object run(ModelFactory factory) {
			return buildModel(5, 7, factory); // 97656 objects
		}
	}

	/**
	 * This test runnable removes and re-adds recursively all children of the model.
	 * 
	 * @author Guillaume
	 * 
	 */
	public static class ManipulateBasicModelRunnable implements TestRunnable {

		private void removeAndReaddChildren(ModelObject object) {
			if (object.getChildren() != null && object.getChildren().size() > 0) {
				List<ModelObject> children = new ArrayList<PerformanceTest.ModelObject>(object.getChildren());
				for (ModelObject child : children) {
					object.removeFromChildren(child);
					removeAndReaddChildren(child);
					object.addToChildren(child);
				}
			}
		}

		@Override
		public Object run(ModelFactory factory) {
			ModelObject object = buildModel(5, 4, factory);
			removeAndReaddChildren(object);
			return object;
		}
	}

	/**
	 * A really simple model holding 1000 objects with a single level of hierarchy (1 root object and 999 children)
	 * 
	 * @author Guillaume
	 * 
	 */
	public static class DumbModelRunnable implements TestRunnable {

		@Override
		public Object run(ModelFactory factory) {
			ModelObject // object = buildModel(99999, 1, factory);
			object = buildModel(999, 1, factory);// 1000 objects
			return object;
		}
	}

	/**
	 * A method to build a hierarchical model where all objects have <code>numbeOfChildren</code> children (except the leaves of the tree)
	 * and the depth of the tree is defined by <code>depth</code>
	 * 
	 * @param numberOfChildren
	 *            the number of children each objects of the hierarchy should have
	 * @param depth
	 *            the depth of the hierarchy
	 * @param factory
	 *            the factory to use to build the model with pamela, else the factory is null
	 * @return the root object of the model.
	 */
	public static ModelObject buildModel(int numberOfChildren, int depth, ModelFactory factory) {
		ModelObject object;
		if (factory != null) {
			object = factory.newInstance(ModelObject.class, null);
		} else {
			object = new ModelObjectImpl();
		}
		if (depth > 0) {
			for (int i = 0; i < numberOfChildren; i++) {
				ModelObject child = buildModel(numberOfChildren, depth - 1, factory);
				object.addToChildren(child);
			}
		}
		return object;
	}

	/**
	 * Runs a TestRunnable <code>runnable</code> with the given <code>factory</code>. The factory may be null.
	 * 
	 * @param factory
	 *            the factory to pass to the TestRunnable; may be null.
	 * @param runnable
	 *            the TestRunnable to run
	 * @return the TestResult, ie, time execution, memory footprint and the root object of the model
	 */
	private TestRunnableResult runRunnable(ModelFactory factory, TestRunnable runnable) {
		TestRunnableResult result = new TestRunnableResult();
		long startMem, endMem, start, end;
		startMem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
		start = System.currentTimeMillis();
		result.returned = runnable.run(factory);
		end = System.currentTimeMillis();
		result.usedTime = end - start;
		endMem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
		result.usedMemory = endMem - startMem;
		return result;
	}

	/**
	 * Method to invoke to run a test runnable 20 times in a row, 10 times using pamela objects and 10 times using regular objects.
	 * Eventually, the method prints out the result.
	 * 
	 * @param runnable
	 *            the test runnable to execute.
	 * @param factory
	 *            the factory to pass to the TestRunnable when using pamela objects. Cannot be null
	 */
	private void testModel(TestRunnable runnable, ModelFactory factory, ModelFactory factory2) {
		long proxyTime = 0, proxyMem = 0, regularTime = 0, regularMem = 0;
		for (int i = 0; i < 10; i++) {
			TestRunnableResult result = runRunnable(factory2, runnable);
			if (i > 0) {
				regularTime += result.usedTime;
				regularMem += result.usedMemory;
			}
			System.gc();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}

			result = runRunnable(factory, runnable);
			if (i > 0) {
				proxyTime += result.usedTime;
				proxyMem += result.usedMemory;
			}
			result = null;
			System.gc();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}

		}
		System.err.println("Test " + runnable.getClass().getSimpleName());
		System.err.println("\tUsing PAMELA " + " took: " + proxyTime + "ms");
		System.err.println("\tUsing regular classes took: " + regularTime + "ms");
		System.err.println("\tPAMELA is " + (double) proxyTime / regularTime + " slower than regular classes");
		System.err.println("\tUsing PAMELA " + " took: " + proxyMem + " bytes");
		System.err.println("\tUsing regular classes took: " + regularMem + " bytes");
		System.err.println("\tPAMELA eats " + (double) proxyMem / regularMem + " more memory than regular classes");
	}

	public static void main(String[] args) throws ModelDefinitionException {
		PerformanceTest test = new PerformanceTest();
		ModelContext mapping = new ModelContext(ModelObject.class);
		ModelFactory factory = new ModelFactory(mapping);
		factory.setListImplementationClass(ArrayList.class);
		ModelFactory factory2 = new ModelFactory(mapping);
		factory.setListImplementationClass(Vector.class);
		test.testModel(new DumbModelRunnable(), factory, factory2);
		test.testModel(new BuildBasicModelRunnable(), factory, factory2);
		test.testModel(new ManipulateBasicModelRunnable(), factory, factory2);
	}
}
