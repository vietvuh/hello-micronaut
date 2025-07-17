package vvu.centrauthz.utilities;

import io.micronaut.json.JsonMapper;
import io.micronaut.json.tree.JsonNode;
import vvu.centrauthz.exceptions.IllegalJsonValue;

import java.io.IOException;

public class JsonTools {

    JsonTools() {
        throw new IllegalStateException();
    }

    public static <T> T jsonContext(SupplierWithThrowable<T, IOException> supplier) {
        try {
            return supplier.get();
        } catch (IOException e) {
            throw new IllegalJsonValue(e);
        }

    }

    public static <T> JsonNode toJson(JsonMapper mapper, T object) {
        return jsonContext(() -> mapper.writeValueToTree(object));
    }

    public static <T> T toValue(JsonMapper mapper, JsonNode node, Class<T> clazz) {
        return jsonContext(() -> mapper.readValueFromTree(node, clazz));
    }

}
