package com.ywh.concurrency.stm;

import com.ywh.concurrency.stm.model.Account;
import com.ywh.concurrency.stm.model.AccountAtomic;
import com.ywh.concurrency.stm.model.AccountSTM;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * STM 功能测试
 *
 * @author ywh
 * @since 5/5/2021
 */
@DisplayName("STM 功能测试")
public class ConcurrentTest {

    private AccountAtomic accountAtomic;

    private AccountSTM accountSTM;

    @BeforeEach
    void init() {
        accountAtomic = new AccountAtomic(0L);
        accountSTM = new AccountSTM(0L);
    }

    /**
     * @param args
     * @throws InterruptedException
     */
    @ParameterizedTest
    @DisplayName("测试并发设值（线程数，设值，期望值）")
    @CsvSource({
        "1000, 1, 1000",
        "100, 10, 1000",
        "10, 100, 1000",
        "1, 1000, 1000"
    })
    void stmTest(ArgumentsAccessor args) throws InterruptedException {
        int threadSize = args.getInteger(0);
        long amount = args.getLong(1);
        long expected = args.getLong(2);

        final CountDownLatch countDownLatch = new CountDownLatch(threadSize);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadSize; i++) {
            executorService.execute(() -> {
                AccountSTM x = new AccountSTM(amount);
                x.transferTo(accountSTM, amount);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        Assertions.assertEquals((Long) expected, accountSTM.getBalance());
    }

}
