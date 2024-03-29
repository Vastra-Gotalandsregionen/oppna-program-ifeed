package se.vgregion.ifeed.tools;

import se.vgregion.ifeed.types.IFeedFilter;

import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Consumer;

public class Filter extends Tuple {

    private Filter parent;

    private final List<Filter> children = new ArrayList<>();

    private DatabaseApi database;
    private FieldInf fieldInf;

    public Filter(Map<String, Object> from) {
        super(from);
    }

    public Filter(Map<String, Object> from, FieldInf fieldInf) {
        super(from);
        this.fieldInf = fieldInf;
        put("field_inf_pk", fieldInf.get("pk"));
    }

    public Filter() {
        super();
    }

    public Filter(Filter filter) {
        this();
        putAll(filter);
    }

    public static List<? extends Filter> toFilters(List<Tuple> findings) {
        List<Filter> result = new ArrayList<>();
        for (Tuple finding : findings) {
            result.add(toFilter(finding));
        }
        return result;
    }

    public static Filter toFilter(Tuple tuple) {
        Filter result = new Filter();
        result.putAll(tuple);
        return result;
    }

    public void fill(DatabaseApi database) {
        this.database = database;
        if (get("id") != null) {
            List<Tuple> findings = database.query("select * from vgr_ifeed_filter where parent_id = ?", get("id"));
            children.addAll(toFilters(findings));
            for (Filter child : children) {
                child.fill(database);
            }
        }
    }

    public FieldInf getFieldInf() {
        if (fieldInf != null) {
            return fieldInf;
        }
        Long fieldInfPk = (Long) get("field_inf_pk");
        if (fieldInfPk == null) {
            // throw new RuntimeException("Missing field_inf_pk!");
            return null;
        }
        if (database != null) {
            List<Tuple> items = database.query("select * from field_inf where pk = ?", fieldInfPk);
            for (Tuple item : items) {
                fieldInf = FieldInf.toFieldInf(item);
                fieldInf.load(database);
                break;
            }
            return fieldInf;
        } else {
            return null;
        }
    }

    public void setFieldInf(FieldInf fieldInf) {
        this.fieldInf = fieldInf;
    }

    public List<Filter> getChildren() {
        return children;
    }

    public void insert(DatabaseApi into, Feed withFeed, Filter orParent) {
        //List<Tuple> r = into.query("select max(id) + 1 from vgr_ifeed_filter");
        //final long id = (long) r.get(0).values().iterator().next();
        final long id = SequenceUtil.getNextHibernateSequeceValue(into);
        put("id", id);

        if (withFeed != null) {
            put("ifeed_id", withFeed.get("id"));
        } else {
            remove("ifeed_id");
        }

        if (orParent != null) {
            put("parent_id", orParent.get("id"));
        }

        into.insert("vgr_ifeed_filter", this);

        for (Filter child : children) {
            child.put("parent_id", id);
            child.insert(into, null, this);
        }
    }

    public static void main(String[] args) {
        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));
        System.out.println(generatedString);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>(this);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Filter child : children) list.add(child.toMap());
        result.put("children", list);
        return result;
    }

    public void delete(DatabaseApi fromHere) {
        for (Filter child : children) {
            child.delete(fromHere);
        }
        fromHere.execute("delete from vgr_ifeed_filter where id = ?", get("id"));
    }

    public void initParentValue() {
        for (Filter child : getChildren()) {
            child.setParent(this);
            child.initParentValue();
        }
    }

    public void visit(Consumer<Filter> that) {
        initParentValue();
        visitImp(that);
    }

    private void visitImp(Consumer<Filter> that) {
        that.accept(this);
        for (Filter child : new ArrayList<>(children)) {
            child.visit(that);
        }
    }

    public IFeedFilter toIFeedFilter() {
        IFeedFilter result = new IFeedFilter();

        result.setFilterKey((String) get("filterkey"));
        result.setFilterQuery((String) get("filterquery"));
        result.setOperator((String) get("operator"));

        for (Filter child : getChildren()) {
            result.getChildren().add(child.toIFeedFilter());
        }

        FieldInf fi = getFieldInf();
        if (fi != null) {



            result.setFieldInf(getFieldInf().toJpaVersion());
        }

        return result;
    }

    public Filter getParent() {
        return parent;
    }

    public void setParent(Filter parent) {
        this.parent = parent;
    }

    /*@Override
    public Object put(String key, Object value) {
        if ("filterkey".equals(key) && value == null)
            throw new NullPointerException();
        return super.put(key, value);
    }*/
}
