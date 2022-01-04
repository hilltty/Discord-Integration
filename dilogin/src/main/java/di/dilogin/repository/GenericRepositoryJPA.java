/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 *
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */
package di.dilogin.repository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Optional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class GenericRepositoryJPA<T> implements GenericRepository<T> {

	private Class<T> type;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GenericRepositoryJPA() {
		Type t = getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) t;
		type = (Class) pt.getActualTypeArguments()[0];
	}

	public T save(final T t) {
		try (Session s = session().openSession()) {
			Transaction transaction = null;
			transaction = s.beginTransaction();
			s.save(t);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	public void delete(final T t) {
		try (Session s = session().openSession()) {
			Transaction transaction = null;
			transaction = s.beginTransaction();
			s.remove(t);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Optional<T> find(final Object id) {
		try (Session s = session().openSession()) {
			return Optional.ofNullable((T) s.find(type, id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public T update(final T t) {
		try (Session s = session().openSession()) {
			Transaction transaction = null;
			transaction = s.beginTransaction();
			s.update(t);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	public Iterable<T> findAll() {
		try (Session s = session().openSession()) {
			CriteriaBuilder cb = s.getCriteriaBuilder();
			CriteriaQuery<T> criteriaQuery = cb.createQuery(type);
			Root<T> root = criteriaQuery.from(type);
			criteriaQuery.select(root);
			TypedQuery<T> query = s.createQuery(criteriaQuery);
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
}