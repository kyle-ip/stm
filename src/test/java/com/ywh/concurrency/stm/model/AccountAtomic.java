package com.ywh.concurrency.stm.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ywh
 * @since 5/5/2021
 */
public class AccountAtomic implements Account {

    public AtomicLong balance = new AtomicLong();

    /**
     *
     * @param balance
     */
    public AccountAtomic(Long balance) {
        this.balance.set(balance);
    }

    /**
     * @return
     */
    @Override
    public Long getBalance() {
        return this.balance.get();
    }

    /**
     * @param target
     * @param amount
     */
    @Override
    public void transferTo(final Account target, final Long amount) {
        synchronized (AccountAtomic.class) {
            long oldFrom = this.balance.get();
            this.balance.set(oldFrom - amount);
            long oldTO = target.getBalance();
            ((AccountAtomic) target).balance.set(oldTO + amount);
        }
    }
}
