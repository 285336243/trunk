package com.mzs.guaji.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic resource pager for elements with an id that can be paged
 * Created by wlanjie on 14-2-15.
 */
public abstract class ResourcePager<E> {

    /**
     * Next page to request
     */
    protected int page = 1;

    /**
     * Number of pages to request
     */
    protected int count = 1;

    /**
     * NetWork request count
     */
    protected int requestCount = 20;

    /**
     * All resources retrieved
     */
    protected final Map<Object, E> resources = new LinkedHashMap<Object, E>();

    /**
     * Are more pages available?
     */
    protected boolean hasMore = true;

    /**
     * Reset the number of the next page to be requested from {@link #next()}
     * and clear all stored state
     *
     * @return this pager
     */
    public ResourcePager<E> reset() {
        page = 1;
        return clear();
    }

    /**
     * Clear all stored resources and have the next call to {@link #next()} load
     * all previously loaded pages
     *
     * @return this pager
     */
    public ResourcePager<E> clear() {
        count = Math.max(1, page - 1);
        page = 1;
        resources.clear();
        hasMore = true;
        return this;
    }

    /**
     * Get number of resources loaded into this pager
     *
     * @return number of resources
     */
    public int size() {
        return resources.size();
    }

    /**
     * Get resources
     *
     * @return resources
     */
    public List<E> getResources() {
        return new ArrayList<E>(resources.values());
    }

    public E get() {
        return new ArrayList<E>(resources.values()).get(0);
    }

    /**
     * Get the next page of issues
     *
     * @return true if more pages
     * @throws java.io.IOException
     */
    public boolean next() throws Exception {
        PageIterator<E> iterator = createIterator(page, requestCount);
        try {
            for (int i=0; i<count && iterator.hasNext(); i++) {
                Collection<E> resourcePage = iterator.next();
                for (E resource : resourcePage) {
                    resource = register(resource);
                    if (resource == null) {
                        continue;
                    }
                    resources.put(getId(resource), resource);
                }
            }
            if (count > 1) {
                page = count;
                count = 1;
            }
            page++;
        } catch (Exception e) {
            hasMore = false;
            throw e;
        }
        return hasMore;
    }

    /**
     * Set List judge is empty
     * @param data
     */
    public void setList(List<?> data) {
        if (data != null) {
            if (!data.isEmpty() && data.size() >= count) {
                hasMore = true;
            }
        } else {
            hasMore = false;
        }
    }

    public E start() throws Exception {
        next();
        return get();
    }

    /**
     * Are more pages available to request?
     *
     * @return true if the last call to {@link #next()} returned true, false
     *         otherwise
     */
    public boolean hasMore() {
        return hasMore;
    }

    /**
     * Callback to register a fetched resource before it is stored in this pager
     * <p>
     * Sub-classes may override
     *
     * @param resource
     * @return resource
     */
    protected E register(final E resource) {
        return resource;
    }

    /**
     * Get id for resource
     *
     * @param resource
     * @return id
     */
    protected Object getId(E resource) {
        return null;
    }

    /**
     * Create iterator to return given page and size
     *
     * @param page
     * @param count
     * @return iterator
     */
    public abstract PageIterator<E> createIterator(final int page, final int count);
}
