package com.kicinger.openjms.messages.objectmessages;

import java.io.Serializable;

public class Car implements Serializable {

	private String model;
	private String make;
	
	public Car() {
		super();
	}

	public Car(String model, String make) {
		super();
		this.model = model;
		this.make = make;
	}

	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getMake() {
		return make;
	}
	public void setMake(String make) {
		this.make = make;
	}
	
	@Override
	public String toString() {
		return "Car [model=" + model + ", make=" + make + "]";
	}
	
}
