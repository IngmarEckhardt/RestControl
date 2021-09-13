<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Insert a new Cat</title>
</head>
<body>
<h2 align="center">Füge eine neue Katze hinzu</h2>
<h5>Wenn du Werte nicht kennst, lass das Eingabefeld frei.</h5>

<form:form action="create-cat" modelAttribute="catDTO">
    <form:input type="hidden" path="id"/>
    <div>

        <label for="name">Name der Katze: </label>
        <form:input path="name" id="name"/>
        <br><br>

        <label for="age">Alter der Katze:</label>
        <form:input type="number" path="age" id="age" min="1" max="25"/>
        <br><br>

        <label for="date">Impfdatum</label>
        <form:input type="date" path="realDate" id="date"/>
        <br><br>
        <label for="weight">Gewicht der Katze:</label>
        <form:input type="number" path="weight" id="weight" min="0" max="10" step="0.2"/>
        <p>Die Katze ist rund</p>

        <div>
            <input type="radio" id="chubbyTrue" name="chubby" value="true"
                   checked>
            <label for="chubbyTrue">wahr</label>
        </div>

        <div>
            <input type="radio" id="chubbyFalse" name="chubby" value="false">
            <label for="chubbyFalse">falsch</label>
        </div>
        <p>Die Katze ist süß</p>

        <div>
            <input type="radio" id="sweetTrue" name="sweet" value="true"
                   checked>
            <label for="sweetTrue">wahr</label>
        </div>

        <div>
            <input type="radio" id="sweetFalse" name="sweet" value="false">
            <label for="sweetFalse">falsch</label>
        </div>
        <br>

        <input type="submit" value="Speichern"> <a href="index">zurück</a><br>

    </div>
</form:form>
</body>
</html>