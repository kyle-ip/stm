package com.ywh.concurrency.stm.model;

/**
 * @author ywh
 * @since 5/5/2021
 */
public interface Account {

    /**
     * @return
     */
    Long getBalance();

    /**
     * @param target
     * @param amount
     */
    void transferTo(final Account target, final Long amount);
}
