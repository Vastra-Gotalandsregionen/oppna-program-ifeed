<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/tld/util.tld" prefix="vgr"%>

<html>
<head>
<link href="${pageContext.request.contextPath}/resources/style.css" rel="stylesheet" type="text/css">
</head>
<body>
  <ul class="doc-list">
    <c:forEach items="${result}" var="item">
      <li><a
        href="${pageContext.request.contextPath}/documents/${fn:replace(item['dc.identifier.documentid'], 'workspace://SpacesStore/', '')}">
          <img src="${pageContext.request.contextPath}/resources/information.png" /></a> 
          <a target="_blank" href="${item.url}">${item['dc.title']}</a>
      </li>
    </c:forEach>
  </ul>
</body>
</html>