package integrationtests;

import java.util.function.Supplier;

public class KeySupplier implements Supplier<String> {
    private String key;

    public KeySupplier(String key) {
        this.key = key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String get() {
        return key;
    }
}
