package se.vgregion.ifeed.viewer;

import java.util.Map;

import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 * This class will make sure that if there is a single object to transform to JSON, it won't be rendered inside a
 * map.
 */
public class CustomMappingJacksonJsonView extends MappingJackson2JsonView {
    @SuppressWarnings("unchecked")
    @Override
    protected Object filterModel(Map<String, Object> model) {
        Object result = super.filterModel(model);
        if (!(result instanceof Map)) {
            return result;
        }

        Map map = (Map) result;
        if (map.size() == 1) {
            return map.values().toArray()[0];
        }
        return map;
    }
}
