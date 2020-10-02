package org.innopolis.kuzymvas.ejb;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class EnvironmentVarsProviderBeanTests {

    private EnvironmentVarsProviderBean bean;
    private String existingName;
    private String existingValue;
    private String notExistingName;

    @Before
    public void setUp() {
        bean = new EnvironmentVarsProviderBean();
        existingName = "Test Name";
        existingValue = "Test Value";
        notExistingName = UUID.randomUUID().toString();
        while (System.getenv(notExistingName) != null) {
            notExistingName = UUID.randomUUID().toString();
        }
    }

    @Test
    public void getAllVars() throws Exception {
        SystemLambda.withEnvironmentVariable(existingName, existingValue).execute(() -> {
            List<EnvironmentVarsProviderBean.NamedVariable> vars = bean.getAllVars();
            Map<String, String> env = System.getenv();
            Assert.assertEquals("Bean returned list of wrong size",env.size(), vars.size());
            for (EnvironmentVarsProviderBean.NamedVariable var : vars) {
                Assert.assertTrue("One of the variables in returned list is not an environmental variable",
                                  env.containsKey(var.getName()));
                Assert.assertEquals("One of the variables in returned list has wrong value",
                                    env.get(var.getName()), var.getValue());
            }
            for (int i = 0; i < vars.size() - 1; i++) {
                Assert.assertTrue("Returned list is unsorted",
                                  vars.get(i).compareTo(vars.get(i + 1)) <= 0);
            }
        });
    }

    @Test
    public void getVarValue() throws Exception {
        SystemLambda.withEnvironmentVariable(existingName, existingValue).execute(() -> {
            Optional<String> returnedExistingValue = bean.getVarValue(existingName);
            Assert.assertTrue("Bean was unable to return existing environmental variable",
                              returnedExistingValue.isPresent());
            Assert.assertEquals("Bean returned wrong value for existing environmental variable",
                                existingValue, returnedExistingValue.get());
            Optional<String> returnedNotExistingValue = bean.getVarValue(notExistingName);
            Assert.assertFalse("Bean was able to return non-existing environmental variable",
                               returnedNotExistingValue.isPresent());
        });
    }
}