package ru.y_lab.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import ru.y_lab.dto.ResourceDTO;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.impl.ResourceServiceImpl;

import java.util.List;

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
        try {
            ResourceDTO resourceDTO = resourceService.addResource(req.getReader().readLine());
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(resourceDTO.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }

    /**
     * Handles HTTP PATCH requests for updating an existing resource.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    @SneakyThrows
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) {
        try {
            resourceService.updateResource(req.getReader().readLine());
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
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
        if ("/all".equals(path)) {
            getAllResources(resp);
        } else {
            getResourceById(req, resp);
        }
    }

    /**
     * Handles HTTP GET requests for retrieving all resources.
     *
     * @param resp the HttpServletResponse object
     */
    @SneakyThrows
    private void getAllResources(HttpServletResponse resp) {
        try {
            List<ResourceDTO> resources = resourceService.viewResources();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(resources.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }

    /**
     * Handles HTTP GET requests for retrieving a resource by its ID.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @SneakyThrows
    private void getResourceById(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Long resourceId = Long.parseLong(req.getParameter("id"));
            ResourceDTO resourceDTO = resourceService.getResourceById(resourceId);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(resourceDTO.toString());
        } catch (ResourceNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
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
        try {
            Long resourceId = Long.parseLong(req.getParameter("id"));
            resourceService.deleteResource(resourceId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }
}
