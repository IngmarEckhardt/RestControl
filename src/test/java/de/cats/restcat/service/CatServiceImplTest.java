package de.cats.restcat.service;

import de.cats.restcat.CatAppInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringJUnitWebConfig(classes = CatAppInitializer.class)
public class CatServiceImplTest {
    private CatRepoService repoService;
    private CatService catService;
    private ArrayList<Cat> catArrayList;
    private Cat testCat;

    private final Cat dummyCatDateNull =
            new Cat(3, "DummyNullDate", 1, null, 2.2f, true, true);
    private final Cat dummyCatWithDate =
            new Cat(55, "DummyWithDate", 1, LocalDate.now(), 2, true, true);

    @BeforeEach
    void beforeTestMethod() {
        repoService = mock(CatRepoService.class);
        when(repoService.readCats()).thenReturn(new ArrayList<>(Arrays.asList(dummyCatDateNull, dummyCatWithDate)));
        catService = new CatServiceImpl(repoService);
        catArrayList = null;
    }

    @Test
    void getCatlist_withCatRepoServiceWorking_shouldReturnArrayList() {
        //given
        when(repoService.readCats()).thenReturn(new ArrayList<>(Arrays.asList(dummyCatDateNull, dummyCatWithDate)));

        //when
        catArrayList = catService.getCatlist();

        //then
        assertAll("it should invoke readCats-Method from RepoService-Objekt and return this ArrayList ",
                () -> assertThat(catArrayList.get(0), isA(Cat.class)),
                () -> assertEquals(dummyCatDateNull.getName(), catArrayList.get(0).getName()),
                () -> verify(repoService, times(2)).readCats());
    }

    @Test
    void getCat_withCatlistWorkingAndCorrectID_shouldFilterOutTheCorrectCatWithID() {
        //given-BeforeEach

        //when
        testCat = catService.getCat(55);

        //then
        assertAll("it should find the correct Cat in the ArrayList",
                () -> assertEquals(testCat, dummyCatWithDate),
                () -> verify(repoService, times(1)).readCats()
        );
    }

    @Test
    void getCat_withCatlistWorkingAndFalseID_shouldReturnNull() {
        //given-BeforeEach

        //when
        testCat = catService.getCat(53);

        //then
        assertAll("it should find the correct Cat in the ArrayList",
                () -> assertNull(testCat),
                () -> verify(repoService, times(1)).readCats()
        );
    }

    @Test
    void saveCat_withANewCat_shouldUseRepoServiceAddNewCatMethodAndReturnUpdatedArrayList() {
        //given-BeforeEach
        Cat newCatDateNull =
                new Cat(3, "DummyNewCat", 1, null, 2.2f, true, true);
        newCatDateNull.setId(null);
        //when
        catArrayList = catService.saveCat(newCatDateNull);

        //then
        assertAll("it should add the Cat with the addNewCat-Method vom RepoService and return a updated ArrayList",
                () -> verify(repoService, times(1)).readCats(),
                () -> verify(repoService, times(1)).addNewCat(newCatDateNull),
                () -> assertEquals(catArrayList.get(catArrayList.size() - 1), newCatDateNull)
        );
    }

    @Test
    void saveCat_withEditedCat_shouldUseRepoServiceEditCatMethodAndReturnUpdatedArrayList() {
        //given-BeforeEach
        Cat newCatDateNull =
                new Cat(6, "DummyEditCat", 1, null, 2.2f, true, true);
        //when
        catArrayList = catService.saveCat(newCatDateNull);

        //then
        assertAll("it should add the Cat with the editCat()-Method vom RepoService and return a updated ArrayList",
                () -> verify(repoService, times(1)).readCats(),
                () -> verify(repoService, times(1)).editCat(newCatDateNull),
                () -> assertEquals(catArrayList.get(catArrayList.size() - 1), newCatDateNull)
        );
    }

    @Test
    void saveCat_withNullCat_shouldUseRepoServiceEditCatMethodAndReturnUpdatedArrayList() {
        //given-BeforeEach
        Cat newCatDateNull = null;
        //when
        catArrayList = catService.saveCat(newCatDateNull);

        //then
        assertAll("it should add the Cat with the editCat()-Method vom RepoService and return a updated ArrayList",
                () -> verify(repoService, times(1)).readCats(),
                () -> assertNull(catArrayList)
        );
    }
    @Test
    void deleteCat_withExistingCat_shouldUseRepoServiceDeleteCatMethodAndReturnUpdatedArrayList() {
        //given-BeforeEach
        //when
        catArrayList = catService.deleteCat(dummyCatWithDate);

        //then
        assertAll("it should add the Cat with the editCat()-Method vom RepoService and return a updated ArrayList",
                () -> verify(repoService, times(1)).readCats(),
                () -> verify(repoService, times(1)).deleteCat(dummyCatWithDate),
                () -> assertFalse(catArrayList.contains(dummyCatWithDate))
        );
    }
    @Test
    void deleteCat_withNullCat_shouldReturnNullAndDidntInvokeRepoService() {
        //given-BeforeEach
        //when
        catArrayList = catService.deleteCat(null);

        //then
        assertAll("it should add the Cat with the editCat()-Method vom RepoService and return a updated ArrayList",
                () -> verify(repoService, times(1)).readCats(),
                () -> verify(repoService, times(0)).deleteCat(dummyCatWithDate),
                () -> assertNull(catArrayList)
        );
    }

    @Test
    void deleteCatWithID_withExistingCat_shouldUseRepoServiceDeleteCatMethodAndReturnUpdatedArrayList() {
        //given-BeforeEach
        //when
        catArrayList = catService.deleteCatWithID(55);

        //then
        assertAll("it should add the Cat with the editCat()-Method vom RepoService and return a updated ArrayList",
                () -> verify(repoService, times(1)).readCats(),
                () -> verify(repoService, times(1)).deleteCat(dummyCatWithDate),
                () -> assertFalse(catArrayList.contains(dummyCatWithDate))
        );
    }
    @Test
    void deleteCatWithID_withWrongCat_shouldReturnNullAndDidntInvokeRepoService() {
        //given-BeforeEach
        //when
        catArrayList = catService.deleteCatWithID(0);

        //then
        assertAll("it should add the Cat with the editCat()-Method vom RepoService and return a updated ArrayList",
                () -> verify(repoService, times(1)).readCats(),
                () -> verify(repoService, times(0)).deleteCat(dummyCatWithDate),
                () -> assertNull(catArrayList)
        );
    }
    @Test
    void deleteCatWithID_withIdNull_shouldReturnNullAndDidntInvokeRepoService() {
        //given-BeforeEach
        //when
        catArrayList = catService.deleteCatWithID(null);

        //then
        assertAll("it should add the Cat with the editCat()-Method vom RepoService and return a updated ArrayList",
                () -> verify(repoService, times(1)).readCats(),
                () -> verify(repoService, times(0)).deleteCat(dummyCatWithDate),
                () -> assertNull(catArrayList)
        );
    }

    @Test
    void findAll_withActualNameFromOneCat_shouldReturnArrayListOnlyWithThisCat() {
        //given-BeforeEach
        //when
        catArrayList = catService.findAll("DummyNullDate");

        //then
        assertAll("it should add the Cat with the editCat()-Method vom RepoService and return a updated ArrayList",
                () -> verify(repoService, times(1)).readCats(),
                () -> assertEquals(catArrayList.get(0),dummyCatDateNull)
        );
    }
    @Test
    void findAll_withActualNameFromTwo_shouldReturnArrayListWithAllMatches() {
        //given-BeforeEach
        //when
        catArrayList = catService.findAll("Dummy");

        //then
        assertAll("it should add the Cat with the editCat()-Method vom RepoService and return a updated ArrayList",
                () -> verify(repoService, times(1)).readCats(),
                () -> assertTrue(catArrayList.contains(dummyCatWithDate)),
                () -> assertTrue(catArrayList.contains(dummyCatDateNull))
        );
    }
    @Test
    void findAll_withNoMatchingName_shouldReturnEmptyArrayList() {
        //given-BeforeEach
        //when
        catArrayList = catService.findAll("NameOfNonExistingCat");

        //then
        assertAll("it should add the Cat with the editCat()-Method vom RepoService and return a updated ArrayList",
                () -> verify(repoService, times(1)).readCats(),
                () -> assertTrue(catArrayList.isEmpty())
        );
    }
    @Test
    void setNewCatlist_withExistingList_shouldInvokeReplaceCatlistAndReturnTheNewList() {
        //given-BeforeEach
        Cat catForReplacement =
                new Cat(3, "DummyFirstReplacement", 1, null, 2.2f, true, true);
        Cat secondCatForReplacement =
                new Cat(55, "DummySecondReplacement", 1, LocalDate.now(), 2, true, true);
        ArrayList<Cat> replacementList = new ArrayList<>(Arrays.asList(catForReplacement, secondCatForReplacement));
        //when
        assertTrue(catService.getCatlist().contains(dummyCatDateNull));
        when(repoService.readCats()).thenReturn(replacementList);
        catArrayList = catService.setNewCatList(replacementList);


        //then
        assertAll("it should add the Cat with the editCat()-Method vom RepoService and return a updated ArrayList",
                () -> verify(repoService, times(3)).readCats(),
                () -> verify(repoService, times(1)).replaceCatlist(replacementList),
                () -> assertTrue(catArrayList.contains(catForReplacement)),
                () -> assertTrue(catArrayList.contains(secondCatForReplacement))
        );
    }
}
