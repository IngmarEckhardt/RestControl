package de.cats.restcat.service;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

class CatRepositoryPrimaryRepo implements CatRepository {
    private static final String ALL_VALUES =
            "select ID, NAME, AGE, VACCINEDATE, WEIGHT, CHUBBY, SWEET from Cats";
    private static final String ERASE_TABLE =
            "TRUNCATE TABLE Cats";
    private static final String COUNT_CATS =
            "select COUNT(*) from Cats";
    private static final String INSERT_CAT =
            "INSERT INTO Cats (NAME, AGE, VACCINEDATE, WEIGHT, CHUBBY, SWEET) VALUES (?,?,?,?,?,?)";
    private static final String GET_MAX_ID =
            "select MAX(ID) from Cats";
    private static final String DELETE_ID =
            "DELETE FROM Cats WHERE ID = ?";
    private static final String UPDATE_ID =
            "REPLACE INTO Cats (ID, NAME, AGE, VACCINEDATE, WEIGHT, CHUBBY, SWEET) VALUES (?,?,?,?,?,?,?)";
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
            ResultSet rowCount = getRowCountStatement(connection).executeQuery();
            if (rowCount.next()) length = rowCount.getInt(1);

            catArray.clear();
            catArray.ensureCapacity(length);

            ResultSet resultSet = getAllCatsStatement(connection).executeQuery();
            while (resultSet.next()) catArray.add(createCat(resultSet));

        } catch (SQLException e) {
            throw new RuntimeException("SQL-Verbindung zur MariaDB fehlgeschlagen");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
        return catArray;
    }

    @Override
    public boolean writeCats(ArrayList<Cat> cats) {

        try (Connection connection = dataSource.getConnection()) {
            eraseTableStatement(connection).executeQuery();
            for (Cat cat : cats) insertCatStatement(connection, cat).executeQuery();

        } catch (SQLException e) {
            throw new RuntimeException("SQL-Verbindung zur MariaDB fehlgeschlagen");
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
            ResultSet set = getHighestIdStatement(connection).executeQuery();
            if (set.next()) cat.setId(set.getInt(1));

        } catch (SQLException e) {
            throw new RuntimeException("SQL-Verbindung zur MariaDB fehlgeschlagen");
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
            throw new RuntimeException("SQL-Verbindung zur MariaDB fehlgeschlagen");
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
            throw new RuntimeException("SQL-Verbindung zur MariaDB fehlgeschlagen");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private PreparedStatement getRowCountStatement(Connection conn) throws SQLException {
        return conn.prepareStatement(COUNT_CATS);
    }

    private PreparedStatement getAllCatsStatement(Connection conn) throws SQLException {
        return conn.prepareStatement(ALL_VALUES);
    }

    private PreparedStatement eraseTableStatement(Connection conn) throws SQLException {
        return conn.prepareStatement(ERASE_TABLE);
    }

    private PreparedStatement insertCatStatement(Connection conn, Cat catToInsert) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement(
                INSERT_CAT);

        stmt.setString(1, catToInsert.getName());
        if (catToInsert.getName() == null) throw new RuntimeException("name is null");
        stmt.setInt(2, catToInsert.getAge());
        if (catToInsert.getVaccineDate() != null)
            stmt.setDate(3, Date.valueOf(catToInsert.getVaccineDate()));
        else stmt.setDate(3, Date.valueOf("2001-01-01"));
        stmt.setFloat(4, catToInsert.getWeight());
        stmt.setString(5, catToInsert.isChubby() ? "true" : "false");
        stmt.setString(6, catToInsert.isSweet() ? "true" : "false");
        return stmt;
    }

    private PreparedStatement getHighestIdStatement(Connection conn) throws SQLException {
        return conn.prepareStatement(GET_MAX_ID);
    }

    private PreparedStatement deleteCatStatement(Connection conn, Cat cat) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(DELETE_ID);
        stmt.setInt(1, cat.getId());
        return stmt;
    }

    private PreparedStatement editCatStatement(Connection conn, Cat catToEdit) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(UPDATE_ID);

        stmt.setInt(1, catToEdit.getId());
        if (catToEdit.getName() == null) throw new RuntimeException("name is null");
        stmt.setString(2, catToEdit.getName());
        stmt.setInt(3, catToEdit.getAge());
        if (catToEdit.getVaccineDate() != null)
            stmt.setDate(4, Date.valueOf(catToEdit.getVaccineDate()));
        else stmt.setDate(4, Date.valueOf("2001-01-01"));
        stmt.setFloat(5, catToEdit.getWeight());
        stmt.setString(6, catToEdit.isChubby() ? "true" : "false");
        stmt.setString(7, catToEdit.isSweet() ? "true" : "false");
        return stmt;
    }

    private Cat createCat(ResultSet results) throws SQLException {
        LocalDate vaccineDate;

        String name = results.getString("NAME");
        if (name == null || name.length() < 1 || name.length() > 50) return null;

        int id = results.getInt("ID");
        int age = results.getInt("AGE");
        float weight = results.getFloat("WEIGHT");
        boolean chubby = results.getString("CHUBBY").equals("true");
        boolean sweet = results.getString("SWEET").equals("true");
        vaccineDate = results.getDate("VACCINEDATE").toLocalDate();

        return new Cat(id, name, age, vaccineDate, weight, chubby, sweet);
    }
}