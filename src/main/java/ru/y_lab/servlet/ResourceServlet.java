package ru.y_lab.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import ru.y_lab.annotation.Loggable;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.impl.ResourceServiceImpl;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static ru.y_lab.util.ResponseUtil.sendErrorResponse;

/**
 * ResourceServlet handles HTTP requests for resource-related operations.
 */
@Loggable
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
        String path = req.getPathInfo();

        if (path.equals("/add")) {
            resourceService.addResource(req, resp);
        } else {
            sendErrorResponse(resp, SC_BAD_REQUEST, "Invalid POST endpoint");
        }
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
        String resourceId = req.getParameter("resourceId");

        if ((path == null || path.equals("/")) && resourceId != null) {
            resourceService.getResourceById(req, resp);
        } else if ("/all".equals(path)) {
            resourceService.getAllResources(req, resp);
        } else {
            sendErrorResponse(resp, SC_BAD_REQUEST, "Invalid GET endpoint or missing resourceId parameter");
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
        String path = req.getPathInfo();
        String resourceId = req.getParameter("resourceId");

        if ((path == null || path.equals("/")) && resourceId != null) {
            resourceService.updateResource(req, resp);
        } else {
            sendErrorResponse(resp, SC_BAD_REQUEST, "Invalid PUT endpoint or missing resourceId parameter");
        }
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
        String path = req.getPathInfo();
        String resourceId = req.getParameter("resourceId");

        if ((path == null || path.equals("/")) && resourceId != null) {
            resourceService.deleteResource(req, resp);
        } else {
            sendErrorResponse(resp, SC_BAD_REQUEST, "Invalid PUT endpoint or missing resourceId parameter");
        }
    }
}
