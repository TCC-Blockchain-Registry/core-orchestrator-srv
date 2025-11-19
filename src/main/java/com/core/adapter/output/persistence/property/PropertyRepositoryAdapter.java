package com.core.adapter.output.persistence.property;

import com.core.adapter.output.persistence.property.entity.PropertyEntity;
import com.core.adapter.output.persistence.property.mapper.PropertyPersistenceMapper;
import com.core.domain.model.property.PropertyModel;
import com.core.port.output.property.PropertyRepositoryPort;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation of PropertyRepositoryPort
 * Uses pure Hibernate with SessionFactory
 */
@Repository
public class PropertyRepositoryAdapter implements PropertyRepositoryPort {
    
    private final SessionFactory sessionFactory;
    private final PropertyPersistenceMapper mapper;
    
    public PropertyRepositoryAdapter(SessionFactory sessionFactory, 
                                    PropertyPersistenceMapper mapper) {
        this.sessionFactory = sessionFactory;
        this.mapper = mapper;
    }
    
    @Override
    public PropertyModel save(PropertyModel property) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        
        try {
            transaction = session.beginTransaction();
            
            PropertyEntity entity = mapper.toEntity(property);
            session.saveOrUpdate(entity);
            
            transaction.commit();
            
            return mapper.toDomain(entity);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to save property", e);
        } finally {
            session.close();
        }
    }
    
    @Override
    public Optional<PropertyModel> findById(Long id) {
        Session session = sessionFactory.openSession();
        
        try {
            PropertyEntity entity = session.get(PropertyEntity.class, id);
            return entity != null ? Optional.of(mapper.toDomain(entity)) : Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find property by id", e);
        } finally {
            session.close();
        }
    }
    
    @Override
    public Optional<PropertyModel> findByMatriculaId(Long matriculaId) {
        Session session = sessionFactory.openSession();
        
        try {
            String hql = "FROM PropertyEntity p WHERE p.matriculaId = :matriculaId";
            PropertyEntity entity = session.createQuery(hql, PropertyEntity.class)
                    .setParameter("matriculaId", matriculaId)
                    .uniqueResult();
            
            return entity != null ? Optional.of(mapper.toDomain(entity)) : Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find property by matricula id", e);
        } finally {
            session.close();
        }
    }
    
    @Override
    public List<PropertyModel> findAll() {
        Session session = sessionFactory.openSession();
        
        try {
            String hql = "FROM PropertyEntity";
            List<PropertyEntity> entities = session.createQuery(hql, PropertyEntity.class).list();
            
            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to find all properties", e);
        } finally {
            session.close();
        }
    }
    
    @Override
    public List<PropertyModel> findByProprietario(Long proprietarioId) {
        Session session = sessionFactory.openSession();
        
        try {
            // Query by user ID using the relationship
            String hql = "FROM PropertyEntity p WHERE p.proprietario.id = :proprietarioId";
            List<PropertyEntity> entities = session.createQuery(hql, PropertyEntity.class)
                    .setParameter("proprietarioId", proprietarioId)
                    .list();
            
            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to find properties by proprietario", e);
        } finally {
            session.close();
        }
    }
    
    @Override
    public List<PropertyModel> findByComarca(String comarca) {
        Session session = sessionFactory.openSession();
        
        try {
            String hql = "FROM PropertyEntity p WHERE p.comarca = :comarca";
            List<PropertyEntity> entities = session.createQuery(hql, PropertyEntity.class)
                    .setParameter("comarca", comarca)
                    .list();
            
            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to find properties by comarca", e);
        } finally {
            session.close();
        }
    }
    
    @Override
    public void deleteById(Long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        
        try {
            transaction = session.beginTransaction();
            
            PropertyEntity entity = session.get(PropertyEntity.class, id);
            if (entity != null) {
                session.delete(entity);
            }
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to delete property", e);
        } finally {
            session.close();
        }
    }
}

