package com.abcbank.accountmaintenance.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class TransactionLock {

	private Map<String, AccountLock> accountLockMap;

	@PostConstruct
	public void init() {
		accountLockMap = new ConcurrentHashMap<>();
	}

	public void lock(String accoutNumber) {
		AccountLock accLock = accountLockMap.get(accoutNumber);
		// double locking : to initialize the lock object
		if (accLock == null) {
			synchronized (this) {
				accLock = accountLockMap.get(accoutNumber);
				if (accLock == null) {
					AccountLock newLock = new AccountLock(new ReentrantLock(), 0);
					accountLockMap.put(accoutNumber, newLock);
					accLock = newLock;
				}
			}

		}
		synchronized (this) {
			accLock.lockCount.incrementAndGet();
			// if other concurrent tx has removed from map using unlock- up it back
			if (null == accountLockMap.get(accoutNumber)) {
				accountLockMap.put(accoutNumber, accLock);
			}
		}

		accLock.lock.lock();
	}

	public void unlock(String accoutNumber) {
		AccountLock accLock = accountLockMap.get(accoutNumber);
		if (accLock != null) {
			int lockCount = accLock.lockCount.decrementAndGet();
			accLock.lock.unlock();
			/*
			 * if count zero take lock and insure count is still zero (no one acquire lock after unlock call)
			 */
			if (lockCount == 0) {
				synchronized (this) {
					lockCount = accLock.lockCount.get();
					if (lockCount == 0) {
						accountLockMap.remove(accoutNumber);
					}
				}
			}
		}
	}

	public boolean tryLock(String accoutNumber) {

		AccountLock accLock = accountLockMap.get(accoutNumber);
		if (accLock == null) {
			synchronized (this) {
				accLock = accountLockMap.get(accoutNumber);
				if (accLock == null) {
					AccountLock newLock = new AccountLock(new ReentrantLock(), 0);
					accountLockMap.put(accoutNumber, newLock);
					accLock = newLock;
				}
			}

		}

		boolean isAquired = accLock.lock.tryLock();
		if (isAquired) {
			synchronized (this) {
				accLock.lockCount.incrementAndGet();
				if (null == accountLockMap.get(accoutNumber)) {
					accountLockMap.put(accoutNumber, accLock);
				}
			}
		}

		return isAquired;
	}


	private static class AccountLock {
		private ReentrantLock lock;

		private AtomicInteger lockCount;

		public AccountLock(ReentrantLock lock, int initialValue) {
			this.lock = lock;
			lockCount = new AtomicInteger(initialValue);
		}
	}
}
