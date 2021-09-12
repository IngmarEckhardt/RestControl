<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Insert a new Cat</title>
</head>
<body>
<h1 align="center">CatControl - gibt dir die Kontrolle zurück!</h1>
<hr>
<h2 align="center">Füge eine neue Katze hinzu</h2>
<h5>Wenn du Werte nicht kennst, lass das Eingabefeld frei.</h5>

<form action="createCat">
<div>
    <label for="name">Name der Katze:  </label>
    <input type="text" name="name" placeholder="Max Musterkatz" id="name">

    <label for="age">Alter der Katze:</label>
    <input type="number" name="age" id="age" min="1" max="25" />
    <br><br>

    <label for="date">Impfdatum</label>
    <input type="date" id="date" name="date"
           value="2020-01-01">

    <label for="weight">Gewicht der Katze:</label>
    <input type="number" name="weight" id="weight" min="0" max="10" step="0.2"/>
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
    <input type="submit" value="Speichern">

</div>
</form>
</body>
</html>