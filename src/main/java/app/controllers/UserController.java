package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> ctx.render("/team12/team12_index.html"));
        app.get("/team12", ctx -> ctx.render("/team12/team12_index.html"));
        app.post("/team12/login", ctx -> login(ctx, connectionPool));
        app.get("/team12/tracker", ctx -> ctx.render("/team12/team12_tracker.html"));
        app.get("/team12/logout", UserController::logout);
        app.get("/team12/createuser", ctx -> ctx.render("/team12/team12_createuser.html"));
        app.post("/team12/createuser", ctx -> createUser(ctx, connectionPool));
    }


    private static void createUser(Context ctx, ConnectionPool connectionPool) {
        String username = ctx.formParam("username");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");

        if (!password1.equals(password2)) {
            ctx.attribute("message", "Passwords do not match, try again.");
            ctx.render("/team12/team12_createuser.html");
            return;
        }

        try {
            Team12UserMapper.createUser(username, password1, connectionPool);
            ctx.attribute("message", "User created successfully. Please log in.");
            ctx.render("/team12/team12_index.html");
        } catch (Team12DatabaseException e) {
            ctx.attribute("message", "User already exists. Try again or log in.");
            ctx.render("/team12/team12_createuser.html");
        }
    }

    private static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/team12");
    }

    private static void login(Context ctx, ConnectionPool connectionPool) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        try {
            Team12User user = Team12UserMapper.login(username, password, connectionPool);

            if (user == null) {
                ctx.attribute("message", "Invalid username or password.");
                ctx.render("/team12/team12_index.html");
                return;
            }

            ctx.sessionAttribute("currentUser", user);
            ctx.redirect("/team12/tracker");
        } catch (Team12DatabaseException e) {
            ctx.attribute("message", "Login failed. Please try again.");
            ctx.render("/team12/team12_index.html");
        }
    }
}
