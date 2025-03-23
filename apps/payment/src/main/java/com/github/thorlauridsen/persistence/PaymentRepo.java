package com.github.thorlauridsen.persistence;

import com.github.thorlauridsen.model.Payment;
import com.github.thorlauridsen.model.PaymentCreate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Payment event repository class.
 * <p>
 * This class implements the {@link IPaymentRepo} interface.
 * It is responsible for interacting with the payment table in the database.
 * A service class can use this to easily interact with the database
 * without needing to know about the database entity {@link PaymentEntity}.
 * <p>
 * It is annotated with {@link Repository} to allow Spring to
 * automatically detect it as a bean and inject it where needed.
 */
@Repository
public class PaymentRepo implements IPaymentRepo {

    private final PaymentJpaRepo jpaRepo;

    /**
     * Constructor for PaymentRepo.
     *
     * @param jpaRepo JpaRepository {@link PaymentJpaRepo} for directly interacting with the payment table.
     */
    public PaymentRepo(PaymentJpaRepo jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    /**
     * Save a payment in the database.
     * This will create a new {@link PaymentEntity} and save it to the database.
     *
     * @param payment {@link PaymentCreate} object for creating a payment.
     * @return {@link Payment} model class.
     */
    @Override
    public Payment save(PaymentCreate payment) {
        var entity = new PaymentEntity(
                payment.orderId(),
                payment.status(),
                payment.amount()
        );
        var saved = jpaRepo.save(entity);
        return saved.toModel();
    }

    /**
     * Find a payment by order id.
     *
     * @param orderId UUID of the order related to the payment.
     * @return {@link Optional} of {@link Payment}.
     */
    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        var found = jpaRepo.findByOrderId(orderId);
        return found.map(PaymentEntity::toModel);
    }
}
