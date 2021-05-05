package com.ywh.concurrency.stm.model;

import com.ywh.concurrency.stm.STM;
import com.ywh.concurrency.stm.transaction.STMTransaction;
import com.ywh.concurrency.stm.transaction.TransactionReference;

/**
 * @author ywh
 * @since 5/5/2021
 */
public class AccountSTM implements Account {

    STMTransaction<Long> tx = new STMTransaction<>();

    private final TransactionReference<Long> txnRef;

    public AccountSTM(Long balance) {
        txnRef = new TransactionReference<>(balance);
    }

    public TransactionReference<Long> getRef() {
        return txnRef;
    }


    /**
     * @return
     */
    @Override
    public Long getBalance() {
        return txnRef.getValue(tx);
    }


    /**
     * @param target
     * @param amount
     */
    @Override
    public void transferTo(final Account target, final Long amount) {

        STM.<Long>atomic(
            txn -> {
                Long oldFrom = this.getRef().getValue(txn);
                this.getRef().setValue(oldFrom - amount, txn);
                Long oldTo = ((AccountSTM) target).getRef().getValue(txn);
                ((AccountSTM) target).getRef().setValue(oldTo + amount, txn);
            }
        );
    }
}
