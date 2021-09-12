package de.cats.restcat;

import de.cats.restcat.controller.CatRestController;
import de.cats.restcat.controller.CatViewController;
import de.cats.restcat.controller.CatViewControllerConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class CatAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
        webApplicationContext.scan("de.cats.restcat.service");
        webApplicationContext.register(CatViewControllerConfig.class, CatViewController.class, CatRestController.class);
        servletContext.addListener(new ContextLoaderListener(webApplicationContext));

        DispatcherServlet dispatcherServlet = new DispatcherServlet(webApplicationContext);

        ServletRegistration.Dynamic catDispatcherServlet =
                servletContext.addServlet("catDispatcherServlet", dispatcherServlet);

        catDispatcherServlet.setLoadOnStartup(1);
        catDispatcherServlet.addMapping("/");
    }
}
