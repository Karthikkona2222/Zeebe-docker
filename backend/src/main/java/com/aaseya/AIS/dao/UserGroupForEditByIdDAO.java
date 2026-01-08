package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.UserGroup;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserGroupForEditByIdDAO {

    @Autowired
    private SessionFactory sessionFactory;

    
public UserGroup getUserGroupById(Long groupId) {
    Session session = null;
    UserGroup userGroup = null;
    
    try {
        session = sessionFactory.openSession();
        userGroup = session.get(UserGroup.class, groupId);
    } finally {
        if (session != null) {
            session.close(); 
        }
    }
    
    return userGroup;
}
}