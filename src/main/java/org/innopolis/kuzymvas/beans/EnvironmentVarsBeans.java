package org.innopolis.kuzymvas.beans;

import javax.ejb.Stateless;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * EJB-обертка вокруг системной функции доступа к переменным среды
 */
@Stateless
public class EnvironmentVarsBeans {

    /**
     * Возвращает множество всех пар имя-значения, хранимых в таблице переменных среды
     * @return - множество всех пар имя-значения, хранимых в таблице переменных среды
     */
    public Set<Map.Entry<String,String>> getAllVars() {
        return System.getenv().entrySet();
    }

    /**
     * Возвращает значение для заданного имени переменной среды
     * @param varName - имя искомой переменной
     * @return - значение переменной или пустой Optional, если такой переменной не существует.
     */
    public Optional<String> getVarValue(String varName) {
        Map<String,String> env = System.getenv();
        return Optional.ofNullable(env.get(varName));
    }


}
