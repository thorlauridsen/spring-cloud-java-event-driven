package com.github.thorlauridsen.controller;

import com.github.thorlauridsen.dto.PaymentDto;
import com.github.thorlauridsen.exception.PaymentNotFoundException;
import com.github.thorlauridsen.service.PaymentService;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Payment controller class.
 * This class implements the {@link IPaymentController} interface and
 * overrides the methods defined in the interface with implementations.
 * The controller is responsible for converting data transfer objects to models and vice versa.
 */
@RestController
public class PaymentController implements IPaymentController {

    private final PaymentService paymentService;

    /**
     * Constructor for PaymentController.
     *
     * @param paymentService {@link PaymentService}.
     */
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Get method for payment.
     * This method will convert the model to a DTO and return it.
     *
     * @param orderId UUID of the order related to the payment.
     * @return {@link ResponseEntity} with {@link PaymentDto}.
     * @throws PaymentNotFoundException if the payment is not found.
     */
    @Override
    public ResponseEntity<PaymentDto> getByOrderId(UUID orderId) throws PaymentNotFoundException {
        var payment = paymentService.findByOrderId(orderId);
        return ResponseEntity.ok(PaymentDto.fromModel(payment));
    }
}
