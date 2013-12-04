package org.openflexo.fib.sampleData;

public class Numbers extends SampleData {

	private byte byteP;
	private short shortP;
	private int intP;
	private long longP;
	private float floatP;
	private double doubleP;

	private Byte byteO;
	private Short shortO;
	private Integer intO;
	private Long longO;
	private Float floatO;
	private Double doubleO;

	public Numbers() {
		super();
		this.byteP = 1;
		this.shortP = 2;
		this.intP = 3;
		this.longP = 4;
		this.floatP = 5.0f;
		this.doubleP = 6.0;
		this.byteO = 7;
		this.shortO = 8;
		this.intO = 9;
		this.longO = Long.valueOf(10);
		this.floatO = 11.0f;
		this.doubleO = 12.0;
	}

	public byte getByteP() {
		return byteP;
	}

	public void setByteP(byte byteP) {
		byte oldByteP = this.byteP;
		this.byteP = byteP;
		getPropertyChangeSupport().firePropertyChange("byteP", oldByteP, byteP);
	}

	public short getShortP() {
		return shortP;
	}

	public void setShortP(short shortP) {
		short oldShortP = this.shortP;
		this.shortP = shortP;
		getPropertyChangeSupport().firePropertyChange("shortP", oldShortP, shortP);
	}

	public int getIntP() {
		return intP;
	}

	public void setIntP(int intP) {
		int oldIntP = this.intP;
		this.intP = intP;
		getPropertyChangeSupport().firePropertyChange("intP", oldIntP, intP);
	}

	public long getLongP() {
		return longP;
	}

	public void setLongP(long longP) {
		long oldLongP = this.longP;
		this.longP = longP;
		getPropertyChangeSupport().firePropertyChange("longP", oldLongP, longP);
	}

	public float getFloatP() {
		return floatP;
	}

	public void setFloatP(float floatP) {
		float oldFloatP = this.floatP;
		this.floatP = floatP;
		getPropertyChangeSupport().firePropertyChange("floatP", oldFloatP, floatP);
	}

	public double getDoubleP() {
		return doubleP;
	}

	public void setDoubleP(double doubleP) {
		double oldDoubleP = this.doubleP;
		this.doubleP = doubleP;
		getPropertyChangeSupport().firePropertyChange("doubleP", oldDoubleP, doubleP);
	}

	public Byte getByteO() {
		return byteO;
	}

	public void setByteO(Byte byteO) {
		Byte oldByteO = this.byteO;
		this.byteO = byteO;
		getPropertyChangeSupport().firePropertyChange("byteO", oldByteO, byteO);
	}

	public Short getShortO() {
		return shortO;
	}

	public void setShortO(Short shortO) {
		Short oldShortO = this.shortO;
		this.shortO = shortO;
		getPropertyChangeSupport().firePropertyChange("shortO", oldShortO, shortO);
	}

	public Integer getIntO() {
		return intO;
	}

	public void setIntO(Integer intO) {
		Integer oldIntO = this.intO;
		this.intO = intO;
		getPropertyChangeSupport().firePropertyChange("intO", oldIntO, intO);
	}

	public Long getLongO() {
		return longO;
	}

	public void setLongO(Long longO) {
		Long oldLongO = this.longO;
		this.longO = longO;
		getPropertyChangeSupport().firePropertyChange("longO", oldLongO, longO);
	}

	public Float getFloatO() {
		return floatO;
	}

	public void setFloatO(Float floatO) {
		Float oldFloatO = this.floatO;
		this.floatO = floatO;
		getPropertyChangeSupport().firePropertyChange("floatO", oldFloatO, floatO);
	}

	public Double getDoubleO() {
		return doubleO;
	}

	public void setDoubleO(Double doubleO) {
		Double oldDoubleO = this.doubleO;
		this.doubleO = doubleO;
		getPropertyChangeSupport().firePropertyChange("doubleO", oldDoubleO, doubleO);
	}

}