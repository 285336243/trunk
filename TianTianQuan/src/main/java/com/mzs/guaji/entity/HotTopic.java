package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by sunjian on 13-12-21.
 */
public class HotTopic extends ToPicSearch{

    @Expose
    private List<EntryForm> entryForms;

    public List<EntryForm> getEntryForms() {
        return entryForms;
    }

    public void setEntryForms(List<EntryForm> entryForms) {
        this.entryForms = entryForms;
    }
}
