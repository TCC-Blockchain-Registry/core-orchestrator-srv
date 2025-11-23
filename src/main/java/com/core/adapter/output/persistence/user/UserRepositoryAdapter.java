package com.core.adapter.output.persistence.user;

import com.core.domain.model.user.UserModel;
import com.core.port.output.user.UserRepositoryPort;
import com.core.adapter.output.persistence.user.entity.UserEntity;
import com.core.adapter.output.persistence.user.mapper.UserPersistenceMapper;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Hibernate implementation of UserRepositoryPort
 */
@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {
    
    private final SessionFactory sessionFactory;
    private final UserPersistenceMapper mapper;
    
    public UserRepositoryAdapter(SessionFactory sessionFactory, UserPersistenceMapper mapper) {
        this.sessionFactory = sessionFactory;
        this.mapper = mapper;
    }
    
    @Override
    public UserModel save(UserModel user) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        
        try {
            transaction = session.beginTransaction();
            
            UserEntity entity = mapper.toEntity(user);
            session.saveOrUpdate(entity);
            
            transaction.commit();
            
            return mapper.toDomain(entity);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to save user", e);
        } finally {
            session.close();
        }
    }
    
    @Override
    public Optional<UserModel> findById(Long id) {
        Session session = sessionFactory.openSession();
        
        try {
            UserEntity entity = session.get(UserEntity.class, id);
            return entity != null ? Optional.of(mapper.toDomain(entity)) : Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find user by id", e);
        } finally {
            session.close();
        }
    }
    
    @Override
    public Optional<UserModel> findByEmail(String email) {
        Session session = sessionFactory.openSession();

        try {
            String hql = "FROM UserEntity u WHERE u.email = :email";
            UserEntity entity = session.createQuery(hql, UserEntity.class)
                    .setParameter("email", email)
                    .uniqueResult();

            return entity != null ? Optional.of(mapper.toDomain(entity)) : Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find user by email", e);
        } finally {
            session.close();
        }
    }

    @Override
    public Optional<UserModel> findByWalletAddress(String walletAddress) {
        Session session = sessionFactory.openSession();

        try {
            String hql = "FROM UserEntity u WHERE u.walletAddress = :walletAddress";
            UserEntity entity = session.createQuery(hql, UserEntity.class)
                    .setParameter("walletAddress", walletAddress)
                    .uniqueResult();

            return entity != null ? Optional.of(mapper.toDomain(entity)) : Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find user by wallet address", e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<UserModel> findAll() {
        Session session = sessionFactory.openSession();
        
        try {
            String hql = "FROM UserEntity ORDER BY createdAt DESC";
            List<UserEntity> entities = session.createQuery(hql, UserEntity.class).list();
            
            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to find all users", e);
        } finally {
            session.close();
        }
    }
}
