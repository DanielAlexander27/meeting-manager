package org.intiandes.central.database;

import com.zaxxer.hikari.HikariDataSource;

import java.util.Objects;

public class H2Database {
    private static final HikariDataSource DATA_SOURCE;
    private static final String DB_DIRECTORY = "./database/meeting_db";
    private static boolean dbInitialized = false;

    static {
        DATA_SOURCE = new HikariDataSource();
    }

    public void initializeDB() {
        if (dbInitialized) return;

        String initialDataPath = Objects.requireNonNull(getClass().getResource("/script/sql/initial_data.sql")).toString();
        DATA_SOURCE.setJdbcUrl(String.format("jdbc:h2:%s;INIT=RUNSCRIPT FROM '%s'", DB_DIRECTORY, initialDataPath));

        dbInitialized = true;

    }
}