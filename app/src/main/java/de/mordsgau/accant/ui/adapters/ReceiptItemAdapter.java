package de.mordsgau.accant.ui.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.text.InputType;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import de.mordsgau.accant.R;
import de.mordsgau.accant.model.Receipt;
import de.mordsgau.accant.model.atomic.ReceiptItem;
import de.mordsgau.accant.model.atomic.ReceiptItemTag;
import de.mordsgau.accant.ui.viewmodel.ReceiptModel;

public class ReceiptItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ReceiptModel receiptModel;
    private Context context;

    public ReceiptItemAdapter(final ReceiptModel receipt) {
        this.receiptModel = receipt;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.receipt_item, parent, false);
        return new ReceiptItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Receipt receipt = receiptModel.getReceipt().getValue();
        if (receipt == null || receipt.getItems() == null || receipt.getItems().size() == 0) {
            Log.d("ASD", "cannot update, no receipt or no receipt items");
            return;
        }
        ReceiptItemHolder receiptItemHolder = (ReceiptItemHolder) holder;
        receiptItemHolder.chipGroup.removeAllViews();
        receiptItemHolder.layout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DROP) {
                    // Chip was dropped on this item
                    final CharSequence chipText = event.getClipData().getItemAt(0).getText();
                    /*if (event.getClipData().getItemCount() > 1) {
                        final int color = Integer.parseInt(event.getClipData().getItemAt(1).getText().toString());
                        addTagChip(receiptItemHolder, receiptItemHolder.chipGroup, color, chipText.toString(), position);
                        final ReceiptItemTag tag = new ReceiptItemTag(color, chipText.toString(), 0D);
                        //FIXME: pass ID during drag and assign it to the created tag, so we can ensure not creating new tags pointlessly.
                        receiptModel.addTag(position, tag);
                    } else {*/
                    final int color = receiptModel.getColorForTag(chipText.toString());
                    addTagChip(receiptItemHolder, receiptItemHolder.chipGroup, color, chipText.toString(), position);
                    final ReceiptItemTag tag = new ReceiptItemTag(color, chipText.toString(), 0D);
                    //receipt.getItems().get(position).getTags().add(tag);
                    receiptModel.addTag(position, tag);
                }
                return true;
            }
        });

        final ReceiptItem receiptItem = receipt.getItems().get(position);
        final EditText receiptItemName = receiptItemHolder.receiptItemName;
        receiptItemName.setText(receiptItem.getItemName());

        final EditText receiptItemPrice = receiptItemHolder.receiptItemPrice;
        receiptItemPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        receiptItemName.setImeOptions(EditorInfo.IME_FLAG_NAVIGATE_NEXT);
        receiptItemName.setInputType(InputType.TYPE_CLASS_TEXT);
        receiptItemName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    final List<ReceiptItem> items = receipt.getItems();
                    if (position < items.size()) {
                        final String newText = receiptItemName.getText().toString();
                        final ReceiptItem receiptItem1 = items.get(position);
                        final String oldText = receiptItem1.getItemName();
                        if (!newText.equals(oldText)) {
                            receiptModel.setItemName(receiptItem1, newText);
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        receiptItemPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    updatePrice(position, receiptItemPrice, receiptItem);
                    return true;
                } else {
                    return false;
                }
            }
        });

        receiptItemName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                final List<ReceiptItem> items = receipt.getItems();
                if (position < items.size()) {
                    final String newText = receiptItemName.getText().toString();
                    final ReceiptItem receiptItem1 = items.get(position);
                    final String oldText = receiptItem1.getItemName();
                    if (!newText.equals(oldText)) {
                        receiptModel.setItemName(receiptItem1, newText);
                    }
                }
            }
        });
        receiptItemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) v;
                editText.setSaveEnabled(true);
            }
        });

        if (receiptItem.getItemPrice() != null && receiptItem.getItemPrice() != 0D) {
            receiptItemPrice.setText(String.format(Locale.getDefault(), "%.02f", receiptItem.getItemPrice()));
        } else {
            receiptItemPrice.setText("");
        }

        receiptItemPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Don't care, we just got focus, nothing has been changed yet
                    return;
                }
                updatePrice(position, receiptItemPrice, receiptItem);
            }
        });
        List<ReceiptItemTag> tags = receiptItem.getTags();
        final ChipGroup chipGroup = ((ReceiptItemHolder) holder).chipGroup;
        for (ReceiptItemTag tag : tags) {
            final int color = tag.getColor();
            final String tagName = tag.getTagName();
            addTagChip(receiptItemHolder, chipGroup, color, tagName, position);
        }
    }

    private void updatePrice(int position, EditText receiptItemPrice, ReceiptItem receiptItem) {
        final Double previousPrice = receiptModel.getReceipt().getValue().getItems().get(position).getItemPrice();
        final String newText = receiptItemPrice.getText().toString();
        try {
            final NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
            final double newValue = numberFormat.parse(newText).doubleValue();
            if (newValue != previousPrice) {
                receiptModel.setItemPrice(receiptItem, newValue);
            }
        } catch (ParseException ex) {
            Log.d("AccAnt", "Failed to parse decimal number input as price " + ex + ", new text: " + newText);
        }
    }

    private void addTagChip(ReceiptItemHolder receiptItemHolder, ChipGroup chipGroup, int color, String tagName, int index) {
        final Chip chip = new Chip(context);
        if (color != 0) {
            chip.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }
        chip.setText(tagName);
        chip.setTextAppearance(R.style.Subtitle2);
        chip.setCloseIcon(context.getResources().getDrawable(R.drawable.baseline_highlight_off_black_48, null));
        chip.setCloseIconSize(48);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chip chip = (Chip) v;
                final int indexOfChild = chipGroup.indexOfChild(chip);
                final List<ReceiptItem> receiptItems = receiptModel.getReceipt().getValue().getItems();
                if (indexOfChild != -1 && indexOfChild < receiptItems.get(index).getTags().size()) {
                    final ReceiptItem receiptItem = receiptItems.get(index);
                    final ReceiptItemTag tag = receiptItem.getTags().get(indexOfChild);
                    chipGroup.removeView(chip);
                    receiptModel.removeTag(tag);
                }
                receiptItemHolder.chipGroup.removeView(v);
            }
        });
        chipGroup.addView(chip);
    }

    @Override
    public int getItemCount() {
        final LiveData<Receipt> receipt = receiptModel.getReceipt();
        if (receipt == null || receipt.getValue() == null || receipt.getValue().getItems() == null) {
            return 0;
        }
        return receipt.getValue().getItems().size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public class ReceiptItemHolder extends RecyclerView.ViewHolder {
        private final EditText receiptItemName;
        private final EditText receiptItemPrice;
        private final ChipGroup chipGroup;
        private final ConstraintLayout layout;

        ReceiptItemHolder(@NonNull View itemView) {
            super(itemView);
            this.receiptItemName = itemView.findViewById(R.id.receiptItemName);
            this.receiptItemPrice = itemView.findViewById(R.id.receiptItemPrice);
            this.chipGroup = itemView.findViewById(R.id.chipGroup);
            this.layout = (ConstraintLayout) itemView;
        }
    }
}
