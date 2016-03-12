/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package com.socialtv.core;

import android.content.Context;

import com.android.volley.PagedRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base GitHub service class.
 */
public abstract class AbstractService {
    /**
     * First page
     */
    public static final int PAGE_FIRST = 1;

    /**
     * Default page size
     */
    public static final int PAGE_SIZE = 100;

    /**
     * Unified request creation method that all sub-classes should use so
     * overriding classes can extend and configure the default request.
     *
     * @return request
     */
    protected PagedRequest createRequest() {
        return new PagedRequest();
    }

    /**
     * Unified paged request creation method that all sub-classes should use so
     * overriding classes can extend and configure the default request.
     *
     * @return request
     */
    protected <V> PagedRequest<V> createPagedRequest() {
        return createPagedRequest(PAGE_FIRST, PAGE_SIZE);
    }

    /**
     * Unified paged request creation method that all sub-classes should use so
     * overriding classes can extend and configure the default request.
     *
     * @param start
     * @param size
     * @return request
     */
    protected <V> PagedRequest<V> createPagedRequest(int start, int size) {
        return new PagedRequest<V>(start, size);
    }

    /**
     * Unified page iterator creation method that all sub-classes should use so
     * overriding classes can extend and configure the default iterator.
     *
     * @param request
     * @return iterator
     */
    protected <V> PageIterator<V> createPageIterator(Context context, PagedRequest<V> request) {
        return new PageIterator<V>(context, request);
    }

    /**
     * Get paged request by performing multiple requests until no more pages are
     * available or an exception occurs.
     *
     * @param <V>
     * @param request
     * @return list of all elements
     * @throws java.io.IOException
     */
    protected <V> List<V> getAll(Context context, PagedRequest<V> request) throws IOException {
        return getAll(createPageIterator(context, request));
    }

    /**
     * Get paged request by performing multiple requests until no more pages are
     * available or an exception occurs.
     *
     * @param <V>
     * @param iterator
     * @return list of all elements
     * @throws java.io.IOException
     */
    protected <V> List<V> getAll(PageIterator<V> iterator) throws IOException {
        List<V> elements = new ArrayList<V>();
        try {
            while (iterator.hasNext())
                elements.addAll(iterator.next());
        } catch (NoSuchPageException pageException) {
            throw pageException.getCause();
        }
        return elements;
    }
}
