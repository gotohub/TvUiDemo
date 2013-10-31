package io.viva.tv.lib;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataCache<K, E> {
	private int mCapacity;
	private LinkedHashMap<K, E> mCache;
	private final ReadWriteLock mReadWriteLock = new ReentrantReadWriteLock();

	public DataCache(int paramInt) {
		this.mCapacity = paramInt;
		this.mCache = new LinkedHashMap<K, E>(this.mCapacity) {
			private static final long serialVersionUID = -9165777183357349715L;

			protected boolean removeEldestEntry(Map.Entry<K, E> paramAnonymousEntry) {
				if (size() > DataCache.this.mCapacity) {
					DataCache.this.mReadWriteLock.writeLock().lock();
					remove(paramAnonymousEntry.getKey());
					DataCache.this.mReadWriteLock.writeLock().unlock();
				}
				return false;
			}
		};
	}

	public E objectForKey(K paramK) {
		this.mReadWriteLock.readLock().lock();
		E localObject = this.mCache.get(paramK);
		this.mReadWriteLock.readLock().unlock();
		return localObject;
	}

	public void putObjectForKey(K paramK, E paramE) {
		if ((paramK != null) && (paramE != null)) {
			this.mReadWriteLock.writeLock().lock();
			this.mCache.put(paramK, paramE);
			this.mReadWriteLock.writeLock().unlock();
		}
	}

	public boolean containsKey(K paramK) {
		this.mReadWriteLock.readLock().lock();
		boolean bool = this.mCache.containsKey(paramK);
		this.mReadWriteLock.readLock().unlock();
		return bool;
	}

	public void clear() {
		this.mReadWriteLock.writeLock().lock();
		this.mCache.clear();
		this.mReadWriteLock.writeLock().unlock();
	}

	public void setCapacity(int paramInt) {
		this.mCapacity = paramInt;
	}
}

/*
 * Location: C:\Users\Administrator\Desktop\AliTvAppSdk.jar Qualified Name:
 * com.yunos.tv.lib.DataCache JD-Core Version: 0.6.2
 */