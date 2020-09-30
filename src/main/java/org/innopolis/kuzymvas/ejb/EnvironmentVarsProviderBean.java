package org.innopolis.kuzymvas.ejb;

import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * EJB-обертка вокруг системной функции доступа к переменным среды
 */
@Stateless
public class EnvironmentVarsProviderBean {

    /**
     * Возвращает отсортированный по имени список всех переменных,
     * хранимых в системной таблице переменных среды
     * @return - список всех переменных среды, представленных в виде иммутабельных POJO
     */
    public List<NamedVariable> getAllVars() {
        List<NamedVariable> varsList = new ArrayList<>();
        System.getenv().entrySet().stream()
                .map(entry-> new NamedVariable(entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(()->varsList));
        varsList.sort(Comparator.naturalOrder());
        return varsList;
    }

    /**
     * Возвращает значение для заданного имени переменной среды, обернутое в Optional
     * @param varName - имя искомой переменной
     * @return - значение переменной или пустой Optional, если такой переменной не существует.
     */
    public Optional<String> getVarValue(String varName) {
        Map<String,String> env = System.getenv();
        return Optional.ofNullable(env.get(varName));
    }

    /**
     * POJO класс для хранения иммутабельной именованной перменной
     * Поддерживает возможность сравнения по именам переменных
     */
    public static class NamedVariable implements  Comparable<NamedVariable>, Serializable {
        private final  String name;
        private final String value;

        public NamedVariable(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        @Override
        public int compareTo(NamedVariable o) {
            return name.compareTo(o.name);
        }

        @Override
        public String toString() {
            return getName() + " = " + getValue();
        }
    }


}
