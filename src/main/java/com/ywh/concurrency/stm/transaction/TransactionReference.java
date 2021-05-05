package com.ywh.concurrency.stm.transaction;

/**
 * 对象引用封装（附带版本号）
 *
 * @author ywh
 * @since 5/5/2021
 */
public final class TransactionReference<T> {

    /**
     *
     */
    public volatile VersionedObject<T> curRef;

    /**
     *
     * @param value
     */
    public TransactionReference(T value) {
        this.curRef = new VersionedObject<>(value, 0L);
    }

    /**
     *
     * @param txn
     * @return
     */
    public T getValue(STMTransaction<T> txn) {
        return txn.get(this);
    }

    /**
     *
     * @param value
     * @param txn
     */
    public void setValue(T value, STMTransaction<T> txn) {
        txn.set(this, value);
    }

    /**
     *
     * @param <T>
     */
    public static final class VersionedObject<T> {

        /**
         * 值
         */
        final T value;

        /**
         * 版本号
         */
        final long version;

        /**
         *
         * @param value
         * @param version
         */
        public VersionedObject(T value, long version) {
            this.value = value;
            this.version = version;
        }


    }

}
