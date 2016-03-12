package com.jiujie8.choice.persioncenter.event;

import in.srain.cube.request.JsonData;

/**
 * Created by srain on 10/2/14.
 */
public class BaseJsonDataEvent {
    public JsonData data;

    public BaseJsonDataEvent(JsonData jsonData) {
        data = jsonData;
    }
}
