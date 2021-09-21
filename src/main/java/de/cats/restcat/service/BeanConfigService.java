package de.cats.restcat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@Configuration
public class BeanConfigService {
    private Context envContext;

    @PostConstruct
    public void init() throws NamingException {
        System.out.println("BeanConfigService: init() Context");
        Context initContext;
        try {
            initContext = new InitialContext();
            envContext = (Context) initContext.lookup("java:/comp/env");
        } catch (NamingException e) {
            throw new NamingException("Context not found");
        }
    }

    @Bean
    public DataSource dataSource() {
        DataSource dataSource = null;

        try {
            dataSource = (DataSource) envContext.lookup("jdbc/MariaDB");
        } catch (NamingException e) {
            System.out.println("Object jdbc/MariaDB not found");
            e.printStackTrace();
        }
        envContext = null;
        System.out.println("DataSource initialized");
        return dataSource;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        System.out.println("Objektmapper initialized");
        return objectMapper;
    }

    @Bean
    public CatRepositoryBackupRepo backupRepo() {
        return new CatRepositoryBackupRepo(objectMapper());
    }

    @Bean
    public CatRepositoryPrimaryRepo primaryRepo() {
        return new CatRepositoryPrimaryRepo(dataSource());
    }

    @Bean
    public CatRepoService catRepoService() {
        return new CatRepoServiceImpl( primaryRepo(), backupRepo());
    }

    @Bean
    public CatService catService() {
        return new CatServiceImpl(catRepoService());
    }
}