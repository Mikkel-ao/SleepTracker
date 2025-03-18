package app.entitites;

import java.security.Timestamp;
import java.util.Date;

public class SleepAnalytics {
    private int analytics_id;
    private int user_id;
    private Date sleep_start_date;
    private double total_sleep;
    private Timestamp last_updated;

    public SleepAnalytics(int analytics_id, int user_id, Date sleep_start_date, double total_sleep, Timestamp last_updated) {
        this.analytics_id = analytics_id;
        this.user_id = user_id;
        this.sleep_start_date = sleep_start_date;
        this.total_sleep = total_sleep;
        this.last_updated = last_updated;
    }
}
