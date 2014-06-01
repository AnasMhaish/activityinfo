package org.activityinfo.datamodel.shared;


/**
 * Collision-Resistant Unique ID.
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Cuid cuid = (Cuid) o;

        if (!text.equals(cuid.text)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }
}
