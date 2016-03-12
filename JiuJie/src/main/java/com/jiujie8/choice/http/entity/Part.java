package com.jiujie8.choice.http.entity;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 */
public interface Part {
    public long getContentLength(Boundary boundary);
    public void writeTo(final OutputStream out, Boundary boundary) throws IOException;
}
