package org.openflexo.antar.binding;

public class TargetObject {
	public Object target;
	public String propertyName;

	public TargetObject(Object target, String propertyName) {
		super();
		this.target = target;
		this.propertyName = propertyName;
	}

	@Override
	public String toString() {
		return "TargetObject[" + target + "/" + propertyName + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TargetObject) {
			TargetObject t = (TargetObject) obj;
			return target == t.target && propertyName != null && propertyName.equals(t.propertyName);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return target.hashCode() + (propertyName == null ? 0 : propertyName.hashCode());
	}
}