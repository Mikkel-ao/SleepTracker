package app.persistence;

import app.entities.SleepRecord;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SleepMapper {

    // Saves sleep data (user ID, start time, end time) to the database
    public static void saveSleepData(int userId, Timestamp sleepStart, Timestamp sleepEnd, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO sleep_records (user_id, sleep_start, sleep_end) VALUES (?, ?, ?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, userId);
            ps.setTimestamp(2, sleepStart);
            ps.setTimestamp(3, sleepEnd);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Failed to insert sleep data.");
            }
        } catch (SQLException e) {
            String msg = "An error occurred. Please try again.";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "Duplicate entry error.";
            }
            System.out.println("Error in database: " + e.getMessage());
            throw new DatabaseException(msg, e.getMessage());
        }
    }

    public static List<SleepRecord> getSleepDataByUserId(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT record_id, user_id, sleep_start, sleep_end, sleep_duration, created_at FROM sleep_records WHERE user_id = ?";
        List<SleepRecord> sleepRecords = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No sleep records found for user ID: " + userId);
            }

            while (rs.next()) {
                SleepRecord record = new SleepRecord(
                        rs.getInt("record_id"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("sleep_start"),
                        rs.getTimestamp("sleep_end"),
                        rs.getDouble("sleep_duration"),
                        rs.getString("created_at")  // Use the correct column name here
                );
                sleepRecords.add(record);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching sleep data for user ID: " + userId);
            System.out.println("Error in database: " + e.getMessage());
            throw new DatabaseException("Error fetching sleep data for user ID: " + userId);
        }

        return sleepRecords;
    }

}
