package org.mziuri.Servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mziuri.service.DatabaseService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class MessageServlet extends HttpServlet {

    private DatabaseService dbService;
    public MessageServlet(DatabaseService dbService) {
        this.dbService = dbService;
    }

    @Override
    public void init(ServletConfig config) {
        dbService = new DatabaseService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        boolean validUser = dbService.validateUser(username, password);
        if (!validUser) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 403 Forbidden
            response.getWriter().write("Invalid credentials.");
            return;
        }

        List<String> messages = dbService.getUserMessages(username);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(messages);

        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
    }

    // POST method to send a message
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String message = request.getParameter("message");

        if (message.contains("\n")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 403 Forbidden
            response.getWriter().write("Message contains invalid characters.");
            return;
        }

        boolean userExists = dbService.checkUserExists(username);
        if (!userExists) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 403 Forbidden
            response.getWriter().write("User does not exist.");
            return;
        }
        boolean success = dbService.sendMessage(username, message);
        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);  // 200 OK
            response.getWriter().write("Message sent successfully.");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  // 500 Internal Server Error
            response.getWriter().write("Error sending message.");
        }
    }
}
