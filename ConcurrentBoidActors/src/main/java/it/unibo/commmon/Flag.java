package it.unibo.commmon;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Flag {
    private boolean flag;
    private final ReadWriteLock lock;

    public Flag() {
        this.flag = false;
        this.lock = new ReentrantReadWriteLock();
    }

    public void reset() {
        lock.writeLock().lock();
        try {
            flag = false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void set() {
        lock.writeLock().lock();
        try {
            flag = true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean isSet() {
        lock.readLock().lock();
        try {
            return flag;
        } finally {
            lock.readLock().unlock();
        }
    }
}
