package com.ywh.concurrency.stm;

import com.ywh.concurrency.stm.model.AccountAtomic;
import com.ywh.concurrency.stm.model.AccountSTM;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * STM 基准测试
 *
 * @author ywh
 * @since 5/5/2021
 */
@State(Scope.Thread)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class STMBenchmarkTest {

    private AccountSTM accountSTM;

    private AccountAtomic accountAtomic;

    @Setup(Level.Trial)
    public void setup2() {
        accountSTM = new AccountSTM(0L);
        accountAtomic = new AccountAtomic(0L);
    }

    /**
     *
     * @return
     */
    @Benchmark
    public void stmTest() throws InterruptedException {
        int threadSize = 100;
        long amount = 10;
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
    }

    /**
     *
     * @return
     */
    @Benchmark
    public void atomicTest() throws InterruptedException {
        int threadSize = 100;
        long amount = 10;
        final CountDownLatch countDownLatch = new CountDownLatch(threadSize);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadSize; i++) {
            executorService.execute(() -> {
                AccountAtomic x = new AccountAtomic(amount);
                x.transferTo(accountAtomic, amount);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
    }

    /**
     *
     * @param args
     * @throws RunnerException
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(STMBenchmarkTest.class.getSimpleName())
            .forks(1)
            .result("result.json")
            .build();
        new Runner(opt).run();
    }

}
