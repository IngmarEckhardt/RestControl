package de.cats.restcat.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import de.cats.restcat.CatAppInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitWebConfig(classes = CatAppInitializer.class)
public class BeanConfigServiceTest {
    private BeanConfigService beanConfigService;
    private CatRepositoryPrimary mariaDB;
    private InitialContext initContext;
    private DataSource dataSource;
    private Connection connection;


    private DataSource dataSource() throws Exception {
        SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        this.initContext = new InitialContext();
        this.initContext.bind("java:comp/env/jdbc/datasource",
                new DriverManagerDataSource("jdbc:mariadb://localhost:3306/CatControlTest", "max1", "password"));
        return dataSource = (DataSource) this.initContext.lookup("java:comp/env/jdbc/datasource");
//        mariaDB = new CatRepositoryPrimary(dataSource);
    }

    @Test
    void init_withWorkingContext_shouldWithoutErrors () throws NamingException {
        //given
        beanConfigService = new BeanConfigService();
        initContext = mock(InitialContext.class);
        beanConfigService.setInitContext(initContext);
        Context envContext = mock(Context.class);
        when(initContext.lookup("java:/comp/env")).thenReturn(envContext);

        //when-then
        beanConfigService.init();
    }

    @Test
    void init_withNotWorkingContext_shouldThrowNamingExceptionWithMessage () throws NamingException {
        //given
        beanConfigService = new BeanConfigService();
        initContext = mock(InitialContext.class);
        beanConfigService.setInitContext(initContext);
        //when
        when(initContext.lookup("java:/comp/env")).thenThrow(NamingException.class);

        //then
        Exception exception = assertThrows(NamingException.class, () ->  beanConfigService.init());
        assertEquals(exception.getMessage(), "Context not found");
    }
    @Test
    void dataSource_withWorkingContext_shouldReturnDatasourceObject () throws Exception {
        //given
        beanConfigService = new BeanConfigService();
        InitialContext initContext = mock(InitialContext.class);
        beanConfigService.setInitContext(initContext);
        Context envContext = mock(Context.class);
        when(initContext.lookup("java:/comp/env")).thenReturn(envContext);
        beanConfigService.init();
        when(envContext.lookup("jdbc/MariaDB")).thenReturn(dataSource());

        //when
        Object dataSourceFromMethod = beanConfigService.dataSource();

        //then
        assertAll("it should return the Datasource-Objekt and Invoke the Mock-Context",
                () -> assertInstanceOf(DataSource.class,dataSourceFromMethod),
                () -> verify(initContext,times(1)).lookup(Mockito.anyString()),
                () -> verify(envContext, times(1)).lookup(Mockito.anyString())
        );
    }
    @Test
    void dataSource_withoutContext_shouldReturnNull () throws Exception {
        //given
        beanConfigService = new BeanConfigService();
        InitialContext initContext = mock(InitialContext.class);
        beanConfigService.setInitContext(initContext);
        Context envContext = mock(Context.class);
        when(initContext.lookup("java:/comp/env")).thenReturn(envContext);
        beanConfigService.init();
        when(envContext.lookup("jdbc/MariaDB")).thenThrow(NamingException.class);

        //when
        DataSource dataSourceFromMethod = beanConfigService.dataSource();

        //then
        assertAll("it should handle the naming Exception and return null",
                () -> assertNull(dataSourceFromMethod),
                () -> verify(initContext,times(1)).lookup(Mockito.anyString()),
                () -> verify(envContext, times(1)).lookup(Mockito.anyString())
        );
    }

    @Test
    void objectMapper_withJacksonInstalled_shouldReturnObjectMapperSingleton() {
        //given
        beanConfigService = new BeanConfigService();
        // when
        Object mapper = beanConfigService.objectMapper();

        //then
        assertInstanceOf(ObjectMapper.class,mapper);
    }

}
