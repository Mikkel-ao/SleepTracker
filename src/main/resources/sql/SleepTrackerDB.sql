CREATE DATABASE SleepTrackerDB;
USE SleepTrackerDB;

-- Users table
CREATE TABLE Users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sleep Records table
CREATE TABLE SleepRecords (
                              record_id INT AUTO_INCREMENT PRIMARY KEY,
                              user_id INT NOT NULL,
                              sleep_date DATE NOT NULL,
                              sleep_start DATETIME NOT NULL,
                              sleep_end DATETIME NOT NULL,
                              sleep_duration DECIMAL(5,2) GENERATED ALWAYS AS (TIMESTAMPDIFF(MINUTE, sleep_start, sleep_end) / 60.0) STORED,
                              notes TEXT,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- Sleep Analytics Table
CREATE TABLE SleepAnalytics (
                                analytics_id INT AUTO_INCREMENT PRIMARY KEY,
                                user_id INT NOT NULL,
                                sleep_date DATE NOT NULL,
                                total_sleep DECIMAL(5,2) NOT NULL,
                                last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);
