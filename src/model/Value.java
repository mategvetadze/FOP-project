package model;

import java.util.Optional;
import java.util.function.Function;

public class Value {
    private Optional<?> value;
    private Function<String, ?> type;
    // Constructor for Value class
    public Value(Optional<?> value, Function<String, ?> type) {
        this.value = value;
        this.type = type;
    }

    public Optional<?> getValue() {
        return value;
    }

    public <T> void setValue(T value) {
        this.value = Optional.of(value);
    }

    @Override
    public String toString() {
        return "Value{" +
                "type=" + type +
                ", value=" + value +
                "}\n";
    }

    public Function<String, ?> getType() {
        return type;
    }


}
