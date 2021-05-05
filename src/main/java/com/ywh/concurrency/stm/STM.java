package com.ywh.concurrency.stm;

import com.ywh.concurrency.stm.transaction.STMTransaction;

/**
 * STM 工具类
 *
 * @author ywh
 * @since 5/5/2021
 */
public final class STM {

    private STM() {}

    public static final Object COMMIT_LOCK = new Object();

    /**
     *
     * @param runnable
     * @param <T>
     */
    public static <T> void atomic(STMRunnable<T> runnable) {
        boolean committed = false;
        while (!committed) {
            STMTransaction<T> tx = new STMTransaction<>();
            runnable.run(tx);
            committed = tx.commit();
        }
    }
}
