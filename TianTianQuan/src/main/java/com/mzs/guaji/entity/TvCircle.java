package com.mzs.guaji.entity;

import java.util.List;

import com.google.gson.annotations.Expose;

public class TvCircle extends GuaJiResponse{

	@Expose
	private Epg epg;
	@Expose
	private List<Activity> activities;
	@Expose
	private List<Group> groups;
    @Expose
    private List<EntryForm> entryForms;
	public Epg getEpg() {
		return epg;
	}
	public void setEpg(Epg epg) {
		this.epg = epg;
	}
	public List<Activity> getActivities() {
		return activities;
	}
	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}
	public List<Group> getGroups() {
		return groups;
	}
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

    public List<EntryForm> getEntryForms() {
        return entryForms;
    }

    public void setEntryForms(List<EntryForm> entryForms) {
        this.entryForms = entryForms;
    }
}
