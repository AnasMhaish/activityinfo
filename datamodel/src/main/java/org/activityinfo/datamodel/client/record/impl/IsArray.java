package org.activityinfo.datamodel.client.record.impl;

public class IsArray {


    public static native void ensurePresent() /*-{
      if(!$wnd.Array.isArray) {
        $wnd.Array.isArray = function(arg) {
          return $wnd.Object.prototype.toString.call(arg) === '[object Array]';
        };
      }
    }-*/;
}
