package com.mzs.guaji.adapter.factory;

import android.content.Context;
import android.view.View;

import com.google.gson.JsonElement;

public interface ViewHolder {

	public void build(View v);
	
	public View renderView(Context context, JsonElement param);

    public String getType();
	
	
}
