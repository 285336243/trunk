/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package com.mzs.guaji.core;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.PagedRequest;
import com.android.volley.SynchronizationHttpRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Iterator for getting paged responses. Each call to {@link #next()} will make
 * a client request for the next page of resources using the URI returned from
 * the previous request.
 *
 * The {@link #hasNext()} method can be used to determine if the last executed
 * request contained the location of the next page of results.
 *
 * This iterator also provides the next and last page numbers as well as the
 * next and last URIs.
 *
 * @param <V>
 *            type of resource being iterated over
 */
public class PageIterator<V> implements Iterator<Collection<V>>,
        Iterable<Collection<V>> {

    /**
     * Request
     */
    protected final PagedRequest request;

    /**
     * Current page number
     */
    protected int nextPage;

    /**
     * Next uri to be fetched
     */
    protected String next;

    /**
     * Last uri to be fetched
     */
    protected String last;

    private final Context context;

    /**
     * Create page iterator
     *
     * @param request
     */
    public PageIterator(Context context, PagedRequest request) {
        this.request = request;
        this.context = context;
    }

    public boolean hasNext() {
        return nextPage == 0 || next != null;
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove not supported"); //$NON-NLS-1$
    }

    @SuppressWarnings("unchecked")
    public Collection<V> next() {
        SynchronizationHttpRequest httpRequest = null;
        if (request.getBodyMap() == null && TextUtils.isEmpty(request.getDelete()))
            httpRequest = RequestUtils.getInstance().createGet(context, request.getUri(), null);
        else if (!TextUtils.isEmpty(request.getDelete()))
            httpRequest = RequestUtils.getInstance().createDelete(context, request.getUri(), null);
        else if (request.getBodyMap() != null)
            httpRequest = RequestUtils.getInstance().createPost(context, request.getUri(), request.getBodyMap(), null);
        Object body = httpRequest.getResponse(request);
        Collection<V> resources = null;
        if (body != null) {
            if (body instanceof Collection) {
                resources = (Collection<V>) body;
            } else if (body instanceof IResourceProvider) {
                resources = ((IResourceProvider<V>) body).getResources();
            } else {
                resources = (Collection<V>) Collections.singletonList(body);
            }
        }
        if (resources == null) {
            resources = Collections.emptyList();
        }
        return resources;
    }

    /**
     * Get request being executed
     *
     * @return request
     */
    public PagedRequest getRequest() {
        return request;
    }

    /**
     * @return this page iterator
     */
    public Iterator<Collection<V>> iterator() {
        return this;
    }
}
