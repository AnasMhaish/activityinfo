package org.activityinfo.io.odk.xform;

import javax.xml.bind.annotation.XmlElement;

public class Item {
    @XmlElement
    public String label;

    @XmlElement
    public String value;
}
