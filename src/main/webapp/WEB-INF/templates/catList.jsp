<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>Katzendatenbank</title>
</head>
<body>
<h2 align="center">Katzen in der Datenbank </h2>
<hr>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Alter</th>
        <th>Impfdatum</th>
        <th>Gewicht</th>
        <th>Rund</th>
        <th>Süß</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${cats}" var="cat">
        <tr>
            <td>${cat.id}</td>
            <td>${cat.name}</td>
            <td>${cat.age}</td>
            <td>${cat.vaccineDate}</td>
            <td>${cat.weight}</td>
            <td>${cat.chubby}</td>
            <td>${cat.sweet}</td>

        </tr>
    </c:forEach>
    </tbody>
</table>
<a href="index">zurück</a><br>
</body>
</html>