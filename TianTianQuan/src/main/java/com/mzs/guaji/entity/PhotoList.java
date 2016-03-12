package com.mzs.guaji.entity;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;

public class PhotoList extends GuaJiResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Expose
	private List<Pic> pics;

	public List<Pic> getPics() {
		return pics;
	}

	public void setPics(List<Pic> pics) {
		this.pics = pics;
	}
	
}
