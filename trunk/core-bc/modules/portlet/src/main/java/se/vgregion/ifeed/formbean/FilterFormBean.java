package se.vgregion.ifeed.formbean;

import se.vgregion.ifeed.service.solr.DateFormatter;
import se.vgregion.ifeed.service.solr.DateFormatter.DateFormats;
import se.vgregion.ifeed.types.FilterType.Filter;
import se.vgregion.ifeed.types.MetadataType;

public class FilterFormBean {
    private Filter filter;
    private String filterValue;
    private int validFromYear;
    private int validFromMonth;
    private int validFromDay;

    public FilterFormBean() {
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public String getFilterValue() {
        if (filter.getMetadataType() == MetadataType.DATE) {
            filterValue = DateFormatter.format(validFromYear, validFromMonth, validFromDay, DateFormats.SOLR_DATE_FORMAT);
        }
        System.out.println("Filter value: " + filterValue);
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public void setValidFromYear(int validFromYear) {
        this.validFromYear = validFromYear;
    }

    public void setValidFromMonth(int validFromMonth) {
        //In Java Calendar January = 0, December = 11
        this.validFromMonth = validFromMonth;
    }

    public void setValidFromDay(int validFromDay) {
        this.validFromDay = validFromDay;
    }
}
