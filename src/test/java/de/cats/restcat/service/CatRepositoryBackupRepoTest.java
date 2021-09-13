package de.cats.restcat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CatRepositoryBackupRepoTest {

    private ObjectMapper objectMapper;
    private CatRepository catsRepository;
    private ArrayList<Cat> catArray;
    private Boolean successful;

    private final File datei = new File(System.getProperty("user.home") + File.separator + "Cats.json");
    private final Cat dummyCatDateNull =
            new Cat(0, "DummyNullDate", 1, null, 2.2f, true, true);
    private final Cat dummyCatWithDate =
            new Cat(0, "DummyWithDate", 1, LocalDate.now(), 2, true, true);

    @Captor
    ArgumentCaptor<ArrayList> captor;
    @Captor
    ArgumentCaptor<File> filecaptor;


    @Test
    void readCats_withHDDready_shouldReturnArrayList() {
        //given
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        catsRepository = new CatRepositoryBackupRepo(objectMapper);

        //when
        ArrayList<Cat> results = catsRepository.readCats();

        //then
        assertTrue(results.get(0).getClass() == Cat.class);

    }

    @Test
    void readCats_withMockedListAndObjectMapper_shouldCreateAndReturnArrayList() throws IOException {
        //given
        setupWithMockObjectMapper();
        Cat[] cats = {dummyCatDateNull,dummyCatWithDate};
        catArray = new ArrayList<>(Arrays.asList(cats));
        when(objectMapper.readValue(any(File.class), eq(Cat[].class))).thenReturn(cats);

        //when
        List<Cat> listUnderTest = catsRepository.readCats();

        //then
        assertThat(listUnderTest, is(catArray));
        assertSame(listUnderTest.get(0).getClass(), Cat.class);
    }

    @Test
    public void readCats_throwingAnIOException_shouldThrowNewRuntimeExceptionWithMessage() throws IOException {
        //given
        setupWithMockObjectMapper();

        //do-When
        doThrow(new IOException()).when(objectMapper).readValue(any(File.class), any(Class.class));

        //when-then
        Exception exception = assertThrows(RuntimeException.class, () -> catsRepository.readCats());
        assertEquals(exception.getMessage(), "Die Cats.json-Datei war nicht lesbar");
    }


    @Test
    void writeCats_withHDDworking_shouldInvokeObjectmapper() throws IOException {
        //given
        setupWithMockObjectMapper();
        catArray = new ArrayList<>(Arrays.asList(dummyCatDateNull,dummyCatWithDate));

        //when
        successful = catsRepository.writeCats(catArray);

        //then
        assertAll("should invoke the Objectmapper with the correct path and Array",
                () -> verify(objectMapper, times(1)).writeValue(filecaptor.capture(), captor.capture()),
                () -> assertEquals(captor.getValue(), catArray),
                () -> assertEquals(filecaptor.getValue(), datei),
                () -> assertTrue(successful));
    }

    @Test
    public void writeCats_throwingAnIOException_shouldThrowNewRuntimeExceptionWithMessage() throws IOException {
        //given
        setupWithMockObjectMapper();
        catArray = new ArrayList<>(Arrays.asList(dummyCatDateNull,dummyCatWithDate));

        //when
        doThrow(new IOException()).when(objectMapper).writeValue(datei, catArray);

        //then
        Exception exception = assertThrows(RuntimeException.class, () -> successful = catsRepository.writeCats(catArray));
        assertAll("it should return null and throw a RuntimeException with a Message",
                () -> assertEquals(exception.getMessage(), "Die Cats.json-Datei war nicht schreibbar"),
                () -> assertNull(successful));
    }

    @Test
    void addCatEditCatDeleteCat_whenNotImplemented_shouldReturnFalse() {
        //given
        setupWithMockObjectMapper();

        //when
        Boolean successAddCat = catsRepository.addNewCat(dummyCatDateNull);
        Boolean successEditCat = catsRepository.editCat(dummyCatDateNull);
        Boolean successDeleteCat = catsRepository.deleteCat(dummyCatDateNull);

        //then
        assertFalse(successAddCat);
        assertFalse(successEditCat);
        assertFalse(successDeleteCat);
    }

    private void setupWithMockObjectMapper() {
        objectMapper = mock(ObjectMapper.class);
        catsRepository = new CatRepositoryBackupRepo(objectMapper);
    }
}