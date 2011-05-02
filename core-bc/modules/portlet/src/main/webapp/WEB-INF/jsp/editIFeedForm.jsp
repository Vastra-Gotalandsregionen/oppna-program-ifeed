<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<portlet:actionURL name="editIFeed" var="editIFeedURL">
  <portlet:param name="action" value="editIFeed" />
  <portlet:param name="feedId" value="${iFeed.id}" />
</portlet:actionURL>

<portlet:actionURL name="addFilter" var="addFilterURL">
  <portlet:param name="action" value="addFilter" />
</portlet:actionURL>

<portlet:actionURL name="cancel" var="cancelURL">
  <portlet:param name="action" value="cancel" />
</portlet:actionURL>

<aui:layout>
  <aui:column columnWidth="66">
    <h1>${ifeed.name}</h1>
    <p>${ifeed.description}</p>

    <aui:button-row>
      <aui:button onClick="${editIFeedURL}" value="save" />
      <aui:button onClick="${cancelURL}" type="cancel" />
    </aui:button-row>
  </aui:column>

  <aui:column columnWidth="33">
    <aui:fieldset label="active-filters">
      <ul>
        <c:forEach items="${ifeed.filters}" var="iFeedFilter" varStatus="filtersRow">
          <portlet:actionURL name="removeFilter" var="removeFilter">
            <portlet:param name="action" value="removeFilter" />
            <portlet:param name="feedId" value="${ifeed.id}" />
            <portlet:param name="filter" value="${iFeedFilter.filter}" />
            <portlet:param name="filterQuery" value="${iFeedFilter.filterQuery}" />
          </portlet:actionURL>
          <li><liferay-ui:message key="${iFeedFilter.filter.keyString}" /> : ${iFeedFilter.filterQuery} <aui:a href="${removeFilter}">
            <img src="${themeDisplay.pathThemeImages}/common/delete.png" /><liferay-ui:message key="remove" /></aui:a></li>
        </c:forEach>
      </ul>
    </aui:fieldset>
  </aui:column>
</aui:layout>

<aui:layout>
	<aui:column columnWidth="66">
		<liferay-ui:search-container id='<portlet:namespace/>-parent-search-container' delta="10">
			<liferay-ui:search-container-results results="${ifeed.filters}" total="${fn:length(ifeed.filters)}" />
			<liferay-ui:search-container-row className="se.vgregion.ifeed.types.IFeedFilter" modelVar="iFeedFilter">
				<liferay-ui:search-container-column-text name="Filter" property="filter" />
				<liferay-ui:search-container-column-text name="Query" property="filterQuery" />
			</liferay-ui:search-container-row>
			<liferay-ui:search-iterator />
		</liferay-ui:search-container>
	</aui:column>
	<aui:column columnWidth="33">
		<liferay-ui:panel-container extended="true" id="feedConfiguration" persistState="false" accordion="false">
			<aui:fieldset label="available-filters">
				<c:forEach items="${filterTypes}" var="filterType" varStatus="filterRow">
					<liferay-ui:panel collapsible="true" defaultState="close" extended="true" id="feedConfigurationBlock-${filterRow.index}" persistState="false" title="${filterType.keyString}">
						<aui:form action="${addFilterURL}" method="post" cssClass="edit-feed-configuration-fm">
							<aui:fieldset>
								<aui:field-wrapper inlineField="true" inlineLabel="false">
									<aui:select name="filter" label="">
										<c:forEach items="${filterType.filters}" var="filter">
											<aui:option label="${filter.keyString}" value="${filter}" />
										</c:forEach>
									</aui:select>
									<aui:input name="filterValue" label="" />
								</aui:field-wrapper>
								<aui:field-wrapper inlineField="true" inlineLabel="false">
									<aui:button type="submit" value="add" />
								</aui:field-wrapper>
							</aui:fieldset>
						</aui:form>
					</liferay-ui:panel>
				</c:forEach>
			</aui:fieldset>
		</liferay-ui:panel-container>
	</aui:column>
</aui:layout>