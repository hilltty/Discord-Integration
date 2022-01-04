/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 *
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */
package di.dilogin.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import di.dilogin.entity.DIUserEntity;

public class DIUserRepository extends GenericRepositoryJPA<DIUserEntity> {

	/**
	 * Singleton class. Used for prevent creation of multiple DIUserRepository
	 * class.
	 */
	private static DIUserRepository instance = null;

	/**
	 * Prevent creation of multiple DIUserRepository
	 * @return the repository instance.
	 */
	public static DIUserRepository getInstance() {
		if (instance == null) {
			instance = new DIUserRepository();
		}
		return instance;
	}

	/**
	 * Get the DIUserEntity from the Minecraft user's name.
	 * @param name Player's name.
	 * @return Optional with DIUserEntity if exists. Optional empty if not exist.
	 */
	public Optional<DIUserEntity> findByMinecraftName(String name) {
		try (Session s = session().openSession()) {
			CriteriaBuilder cb = s.getCriteriaBuilder();
			CriteriaQuery<DIUserEntity> cq = cb.createQuery(DIUserEntity.class);
			Root<DIUserEntity> root = cq.from(DIUserEntity.class);
			cq.select(root).where(cb.equal(root.get("userName"), name));
			TypedQuery<DIUserEntity> query = s.createQuery(cq);
			List<DIUserEntity> results = query.getResultList();
			return results.size() > 0 ? Optional.of(results.get(0)) : Optional.empty();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	/**
	 * Delete the user from his name.
	 * @param playerName minecraft user's name.
	 */
	public void deleteByUserName(String playerName) {
		Optional<DIUserEntity> optUser = findByMinecraftName(playerName);
		if (optUser.isPresent())
			delete(optUser.get());
	}

	/**
	 * Get the DIUserEntity from the Discord user's id.
	 * @param discordId Discord user's id.
	 * @return List with DIUserEntity if exists. Clear list if no user is found.
	 */
	public List<DIUserEntity> findByDiscordId(long discordId) {
		try (Session s = session().openSession()) {
			CriteriaBuilder cb = s.getCriteriaBuilder();
			CriteriaQuery<DIUserEntity> cq = cb.createQuery(DIUserEntity.class);
			Root<DIUserEntity> root = cq.from(DIUserEntity.class);
			cq.select(root).where(cb.equal(root.get("discordId"), discordId));
			TypedQuery<DIUserEntity> query = s.createQuery(cq);
			List<DIUserEntity> results = query.getResultList();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

}
