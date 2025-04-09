package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> ctx.render("/login.html"));
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.get("/tracker", ctx -> ctx.render("/tracker.html"));
        app.get("/logout", UserController::logout);
        app.get("/createuser", ctx -> ctx.render("/createuser.html"));
        app.post("/createuser", ctx -> createUser(ctx, connectionPool));
    }


    private static void createUser(Context ctx, ConnectionPool connectionPool) {
        String username = ctx.formParam("username");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");

        if (!password1.equals(password2)) {
            ctx.attribute("message", "Passwords do not match, try again.");
            ctx.render("/createuser.html");
            return;
        }

        try {
            UserMapper.createUser(username, password1, connectionPool);
            ctx.attribute("message", "User created successfully. Please log in.");
            ctx.render("/login.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", "User already exists. Try again or log in.");
            ctx.render("/createuser.html");
        }
    }

    private static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }

    private static void login(Context ctx, ConnectionPool connectionPool) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(username, password, connectionPool);

            if (user == null) {
                ctx.attribute("message", "Invalid username or password.");
                ctx.render("/login.html");
                return;
            }

            ctx.sessionAttribute("currentUser", user);
            ctx.redirect("/tracker");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Login failed. Please try again.");
            ctx.render("/login.html");
        }
    }
}
