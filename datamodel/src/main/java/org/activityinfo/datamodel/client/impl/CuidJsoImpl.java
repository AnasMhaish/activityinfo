package org.activityinfo.datamodel.client.impl;

/**
 * Provides an implementation or the browser so that
 * Cuid will compile away into Javascript.
 *
 */
public class CuidJsoImpl {

    protected CuidJsoImpl() {
    }

    public static native CuidJsoImpl create(String string) /*-{
      return string;
    }-*/;


    public native String asString() /*-{
      return this;
    }-*/;
}
