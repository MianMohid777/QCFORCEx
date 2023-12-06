package com.jtspringproject.JtSpringProject.dao;

import com.jtspringproject.JtSpringProject.models.Cart;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CartDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public CartDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public Cart addCart(Cart cart) {
        Session session = sessionFactory.getCurrentSession();
        session.save(cart);
        return cart;
    }

    @Transactional
    public List<Cart> getCarts() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT DISTINCT c FROM CART c JOIN FETCH c.products";
        Query<Cart> query = session.createQuery(hql, Cart.class);
        return query.getResultList();
    }

    @Transactional
    public void updateCart(Cart cart) {
        Session session = sessionFactory.getCurrentSession();
        session.update(cart);
    }

    @Transactional
    public void deleteCart(Cart cart) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(cart);
    }

    @Transactional
    public Cart getCart(int id) {
        Session session = sessionFactory.getCurrentSession();
        Query<Cart> query = session.createQuery("FROM CART WHERE id = :id", Cart.class);
        query.setParameter("id", id);
        return query.uniqueResult();
    }
}
