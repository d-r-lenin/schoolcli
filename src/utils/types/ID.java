package utils.types;

import java.io.Serializable;

public interface ID<T> extends Serializable {
    T getValue();
    @Override
    boolean equals(Object obj);
}
