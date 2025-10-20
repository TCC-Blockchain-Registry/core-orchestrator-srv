package com.core.adapter.output.persistence.property;

import com.core.adapter.output.persistence.property.entity.PropertyTransferEntity;
import com.core.adapter.output.persistence.property.mapper.PropertyTransferPersistenceMapper;
import com.core.domain.model.property.PropertyTransferModel;
import com.core.port.output.property.PropertyTransferRepositoryPort;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation of PropertyTransferRepositoryPort
 * Uses pure Hibernate with SessionFactory
 */
@Repository
public class PropertyTransferRepositoryAdapter implements PropertyTransferRepositoryPort {
    
    private final SessionFactory sessionFactory;
    private final PropertyTransferPersistenceMapper mapper;
    
    public PropertyTransferRepositoryAdapter(SessionFactory sessionFactory,
                                            PropertyTransferPersistenceMapper mapper) {
        this.sessionFactory = sessionFactory;
        this.mapper = mapper;
    }
    
    @Override
    public PropertyTransferModel save(PropertyTransferModel transfer) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        
        try {
            transaction = session.beginTransaction();
            
            PropertyTransferEntity entity = mapper.toEntity(transfer);
            session.saveOrUpdate(entity);
            
            transaction.commit();
            
            return mapper.toDomain(entity);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to save property transfer", e);
        } finally {
            session.close();
        }
    }
    
    @Override
    public Optional<PropertyTransferModel> findById(Long id) {
        Session session = sessionFactory.openSession();
        
        try {
            PropertyTransferEntity entity = session.get(PropertyTransferEntity.class, id);
            return entity != null ? Optional.of(mapper.toDomain(entity)) : Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find transfer by id", e);
        } finally {
            session.close();
        }
    }
    
    @Override
    public List<PropertyTransferModel> findByPropertyId(Long propertyId) {
        Session session = sessionFactory.openSession();
        
        try {
            String hql = "FROM PropertyTransferEntity t WHERE t.propertyId = :propertyId ORDER BY t.transferDate DESC";
            List<PropertyTransferEntity> entities = session.createQuery(hql, PropertyTransferEntity.class)
                    .setParameter("propertyId", propertyId)
                    .list();
            
            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to find transfers by property id", e);
        } finally {
            session.close();
        }
    }
    
    @Override
    public List<PropertyTransferModel> findByFromProprietario(String fromProprietario) {
        Session session = sessionFactory.openSession();
        
        try {
            String hql = "FROM PropertyTransferEntity t WHERE t.fromProprietario = :fromProprietario ORDER BY t.transferDate DESC";
            List<PropertyTransferEntity> entities = session.createQuery(hql, PropertyTransferEntity.class)
                    .setParameter("fromProprietario", fromProprietario)
                    .list();
            
            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to find transfers by from proprietario", e);
        } finally {
            session.close();
        }
    }
    
    @Override
    public List<PropertyTransferModel> findByToProprietario(String toProprietario) {
        Session session = sessionFactory.openSession();
        
        try {
            String hql = "FROM PropertyTransferEntity t WHERE t.toProprietario = :toProprietario ORDER BY t.transferDate DESC";
            List<PropertyTransferEntity> entities = session.createQuery(hql, PropertyTransferEntity.class)
                    .setParameter("toProprietario", toProprietario)
                    .list();
            
            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to find transfers by to proprietario", e);
        } finally {
            session.close();
        }
    }
    
    @Override
    public List<PropertyTransferModel> findAll() {
        Session session = sessionFactory.openSession();
        
        try {
            String hql = "FROM PropertyTransferEntity ORDER BY transferDate DESC";
            List<PropertyTransferEntity> entities = session.createQuery(hql, PropertyTransferEntity.class).list();
            
            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to find all transfers", e);
        } finally {
            session.close();
        }
    }
}

