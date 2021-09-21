package de.cats.restcat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CatRepoServiceImplTest {
    private static CatRepositoryPrimaryRepo mariaDB;
    private static CatRepositoryBackupRepo localBackup;
    private static ForkJoinPool repoServiceThreadPool;
    private CatRepoService catRepoService;
    private ArrayList<Cat> catArrayList;
    private final Cat dummyCatDateNull =
            new Cat(0, "DummyNullDate", 1, null, 2.2f, true, true);
    private final Cat dummyCatWithDate =
            new Cat(0, "DummyWithDate", 1, LocalDate.now(), 2, true, true);

    @BeforeEach
    void beforeTestMethod() {
        mariaDB = mock(CatRepositoryPrimaryRepo.class);
        localBackup = mock(CatRepositoryBackupRepo.class);
        repoServiceThreadPool = new ForkJoinPool(4);
        catArrayList = null;
    }

    @Test
    void readCat_bothReposWorking_shouldReadCatsFromPrimaryRepositoryAndSafeToBackup() {
        //given
        when(mariaDB.readCats()).thenReturn(new ArrayList<>(Arrays.asList(dummyCatDateNull, dummyCatWithDate)));
        when(localBackup.readCats()).thenReturn(new ArrayList<>(Arrays.asList(dummyCatWithDate, dummyCatDateNull)));
        catRepoService = new CatRepoServiceImpl( mariaDB, localBackup);

        //when
        catArrayList = catRepoService.readCats();

        //then
        assertAll("it should read Cats from a working primary Repository, but loading parallel from a backUp-Repository," +
                        "the first Cats Name should equal 'Ernst' ",
                () -> assertThat(catArrayList.get(0), isA(Cat.class)),
                () -> assertEquals(dummyCatDateNull.getName(), catArrayList.get(0).getName()),
                () -> verify(mariaDB, times(1)).readCats(),
                () -> verify(localBackup, times(1)).readCats(),
                () -> verify(localBackup, times(1)).writeCats(Mockito.any()));
    }

    @Test
    void readCats_withPrimaryRepoNotWorking_shouldReadCatsFromBackUpRepository() {
        //given
        when(mariaDB.readCats()).thenReturn(null);
        when(localBackup.readCats()).thenReturn(new ArrayList<>(Arrays.asList(dummyCatWithDate, dummyCatDateNull)));
        catRepoService = new CatRepoServiceImpl(mariaDB, localBackup);

        //when
        catArrayList = catRepoService.readCats();

        //then
        assertAll("It should try to read from both Repositories and load Cats from the Backup-Repo if primary wont work" +
                        "first Cat of the BackupList has name 'Dummy'",
                () -> assertThat(catArrayList.get(0), isA(Cat.class)),
                () -> assertEquals(dummyCatWithDate.getName(), catArrayList.get(0).getName()),
                () -> verify(mariaDB, times(1)).readCats(),
                () -> verify(localBackup, times(1)).readCats());

    }
}