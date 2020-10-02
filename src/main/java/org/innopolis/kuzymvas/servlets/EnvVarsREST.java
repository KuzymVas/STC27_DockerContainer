package org.innopolis.kuzymvas.servlets;

import org.innopolis.kuzymvas.ejb.EnvironmentVarsProviderBean;

import javax.ejb.EJB;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Сервлет, позволяющий через GET запросы просматривать все или отдельно взятые значения переменных среды
 */
public class EnvVarsREST extends HttpServlet {

    private static final String NAME_PARAMETER = "name";

    @EJB
    private EnvironmentVarsProviderBean envVars;

    /**
     * Обрабатывет GET запросы, возвращая либо список всех переменных среды,
     * либо значение конкретной переменной, если запрос содержит параметр с именем
     * NAME_PARAMETER или имя переменной в качестве пути в URL
     *
     * @param req  - HTTP запрос
     * @param resp - HTTP ответ
     * @throws IOException - при проблемах записи в ответ
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(200);
        resp.setContentType("text/plain");
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            doGetSingle(pathInfo.substring(1), resp);
        } else {
            doGetRoot(req, resp);
        }
    }

    /**
     * Обрабатывает запросы, не содержащие пути в URL, т. е. обращающиеся к корневому пути сервлета
     * Возвращает либо список всех переменных среды, либо значение конкретной переменной,
     * если запрос содержит параметр с именем NAME_PARAMETER
     *
     * @param req  - HTTP запрос
     * @param resp - HTTP ответ
     * @throws IOException - при проблемах записи в ответ
     */
    private void doGetRoot(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String envName = req.getParameter(NAME_PARAMETER);
        if (!(envName == null)) {
            doGetSingle(envName, resp);
        } else {
            doGetAll(resp);
        }
    }

    /**
     * Пишет в ответ список всех переменных среды.
     * @param resp - HTTP ответ
     * @throws IOException - при проблемах записи в ответ
     */
    private void doGetAll(HttpServletResponse resp) throws IOException {
        for (EnvironmentVarsProviderBean.NamedVariable var : envVars.getAllVars()) {
            resp.getWriter().println(var.toString());
        }
        resp.getWriter().flush();
    }
    /**
     * Пишет в ответ  значение искомой переменной среды
     * или сообщение об ее отсутствии в списке переменных.
     * @param name - имя искомой переменной
     * @param resp - HTTP ответ
     * @throws IOException - при проблемах записи в ответ
     */
    private void doGetSingle(String name, HttpServletResponse resp) throws IOException {
        final Optional<String> value = envVars.getVarValue(name);
        if (value.isPresent()) {
            resp.getWriter().println("Environment variable \"" + name + "\" has value: " + value.get());
        } else {
            resp.getWriter().println("Environment variable " + name + " do not exist.");
        }
        resp.getWriter().flush();
    }
}
