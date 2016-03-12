package com.jiujie8.choice.http.entity;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 */
public class UploadMultipartEntity extends MultipartEntity {

    private OnProgressListener listener;
    private long offset;

    public void setListener(OnProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        if (listener == null) {
            super.writeTo(outstream);
        } else {
            super.writeTo(new CountingOutputStream(outstream, offset, this.listener));
        }
    }
    
    class CountingOutputStream  extends FilterOutputStream {
        private final OnProgressListener listener;
        private long transferred;
        private long offset;

        public CountingOutputStream(final OutputStream out, long offset, final OnProgressListener listener) {
            super(out);
            this.listener = listener;
            this.transferred = 0;
            this.offset = offset;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            this.transferred += len;
            this.listener.transferred(this.transferred + offset, getContentLength());
        }

        public void write(int b) throws IOException {
            out.write(b);
            this.transferred++;
            this.listener.transferred(this.transferred + offset, getContentLength());
        }
        
        @Override
        public void write(byte[] buffer) throws IOException {
            out.write(buffer);
            this.transferred += buffer.length;
            this.listener.transferred(this.transferred + offset, getContentLength());
        }
        
    }

    public static interface OnProgressListener {
        void transferred(long current, long total);
    }
}
