package utils.types;

import java.io.Serial;

public class StringID implements ID<String> {
    @Serial
    private static final long serialVersionUID = 2L;

    private final String value;

    public StringID(String value) {
        this.value = value;
    }
    public StringID(Object value) {
        this.value = value.toString();
    }

    public String getValue() {
        return value;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ID<?> id = (ID<?>) obj;
        return value.equals(id.getValue());
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
