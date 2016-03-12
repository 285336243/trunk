package com.shengzhish.xyj.gallery;

import android.content.Context;

import com.android.volley.PagedRequest;
import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.shengzhish.xyj.core.AbstractService;
import com.shengzhish.xyj.core.PageIterator;
import com.shengzhish.xyj.gallery.entity.Gallery;
import com.shengzhish.xyj.gallery.entity.GalleryPost;
import com.shengzhish.xyj.http.GsonRequest;

/**
 * Created by wlanjie on 14-6-3.
 */
public class GalleryServices extends AbstractService {

    private final static String URL = "gallery/read.json?id=%s";
    private final static String POST_URL = "gallery/post.json?id=%s&cusorId=%s&cnt=%s";

    @Inject
    private Context context;

    public PageIterator<Gallery> pageGallery(final String id) {
        PagedRequest<Gallery> request = createRequest();
        request.setUri(String.format(URL, id));
        request.setType(new TypeToken<Gallery>(){}.getType());
        return createPageIterator(context, request);
    }

    public PageIterator<GalleryPost> pageGalleryPost(final String id, final String cusorId, final int count) {
        PagedRequest<GalleryPost> request = createRequest();
        request.setUri(String.format(POST_URL, id, cusorId, count));
        request.setType(new TypeToken<GalleryPost>(){}.getType());
        return createPageIterator(context, request);
    }

    public Request<Gallery> createGalleryRequest(final String id) {
        GsonRequest<Gallery> request = new GsonRequest<Gallery>(Request.Method.GET, String.format(URL, id));
        request.setClazz(Gallery.class);
        return request;
    }

    public Request<GalleryPost> createGalleryPostRequest(final String id, final String cursorId, final int count) {
        GsonRequest<GalleryPost> request = new GsonRequest<GalleryPost>(Request.Method.GET, String.format(POST_URL, id, cursorId, count));
        request.setClazz(GalleryPost.class);
        return request;
    }
}
