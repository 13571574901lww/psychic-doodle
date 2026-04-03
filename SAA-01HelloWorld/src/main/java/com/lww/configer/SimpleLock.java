package com.lww.configer;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class SimpleLock implements Lock {
    // 核心：内部同步器，继承AQS并实现模板方法
    private final Sync sync = new Sync();

    /*
    1.state 状态
    2.FIFO等待队列
    3,模板方法
     */
    private static class Sync  extends AbstractQueuedSynchronizer{



        //尝试获取锁
        @Override
        protected boolean tryAcquire(int acquires) {
            assert acquires == 1;
            // 使用CAS将state从0改为1，成功则表示获取到锁
            if (compareAndSetState(0,1)){
                // 记录当前持有锁的线程（用于可重入性检查等，本例中非必须但规范）
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;// CAS失败，说明锁已被其他线程占用，获取失败
        }
        // 尝试释放锁
        @Override
        protected boolean tryRelease(int releases) {
            assert releases  == 1;
            if (getState() == 0){
                throw new IllegalMonitorStateException("尝试释放未持有的锁");
            }
            setExclusiveOwnerThread(null); // 清空持有线程
            setState(0); // 状态归零，表示锁空闲
            return true;
        }

        // 查询锁是否被当前线程独占持有
        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }
    }

    @Override
    public void lock() {
        sync.acquire(1); // AQS核心方法，会调用我们重写的tryAcquire
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
        return false;
    }



    /*

     */

    @Override
    public void unlock() {
        sync.release(1);
    }

    @NotNull
    @Override
    public Condition newCondition() {
        return null;
    }
}
