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
    <label fore="catNameID">Name der Katze:  </label>
    <input type="text" name="catName" placeholder="Max Musterkatz" id="catNameID">
    <input type="submit" value="Speichern">
</div>
</form>
</body>
</html>
