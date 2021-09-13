package de.cats.restcat.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CatRepositoryPrimaryRepoTest {

    private static final String ALL_VALUES =
            "select ID, NAME, AGE, VACCINEDATE, WEIGHT, CHUBBY, SWEET from CatControl.Cats";
    private static final String ERASE_TABLE =
            "TRUNCATE TABLE CatControl.Cats";
    private static final String COUNT_CATS =
            "select COUNT(*) from CatControl.Cats";
    private static final String INSERT_CAT =
            "INSERT INTO CatControl.Cats (NAME, AGE, VACCINEDATE, WEIGHT, CHUBBY, SWEET) VALUES (?,?,?,?,?,?)";
    private static final String GET_MAX_ID =
            "select MAX(ID) from CatControl.Cats";
    private static final String GET_CAT =
            "select ID, NAME, AGE, VACCINEDATE, WEIGHT, CHUBBY, SWEET from CatControl.Cats where ID = ?";
    private static final String DELETE_ID =
            "DELETE FROM CatControl.Cats WHERE ID = ?";
    private static final String UPDATE_ID =
            "REPLACE INTO CatControl.Cats (ID, NAME, AGE, VACCINEDATE, WEIGHT, CHUBBY, SWEET) VALUES (?,?,?,?,?,?,?)";

    private Boolean success;
    private Connection connection;
    private CatRepositoryPrimaryRepo mariaDB;
    private ArrayList<Cat> catlist;
    private int idTestCat;
    private String nameTestCat;
    private final Cat dummyCatDateNull = new Cat(0, "DummyNullDate", 1, null, 2.2f, true, true);
    private final Cat dummyCatWithDate = new Cat(0, "DummyWithDate", 1, LocalDate.now(), 2, true, true);


    @Test
    public void readCats_withMariaDbOnline_shouldLoadCats() throws Exception {
        //given
        setUpWithTestDB();

        //when

        catlist = mariaDB.readCats();
        //then

        assertAll("There is a cat with name Ernst at first position in the Database",
                () -> assertEquals("DummyWithDate", catlist.get(0).getName()),
                () -> assertThat(catlist.get(0), isA(Cat.class)));
    }

    @Test
    public void readCats_throwingSQLException_getRuntimeExceptionWithMessage() throws Exception {
        //given
        setUpWithMockDB();

        //when
        when(connection.prepareStatement(COUNT_CATS)).thenThrow(SQLException.class);

        //then
        Exception exception = assertThrows(RuntimeException.class, () -> catlist = mariaDB.readCats());
        assertAll("SQL-Exceptions",
                () -> assertEquals(exception.getMessage(), "SQL-Verbindung zur MariaDB fehlgeschlagen"),
                () -> assertNull(catlist),
                () -> verify(connection, times(1)).prepareStatement(COUNT_CATS));
    }

    @Test
    public void readCats_throwingRuntimeException_shouldReturnArrayListNull() throws Exception {
        //given
        catlist = null;
        setUpWithMockDB();

        //when
        when(connection.prepareStatement(COUNT_CATS)).thenThrow(RuntimeException.class);
        catlist = mariaDB.readCats();

        //then
        assertAll("It Should Return a ArrayList with the value null",
                () -> assertNull(catlist),
                () -> verify(connection, times(1)).prepareStatement(COUNT_CATS));

    }


    @Test
    void addNewCat_withMariaDbOnline_shouldSafeACat() throws Exception {
        //given
        setUpWithTestDB();

        //when
        success = mariaDB.addNewCat(dummyCatDateNull);
        mariaDB.addNewCat(dummyCatWithDate);

        PreparedStatement stmt = connection.prepareStatement(GET_MAX_ID);
        ResultSet set = stmt.executeQuery();
        if (set.next()) idTestCat = set.getInt(1);

        PreparedStatement getCats = connection.prepareStatement(ALL_VALUES);
        set = getCats.executeQuery();
        do {
            if (!set.next()) break;
            nameTestCat = set.getString("NAME");
        } while (idTestCat != set.getInt("ID"));

        //then
        assertAll("it should return a boolean true and the last cat in the test db should have the name DummyCatWithDate",
                () -> assertTrue(success),
                () -> assertEquals("DummyWithDate", nameTestCat));


    }

    @Test
    void addNewCat_throwingSQLException_getRuntimeExceptionWithMessageAndReturnNull() throws Exception {
        //given
        setUpWithMockDB();

        //when
        when(connection.prepareStatement(INSERT_CAT)).thenThrow(SQLException.class);

        //then
        Exception exception = assertThrows(RuntimeException.class, () -> success = mariaDB.addNewCat(dummyCatDateNull));
        assertAll("it should catch a SQLException and throw a Runtime Exception with own Message",
                () -> assertEquals(exception.getMessage(), "SQL-Verbindung zur MariaDB fehlgeschlagen"),
                () -> assertNull(success));
    }

    @Test
    void addNewCat_throwingRuntimeException_shouldReturnFalse() throws Exception {
        //given
        setUpWithMockDB();
        success = null;

        //when
        when(connection.prepareStatement(INSERT_CAT)).thenThrow(RuntimeException.class);
        success = mariaDB.addNewCat(dummyCatDateNull);

        //then
        assertFalse(success);
    }



    @Test
    void writeCats_withMariaDbOnline_shouldWriteCatArrayWithSecondNameDummy() throws Exception {
        //given
        catlist = new ArrayList<>(Arrays.asList(dummyCatWithDate,dummyCatDateNull));
        setUpWithTestDB();
        success = null;

        //when
        success = mariaDB.writeCats(catlist);
        setUpWithTestDB();
        ArrayList<Cat> results = mariaDB.readCats();

        //then
        assertTrue(success);
        assertAll("it should write the cats in the test-Repository, second cat-name should be 'Dummy'.",
                () -> assertEquals(2, results.get(1).getId()),
                () -> assertEquals(dummyCatDateNull.getName(), results.get(1).getName()));
    }

    @Test
    void writeCats_throwingSQLException_getRuntimeExceptionWithMessageAndReturnNull() throws Exception {
        //given
        setUpWithMockDB();
        catlist = new ArrayList<>(Arrays.asList(dummyCatWithDate,dummyCatDateNull));

        //when
        when(connection.prepareStatement(ERASE_TABLE)).thenThrow(SQLException.class);

        //then
        Exception exception = assertThrows(RuntimeException.class, () -> success = mariaDB.writeCats(catlist));
        assertAll("when a SQLException occurs it should catch it and throw a RuntimeException with own message",
                () -> assertEquals(exception.getMessage(), "SQL-Verbindung zur MariaDB fehlgeschlagen"),
                () -> assertNull(success),
                () -> verify(connection, times(1)).prepareStatement(ERASE_TABLE));
    }

    @Test
    void writeCats_throwingRuntimeException_shouldReturnFalse() throws Exception {
        //given
        setUpWithMockDB();
        catlist = new ArrayList<>(Arrays.asList(dummyCatWithDate,dummyCatDateNull));
        //when
        when(connection.prepareStatement(ERASE_TABLE)).thenThrow(RuntimeException.class);
        assertNull(success);
        success = mariaDB.writeCats(catlist);

        //then
        assertFalse(success);
        verify(connection, times(1)).prepareStatement(ERASE_TABLE);
    }

    @Test
    void deleteCat_withMariaDbOnline_ShouldDeleteCat() throws SQLException {
        //given
        setUpWithTestDB();
        PreparedStatement eraseTable = connection.prepareStatement(ERASE_TABLE);
        eraseTable.executeQuery();

        dummyCatDateNull.setName("DummyToStay");
        addCatToTestDatabase(dummyCatDateNull);
        addCatToTestDatabase(dummyCatWithDate);

        //when
        success = mariaDB.deleteCat(dummyCatWithDate);

        //getLastCat
        PreparedStatement stmtGetHighestID = connection.prepareStatement(GET_MAX_ID);
        ResultSet setID = stmtGetHighestID.executeQuery();
        if (setID.next()) idTestCat = setID.getInt(1);

        PreparedStatement stmtGetLast = connection.prepareStatement(GET_CAT);
        stmtGetLast.setInt(1, idTestCat);
        ResultSet lastCatObj = stmtGetLast.executeQuery();
        if (lastCatObj.next()) nameTestCat = lastCatObj.getString("NAME");

        //then
        assertNotEquals("DummyWithDate", nameTestCat);
        assertTrue(success);
    }

    @Test
    void deleteCat_throwingSQLException_getRuntimeExceptionWithMessageAndReturnNull() throws Exception {
        //given
        setUpWithMockDB();

        //when
        when(connection.prepareStatement(DELETE_ID)).thenThrow(SQLException.class);

        //then
        Exception exception = assertThrows(RuntimeException.class, () -> success = mariaDB.deleteCat(Mockito.any()));
        assertAll("when a SQLException occurs it should catch it and throw a RuntimeException with own message",
                () -> assertEquals(exception.getMessage(), "SQL-Verbindung zur MariaDB fehlgeschlagen"),
                () -> assertNull(success),
                () -> verify(connection, times(1)).prepareStatement(DELETE_ID));
    }

    @Test
    void deleteCat_throwingRuntimeException_shouldReturnFalse() throws Exception {
        //given
        setUpWithMockDB();

        //when
        when(connection.prepareStatement(DELETE_ID)).thenThrow(RuntimeException.class);

        success = mariaDB.deleteCat(Mockito.any());
        //then
        assertFalse(success);
        verify(connection, times(1)).prepareStatement(DELETE_ID);
    }

    @Test
    void editCat_withMariaDbOnline_shouldUpdateACat() throws Exception {
        //given
        setUpWithTestDB();
        addCatToTestDatabase(dummyCatDateNull);
        addCatToTestDatabase(dummyCatWithDate);
        dummyCatDateNull.setName("NewName");
        dummyCatWithDate.setName("DummyToStay");

        //when
        mariaDB.editCat(dummyCatWithDate);
        mariaDB.editCat(dummyCatDateNull);

        PreparedStatement stmtGetLastCat = connection.prepareStatement(GET_CAT);
        stmtGetLastCat.setInt(1, dummyCatDateNull.getId());
        ResultSet lastCat = stmtGetLastCat.executeQuery();
        if (lastCat.next()) nameTestCat = lastCat.getString("NAME");

        //then
        assertEquals("NewName", nameTestCat);
    }

    @Test
    void editCat_throwingSQLException_getRuntimeExceptionWithMessageAndReturnNull() throws Exception {
        //given
        setUpWithMockDB();

        //when
        when(connection.prepareStatement(UPDATE_ID)).thenThrow(SQLException.class);

        //then
        Exception exception = assertThrows(RuntimeException.class, () -> success = mariaDB.editCat(Mockito.any()));
        assertAll("when a SQLException occurs it should catch it and throw a RuntimeException with own message",
                () -> assertEquals(exception.getMessage(), "SQL-Verbindung zur MariaDB fehlgeschlagen"),
                () -> assertNull(success),
                () -> verify(connection, times(1)).prepareStatement(UPDATE_ID));
    }

    @Test
    void editCat_throwingRuntimeException_shouldReturnFalse() throws Exception {
        //given
        setUpWithMockDB();

        //when
        when(connection.prepareStatement(UPDATE_ID)).thenThrow(RuntimeException.class);

        success = mariaDB.editCat(Mockito.any());
        //then
        assertFalse(success);
        verify(connection, times(1)).prepareStatement(UPDATE_ID);
    }



    private void setUpWithMockDB() {
        this.connection = mock(Connection.class);
        mariaDB = new CatRepositoryPrimaryRepo(connection);
    }

    private void setUpWithTestDB() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:mariadb://localhost/", "max1", "password");
        mariaDB = new CatRepositoryPrimaryRepo(connection);
    }

    private void addCatToTestDatabase(Cat newCat) throws SQLException {
        insertCatStatement(connection, newCat).executeQuery();

        PreparedStatement stmtGetIdNewCat = connection.prepareStatement(GET_MAX_ID);
        ResultSet set = stmtGetIdNewCat.executeQuery();
        if (set.next()) idTestCat = set.getInt(1);

        PreparedStatement stmtGetLastCat = connection.prepareStatement(GET_CAT);
        stmtGetLastCat.setInt(1, idTestCat);
        ResultSet lastCat = stmtGetLastCat.executeQuery();
        if (lastCat.next()) nameTestCat = lastCat.getString("NAME");

        assertEquals(newCat.getName(), nameTestCat);
        newCat.setId(idTestCat);
    }

    private PreparedStatement insertCatStatement(Connection conn, Cat catToInsert) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement(
                INSERT_CAT);

        stmt.setString(1, catToInsert.getName());
        stmt.setInt(2, catToInsert.getAge());
        if (catToInsert.getVaccineDate() != null)
            stmt.setDate(3, Date.valueOf(catToInsert.getVaccineDate()));
        else stmt.setDate(3, Date.valueOf("2001-01-01"));
        stmt.setFloat(4, catToInsert.getWeight());
        stmt.setString(5, catToInsert.isChubby() ? "true" : "false");
        stmt.setString(6, catToInsert.isSweet() ? "true" : "false");

        return stmt;
    }
}

