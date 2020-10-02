package org.innopolis.kuzymvas.namedbeans;

import org.innopolis.kuzymvas.ejb.EnvironmentVarsProviderBean;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * CDI Bean обертка вокруг EJB, обеспечиюващего доступ к переменным среды.
 * Нужна, как мост, для работы с JSF контекстом, не имеющим доступа к EJB
 */
@Named(value = "envVarsJSFController")
@RequestScoped
public class EnvVarsJSFController implements Serializable  {

    @EJB
    private EnvironmentVarsProviderBean envVars;

    public List<EnvironmentVarsProviderBean.NamedVariable> getEnvVars() {
        return envVars.getAllVars();
    }

}