
package com.org.payment.model.enums;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;
import java.util.Set;

public enum PaymentStatus {
    INDETERMINISTIC,
    CREATED,
    PROCESSING,
    SUCCESS,
    FAILED;

    private final Logger logger = LogManager.getLogger(PaymentStatus.class);

    private Set<PaymentStatus> allowedTransitions;

    public boolean canTransitionTo(PaymentStatus next) {
        logger.info("Checking if state-transition is possible or not! ");
        return allowedTransitions.contains(next);
    }

    public boolean isTerminal() {
        logger.info("Checking if it's a terminal state or not! ");
        return this == SUCCESS || this == FAILED;
    }

    static {
        CREATED.allowedTransitions = EnumSet.of(PROCESSING);
        PROCESSING.allowedTransitions = EnumSet.of(PROCESSING, SUCCESS, FAILED);
        SUCCESS.allowedTransitions = EnumSet.noneOf(PaymentStatus.class);
        FAILED.allowedTransitions = EnumSet.noneOf(PaymentStatus.class);
    }
}


