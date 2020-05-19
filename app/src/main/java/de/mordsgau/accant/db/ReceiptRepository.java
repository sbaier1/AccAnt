package de.mordsgau.accant.db;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import de.mordsgau.accant.db.dao.ReceiptDao;
import de.mordsgau.accant.model.Receipt;
import de.mordsgau.accant.model.ReceiptWithoutItems;
import de.mordsgau.accant.model.atomic.ReceiptItem;
import de.mordsgau.accant.model.atomic.ReceiptItemTag;
import de.mordsgau.accant.model.atomic.ReceiptItemWithoutTags;

/**
 * Rudimentary repository class for receipts, uses guava LoadingCache to reduce database interaction
 */
@Singleton
public class ReceiptRepository {
    public static final long UNASSIGNED_TAG = Long.MAX_VALUE;
    private final Cache<Integer, LiveData<Receipt>> receiptCache;
    private final ReceiptDao receiptDao;

    private ExecutorService executorService;

    public ReceiptRepository(ReceiptDao receiptDao, ExecutorService dbExecutor) {
        this.executorService = dbExecutor;
        this.receiptDao = receiptDao;
        CacheLoader<Integer, LiveData<Receipt>> loader = new CacheLoader<Integer, LiveData<Receipt>>() {
            @Override
            public LiveData<Receipt> load(Integer key) throws Exception {
                return receiptDao.load(key);
            }
        };
        this.receiptCache = CacheBuilder.newBuilder()
                .maximumSize(50)
                .expireAfterWrite(10, TimeUnit.HOURS)
                .build(loader);
    }

    public LiveData<Receipt> getReceipt(final int receiptId) {
        final LiveData<Receipt> value = receiptCache.getIfPresent(receiptId);
        if (value == null) {
            final LiveData<Receipt> fromDB = receiptDao.load(receiptId);
            return fromDB;
        } else {
            return value;
        }
    }

    public LiveData<List<Receipt>> getReceipts() {
        return receiptDao.allReceipts();
    }

    public LiveData<List<ReceiptItemTag>> getTags() {
        return receiptDao.getUnassignedTags();
    }

    public Future<Integer> saveReceipt(final Receipt receipt) {
        /*
        FIXME caching??
        final MutableLiveData<Receipt> receiptData = new MutableLiveData<>();
        receiptData.postValue(receipt);
        receiptCache.put(receipt.getId(), receiptData);*/
        final Future<Integer> task = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return receiptDao.save(receipt);
            }
        });
        return task;
    }

    public void deleteReceiptItem(final ReceiptItem receiptItem) {

    }

    public LiveData<List<Integer>> receiptIds() {
        return receiptDao.receiptIds();
    }

    public void refreshReceipt(final String receiptId) {
        // FIXME web API request
    }

    /**
     * Add a tag to a receipt item
     *
     * @param item item to add to
     * @param tag  tag to add
     */
    public void addTag(ReceiptItem item, ReceiptItemTag tag) {
        executorService.submit(() -> {
            tag.setItemId(item.getId());
            receiptDao.insertReceiptItemTag(tag);
        });
    }

    public void addTag(ReceiptItemTag tag) {
        executorService.submit(() -> {
            tag.setItemId(UNASSIGNED_TAG);
            receiptDao.insertReceiptItemTag(tag);
        });
    }

    public void setItemName(ReceiptItem receiptItem, String newText) {
        executorService.submit(() -> {
            final ReceiptItemWithoutTags withoutTags = receiptItem.getReceiptItem();
            withoutTags.setItemName(newText);
            receiptDao.updateItemName(withoutTags);
        });
    }

    public void removeItem(ReceiptItemWithoutTags receiptItem) {
        executorService.submit(() -> {
            receiptDao.deleteReceiptItem(receiptItem);
        });
    }

    public void addItem(ReceiptWithoutItems receipt, ReceiptItemWithoutTags newItem, ReceiptItemTag... tags) {
        executorService.submit(() -> {
            newItem.setReceiptId(receipt.getId());
            receiptDao.insertReceiptItem(newItem);
            for (ReceiptItemTag tag : tags) {
                receiptDao.insertReceiptItemTag(tag);
            }
        });
    }

    public void setItemPrice(ReceiptItemWithoutTags receiptItem, Double newPrice) {
        executorService.submit(() -> {
            receiptItem.setItemPrice(newPrice);
            receiptDao.updateItemPrice(receiptItem);
        });
    }

    public void updateReceiptTotal(ReceiptWithoutItems receipt, Double newTotal) {
        executorService.submit(() -> {
            receipt.setSum(newTotal);
            receiptDao.updateReceipt(receipt);
        });
    }

    public void removeTag(ReceiptItemTag tag) {
        executorService.submit(() -> {
            receiptDao.deleteTag(tag);
        });
    }

    public int getColorForTag(String tagName) {
        try {
            executorService.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return receiptDao.getColorForTag(tagName);
                }
            }).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Future<?> updateReceipt(ReceiptWithoutItems receipt) {
        return executorService.submit(() -> {
            receiptDao.updateReceipt(receipt);
        });
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void setAccount(Receipt receipt) {
        receiptDao.updateReceipt(receipt.getReceipt());
    }

    public void deleteReceipt(Receipt receipt) {
        executorService.submit(() -> {
            for (ReceiptItem item : receipt.getItems()) {
                receiptDao.deleteReceiptItem(item.getReceiptItem());
            }
            receiptDao.deleteReceipt(receipt.getReceipt());
        });
    }
}
