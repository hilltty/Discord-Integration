/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 *
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */
package di.dilogin.controller;

import java.io.File;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import di.dilogin.BukkitApplication;
import di.dilogin.entity.DIUserEntity;
import di.internal.controller.file.ConfigManager;

/**
 * Hibernate controller class.
 */
public class HibernateController {

	/**
	 * Hibernate session factory.
	 */
	private static SessionFactory sessionFactory;

	/**
	 * DILogin config manager.
	 */
	private final static ConfigManager configManager = BukkitApplication.getDIApi().getInternalController()
			.getConfigManager();

	/**
	 * The class should not be instantiated.
	 */
	private HibernateController() {
		throw new IllegalStateException();
	}

	/**
	 * Launch the necessary configuration for hibernate to work.
	 */
	public static void initHibernate() {
		try {
			Configuration configuration = new Configuration();
			Properties properties = catchDataBaseInfo();

			configuration.setProperties(properties);
			configuration.addAnnotatedClass(DIUserEntity.class);
			
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the hibernate session.
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * Get the necessary properties from the database based on the one chosen in the plugin configuration.
	 * @return data base properties.
	 */
	private static Properties catchDataBaseInfo() {
		Properties p;
		String string = configManager.getString("database");

		if (string.equalsIgnoreCase("mysql")) {
			p = catchDataBaseInfoMySQL();
		} else {
			p = catchDataBaseInfoH2();
		}

		BukkitApplication.getPlugin().getLogger().info("Database connection type: " + string.toUpperCase());
		return p;
	}

	/**
	 * Create properties to MySQL database.
	 * 
	 * @return mysql properties.
	 */
	private static Properties catchDataBaseInfoMySQL() {
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
	 * Create properties to H2 database.
	 * 
	 * @return H2 properties.
	 */
	private static Properties catchDataBaseInfoH2() {
		Properties p = new Properties();
		File dataFolder = getSQLiteFolder();
		
		if (dataFolder==null)
			throw new IllegalStateException("No valid folder in SQLite");

		p.put(Environment.DRIVER, "org.h2.Driver");
		p.put(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
		p.put(Environment.URL, "jdbc:h2:file:"+ dataFolder+";mv_store=false");

		addCommonProperties(p);

		return p;
	}

	/**
	 * Get the file of SQLite data base. if the file does not exist, it is created.
	 * @return SQLite file.
	 */
	private static File getSQLiteFolder() {
		try {
			File dataFolder = new File(
					BukkitApplication.getDIApi().getInternalController().getDataFolder().getAbsolutePath(), "users");
			if (!dataFolder.exists())
				dataFolder.createNewFile();

			return dataFolder;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Add to the properties the common settings.
	 * @param p data base properties.
	 */
	private static void addCommonProperties(Properties p) {
		p.put(Environment.SHOW_SQL, false);
		p.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
		p.put(Environment.HBM2DDL_AUTO, "update");
		p.put(Environment.POOL_SIZE, 1);
		p.put(Environment.AUTOCOMMIT, false);
	}

}
