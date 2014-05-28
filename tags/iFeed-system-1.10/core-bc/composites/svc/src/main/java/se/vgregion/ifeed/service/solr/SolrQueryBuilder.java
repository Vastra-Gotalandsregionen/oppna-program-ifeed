package se.vgregion.ifeed.service.solr;

import java.util.*;

import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.FilterType.Filter;
import se.vgregion.ifeed.types.IFeedFilter;

public class SolrQueryBuilder {

    public static String createOrQuery(Collection<IFeedFilter> filters) {
        //fq=(dc.contributor.savedby.id%3Asusro3 OR dc.contributor.savedby.id%3Aclalu4)
        if (filters.isEmpty()) {
            throw new RuntimeException("No filter provided");
        }
        if (filters.size() == 1) {
            return createQuery(filters.iterator().next(), (Map<String, FieldInf>) null);
        }
        List<String> parts = new ArrayList<String>();
        for (IFeedFilter filter: filters) {
            parts.add(createQuery(filter, (Map<String, FieldInf>) null));
        }
        return "(" + join(parts, " OR ") + ")";
    }

    private static String join(List<?> list, String junctor) {
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

    public static String createQuery(IFeedFilter iFeedFilter, Map<String, FieldInf> id2infs) {
        String query = "";
        Filter filter = iFeedFilter.getFilter();
        String filterQuery = iFeedFilter.getFilterQuery();

        if (filter == null || filter.getMetadataType() == null) {

            if (iFeedFilter.getFilterKey().equalsIgnoreCase("DC.date.validfrom")
                    || iFeedFilter.getFilterKey().equalsIgnoreCase("DC.date.availablefrom")) {
                // query = filter.getFilterField() + ":[" + filterQuery + " TO *]";
                query = iFeedFilter.getFilterKey() + ":[" + filterQuery + " TO *]";
            } else if (iFeedFilter.getFilterKey().equalsIgnoreCase("DC.date.validto")
                    || iFeedFilter.getFilterKey().equalsIgnoreCase("DC.date.availableto")) {
                // query = filter.getFilterField() + ":[* TO " + filterQuery + "]";
                query = iFeedFilter.getFilterKey() + ":[* TO " + filterQuery + "]";
            } else {
                query = iFeedFilter.getFilterKey() + ":" + SolrQueryEscaper.escape(filterQuery) + "";
            }

        } else {
            switch (filter.getMetadataType()) {
                case TEXT_FREE:
                    query = filter.getFilterField() + ":\"" + SolrQueryEscaper.escape(filterQuery) + "\"";
                    break;
                case TEXT_FIX:
                    query = filter.getFilterField() + ":\"" + SolrQueryEscaper.escape(filterQuery) + "\"";
                    break;
                case LDAP_VALUE:
                    query = filter.getFilterField() + ":" + SolrQueryEscaper.escape(filterQuery) + "";
                    break;
                case LDAP_ORG_VALUE:
                    query = filter.getFilterField() + ":" + SolrQueryEscaper.escape(filterQuery) + "";
                    break;
                case DATE:
                    query = toDateFilterValue(filter, filterQuery) ;
                    break;
                default:
                    query = filter.getFilterField() + ":\"" + SolrQueryEscaper.escape(filterQuery) + "\"";
                    break;
            }
        }
        return query;
    }

    private static String toDateFilterValue(Filter filter, String filterQuery) {
        String query;
        if (filter.name().contains("FROM_DATE")) {
            query = filter.getFilterField() + ":[" + filterQuery + " TO *]";
        } else if (filter.name().contains("TO_DATE")) {
            query = filter.getFilterField() + ":[* TO " + filterQuery + "]";
        } else {
            throw new RuntimeException("Unable to build query. " + "Unknown filter date type found: "
                    + filter.name());
        }return query;
    }


    public static String createQuery(IFeedFilter iFeedFilter, List<FieldInf> infs) {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

    static {

    }

    public static interface Processor {
        String format(String field, String value);

        // Processor pro = new Processor() {
        // @Override
        // public String format(String field, String value) {
        // if (filter.name().contains("d:date")) {
        // query = filter.getFilterField() + ":[" + filterQuery + " TO *]";
        // } else if (filter.name().contains("TO_DATE")) {
        // query = filter.getFilterField() + ":[* TO " + filterQuery + "]";
        // } else {
        // throw new RuntimeException("Unable to build query. " + "Unknown filter date type found: "
        // + filter.name());
        // }
        // }
        // };
    }

    private static Map<String, Processor> processors = new HashMap<String, SolrQueryBuilder.Processor>();

}