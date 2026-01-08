package com.aaseya.AIS.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;

import org.reflections.Reflections;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UpdateIsActiveDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void updateIsActive(String tableName, String action, List<Object> ids) throws NoSuchFieldException, SecurityException {
        // Dynamically resolve the entity class from the table name
        Class<?> entityClass = getEntityClassByTableName(tableName);

        if (entityClass == null) {
            throw new IllegalArgumentException("No entity found for table name: " + tableName);
        }

        // Build the Criteria API components
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Object> criteriaUpdate = (CriteriaUpdate<Object>) criteriaBuilder.createCriteriaUpdate(entityClass);

        // Root to refer to the entity class
        Root<?> root = criteriaUpdate.from((Class<Object>) entityClass);
        
        Field isActiveAttribute = entityClass.getDeclaredField("isActive");
        Class<?> isActiveType = isActiveAttribute.getType();

        
     // Set the update values based on the action
        if ("activate".equalsIgnoreCase(action)) {
            if (boolean.class.equals(isActiveType)) { // Case for primitive boolean
                criteriaUpdate.set("isActive", true);
            } else if (Boolean.class.equals(isActiveType)) { // Case for Boolean wrapper
                criteriaUpdate.set("isActive", Boolean.TRUE);
            } 
//            else if (String.class.equals(isActiveType)) { // Case for String
//                criteriaUpdate.set("isActive", "true");
//            } 
            else {
                throw new IllegalArgumentException("Unsupported type for isActive: " + isActiveType.getName());
            }
        } else if ("deactivate".equalsIgnoreCase(action)) {
            if (boolean.class.equals(isActiveType)) { // Case for primitive boolean
                criteriaUpdate.set("isActive", false);
            } else if (Boolean.class.equals(isActiveType)) { // Case for Boolean wrapper
                criteriaUpdate.set("isActive", Boolean.FALSE);
            } 
//            else if (String.class.equals(isActiveType)) { // Case for String
//                criteriaUpdate.set("isActive", "false");
//            }
            else {
                throw new IllegalArgumentException("Unsupported type for isActive: " + isActiveType.getName());
            }
        } else {
            throw new IllegalArgumentException("Invalid action: " + action);
        }
        // Set the condition for updating records based on IDs
        criteriaUpdate.where(root.get("id").in(ids));
        
        // Execute the update query
        entityManager.createQuery(criteriaUpdate).executeUpdate();
    }

    private Class<?> getEntityClassByTableName(String tableName) {
        try {
            // Capitalize the table name to match the class name format
            //String capitalizedClassName = capitalizeFirstLetter(tableName);
            
            // Assuming your entities are in the "com.aaseya.AIS.Model" package
            String className = "com.aaseya.AIS.Model." + tableName; // Modify based on your package structure
            
            // Dynamically load the class
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            // Handle the case where the class is not found
            System.err.println("Entity class not found for table: " + tableName);
            return null;
        }
    }

//    private String capitalizeFirstLetter(String input) {
//        if (input == null || input.isEmpty()) {
//            return input;
//        }
//        return input.substring(0, 1).toUpperCase() + input.substring(1);
//    }
}
