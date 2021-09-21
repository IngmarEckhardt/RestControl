package de.cats.restcat.service;

import de.cats.restcat.CatAppInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import org.mockito.Mockito;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitWebConfig(classes = CatAppInitializer.class)
class CatRepositoryPrimaryRepoTest {

    private static final String
            ALL_VALUES = "select ID, NAME, AGE, VACCINEDATE, WEIGHT, CHUBBY, SWEET from Cats",
            ERASE_TABLE = "TRUNCATE TABLE Cats",
            INSERT_CAT = "INSERT INTO Cats (NAME, AGE, VACCINEDATE, WEIGHT, CHUBBY, SWEET) VALUES (?,?,?,?,?,?)",
            GET_MAX_ID = "select MAX(ID) from Cats",
            GET_CAT = "select ID, NAME, AGE, VACCINEDATE, WEIGHT, CHUBBY, SWEET from Cats where ID = ?";

    private CatRepositoryPrimaryRepo mariaDB;
    private DataSource dataSource;
    private Connection connection;
    private Boolean success;
    private InitialContext initContext;
    private ArrayList<Cat> catlist;
    private int idTestCat;
    private String nameTestCat;
    private final Cat dummyCatDateNull = new Cat(0, "DummyNullDate", 1, null, 2.2f, true, true);
    private final Cat dummyCatWithDate = new Cat(0, "DummyWithDate", 1, LocalDate.now(), 2, true, true);

    @BeforeEach
    public void init() throws Exception {
        SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        this.initContext = new InitialContext();
    }

    @Test
    public void whenMockJndiDataSource_thenReturnJndiDataSource() throws Exception {
        this.initContext.bind("java:comp/env/jdbc/datasource",
                new DriverManagerDataSource("jdbc:mariadb://localhost:3306/CatControlTest", "max1", "password"));
        DataSource ds = (DataSource) this.initContext.lookup("java:comp/env/jdbc/datasource");

        assertNotNull(ds.getConnection());
    }

    @Test
    public void readCats_withMariaDbOnline_shouldLoadCats() throws Exception {
        //given
        setUpWithTestDB();

        //when

        System.out.println(connection);

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
        when(dataSource.getConnection()).thenThrow(SQLException.class);

        //then
        Exception exception = assertThrows(RuntimeException.class, () -> catlist = mariaDB.readCats());
        assertAll("SQL-Exceptions",
                () -> assertEquals(exception.getMessage(), "SQL-Verbindung zur MariaDB fehlgeschlagen"),
                () -> assertNull(catlist),
                () -> verify(dataSource, times(1)).getConnection());
    }

    @Test
    public void readCats_throwingRuntimeException_shouldReturnArrayListNull() throws Exception {
        //given
        catlist = null;
        setUpWithMockDB();

        //when
        when(dataSource.getConnection()).thenThrow(RuntimeException.class);
        catlist = mariaDB.readCats();

        //then
        assertAll("It Should Return a ArrayList with the value null",
                () -> assertNull(catlist),
                () -> verify(dataSource, times(1)).getConnection());

    }


    @Test
    void addNewCat_withMariaDbOnline_shouldSafeACat() throws Exception {
        //given
        setUpWithTestDB();

        //when
        success = mariaDB.addNewCat(dummyCatDateNull);
        mariaDB.addNewCat(dummyCatWithDate);

        Connection connection = dataSource.getConnection();
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
        when(dataSource.getConnection()).thenThrow(SQLException.class);

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
        when(dataSource.getConnection()).thenThrow(RuntimeException.class);
        success = mariaDB.addNewCat(dummyCatDateNull);

        //then
        assertFalse(success);
    }


    @Test
    void writeCats_withMariaDbOnline_shouldWriteCatArrayWithSecondNameDummy() throws Exception {
        //given
        setUpWithTestDB();

        catlist = new ArrayList<>(Arrays.asList(dummyCatWithDate, dummyCatDateNull));
        success = null;

        //when
        success = mariaDB.writeCats(catlist);
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

        catlist = new ArrayList<>(Arrays.asList(dummyCatWithDate, dummyCatDateNull));

        //when
        when(dataSource.getConnection()).thenThrow(SQLException.class);

        //then
        Exception exception = assertThrows(RuntimeException.class, () -> success = mariaDB.writeCats(catlist));
        assertAll("when a SQLException occurs it should catch it and throw a RuntimeException with own message",
                () -> assertEquals(exception.getMessage(), "SQL-Verbindung zur MariaDB fehlgeschlagen"),
                () -> assertNull(success),
                () -> verify(dataSource, times(1)).getConnection());
    }

    @Test
    void writeCats_throwingRuntimeException_shouldReturnFalse() throws Exception {
        //given
        setUpWithMockDB();

        catlist = new ArrayList<>(Arrays.asList(dummyCatWithDate, dummyCatDateNull));
        //when
        when(dataSource.getConnection()).thenThrow(RuntimeException.class);
        assertNull(success);
        success = mariaDB.writeCats(catlist);

        //then
        assertFalse(success);
        verify(dataSource, times(1)).getConnection();
    }

    @Test
    void deleteCat_withMariaDbOnline_ShouldDeleteCat() throws SQLException, NamingException {
        //given
        setUpWithTestDB();

        connection = dataSource.getConnection();
        PreparedStatement eraseTable = connection.prepareStatement(ERASE_TABLE);
        eraseTable.executeQuery();

        dummyCatDateNull.setName("DummyToStay");
        addCatToTestDatabase(connection, dummyCatDateNull);
        addCatToTestDatabase(connection, dummyCatWithDate);

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
        when(dataSource.getConnection()).thenThrow(SQLException.class);

        //then
        Exception exception = assertThrows(RuntimeException.class, () -> success = mariaDB.deleteCat(dummyCatDateNull));
        assertAll("when a SQLException occurs it should catch it and throw a RuntimeException with own message",
                () -> assertEquals(exception.getMessage(), "SQL-Verbindung zur MariaDB fehlgeschlagen"),
                () -> assertNull(success),
                () -> verify(dataSource, times(1)).getConnection());

    }

    @Test
    void deleteCat_throwingRuntimeException_shouldReturnFalse() throws Exception {
        //given
        setUpWithMockDB();

        //when
        when(dataSource.getConnection()).thenThrow(RuntimeException.class);

        success = mariaDB.deleteCat(dummyCatDateNull);
        //then
        assertFalse(success);
        verify(dataSource, times(1)).getConnection();
    }

    @Test
    void editCat_withMariaDbOnline_shouldUpdateACat() throws Exception {
        //given - adding Cats to Test-Database, then changing the name of the Cats.
        setUpWithTestDB();
        connection = dataSource.getConnection();

        addCatToTestDatabase(connection, dummyCatDateNull);
        addCatToTestDatabase(connection, dummyCatWithDate);
        dummyCatDateNull.setName("NewName");
        dummyCatWithDate.setName("DummyToStay");

        //when - adding changes to Database
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
        when(dataSource.getConnection()).thenThrow(SQLException.class);

        //then
        Exception exception = assertThrows(RuntimeException.class, () -> success = mariaDB.editCat(dummyCatDateNull));
        assertAll("when a SQLException occurs it should catch it and throw a RuntimeException with own message",
                () -> assertEquals(exception.getMessage(), "SQL-Verbindung zur MariaDB fehlgeschlagen"),
                () -> assertNull(success),
                () -> verify(dataSource, times(1)).getConnection());
    }

    @Test
    void editCat_throwingRuntimeException_shouldReturnFalse() throws Exception {
        //given
//        setUpWithMockDB();
        dataSource = mock(DataSource.class);
        mariaDB = new CatRepositoryPrimaryRepo(dataSource);

        //when
        when(dataSource.getConnection()).thenThrow(RuntimeException.class);

        success = mariaDB.editCat(Mockito.any());
        //then
        assertFalse(success);
//        verify(dataSource, times(1)).getConnection();
    }


    private void setUpWithMockDB() {
        dataSource = mock(DataSource.class);
        mariaDB = new CatRepositoryPrimaryRepo(dataSource);
    }

    private void setUpWithTestDB() throws SQLException, NamingException {
        this.initContext.bind("java:comp/env/jdbc/datasource",
                new DriverManagerDataSource("jdbc:mariadb://localhost:3306/CatControlTest", "max1", "password"));
        dataSource = (DataSource) this.initContext.lookup("java:comp/env/jdbc/datasource");
        mariaDB = new CatRepositoryPrimaryRepo(dataSource);
    }

    private DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.mariadb.jdbc.Driver");
        dataSourceBuilder.url("jdbc:mariadb://localhost:3306/CatControlTest");
        dataSourceBuilder.username("max1");
        dataSourceBuilder.password("password");
        return dataSourceBuilder.build();
    }

    private void addCatToTestDatabase(Connection connection, Cat newCat) throws SQLException {
        insertCatStatement(connection, newCat).executeQuery();

        /**checking if the last cat in the Test-Database has the same Name as newCat, setting
         * newCats-id to the ID that was given from the DB*/

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