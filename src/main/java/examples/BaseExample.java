package examples;

import java.util.Objects;

public abstract class BaseExample {
    String pathTo(String filename) {
        return Objects.requireNonNull(getClass().getClassLoader().getResource(filename)).getPath();
    }
}
