# STM

[![Java](https://img.shields.io/badge/language-Java-orange.svg)]()

> since 2021-05-05 18:20

STM (Software Transactional Memory) is one kind of concurrency control mechanism like locks or actors, inspired by 
Database System Transaction 'ACID' (Atomicity, Consistency, Isolation, Durability). 

This is an STM demo implemented in Java.

## Quick Start
1. fork this repository and clone.
2. run example located under test package.

You can also create your test case like this:
```java
public class Account {
    STMTransaction<Long> tx = new STMTransaction<>();

    private final TransactionReference<Long> txnRef;

    public AccountSTM(Long balance) {
        txnRef = new TransactionReference<>(balance);
    }

    public TransactionReference<Long> getRef() {
        return txnRef;
    }

    public Long getBalance() {
        return txnRef.getValue(tx);
    }

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
    
    public static void main(String[] args) throws InterruptedException {
        Account account = new Account(0L);
        final CountDownLatch countDownLatch = new CountDownLatch(1000);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 1000; i++) {
            executorService.execute(() -> {
                Account x = new Account(amount);
                x.transferTo(account, amount);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
    }
}
```

Simple micro benchmarking with JMH (compared to Atomic API):
```text
Benchmark             Mode  Cnt    Score    Error  Units
Benchmark.atomicTest  avgt    5  883.999 ± 22.208  us/op
Benchmark.stmTest     avgt    5  992.872 ± 71.084  us/op
```

# Links
- [Implementing STM in Java](https://www.slideshare.net/mishadoff/implementing-stm-in-java)
- [stm-java](https://github.com/epam-mooc/stm-java)
- [Wikipedia: Software transactional memory](https://en.wikipedia.org/wiki/Software_transactional_memory)
- [软件事务内存：借鉴数据库的并发经验](https://time.geekbang.org/column/article/99251)

## License
See LICENSE file.