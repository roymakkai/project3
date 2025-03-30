package org.mziuri.Servlets;


import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mziuri.service.DatabaseService;

import java.io.IOException;

public class UserServlet extends HttpServlet {

    private DatabaseService dbService;
    public UserServlet(DatabaseService dbService) {
        this.dbService = dbService;
    }


    @Override
    public void init(ServletConfig config) {
        dbService = new DatabaseService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        boolean userExists = dbService.checkUserExists(username);
        if (userExists) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 403 Forbidden
            response.getWriter().write("Username already exists.");
            return;
        }
        boolean success = dbService.registerUser(username, password);
        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);  // 200 OK
            response.getWriter().write("User registered successfully.");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  // 500
            response.getWriter().write("Error registering user.");
        }
    }
}
