package di.dilogin.model;

import java.io.File;
import java.util.Properties;

import di.dilogin.BukkitApplication;
import di.internal.controller.file.ConfigManager;
import org.hibernate.cfg.Environment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public enum DatabaseType {

    MySQL, H2, SQlite;

    /**
     * DILogin config manager.
     */
    private final static ConfigManager configManager = BukkitApplication.getDIApi().getInternalController()
            .getConfigManager();

    /**
     * Get the ENUM DatabaseType from his name.
     *
     * @param name possible enum name.
     * @return the enum corresponding to the given name or else it returns the
     * default type.
     */
    public static DatabaseType getDatabaseTypeByName(String name) {

        // Default database
        DatabaseType dbt = DatabaseType.SQlite;

        for (DatabaseType d : DatabaseType.values()) {
            if (d.name().equalsIgnoreCase(name)) {
                dbt = d;
                break;
            }
        }
        return dbt;
    }

    /**
     * Gets the hibernate properties of the database.
     *
     * @return hibernate properties.
     */
    public Properties getProperties() {
        Properties p = new Properties();
        switch (this.name().toLowerCase()) {
            case "mysql":
                p = getMySQLProperties();
                break;
            case "h2":
				p = getH2Properties();
                break;
            default:
                p = getSQLiteProperties();
                break;
        }

        return p;
    }

    /**
     * Create properties to MySQL database.
     *
     * @return mysql properties.
     */
    private Properties getMySQLProperties() {
        Properties p = new Properties();

        String host = configManager.getString("database_host");
        String port = configManager.getString("database_port");
        String table = configManager.getString("database_table");
        String user = configManager.getString("database_username");
        String password = configManager.getString("database_password");

        p.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        p.put(Environment.URL, host + ":" + port + "/" + table + "?characterEncoding=utf8");
        p.put(Environment.USER, user);
        p.put(Environment.PASS, password);

        addCommonProperties(p);

        return p;
    }

    /**
     * Create properties to SQLite database.
     *
     * @return SQLite properties.
     */
    private Properties getSQLiteProperties() {
        Properties p = new Properties();
        File dataFolder = getDataBaseFolder("users.sqlite");

        if (dataFolder == null)
            throw new IllegalStateException("No valid folder in SQLite");

        p.put(Environment.DRIVER, "org.sqlite.JDBC");
        p.put(Environment.DIALECT, "org.hibernate.dialect.SQLiteDialect");
        p.put(Environment.URL, "jdbc:sqlite:"+dataFolder);

        addCommonProperties(p);

        return p;
    }

    /**
     * Create properties to H2 database.
     *
     * @return H2 properties.
     */
    private Properties getH2Properties() {
        Properties p = new Properties();
        File dataFolder = getDataBaseFolder("users");

        if (dataFolder == null)
            throw new IllegalStateException("No valid folder in SQLite");

        p.put(Environment.DRIVER, "org.h2.Driver");
        p.put(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
        p.put(Environment.URL, "jdbc:h2:file:" + dataFolder + ";mv_store=false");

        addCommonProperties(p);

        return p;
    }

    /**
     * Add to the properties the common settings.
     *
     * @param p data base properties.
     */
    private void addCommonProperties(Properties p) {
        p.put(Environment.SHOW_SQL, false);
        p.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        p.put(Environment.HBM2DDL_AUTO, "update");
        p.put(Environment.POOL_SIZE, 1);
    }

    /**
     * Get the file of SQLite data base. if the file does not exist, it is created.
     *
     * @param fileName name of database file.
     * @return SQLite file.
     */
    private File getDataBaseFolder(String fileName) {
        try {
            File dataFolder = new File(
                    BukkitApplication.getDIApi().getInternalController().getDataFolder().getAbsolutePath(), fileName);
            if (!dataFolder.exists())
                dataFolder.createNewFile();

            return dataFolder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
