package ru.y_lab.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.impl.ResourceServiceImpl;

import static ru.y_lab.util.ResponseUtil.sendErrorResponse;

/**
 * ResourceServlet handles HTTP requests for resource-related operations.
 */
public class ResourceServlet extends HttpServlet {

    private final ResourceService resourceService = new ResourceServiceImpl();

    /**
     * Handles HTTP POST requests for adding a new resource.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    @SneakyThrows
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        resourceService.addResource(req, resp);
    }

    /**
     * Handles HTTP GET requests and dispatches to the appropriate method.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    @SneakyThrows
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String path = req.getPathInfo();

        if (path == null) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Path cannot be null");
            return;
        }

        if (path.equals("/all")) {
            resourceService.getAllResources(req, resp);
        } else if (path.matches("/\\d+")) {
            resourceService.getResourceById(req, resp);
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid GET endpoint");
        }
    }

    /**
     * Handles HTTP PUT requests for updating an existing resource.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    @SneakyThrows
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        resourceService.updateResource(req, resp);
    }

    /**
     * Handles HTTP DELETE requests for deleting an existing resource.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    @SneakyThrows
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        resourceService.deleteResource(req, resp);
    }
}
