package app.controllers;

import app.entities.SleepRecord;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.SleepMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class SleepController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("/calculate", ctx -> sleep(ctx, connectionPool));
        app.get("/dashboard", ctx -> ctx.render("/dashboard.html"));
        app.get("/sleep-data", ctx -> fetchSleepData(ctx, connectionPool));
    }

    private static void sleep(Context ctx, ConnectionPool connectionPool) {
        User currentUser = ctx.sessionAttribute("currentUser");
        if (currentUser == null) {
            ctx.attribute("message", "User not logged in.");
            ctx.render("/tracker.html");
            return;
        }

        try {
            Timestamp sleepStart = Timestamp.valueOf(ctx.formParam("sleep_start").replace('T', ' ') + ":00");
            Timestamp sleepEnd = Timestamp.valueOf(ctx.formParam("sleep_end").replace('T', ' ') + ":00");

            SleepMapper.saveSleepData(currentUser.getUserId(), sleepStart, sleepEnd, connectionPool);
            ctx.attribute("message", "Sleep data recorded.");
        } catch (IllegalArgumentException e) {
            ctx.attribute("message", "Invalid date format.");
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
        ctx.render("/team12/team12_tracker.html");
    }

    private static void fetchSleepData(Context ctx, ConnectionPool connectionPool) {
        User currentUser = ctx.sessionAttribute("currentUser");
        if (currentUser == null) {
            ctx.status(401).json(Map.of("error", "User not logged in"));
            return;
        }

        try {
            List<SleepRecord> sleepRecords = SleepMapper.getSleepDataByUserId(currentUser.getUserId(), connectionPool);
            ctx.json(sleepRecords);
        } catch (DatabaseException e) {
            ctx.status(500).json(Map.of("error", "Failed to fetch sleep data"));
        }
    }
}
