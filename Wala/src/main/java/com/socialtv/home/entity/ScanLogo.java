package com.socialtv.home.entity;

import com.socialtv.Response;
import com.socialtv.home.Scan;

/**
 * Created by wlanjie on 14-7-21.
 */
public class ScanLogo extends Response {

    private Scan scan;

    public Scan getScan() {
        return scan;
    }

    public void setScan(Scan scan) {
        this.scan = scan;
    }
}
