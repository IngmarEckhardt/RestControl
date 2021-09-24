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

@SpringJUnitWebConfig(classes = CatAppInitializer.class)
class CatRepoServiceImplTest {
    private static CatRepositoryPrimary primaryRepo;
    private static CatRepositoryBackup localBackup;
    private CatRepoService catRepoService;
    private ArrayList<Cat> catArrayList;

    private final Cat dummyCatDateNull =
            new Cat(0, "DummyNullDate", 1, null, 2.2f, true, true);
    private final Cat dummyCatWithDate =
            new Cat(0, "DummyWithDate", 1, LocalDate.now(), 2, true, true);

    @BeforeEach
    void beforeTestMethod() {
        primaryRepo = mock(CatRepositoryPrimary.class);
        localBackup = mock(CatRepositoryBackup.class);
        catArrayList = null;
    }

    @Test
    void readCat_bothReposWorking_shouldReadCatsFromPrimaryRepositoryAndSafeToBackup() {
        //given
        when(primaryRepo.readCats()).thenReturn(new ArrayList<>(Arrays.asList(dummyCatDateNull, dummyCatWithDate)));
        when(localBackup.readCats()).thenReturn(new ArrayList<>(Arrays.asList(dummyCatWithDate, dummyCatDateNull)));
        catRepoService = new CatRepoServiceImpl(primaryRepo, localBackup);

        //when
        catArrayList = catRepoService.readCats();

        //then
        assertAll("it should read Cats from a working primary Repository, but loading parallel from a backUp-Repository," +
                        "the first Cats Name should equal 'Ernst' ",
                () -> assertThat(catArrayList.get(0), isA(Cat.class)),
                () -> assertEquals(dummyCatDateNull.getName(), catArrayList.get(0).getName()),
                () -> verify(primaryRepo, times(1)).readCats(),
                () -> verify(localBackup, times(1)).readCats(),
                () -> verify(localBackup, times(1)).writeCats(Mockito.any()));
    }

    @Test
    void readCat_withBackUpNotWorking_shouldReadCatsFromPrimaryRepositoryAndDontTryToWriteBackUp() {
        //given
        when(primaryRepo.readCats()).thenReturn(new ArrayList<>(Arrays.asList(dummyCatDateNull, dummyCatWithDate)));
        when(localBackup.readCats()).thenReturn(null);
        catRepoService = new CatRepoServiceImpl(primaryRepo, localBackup);

        //when
        catArrayList = catRepoService.readCats();

        //then
        assertAll("it should read Cats from a working primary Repository, but loading parallel from a backUp-Repository," +
                        "the first Cats Name should equal 'Ernst' ",
                () -> assertThat(catArrayList.get(0), isA(Cat.class)),
                () -> assertEquals(dummyCatDateNull.getName(), catArrayList.get(0).getName()),
                () -> verify(primaryRepo, times(1)).readCats(),
                () -> verify(localBackup, times(1)).readCats(),
                () -> verify(localBackup, times(0)).writeCats(Mockito.any()));
    }

    @Test
    void readCats_withPrimaryRepoNotWorking_shouldReadCatsFromBackUpRepository() {
        //given
        catRepoService = new CatRepoServiceImpl(primaryRepo, localBackup);

        //when
        when(primaryRepo.readCats()).thenReturn(null);
        when(localBackup.readCats()).thenReturn(new ArrayList<>(Arrays.asList(dummyCatWithDate, dummyCatDateNull)));
        catArrayList = catRepoService.readCats();

        //then
        assertAll("It should try to read from both Repositories and load Cats from the Backup-Repo if primary wont work" +
                        "first Cat of the BackupList has name 'Dummy'",
                () -> assertThat(catArrayList.get(0), isA(Cat.class)),
                () -> assertEquals(dummyCatWithDate.getName(), catArrayList.get(0).getName()),
                () -> verify(primaryRepo, times(1)).readCats(),
                () -> verify(localBackup, times(1)).readCats());
    }
    @Test
    void readCats_withPrimaryThrowingRuntimeException_shouldReadCatsFromBackUpRepository() {
        //given
        catRepoService = new CatRepoServiceImpl(primaryRepo, localBackup);

        //when
        when(primaryRepo.readCats()).thenThrow(RuntimeException.class);
        when(localBackup.readCats()).thenReturn(new ArrayList<>(Arrays.asList(dummyCatWithDate, dummyCatDateNull)));
        catArrayList = catRepoService.readCats();

        //then
        assertAll("It should try to read from both Repositories and load Cats from the Backup-Repo if primary wont work" +
                        "first Cat of the BackupList has name 'Dummy'",
                () -> assertThat(catArrayList.get(0), isA(Cat.class)),
                () -> assertEquals(dummyCatWithDate.getName(), catArrayList.get(0).getName()),
                () -> verify(primaryRepo, times(1)).readCats(),
                () -> verify(localBackup, times(1)).readCats());
    }

    @Test
    void addNewCat_withPrimaryRepoWorking_shouldInvokePrimaryRepoAndReturnTrue() {
        //given
        catRepoService = new CatRepoServiceImpl(primaryRepo, localBackup);
        //when
        when(primaryRepo.addNewCat(Mockito.any())).thenReturn(true);
        Boolean successful = catRepoService.addNewCat(dummyCatDateNull);
        //then
        assertAll("should invoke addNewCat() from the primary Repo with the correct cat and return true",
                () -> verify(primaryRepo, times(1)).addNewCat(dummyCatDateNull),
                () -> assertTrue(successful));
    }
    @Test
    void addNewCat_withPrimaryRepoThrowingRuntimeException_shouldInvokePrimaryRepoAndReturnFalse() {
        //given
        catRepoService = new CatRepoServiceImpl(primaryRepo, localBackup);
        //when
        when(primaryRepo.addNewCat(Mockito.any())).thenThrow(RuntimeException.class);
        Boolean successful = catRepoService.addNewCat(dummyCatDateNull);
        //then
        assertAll("should invoke addNewCat() from the primary Repo with the correct cat and return false",
                () -> verify(primaryRepo, times(1)).addNewCat(dummyCatDateNull),
                () -> assertFalse(successful));
    }
    @Test
    void addNewCat_withPrimaryRepoReturnFalse_shouldInvokePrimaryRepoAndReturnFalse() {
        //given
        catRepoService = new CatRepoServiceImpl(primaryRepo, localBackup);
        //when
        when(primaryRepo.addNewCat(Mockito.any())).thenReturn(false);
        Boolean successful = catRepoService.addNewCat(dummyCatDateNull);
        //then
        assertAll("should invoke addNewCat() from the primary Repo with the correct cat and return false",
                () -> verify(primaryRepo, times(1)).addNewCat(dummyCatDateNull),
                () -> assertFalse(successful));
    }
    @Test
    void editCat_withPrimaryRepoWorking_shouldInvokePrimaryRepoAndReturnTrue() {
        //given
        catRepoService = new CatRepoServiceImpl(primaryRepo, localBackup);
        //when
        when(primaryRepo.editCat(Mockito.any())).thenReturn(true);
        Boolean successful = catRepoService.editCat(dummyCatDateNull);
        //then
        assertAll("should invoke addNewCat() from the primary Repo with the correct cat and return true",
                () -> verify(primaryRepo, times(1)).editCat(dummyCatDateNull),
                () -> assertTrue(successful));
    }

    @Test
    void editCat_withPrimaryRepoThrowingRuntimeException_shouldInvokePrimaryRepoAndReturnFalse() {
        //given
        catRepoService = new CatRepoServiceImpl(primaryRepo, localBackup);
        //when
        when(primaryRepo.editCat(Mockito.any())).thenThrow(RuntimeException.class);
        Boolean successful = catRepoService.editCat(dummyCatDateNull);
        //then
        assertAll("should invoke addNewCat() from the primary Repo with the correct cat and return false",
                () -> verify(primaryRepo, times(1)).editCat(dummyCatDateNull),
                () -> assertFalse(successful));
    }
    @Test
    void editCat_withPrimaryRepoReturnFalse_shouldInvokePrimaryRepoAndReturnFalse() {
        //given
        catRepoService = new CatRepoServiceImpl(primaryRepo, localBackup);
        //when
        when(primaryRepo.editCat(Mockito.any())).thenReturn(false);
        Boolean successful = catRepoService.editCat(dummyCatDateNull);
        //then
        assertAll("should invoke addNewCat() from the primary Repo with the correct cat and return false",
                () -> verify(primaryRepo, times(1)).editCat(dummyCatDateNull),
                () -> assertFalse(successful));
    }
    @Test
    void deleteCat_withPrimaryRepoWorking_shouldInvokePrimaryRepoAndReturnTrue() {
        //given
        catRepoService = new CatRepoServiceImpl(primaryRepo, localBackup);
        //when
        when(primaryRepo.deleteCat(Mockito.any())).thenReturn(true);
        Boolean successful = catRepoService.deleteCat(dummyCatDateNull);
        //then
        assertAll("should invoke addNewCat() from the primary Repo with the correct cat and return true",
                () -> verify(primaryRepo, times(1)).deleteCat(dummyCatDateNull),
                () -> assertTrue(successful));
    }

    @Test
    void deleteCat_withPrimaryRepoThrowingRuntimeException_shouldInvokePrimaryRepoAndReturnFalse() {
        //given
        catRepoService = new CatRepoServiceImpl(primaryRepo, localBackup);
        //when
        when(primaryRepo.deleteCat(Mockito.any())).thenThrow(RuntimeException.class);
        Boolean successful = catRepoService.deleteCat(dummyCatDateNull);
        //then
        assertAll("should invoke addNewCat() from the primary Repo with the correct cat and return false",
                () -> verify(primaryRepo, times(1)).deleteCat(dummyCatDateNull),
                () -> assertFalse(successful));
    }
    @Test
    void deleteCat_withPrimaryRepoReturnFalse_shouldInvokePrimaryRepoAndReturnFalse() {
        //given
        catRepoService = new CatRepoServiceImpl(primaryRepo, localBackup);
        //when
        when(primaryRepo.deleteCat(Mockito.any())).thenReturn(false);
        Boolean successful = catRepoService.deleteCat(dummyCatDateNull);
        //then
        assertAll("should invoke addNewCat() from the primary Repo with the correct cat and return false",
                () -> verify(primaryRepo, times(1)).deleteCat(dummyCatDateNull),
                () -> assertFalse(successful));
    }
    @Test
    void replaceCatlist_withPrimaryRepoWorking_shouldInvokePrimaryRepoAndReturnTrue() {
        //given
        catArrayList=new ArrayList<>(Arrays.asList(dummyCatDateNull, dummyCatWithDate));
        catRepoService = new CatRepoServiceImpl(primaryRepo, localBackup);
        //when
        when(primaryRepo.writeCats(Mockito.any())).thenReturn(true);
        Boolean successful = catRepoService.replaceCatlist(catArrayList);
        //then
        assertAll("should invoke addNewCat() from the primary Repo with the correct cat and return true",
                () -> verify(primaryRepo, times(1)).writeCats(catArrayList),
                () -> assertTrue(successful));
    }

    @Test
    void replaceCatlist_withPrimaryRepoThrowingRuntimeException_shouldInvokePrimaryRepoAndReturnFalse() {
        //given
        catArrayList=new ArrayList<>(Arrays.asList(dummyCatDateNull, dummyCatWithDate));
        when(primaryRepo.writeCats(Mockito.any())).thenThrow(RuntimeException.class);
        catRepoService = new CatRepoServiceImpl(primaryRepo, localBackup);
        //when
        Boolean successful = catRepoService.replaceCatlist(catArrayList);
        //then
        assertAll("should invoke addNewCat() from the primary Repo with the correct cat and return false",
                () -> verify(primaryRepo, times(1)).writeCats(catArrayList),
                () -> assertFalse(successful));
    }
    @Test
    void replaceCatlist_withPrimaryRepoReturnFalse_shouldInvokePrimaryRepoAndReturnFalse() {
        //given
        catArrayList=new ArrayList<>(Arrays.asList(dummyCatDateNull, dummyCatWithDate));
        when(primaryRepo.writeCats(Mockito.any())).thenReturn(false);
        catRepoService = new CatRepoServiceImpl(primaryRepo, localBackup);
        //when
        Boolean successful = catRepoService.replaceCatlist(catArrayList);
        //then
        assertAll("should invoke addNewCat() from the primary Repo with the correct cat and return false",
                () -> verify(primaryRepo, times(1)).writeCats(catArrayList),
                () -> assertFalse(successful));
    }
}