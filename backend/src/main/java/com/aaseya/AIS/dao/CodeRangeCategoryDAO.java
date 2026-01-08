package com.aaseya.AIS.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.CodeRangeCategory;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Repository
public class CodeRangeCategoryDAO {

    @Autowired
    private SessionFactory sessionFactory;

  
    public List<CodeRangeCategory> findAll() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<CodeRangeCategory> cq = cb.createQuery(CodeRangeCategory.class);
            Root<CodeRangeCategory> root = cq.from(CodeRangeCategory.class);
            cq.select(root);
            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    }




