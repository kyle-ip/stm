package com.ywh.concurrency.stm.transaction;

import com.ywh.concurrency.stm.STM;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * STM 事务操作
 *
 * @author ywh
 * @since 5/5/2021
 */
public final class STMTransaction<T> {

    /**
     * 事务 id 生成器
     */
    private static final AtomicLong TXN_SEQ = new AtomicLong(0);

    /**
     * 当前事务数据
     */
    private final Map<TransactionReference<T>, TransactionReference.VersionedObject<T>> dataMap = new HashMap<>();

    /**
     * 当前事务待更新数据
     */
    private final Map<TransactionReference<T>, T> updateMap = new HashMap<>();

    /**
     * 当前事务 id（版本号），全局唯一且在事务中不可变。
     */
    private final long version;

    public STMTransaction() {
        version = TXN_SEQ.incrementAndGet();
    }

    /**
     * 在当前事务取值
     *
     * @param txnRef
     * @return
     */
    public T get(TransactionReference<T> txnRef) {
        // 将需要读取的数据加入 dataMap。
        if (!dataMap.containsKey(txnRef)) {
            dataMap.put(txnRef, txnRef.curRef);
        }
        return dataMap.get(txnRef).value;
    }

    /**
     * 在当前事务设值
     *
     * @param txnRef
     * @param value
     */
    public void set(TransactionReference<T> txnRef, T value) {
        if (!dataMap.containsKey(txnRef)) {
            dataMap.put(txnRef, txnRef.curRef);
        }
        // 将需要读取的数据加入 updateMap。
        updateMap.put(txnRef, value);
    }

    /**
     * 提交事务
     *
     * @return
     */
    public boolean commit() {
        synchronized (STM.COMMIT_LOCK) {
            boolean isValid = true;
            // 校验所有访问过的数据是否发生过变化。
            for(Map.Entry<TransactionReference<T>, TransactionReference.VersionedObject<T>> entry : dataMap.entrySet()){
                TransactionReference.VersionedObject<T> curRef = entry.getKey().curRef, readRef = entry.getValue();
                // 检验版本号：
                if (curRef.version != readRef.version) {
                    isValid = false;
                    break;
                }
            }
            // 如果未发生变化，则使修改生效。
            if (isValid) {
                updateMap.forEach((k, v) -> k.curRef = new TransactionReference.VersionedObject<>(v, version));
            }
            return isValid;
        }
    }
}
