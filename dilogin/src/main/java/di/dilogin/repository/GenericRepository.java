/**
 * Copyright (C) 2021 Discord Integration Project
 * https://github.com/Alhxe/Discord-Integration
 *
 * This project is under license https://github.com/Alhxe/Discord-Integration/blob/main/LICENSE
 */
package di.dilogin.repository;

import java.util.Optional;

import org.hibernate.SessionFactory;

import di.dilogin.controller.HibernateController;

public interface GenericRepository<T>{
 
    T save(T t);
 
    void delete(T t);
 
    Optional<T> find(T t);
 
    T update(T t);
     
    Iterable<T> findAll();
    
    default SessionFactory session() {
    	return HibernateController.getSessionFactory();
    }
    
}