package com.github.thorlauridsen.persistence;

import com.github.thorlauridsen.model.Payment;
import com.github.thorlauridsen.model.PaymentCreate;
import org.springframework.stereotype.Repository;

/**
 * Payment repository facade class.
 * <p>
 * This class is a facade for the {@link PaymentRepo}.
 * A service class can use this facade to easily interact with the
 * repository without needing to know about the database entity {@link PaymentEntity}.
 * <p>
 * It is annotated with {@link Repository} to allow Spring to automatically
 * detect it as a bean and inject it where needed.
 */
@Repository
public class PaymentRepoFacade {

    private final PaymentRepo repo;

    /**
     * Constructor for PaymentRepoFacade.
     *
     * @param repo {@link PaymentRepo}.
     */
    public PaymentRepoFacade(PaymentRepo repo) {
        this.repo = repo;
    }

    /**
     * Create a new payment.
     * This will create a new {@link PaymentEntity} and save it to the database.
     *
     * @param payment {@link PaymentCreate} object for creating a payment.
     * @return {@link Payment} model class.
     */
    public Payment create(PaymentCreate payment) {
        var entity = new PaymentEntity(
                payment.orderId(),
                payment.status(),
                payment.amount()
        );
        var saved = repo.save(entity);
        return saved.toModel();
    }
}
