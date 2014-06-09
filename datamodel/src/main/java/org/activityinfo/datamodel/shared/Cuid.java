package org.activityinfo.datamodel.shared;


/**
 * Collision-Resistant Unique ID.
 *
 * Note that this type will disappear when compiled to Javascript:
 * it is replaced at compile time by a simple string value
 *
 *
 */
public final class Cuid {
    private final String text;

    public static Cuid create(String string) {
        return new Cuid(string);
    }

    private Cuid(String text) {
        this.text = text;
    }

    public String asString() {
        return this.text;
    }

    public char getDomain() {
        return text.charAt(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Cuid cuid = (Cuid) o;
        return text.equals(cuid.text);
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }
}
