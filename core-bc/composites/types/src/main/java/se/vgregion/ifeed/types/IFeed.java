package se.vgregion.ifeed.types;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.CreationTimestamp;
import se.vgregion.common.utils.MultiMap;
import se.vgregion.dao.domain.patterns.entity.AbstractEntity;
import se.vgregion.ifeed.shared.DynamicTableDef;
import se.vgregion.ifeed.types.util.Junctor;

import javax.persistence.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Entity
@Table(name = "vgr_ifeed")
public class IFeed extends AbstractEntity<Long> implements Serializable, Comparable<IFeed> {

    private static final long serialVersionUID = -2277251806545192506L;

    @Id
    @GeneratedValue
    protected Long id;

    @OneToMany(mappedBy = "ifeed", cascade = CascadeType.ALL)
    private final List<DynamicTableDef> dynamicTableDefs = new ArrayList<DynamicTableDef>();

    @Version
    @Column
    private Long version;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "feed")
    protected Set<IFeedFilter> filters = new HashSet<>();

    @Column
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date timestamp = null;

    @Column
    private String description;

    @Column
    private String userId;

    @Transient
    private String creatorName;

    @ManyToOne
    @Expose(serialize = false, deserialize = true)
    private VgrDepartment department;

    @ManyToOne
    @Expose(serialize = false, deserialize = true)
    private VgrGroup group;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ifeed")
    protected Set<Ownership> ownerships = new HashSet<>();

    @ManyToMany(/*cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}*/)
    protected List<IFeed> composites = new DistinctArrayList<>();

    @Expose(serialize = false, deserialize = true)
    @ManyToMany(mappedBy = "composites"/*, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}*/)
    protected List<IFeed> partOf = new DistinctArrayList<>();

    @Column
    private String sortField;

    @Column
    private String sortDirection;

    @Column
    private Boolean linkNativeDocument;

    @Column(name = "creation_time", updatable = false)
    @CreationTimestamp
    private Date creationTime;

    @Column(name = "filter_change_lock", updatable = false, insertable = true, columnDefinition = "boolean default false")
    private Boolean filterChangeLock = false;

    /**
     * Message to user of ifeed-admin - to show it for the user if it has any value. As for now this value can only be
     * set by technical staff directly in the db.
     */
    @Column(name = "editor_user_message", length = 800, updatable = false, insertable = true)
    private String editorUserMessage;

    public Set<IFeedFilter> getFilters() {
/*        if (filters == null) {
            return Collections.emptySet();
        }*/
        if (filters == null) filters = new HashSet<>();
        return filters;
    }

    public boolean addFilter(IFeedFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException();
        }
        if (filters == null) {
            filters = new HashSet<IFeedFilter>();
        } else if (filters.contains(filter)) {
            return false;
        }
        filters.add(filter);
        return true;
    }

    public IFeedFilter getFilter(IFeedFilter filter) {
        IFeedFilter f = null;
        List<IFeedFilter> filters = new ArrayList<IFeedFilter>(getFilters());
        int index = filters.indexOf(filter);
        if (index >= 0) {
            f = filters.get(index);
        }
        return f;
    }

    public void setFilters(Set<IFeedFilter> filters) {
        /*if (this.filters == null) {
            this.filters = new HashSet<IFeedFilter>();
        }
        this.filters.clear();
        this.filters.addAll(filters);*/
        this.filters = filters;
    }

    public void removeFilter(IFeedFilter filter) {
        filters.remove(filter);
    }

    public void removeFilter(int index) {
        List<IFeedFilter> temp = new ArrayList<IFeedFilter>(filters);
        temp.remove(index);
        filters.retainAll(temp);
    }

    public void removeFilters() {
        filters.clear();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        if (timestamp == null) {
            return null;
        } else {
            return new Date(timestamp.getTime());
        }
    }

    public void clearTimestamp() {
        this.timestamp = null;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimestamp() {
        this.timestamp = new Date();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public final int compareTo(final IFeed o) {
        if (o == null) {
            return +1;
        }
        return new CompareToBuilder().append(name, o.name).toComparison();
    }

    public Set<Ownership> getOwnerships() {
        return ownerships;
    }

    private void setOwnerships(Set<Ownership> ownerships) {
        this.ownerships = ownerships;
    }

    public String getOwnershipsText() {
        List<String> names = new ArrayList<String>();
        for (Ownership ownership : getOwnerships()) {
            names.add(ownership.getUserId());
        }
        names.remove(getUserId());
        names.add(0, getUserId());
        String text = names.toString();
        text = text.replaceAll(Pattern.quote("["), "").replaceAll(Pattern.quote("]"), "");
        return text;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Boolean getLinkNativeDocument() {
        if (linkNativeDocument == null) {
            return false;
        }
        return linkNativeDocument;
    }

    public void setLinkNativeDocument(Boolean linkNativeDocument) {
        this.linkNativeDocument = linkNativeDocument;
    }

    public VgrDepartment getDepartment() {
        return department;
    }

    public void setDepartment(VgrDepartment department) {
        this.department = department;
    }

    public VgrGroup getGroup() {
        return group;
    }

    public void setGroup(VgrGroup group) {
        this.group = group;
    }

    private String toJson() {
        try {
            return toJsonImpl();
        } catch (Exception e) {
            //  throw new RuntimeException(e);
            return e.getMessage();
        }

    }


    private void gatherAllNestedFeeds(Set<IFeed> intoThis) {
        if (!intoThis.contains(this)) {
            intoThis.add(this);
            for (IFeed feed : composites) {
                feed.gatherAllNestedFeeds(intoThis);
            }
        }
    }

    public Set<IFeed> getAllNestedFeedsFlattly() {
        final Set<IFeed> result = new HashSet<IFeed>();
        gatherAllNestedFeeds(result);
        return result;
    }


    static private <T> T copy(T instance) {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);

            oos.writeObject(instance);
            oos.flush();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                oos.close();
                ois.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    private String toJsonImpl() throws IOException, ClassNotFoundException {
        final Set<String> excludeFields = new HashSet<String>(Arrays.asList("iFeed", "ifeed", "ownerships",
                "department", "group", "ifeedDynamicTableDefs", "tableDef", "partOf"));
        Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return excludeFields.contains(fieldAttributes.getName());
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        }).create();

        if (!composites.isEmpty()) {
            // To handle circular dependencies
            IFeed container = new IFeed();
            container.getComposites().addAll(copy(getAllNestedFeedsFlattly()));
            long seq = 0;
            for (IFeed feed : container.getComposites()) {
                feed.getComposites().clear();
                feed.setId(seq--); // to make it unique
            }
            return gson.toJson(container);
        }

        return gson.toJson(this);
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        FileInputStream fin = new
                FileInputStream("C:\\Users\\clalu4\\Downloads\\iFeed.obj.1");

        ObjectInputStream obj_in =
                new ObjectInputStream(fin);

        IFeed iFeed = (IFeed) obj_in.readObject();
        findCirkular(iFeed);
    }


    static void findCirkular(Object currentNode) {
        try {
            findCirkular(currentNode, new ArrayList<String>(), new IdentityHashMap());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    static void findCirkular(Object currentNode, List<String> stack, IdentityHashMap passed) throws IllegalAccessException {
        if (currentNode instanceof Collection) for (Object o : ((Collection) currentNode)) {
            findCirkular(o, stack, passed);
        }
        if (currentNode == null || currentNode.getClass().getName().startsWith("java.")) return;
        if (passed.containsKey(currentNode)) {
            // Nothing
        } else {
            passed.put(currentNode, null);
            java.lang.reflect.Field[] fields = currentNode.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                Object nextNode = field.get(currentNode);
                stack.add(field.getName());
                findCirkular(nextNode, stack, passed);
                if (!stack.isEmpty())
                    stack.remove(stack.size() - 1);
            }
        }
    }


    public static IFeed fromJson(String ifeed) {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(ifeed, IFeed.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<DynamicTableDef> getDynamicTableDefs() {
        return dynamicTableDefs;
    }

    public List<IFeed> getComposites() {
        return composites;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Boolean getFilterChangeLock() {
        return filterChangeLock;
    }

    public void setFilterChangeLock(Boolean filterChangeLock) {
        this.filterChangeLock = filterChangeLock;
    }

    public String getEditorUserMessage() {
        return editorUserMessage;
    }

    public void setEditorUserMessage(String editorUserMessage) {
        this.editorUserMessage = editorUserMessage;
    }

    private static class DistinctArrayList<E> extends ArrayList<E> {
        @Override
        public boolean add(E e) {
            if (!contains(e)) {
                return super.add(e);
            }
            return false;
        }

        @Override
        public void add(int index, E element) {
            if (!contains(element)) {
                return;
            }
            super.add(index, element);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            ArrayList<E> clone = new ArrayList<E>(c);
            clone.removeAll(this);
            return super.addAll(clone);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            ArrayList<E> clone = new ArrayList<E>(c);
            clone.removeAll(this);
            return super.addAll(index, clone);
        }
    }

    @PreRemove
    void preRemove() {
        composites.clear();
        for (IFeed feet : partOf) {
            feet.getComposites().remove(this);
        }
        partOf.clear();
    }

    public List<IFeed> getPartOf() {
        return partOf;
    }

    @Transient
    boolean toStringRuns = false;

    @Override
    public String toString() {
        if (toStringRuns) {
            return super.hashCode() + "";
        }
        try {
            toStringRuns = true;
            return "IFeed{" +
                    "id=" + id +
                    ", dynamicTableDefs=" + dynamicTableDefs +
                    ", version=" + version +
                    ", filters=" + filters +
                    ", name='" + name + '\'' +
                    ", timestamp=" + timestamp +
                    ", description='" + description + '\'' +
                    ", userId='" + userId + '\'' +
                    ", creatorName='" + creatorName + '\'' +
                    ", department=" + department +
                    ", group=" + group +
                    ", ownerships=" + ownerships +
                    ", composites=" + composites +
                    // ", partOf=" + partOf +
                    ", sortField='" + sortField + '\'' +
                    ", sortDirection='" + sortDirection + '\'' +
                    ", linkNativeDocument=" + linkNativeDocument +
                    '}';
        } finally {
            toStringRuns = false;
        }
    }

    public String toQuery(List<Field> meta) {
        Junctor or = new Junctor(" OR ");
        Set<IFeed> flat = getAllNestedFeedsFlattly();
        for (IFeed feed : flat) {
            or.add(feed.toQueryImp(meta));
        }
        String r = or.toQuery();
        System.out.println(r);
        return r;
    }

    public boolean hasNoFilters() {
        int i = 0;
        for (IFeed iFeed : getAllNestedFeedsFlattly()) {
            i += iFeed.getFilters().size();
        }
        return i == 0;
    }


/*    private Set<DefaultFilter> getDefaultFilters(FieldInf that) {
        Set<DefaultFilter> result = new HashSet<>();
        if (that == null) {
            return result;
        }
        if (that.getDefaultFilters() != null) {
            result.addAll(that.getDefaultFilters());
        }
        result.addAll(getDefaultFilters(that.getParent()));
        return result;
    }*/

    private String toQueryImp(List<Field> meta) {
        Set<IFeedFilter> filterz = new HashSet<>(this.filters);
        Set<String> filterKeys = filterz.stream().map(f -> f.getFilterKey()).collect(Collectors.toSet());

        for (IFeedFilter filter : new ArrayList<>(filterz)) {
            filter.visit(f -> {
                if (f.getFieldInf() != null)
                    filterz.addAll(
                            f.getFieldInf().getEntireDefaultCondition().stream()
                                    .filter(dc -> !filterKeys.contains(dc.getFilterKey())).collect(Collectors.toSet())
                    );
            });

            /*if (filter.getFieldInf() != null)
                filterz.addAll(
                        filter.getFieldInf().getEntireDefaultCondition().stream()
                                .filter(dc -> !filterKeys.contains(dc.getFilterKey())).collect(Collectors.toSet())
                );*/
        }

        if (filterz == null || filterz.isEmpty()) {
            return "";
        }

        if (filterz.size() == 1) {
            return filterz.iterator().next().toQuery(meta);
        }

        Junctor and = new Junctor(" AND ");
        Map<String, List<IFeedFilter>> keyToFilters = new HashMap<String, List<IFeedFilter>>() {
            @Override
            public List<IFeedFilter> get(Object key) {
                if (!containsKey(key)) {
                    put(String.valueOf(key), new ArrayList<IFeedFilter>());
                }
                return super.get(key);
            }
        };

        for (IFeedFilter filter : filterz) {
            if (filter.isContainer()) {
                and.add(filter.toQuery(meta));
            } else {
                String key = filter.getFilterKey();
                if (filter.getFieldInf() != null && filter.getFieldInf().getQueryPrefix() != null) {
                    key = key + filter.getFieldInf().getQueryPrefix();
                }
                if (keyToFilters.get(key) == null)
                    System.out.println("hej");
                keyToFilters.get(key).add(filter);
            }
        }

        for (String key : keyToFilters.keySet()) {
            List<IFeedFilter> values = keyToFilters.get(key);
            IFeedFilter first = values.get(0);
            if (values.size() > 1 && !first.isContainer()) {
                Junctor or = new Junctor(first.getUsingAnd() != null && first.getUsingAnd() ? " AND " : " OR ");
                for (IFeedFilter value : values) {
                    or.add(value.toQuery(meta));
                }
                and.add(or.toQuery());
            } else {
                and.add(values.get(0).toQuery(meta));
            }
        }

        return and.toQuery();
    }

    public List<FieldInf> getFieldInfsWithMultipleFilters() {
        MultiMap<FieldInf, FieldInf> mm = new MultiMap<>();
        if (filters != null)
            for (IFeedFilter filter : filters) {
                mm.get(filter.getFieldInf()).add(filter.getFieldInf());
            }
        return new ArrayList<>(mm.entrySet().stream().filter(es -> es.getValue().size() > 1)
                .map(es -> es.getKey()).collect(Collectors.toSet()));
    }

    public List<IFeedFilter> getFiltersByField(FieldInf inf) {
        return getFilters().stream().filter(f -> inf.equals(f.getFieldInf())).collect(Collectors.toList());
    }

    /**
     * Set the filter usingAnd property to false if there are only one occurrence with that same FieldInf.
     */
    public void putFalseIntoEachFilterUseAndThatHasOnlyOneOccurrence() {
        final List<FieldInf> multis = getFieldInfsWithMultipleFilters();

        for (IFeedFilter filter : filters) {
            if (!multis.contains(filter.getFieldInf())) {
                filter.setUsingAnd(false);
            }
        }
    }

    public Boolean isUsingAnd(FieldInf withThis) {
        for (IFeedFilter iff : getFiltersByField(withThis)) {
            return iff.getUsingAnd() != null && iff.getUsingAnd();
        }
        return false;
    }

    public void putValueIntoFiltersUsingAnd(FieldInf withThatField, Boolean newValue) {
        for (IFeedFilter iFeedFilter : getFiltersByField(withThatField)) {
            iFeedFilter.setUsingAnd(newValue);
        }
    }

}
