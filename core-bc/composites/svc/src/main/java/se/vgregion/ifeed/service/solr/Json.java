package se.vgregion.ifeed.service.solr;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by clalu4 on 2014-06-27.
 */
public class Json {

    /**
     * Converts a json-text to a hieararchy of objects.
     * @param s the json to convert.
     * @return either a object of the primitive wrapper types or a container sort of type map or list.
     * @throws Exception if the json text is malformed the method will throw an exception.
     */
    public static Object parse(final String s) throws Exception {
        final ScriptEngineManager factory = new ScriptEngineManager();
        final ScriptEngine engine = factory.getEngineByName("js");

        engine.getBindings(ScriptContext.GLOBAL_SCOPE).put("json", s);
        // The default JavaScript engine in Java 6 does not have JSON.parse
        // but eval('(' + json + ')') would work
        Object val = engine.eval("JSON.parse(json)");

        // The default value is probably a JavaScript internal object and not very useful
        // Java 7 uses Rhino 1.7R3 the objects will implement Map or List where appropriate
        // So in Java 7 we can turn this into something a little more useable
        // This is where Java 6 breaks down, in Java 6 you would have to use the
        // sun.org.mozilla.javascript.internal classes to get any useful data

        return convert(val);
    }

    @SuppressWarnings("unchecked")
    private static Object convert(final Object val) {
        if (val instanceof Map) {
            final Map<String, Object> result = new HashMap<String, Object>();
            for (final Map.Entry<String, Object> entry : ((Map<String, Object>) val).entrySet()) {
                result.put(entry.getKey(), convert(entry.getValue()));
            }
            return result;
        } else if (val instanceof List) {
            final List<Object> result = new ArrayList<Object>();
            for (final Object item : ((List<Object>) val)) {
                result.add(convert(item));
            }
            return result;
        }
        return val;
    }

}
