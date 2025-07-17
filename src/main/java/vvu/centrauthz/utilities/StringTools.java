package vvu.centrauthz.utilities;

import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class StringTools {
    public static boolean isBlank(String s) {
        return Objects.isNull(s) || s.isBlank();
    }

    public static String getNameFromPath(String s, char separator) {
        if (isBlank(s)) {
            return s;
        }

        var lastIdx = s.lastIndexOf(separator);
        if (lastIdx < 0) {
            return s;
        }

        return s.substring(lastIdx + 1);
    }

    public static Map<String, String> exceptionToMap(ConstraintViolationException e) {
        var map = new HashMap<String, String>();
        for (var violation : e.getConstraintViolations()) {

            var key = getNameFromPath(violation.getPropertyPath().toString(),'.');

            if (!map.containsKey(key)) {
                map.put(key, violation.getMessage());
            }
        }
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
