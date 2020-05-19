package de.mordsgau.accant.ui.viewmodel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.mordsgau.accant.db.ReceiptRepository;
import de.mordsgau.accant.model.Receipt;
import de.mordsgau.accant.model.atomic.ReceiptItem;
import de.mordsgau.accant.model.atomic.ReceiptItemTag;
import de.mordsgau.accant.model.atomic.ReceiptItemWithoutTags;

/**
 * Capture a receipt for the view to observe and to be updated by repositories.
 */
public class ReceiptModel extends ViewModel {

    private LiveData<Receipt> receipt;

    private final ReceiptRepository receiptRepository;

    /* Store colors for tags because drag and drop mechanic is stupid */
    private Map<String, Integer> colorMap;

    @Inject
    ReceiptModel(final ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
        this.colorMap = new ConcurrentHashMap<>();
    }

    public LiveData<Receipt> getReceipt() {
        return receipt;
    }

    public void init(int receiptId) {
        this.receipt = receiptRepository.getReceipt(receiptId);
    }

    public void addTag(int position, ReceiptItemTag tag) {
        final ReceiptItem item = getItem(position);
        if(item != null) {
            receiptRepository.addTag(item, tag);
        }
    }

    private ReceiptItem getItem(int position) {
        final Receipt receipt = this.receipt.getValue();
        if(receipt != null && position < receipt.getItems().size()) {
            final ReceiptItem receiptItem = receipt.getItems().get(position);
            return receiptItem;
        }
        return null;
    }

    public void setItemName(ReceiptItem receiptItem, String newText) {
        receiptRepository.setItemName(receiptItem, newText);
    }

    public void setItemPrice(ReceiptItem receiptItem, Double newPrice) {
        receiptRepository.setItemPrice(receiptItem.getReceiptItem(), newPrice);
        Double difference = newPrice - receiptItem.getItemPrice();
        updateTotal(difference);
    }

    private void updateTotal(Double difference) {
        final Receipt currentReceipt = receipt.getValue();
        Double newTotal = currentReceipt.getSum() + difference;
        receiptRepository.updateReceiptTotal(currentReceipt.getReceipt(), newTotal);
    }

    public void removeItem(ReceiptItemWithoutTags receiptItem) {
        receiptRepository.removeItem(receiptItem);
        updateTotal(-receiptItem.getItemPrice());
    }

    public void addItem(ReceiptItem newItem, ReceiptItemTag... tags) {
        receiptRepository.addItem(receipt.getValue().getReceipt(), newItem.getReceiptItem(), tags);
    }

    public void removeTag(ReceiptItemTag tag) {
        receiptRepository.removeTag(tag);
    }

    public int getColorForTag(String tagName) {
        if(colorMap.containsKey(tagName)) {
            return colorMap.get(tagName);
        } else {
            return -1;
        }
    }

    public void setColorForTag(String tagName, int color) {
        colorMap.put(tagName, color);
    }

    public void setAccountId(long id) {
        final Receipt receipt = this.receipt.getValue();
        if(receipt != null) {
            receipt.getReceipt().setAccountId((int) id);
            receiptRepository.updateReceipt(receipt.getReceipt());
        }
    }
}
