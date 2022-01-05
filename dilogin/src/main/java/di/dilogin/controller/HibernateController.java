/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 *
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */
package di.dilogin.controller;

import java.io.File;
import java.util.Properties;

import di.dilogin.model.DatabaseType;
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
		String databaseName = configManager.getString("database");

		DatabaseType databaseType = DatabaseType.getDatabaseTypeByName(databaseName);

		BukkitApplication.getPlugin().getLogger().info("Database connection type: " + databaseType.name());
		return databaseType.getProperties();
	}

}
