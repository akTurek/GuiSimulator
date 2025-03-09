package org.example.guisimulator;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RocnoVneseneTocke {
    private final List<int[]> list = new LinkedList<>();
    private final Lock lock = new ReentrantLock();

    public boolean isEmpty() {
        lock.lock();
        try {
            return list.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public void insert(int x, int y) {
        lock.lock();
        try {
            list.add(new int[]{x, y});
        } finally {
            lock.unlock();
        }
    }

    public List<int[]> readAllDel() {
        lock.lock();
        try {
            List<int[]> tmp = new LinkedList<>(list);
            list.clear();
            return tmp;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return list.size();
        } finally {
            lock.unlock();
        }
    }

}
