package de.cats.restcat.service;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

class CatRepositoryPrimaryRepo implements CatRepository {
    private static final String
            ALL_CATS = "select ID, NAME, AGE, VACCINEDATE, WEIGHT, CHUBBY, SWEET from Cats",
            ERASE_TABLE = "TRUNCATE TABLE Cats",
            COUNT_CATS = "select COUNT(*) from Cats",
            INSERT_CAT = "INSERT INTO Cats (NAME, AGE, VACCINEDATE, WEIGHT, CHUBBY, SWEET) VALUES (?,?,?,?,?,?)",
            GET_MAX_ID = "select MAX(ID) from Cats",
            GET_CAT = "select ID, NAME, AGE, VACCINEDATE, WEIGHT, CHUBBY, SWEET from Cats where ID = ?",
            DELETE_ID = "DELETE FROM Cats WHERE ID = ?",
            UPDATE_ID = "REPLACE INTO Cats (ID, NAME, AGE, VACCINEDATE, WEIGHT, CHUBBY, SWEET) VALUES (?,?,?,?,?,?,?)";
    private final ArrayList<Cat> catArray;
    private final DataSource dataSource;

    CatRepositoryPrimaryRepo(DataSource dataSource) {
        this.dataSource = dataSource;
        this.catArray = new ArrayList<>();
    }

    @Override
    public ArrayList<Cat> readCats() {
        int length = 0;
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("Connection to MariaDB successful, start reading Cats");
            ResultSet rowCount = connection.prepareStatement(COUNT_CATS).executeQuery();
            if (rowCount.next()) length = rowCount.getInt(1);

            catArray.clear();
            catArray.ensureCapacity(length);

            ResultSet resultSet = connection.prepareStatement(ALL_CATS).executeQuery();
            while (resultSet.next()) catArray.add(createCat(resultSet));

        } catch (SQLException e) {
            throw new RuntimeException("SQL-Connection to MariaDB failed");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
        return catArray;
    }

    @Override
    public boolean writeCats(ArrayList<Cat> cats) {

        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement(ERASE_TABLE).executeQuery();
            for (Cat cat : cats) insertCatStatement(connection, cat).executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("SQL-Connection to MariaDB failed");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean addNewCat(Cat cat) {
        if (cat.getName() == null || cat.getName().length() < 1 || cat.getName().length() > 50) return false;

        try (Connection connection = dataSource.getConnection()) {
            insertCatStatement(connection, cat).executeQuery();
            ResultSet set = connection.prepareStatement(GET_MAX_ID).executeQuery();
            if (set.next()) cat.setId(set.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("SQL-Connection to MariaDB failed");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteCat(Cat cat) {
        try (Connection connection = dataSource.getConnection()) {
            deleteCatStatement(connection, cat).executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("SQL-Connection to MariaDB failed");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean editCat(Cat cat) {
        try (Connection connection = dataSource.getConnection()) {
            editCatStatement(connection, cat).executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("SQL-Connection to MariaDB failed");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private PreparedStatement insertCatStatement(Connection conn, Cat catToSave) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(INSERT_CAT);
        return insertCatValuesInStatement(catToSave, stmt, -1);
    }

    private PreparedStatement deleteCatStatement(Connection conn, Cat cat) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(DELETE_ID);
        stmt.setInt(1, cat.getId());
        return stmt;
    }

    private PreparedStatement editCatStatement(Connection conn, Cat catToSave) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(UPDATE_ID);
        return insertCatValuesInStatement(catToSave, stmt, 0);
    }

    private PreparedStatement insertCatValuesInStatement(Cat catToSave, PreparedStatement stmt, Integer index) throws SQLException {

        if (index == 0) stmt.setInt(index + 1, catToSave.getId());
        if (catToSave.getName() == null) throw new RuntimeException("name is null");
        if (catToSave.getName().length() > 50) throw new RuntimeException("name should have less than 50 characters");
        stmt.setString(index + 2, catToSave.getName());
        stmt.setInt(index + 3, catToSave.getAge());
        if (catToSave.getVaccineDate() != null)
            stmt.setDate(index + 4, Date.valueOf(catToSave.getVaccineDate()));
        else stmt.setDate(index + 4, Date.valueOf("2001-01-01"));
        stmt.setFloat(index + 5, catToSave.getWeight());
        stmt.setString(index + 6, catToSave.isChubby() ? "true" : "false");
        stmt.setString(index + 7, catToSave.isSweet() ? "true" : "false");

        return stmt;
    }

    private Cat createCat(ResultSet results) throws SQLException {
        Integer id = results.getInt("ID");
        String name = results.getString("NAME");
        if (name == null || name.length() < 1 || name.length() > 50) return null;
        Integer age = results.getInt("AGE");
        Float weight = results.getFloat("WEIGHT");
        Boolean chubby = results.getString("CHUBBY").equals("true");
        Boolean sweet = results.getString("SWEET").equals("true");
        LocalDate vaccineDate = results.getDate("VACCINEDATE").toLocalDate();

        return new Cat(id, name, age, vaccineDate, weight, chubby, sweet);
    }
}