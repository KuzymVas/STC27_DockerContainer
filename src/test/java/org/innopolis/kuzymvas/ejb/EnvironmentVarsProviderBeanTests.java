package org.innopolis.kuzymvas.ejb;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class EnvironmentVarsProviderBeanTests {

    private static EnvironmentVarsProviderBean bean;
    private static String existingName;
    private static String existingValue;
    private static String notExistingName;

    @org.junit.jupiter.api.BeforeAll
    static void setUp() {
        bean = new EnvironmentVarsProviderBean();
        existingName = "Test Name";
        existingValue = "Test Value";
        notExistingName = UUID.randomUUID().toString();
        while (System.getenv(notExistingName) != null) {
            notExistingName = UUID.randomUUID().toString();
        }
    }

    @org.junit.jupiter.api.Test
    void getAllVars() throws Exception {
        SystemLambda.withEnvironmentVariable(existingName, existingValue).execute(() -> {
            List<EnvironmentVarsProviderBean.NamedVariable> vars = bean.getAllVars();
            Map<String, String> env = System.getenv();
            Assertions.assertEquals(env.size(), vars.size(), "Bean returned list of wrong size");
            for (EnvironmentVarsProviderBean.NamedVariable var : vars) {
                Assertions.assertTrue(env.containsKey(var.getName()),
                                      "One of the variables in returned list is not an environmental variable");
                Assertions.assertEquals(env.get(var.getName()), var.getValue(),
                                        "One of the variables in returned list has wrong value");
            }
            for (int i = 0; i < vars.size() - 1; i++) {
                Assertions.assertTrue(vars.get(i).compareTo(vars.get(i + 1)) <= 0,
                                      "Returned list is unsorted");
            }
        });
    }

    @org.junit.jupiter.api.Test
    void getVarValue() throws Exception {
        SystemLambda.withEnvironmentVariable(existingName, existingValue).execute(() -> {
            Optional<String> returnedExistingValue = bean.getVarValue(existingName);
            Assertions.assertTrue(returnedExistingValue.isPresent(),
                                  "Bean was unable to return existing environmental variable");
            Assertions.assertEquals(existingValue, returnedExistingValue.get(),
                                    "Bean returned wrong value for existing environmental variable");
            Optional<String> returnedNotExistingValue = bean.getVarValue(notExistingName);
            Assertions.assertFalse(returnedNotExistingValue.isPresent(),
                                   "Bean was able to return non-existing environmental variable");
        });
    }
}