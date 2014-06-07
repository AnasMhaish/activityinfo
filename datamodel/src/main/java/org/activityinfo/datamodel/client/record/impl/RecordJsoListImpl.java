package org.activityinfo.datamodel.client.record.impl;

import com.google.gwt.core.client.JavaScriptObject;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * An JavaScript overlay for JavaScript arrays that implements the List interface.
 *
 * Note that only ONE JavaScript overlay can implement a given interface, so
 * throughout AI, no other subclass of JavaScriptObject can implement java.util.List.
 *
 * @param <T>
 */
public final class RecordJsoListImpl<T extends JavaScriptObject> extends JavaScriptObject implements List<T>  {

    protected RecordJsoListImpl() {
    }

    @Override
    public native int size() /*-{
        return this.length;
    }-*/;

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index+ 1 < size();
            }

            @Override
            public T next() {
                return get(index++);
            }

            @Override
            public void remove() {
                RecordJsoListImpl.this.remove(--index);
            }
        };
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public native boolean add(T t) /*-{
        this.push(t);
        return true;
    }-*/;

    @Override
    public native boolean remove(Object o) /*-{
        return false;
    }-*/;

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public native void clear() /*-{
        this.splice(0, this.length);
    }-*/;

    @Override
    public native T get(int index) /*-{
        return this[index];
    }-*/;

    @Override
    public native T set(int index, T element) /*-{
        this[index] = element;
    }-*/;

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public native T remove(int index) /*-{
        var item = this[index];
        this.splice(index, 1);
        return item;
    }-*/;

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }
}
