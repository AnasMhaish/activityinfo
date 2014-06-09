package org.activityinfo.datamodel.shared;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Provides an implementation for the browser so that
 * Cuid will compile away into a simple JavaScript string.
 *
 */
public final class Cuid extends JavaScriptObject {

    protected Cuid() {
    }

    public static native Cuid create(String string) /*-{
      return string;
    }-*/;

    public native String asString() /*-{
      return this;
    }-*/;

    public char getDomain() {
        return asString().charAt(0);
    }
}
