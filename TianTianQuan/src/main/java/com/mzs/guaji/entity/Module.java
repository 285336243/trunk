package com.mzs.guaji.entity;

public class Module {
	private int type;
	private String name;
	private int resourceId;
	
	public Module(int type, String name, int resourceId) {
		super();
		this.type = type;
		this.name = name;
		this.resourceId = resourceId;
	}
	public int getType() {
		return type;
	}
	public String getName() {
		return name;
	}
	public int getResourceId() {
		return resourceId;
	}

	
}
