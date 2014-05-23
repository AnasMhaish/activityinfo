package org.activityinfo.core.shared;

import java.io.Serializable;

/**
 * Internationalized Resource Identifier
 */
public class Iri implements Serializable {

    private final String string;

    public Iri(String string) {
        this.string = string;
    }

    /**
     * @return an encoded string representation of the IRI
     */
    public String asString() {
        return string;
    }

    @Override
    public String toString() {
        return "<" + string + ">";
    }

    /**
     *
     * @return the IRI's scheme (for example, http, cuid, etc)
     */
    public String getScheme() {
        return string.substring(0, string.indexOf(':'));

    }

    /**
     *
     * @return the scheme-specific part of the IRI
     */
    public String getSchemeSpecificPart() {
        return string.substring(string.indexOf(':')+1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Iri iri = (Iri) o;

        return !(string != null ? !string.equals(iri.string) : iri.string != null);
    }

    @Override
    public int hashCode() {
        return string != null ? string.hashCode() : 0;
    }
}
