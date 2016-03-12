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
package com.android.volley;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Paged request class that contains the initial page size and page number of
 * the request.
 *
 */
public class PagedRequest<V>  {

	/**
	 * First page
	 */
	public static final int PAGE_FIRST = 1;

	/**
	 * Default page size
	 */
	public static final int PAGE_SIZE = 100;

	private final int pageSize;

	private final int page;

    private Type arrayType;

    private Type type;

    private String uri;

    private Class<?> clazz;

    private Map<String, String> bodyMap;

    private String delete;

    /**
	 * Create paged request with default size
	 */
	public PagedRequest() {
		this(PAGE_FIRST, PAGE_SIZE);
	}

	/**
	 * Create paged request with given starting page and page size.
	 *
	 * @param start
	 * @param size
	 */
	public PagedRequest(int start, int size) {
		page = start;
		pageSize = size;
	}

	/**
	 * Get initial page size
	 *
	 * @return pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}


    /**
     * Set type to expect if first token is a beginning of an array
     *
     * @param arrayType
     * @return this request
     */
    public PagedRequest setArrayType(Type arrayType) {
        this.arrayType = arrayType;
        return this;
    }

    /**
     * @return arrayType
     */
    public Type getArrayType() {
        return arrayType;
    }

    /**
     * @return type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type
     * @return this request
     */
    public PagedRequest setType(Type type) {
        this.type = type;
        return this;
    }

    public void setBodyMap(Map<String, String> bodyMap) {
        this.bodyMap = bodyMap;
    }

    public Map<String, String> getBodyMap() {
        return bodyMap;
    }


    /**
     * @param uri
     * @return this request
     */
    public PagedRequest setUri(StringBuilder uri) {
        return setUri(uri != null ? uri.toString() : null);
    }

    /**
     * @param uri
     * @return this request
     */
    public PagedRequest setUri(String uri) {
        this.uri = uri;
        return this;
    }

    /**
     * @return uri
     */
    public String getUri() {
        return uri;
    }

	/**
	 * Get initial page number
	 *
	 * @return page
	 */
	public int getPage() {
		return page;
	}

    public Class<?> getClazz() {
        return clazz;
    }

    public PagedRequest setClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }
}
