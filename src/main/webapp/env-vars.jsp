<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>JSP Table of Environmental variables</title>
</head>
<body>
<jsp:useBean id="EVBean" class="org.innopolis.kuzymvas.ejb.EnvironmentVarsProviderBean"/>
<c:set var="allVars" value="${EVBean.allVars}"/>
<style>
    table.data-table {
        border-collapse: collapse;
        border: 1px solid black;
    }

    .data-table td {
        padding: 5px;
        text-align: left;
        border: 1px solid black;
    }

    .data-table th {
        padding: 5px;
        text-align: center;
        border: 1px solid #000000;
    }
</style>
<table class="data-table">
    <thead>
    <tr>
        <th colspan="2">Environmental variables</th>
    </tr>
    <tr>
        <th>Name</th>
        <th>Value</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${allVars}" var="envVar">
        <tr>
            <td>${envVar.name}</td>
            <td>${envVar.value}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>
