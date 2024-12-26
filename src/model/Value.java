package model;

import java.util.Optional;
import java.util.function.Function;

public class Value {
    private Optional<?> value;
    private Function<String, ?> type;

    public Value(Optional<?> value, Function<String, ?> type) {
        this.value = value;
        this.type = type;
    }

    public Optional<?> getValue() {
        return value;
    }

    public Function<String, ?> getType() {
        return type;
    }


}
