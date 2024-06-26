package se.vgregion.ifeed.service.ifeed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.Ownership;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by clalu4 on 2014-06-10.
 */
public class Filter extends IFeed {

    private static final Logger LOGGER = LoggerFactory.getLogger(Filter.class);

    private String idAsText;

    public String toJpqlQuery(List<Object> values) {
        StringBuilder sb = new StringBuilder();

        sb.append("select distinct o from " + IFeed.class.getSimpleName() + " o");
        List condition = new ArrayList<Object>();

        addConditionIfAnyValue("o.name like ?", getName(), condition, values);
        addConditionIfAnyValue("(o.userId like ? or ow.userId like ?)", getUserId(), condition, values);

        addConditionIfAnyValue("o.description like ?", getDescription(), condition, values);
        addConditionIfAnyValue("o.department.id = ?", getDepartment() != null ? getDepartment().getId() : null, condition, values);
        addConditionIfAnyValue("o.group.id = ?", getGroup() != null ? getGroup().getId() : null, condition, values);
        addConditionIfAnyValue("o.id = ?", getId(), condition, values);

        sb.append(" left join fetch o.ownerships ow ");
        sb.append(" left join o.composites cs ");
        sb.append(" left join o.partOf pof ");
        sb.append(" left join o.dynamicTableDefs dtab ");
        sb.append(" left join fetch o.filters fters ");

        if (!values.isEmpty()) {
            sb.append(" where ");
            sb.append(join(condition, " AND "));
        }

        String result = sb + " order by o.id desc, o.name";
        //System.out.println(result);
        //System.out.println(fixUnIndexedParameterQuestionMarks(result));
        return fixUnIndexedParameterQuestionMarks(result);
    }

    static String fixUnIndexedParameterQuestionMarks(String inThatJpql) {
        StringBuilder sb = new StringBuilder();
        String[] parts = inThatJpql.split(Pattern.quote("?"));
        int i = 1;
        for (String part : Arrays.asList(parts).subList(0, parts.length - 1)) {
            sb.append(part);
            sb.append("?" + i);
            i++;
        }
        sb.append(parts[parts.length - 1]);
        return sb.toString();
    }

    private void addConditionIfAnyValue(String jpql, Object value, List<Object> sb, List<Object> values) {
        if (!isBlank(value)) {
            sb.add(jpql);
            int count = jpql.length() - jpql.replace("?", "").length();
            for (int i = 0; i < count; i++) {
                if (value instanceof String) {
                    values.add("%" + value + "%");
                } else {
                    values.add(value);
                }
            }
        }
    }

    private boolean isBlank(Object s) {
        if (s == null) {
            return true;
        }
        if (s instanceof String) {
            return "".equals(((String) s).trim());
        }
        return false;
    }

    /**
     * Concatenates several strings and places another string between each of those.
     *
     * @param junctor what string to concatenate between the other parameters.
     * @param list    the different strings to be concatenated
     * @return as string product of the parameters.
     */
    public static String join(List<?> list, String junctor) {
        StringBuilder sb = new StringBuilder();
        if (list.isEmpty()) {
            return "";
        }
        if (list.size() == 1) {
            return list.get(0) + "";
        }

        for (int i = 0, j = list.size() - 1; i < j; i++) {
            sb.append(list.get(i));
            sb.append(junctor);
        }
        sb.append(list.get(list.size() - 1));
        return sb.toString();
    }

    public String getIdAsText() {
        if (idAsText == null && id != null) {
            return id.toString();
        }
        return idAsText;
    }

    public void setIdAsText(String idAsText) {
        if (idAsText == null || "".equals(idAsText.trim())) {
            setId(null);
            this.idAsText = idAsText;
            return;
        }
        Long id = null;
        try {
            id = Long.parseLong(idAsText);
            setId(id);
            this.idAsText = idAsText;
        } catch (Exception e) {
            LOGGER.warn(idAsText + " is not a long value.");
        }
    }

    public String toTextUserIds(Collection<Ownership> ownerships) {
        List<String> users = new ArrayList<String>();
        for (Ownership ownership : ownerships) {
            users.add(ownership.getUserId());
        }
        return Filter.join(users, " ");
    }

}
