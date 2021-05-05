package com.ywh.concurrency.stm;

import com.ywh.concurrency.stm.transaction.STMTransaction;

/**
 * 事务执行接口
 *
 * @author ywh
 * @since 5/5/2021
 */
@FunctionalInterface
public interface STMRunnable<T> {

    /**
     *
     * @param tx
     */
    void run(STMTransaction<T> tx);
}
