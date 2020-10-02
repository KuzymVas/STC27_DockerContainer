package org.innopolis.kuzymvas.servlets;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import org.innopolis.kuzymvas.ejb.EnvironmentVarsProviderBean;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@RunWith(Arquillian.class)
public class EnvVarsRESTTest {

    @Inject
    private EnvVarsREST rest;
    private String existingName;
    private String existingValue;
    private String notExistingName;
    private String otherExistingName;
    private String otherExistingValue;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ByteArrayOutputStream outputStream;

    @Deployment
    public static WebArchive createDeployment() {

        File[] mockitoFiles = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .resolve(
                        "org.mockito:mockito-core"
                )
                .withTransitivity()
                .asFile();
        File[] systemLambdaFiles = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .resolve(
                        "com.github.stefanbirkner:system-lambda"
                )
                .withTransitivity()
                .asFile();

        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, Filters.exclude(".*Test.*"),
                             EnvVarsREST.class.getPackage(),
                             EnvironmentVarsProviderBean.class.getPackage()
                )
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries(mockitoFiles)
                .addAsLibraries(systemLambdaFiles);
    }

    @Before
    public void setUp() throws IOException {
        existingName = "Test Name";
        existingValue = "Test Value";
        otherExistingName = "Different Name";
        otherExistingValue = "Different Value";
        notExistingName = UUID.randomUUID().toString();
        while (System.getenv(notExistingName) != null) {
            notExistingName = UUID.randomUUID().toString();
        }
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);
        Mockito.when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void doGetAll() throws Exception {

        SystemLambda
                .withEnvironmentVariable(existingName, existingValue)
                .and(otherExistingName, otherExistingValue)
                .execute(() -> {
                    rest.doGet(request, response);
                    Mockito.verify(response, Mockito.atLeastOnce()).setStatus(200);
                    Mockito.verify(response, Mockito.atLeastOnce()).setContentType("text/plain");
                    String respText = outputStream.toString(java.nio.charset.StandardCharsets.UTF_8.name());
                    Assert.assertTrue("One of variables names is missing from servlet output",
                                      respText.contains(existingName));
                    Assert.assertTrue("One of variables values is missing from servlet output",
                                      respText.contains(existingValue));
                    Assert.assertTrue("One of variables names is missing from servlet output",
                                      respText.contains(otherExistingName));
                    Assert.assertTrue("One of variables values is missing from servlet output",
                                      respText.contains(otherExistingValue));
                });
    }

    @Test
    public void doGetOneViaParameter() throws Exception {

        Mockito.when(request.getParameter("name")).thenReturn(existingName);
        SystemLambda
                .withEnvironmentVariable(existingName, existingValue)
                .and(otherExistingName, otherExistingValue)
                .execute(() -> {
                    rest.doGet(request, response);
                    Mockito.verify(response, Mockito.atLeastOnce()).setStatus(200);
                    Mockito.verify(response, Mockito.atLeastOnce()).setContentType("text/plain");
                    String respText = outputStream.toString(java.nio.charset.StandardCharsets.UTF_8.name());
                   Assert.assertTrue("Requested variable name is missing from servlet output",
                                      respText.contains(existingName));
                    Assert.assertTrue("Requested variable  value is missing from servlet output",
                                      respText.contains(existingValue));
                    Assert.assertFalse("Different variable name is present in servlet output",
                                       respText.contains(otherExistingName));
                    Assert.assertFalse("Different variable value is present in servlet output",
                                       respText.contains(otherExistingValue));
                });
    }

    @Test
    public void doGetOneViaPath() throws Exception {
        Mockito.when(request.getPathInfo()).thenReturn("/" + existingName);
        SystemLambda
                .withEnvironmentVariable(existingName, existingValue)
                .and(otherExistingName, otherExistingValue)
                .execute(() -> {
                    rest.doGet(request, response);
                    Mockito.verify(response, Mockito.atLeastOnce()).setStatus(200);
                    Mockito.verify(response, Mockito.atLeastOnce()).setContentType("text/plain");
                    String respText = outputStream.toString(java.nio.charset.StandardCharsets.UTF_8.name());
                    Assert.assertTrue("Requested variable name is missing from servlet output",
                                      respText.contains(existingName));
                    Assert.assertTrue("Requested variable  value is missing from servlet output",
                                      respText.contains(existingValue));
                    Assert.assertFalse("Different variable name is present in servlet output",
                                       respText.contains(otherExistingName));
                    Assert.assertFalse("Different variable value is present in servlet output",
                                       respText.contains(otherExistingValue));
                });
    }

    @Test
    public void doGetNegativeViaParameter() throws Exception {
        Mockito.when(request.getParameter("name")).thenReturn(notExistingName);
        SystemLambda
                .withEnvironmentVariable(existingName, existingValue)
                .and(otherExistingName, otherExistingValue)
                .execute(() -> {
                    rest.doGet(request, response);
                    Mockito.verify(response, Mockito.atLeastOnce()).setStatus(200);
                    Mockito.verify(response, Mockito.atLeastOnce()).setContentType("text/plain");
                    String respText = outputStream.toString(java.nio.charset.StandardCharsets.UTF_8.name());
                    Assert.assertFalse("Different variable name is present in servlet output",
                                       respText.contains(existingName));
                    Assert.assertFalse("Different variable value is present in servlet output",
                                       respText.contains(existingValue));
                    Assert.assertFalse("Different variable name is present in servlet output",
                                       respText.contains(otherExistingName));
                    Assert.assertFalse("Different variable value is present in servlet output",
                                       respText.contains(otherExistingValue));
                });
    }

    @Test
    public void doGetNegativeViaPath() throws Exception {
        Mockito.when(request.getPathInfo()).thenReturn("/" + notExistingName);
        SystemLambda
                .withEnvironmentVariable(existingName, existingValue)
                .and(otherExistingName, otherExistingValue)
                .execute(() -> {
                    rest.doGet(request, response);
                    Mockito.verify(response, Mockito.atLeastOnce()).setStatus(200);
                    Mockito.verify(response, Mockito.atLeastOnce()).setContentType("text/plain");
                    String respText = outputStream.toString(java.nio.charset.StandardCharsets.UTF_8.name());
                    Assert.assertFalse("Different variable name is present in servlet output",
                                       respText.contains(existingName));
                    Assert.assertFalse("Different variable value is present in servlet output",
                                       respText.contains(existingValue));
                    Assert.assertFalse("Different variable name is present in servlet output",
                                       respText.contains(otherExistingName));
                    Assert.assertFalse("Different variable value is present in servlet output",
                                       respText.contains(otherExistingValue));
                });
    }
}