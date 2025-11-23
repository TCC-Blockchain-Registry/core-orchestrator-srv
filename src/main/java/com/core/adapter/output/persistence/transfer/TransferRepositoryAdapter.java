package com.core.adapter.output.persistence.transfer;

import com.core.domain.model.transfer.TransferModel;
import com.core.domain.model.transfer.TransferStatus;
import com.core.port.output.transfer.TransferRepositoryPort;
import com.core.adapter.output.persistence.transfer.entity.TransferEntity;
import com.core.adapter.output.persistence.transfer.mapper.TransferPersistenceMapper;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Hibernate implementation of TransferRepositoryPort
 */
@Repository
public class TransferRepositoryAdapter implements TransferRepositoryPort {

    private final SessionFactory sessionFactory;
    private final TransferPersistenceMapper mapper;

    public TransferRepositoryAdapter(SessionFactory sessionFactory, TransferPersistenceMapper mapper) {
        this.sessionFactory = sessionFactory;
        this.mapper = mapper;
    }

    @Override
    public TransferModel save(TransferModel transfer) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            TransferEntity entity = mapper.toEntity(transfer);
            session.saveOrUpdate(entity);

            transaction.commit();

            return mapper.toDomain(entity);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to save transfer", e);
        } finally {
            session.close();
        }
    }

    @Override
    public Optional<TransferModel> findById(Long id) {
        Session session = sessionFactory.openSession();

        try {
            TransferEntity entity = session.get(TransferEntity.class, id);
            return entity != null ? Optional.of(mapper.toDomain(entity)) : Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find transfer by id", e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<TransferModel> findByMatriculaId(Long matriculaId) {
        Session session = sessionFactory.openSession();

        try {
            String hql = "FROM TransferEntity t WHERE t.matriculaId = :matriculaId ORDER BY t.createdAt DESC";
            List<TransferEntity> entities = session.createQuery(hql, TransferEntity.class)
                    .setParameter("matriculaId", matriculaId)
                    .list();

            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to find transfers by matricula ID", e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<TransferModel> findBySellerId(Long sellerId) {
        Session session = sessionFactory.openSession();

        try {
            String hql = "FROM TransferEntity t WHERE t.seller.id = :sellerId ORDER BY t.createdAt DESC";
            List<TransferEntity> entities = session.createQuery(hql, TransferEntity.class)
                    .setParameter("sellerId", sellerId)
                    .list();

            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to find transfers by seller ID", e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<TransferModel> findByBuyerId(Long buyerId) {
        Session session = sessionFactory.openSession();

        try {
            String hql = "FROM TransferEntity t WHERE t.buyer.id = :buyerId ORDER BY t.createdAt DESC";
            List<TransferEntity> entities = session.createQuery(hql, TransferEntity.class)
                    .setParameter("buyerId", buyerId)
                    .list();

            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to find transfers by buyer ID", e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<TransferModel> findAll() {
        Session session = sessionFactory.openSession();

        try {
            String hql = "FROM TransferEntity ORDER BY createdAt DESC";
            List<TransferEntity> entities = session.createQuery(hql, TransferEntity.class).list();

            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to find all transfers", e);
        } finally {
            session.close();
        }
    }

    @Override
    public boolean hasActiveTransfer(Long matriculaId) {
        Session session = sessionFactory.openSession();

        try {
            String hql = "SELECT COUNT(t) FROM TransferEntity t " +
                        "WHERE t.matriculaId = :matriculaId " +
                        "AND t.status != :completedStatus";

            Long count = session.createQuery(hql, Long.class)
                    .setParameter("matriculaId", matriculaId)
                    .setParameter("completedStatus", TransferStatus.CONCLUIDA)
                    .uniqueResult();

            return count != null && count > 0;
        } catch (Exception e) {
            throw new RuntimeException("Failed to check for active transfer", e);
        } finally {
            session.close();
        }
    }
}
