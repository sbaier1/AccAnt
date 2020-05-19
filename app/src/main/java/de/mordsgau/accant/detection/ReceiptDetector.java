package de.mordsgau.accant.detection;

import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.annotation.RequiresApi;

@Deprecated
public class ReceiptDetector implements Detector.Processor<TextBlock> {
    public static final int ALIGNMENT_CUTOFF = 30;
    private final char decimalSeparator;
    private NumberFormat currency;
    private Map<String, Double> costPrevious;

    public ReceiptDetector() {
        currency = NumberFormat.getCurrencyInstance();
        decimalSeparator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
    }

    @Override
    public void release() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        // TODO: merge textblocks according to their coordinates? might allow for better structure of the concatenated string => easier information extraction
        final SparseArray<TextBlock> detected = detections.getDetectedItems();
        List<TextHolder> textBlocks = new ArrayList<>();
        for (int i = 0; i < detected.size(); ++i) {
            final TextBlock currentBlock = detected.valueAt(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                final List<String> items = currentBlock.getComponents().stream().map((text) -> ((Text) text).getValue()).collect(Collectors.toList());
                textBlocks.add(new TextHolder(currentBlock.getBoundingBox(), items));
            }
        }

        // Find text holders close to each other and merge them
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final Comparator<TextHolder> comparing = Comparator.comparing(TextHolder::getBoundingBox, (o1, o2) -> {
                if (o1.top > o2.top) {
                    return 1;
                } else if (o1.top < o2.top) {
                    return -1;
                } else {
                    // further left, same top value => further left is first
                    return Integer.compare(o1.left, o2.left);
                }
            });
            textBlocks.sort(comparing);
        }
        // Attempt to merge text blocks into one properly
        int topCutOff = 0;
        final StringBuilder mergedText = new StringBuilder();
        for (int i = 0; i < textBlocks.size(); ++i) {
            final TextHolder currentBlock = textBlocks.get(i);
            // Merge the current block with all following blocks within the height of the rectangle
            // Ignore previously processed blocks
            final Rect currentBox = currentBlock.getBoundingBox();
            if (!(currentBox.top <= topCutOff)) {
                final int boxHeight = currentBox.bottom - currentBox.top;
                if (i + 1 < textBlocks.size()) {
                    final TextHolder nextBlock = textBlocks.get(i + 1);
                    // Check if box is at least somewhat aligned with the current box
                    final Rect nextBox = nextBlock.getBoundingBox();
                    /*if ((Math.abs(currentBox.top - nextBox.top) < ALIGNMENT_CUTOFF)
                            || (Math.abs(currentBox.bottom - nextBox.bottom) < ALIGNMENT_CUTOFF)) {*/
                    if (currentBox.top - ALIGNMENT_CUTOFF < nextBox.top || currentBox.bottom + ALIGNMENT_CUTOFF > nextBox.bottom) {
                        // The next box must be to the right of the current box in this case, so merge to the right
                        final List<String> currentComponents = currentBlock.getComponents();
                        final List<String> nextComponents = nextBlock.getComponents();
                        final int maxEndingIndex = Math.min(currentComponents.size(), nextComponents.size());
                        final int maxStartingIndex = Math.max(currentBlock.getComponentIndex(), nextBlock.getComponentIndex());
                        for (int k = maxStartingIndex; k < maxEndingIndex; ++k) {
                            mergedText.append(currentComponents.get(k))
                                    .append(";")
                                    // Price usually shouldn't contain spaces
                                    .append(nextComponents.get(k).replace(" ", ""))
                                    .append("\n");
                        }
                        currentBlock.setComponentIndex(maxEndingIndex);
                        nextBlock.setComponentIndex(maxEndingIndex);
                    }
                }
                // Now set the new cutoff after merging the other boxes
                //topCutOff = Math.min(currentBox.bottom, );
            }
        }

        //Log.d("TAG", "Merged text result: " + mergedText.toString());
        // Now we can attempt to build a preliminary cost map for this iteration
        Map<String, Double> cost;
        if(costPrevious == null) {
            cost = new HashMap<>();
        } else {
            cost = costPrevious;
        }

        final List<String> items = Arrays.asList(mergedText.toString().split("\n"));
        for (int i = 0; i < items.size(); ++i) {
            final List<String> components = Arrays.asList(items.get(i).split(";"));
            final String currentItem = components.get(0);
            Double price = null;
            for (String component : components) {
                price = getPriceOrNull(component);
                if (price != null) {
                    break;
                }
            }

            if (price != null) {
                // Check if any items are similar enough to be replaced (or this value be ignored)
                int minEditDistance = Integer.MAX_VALUE;
                for (String key : cost.keySet()) {
                    final int sed = HeuristicUtil.stringEditDistance(currentItem, key);
                    if(sed < 5 && currentItem.length() > 3 && sed < minEditDistance) {
                        minEditDistance = sed;
                    }
                }

                if(minEditDistance < 3) {
                    // NO-OP for now, item already exists: future: compare edit distance of future value do find minimum error among all previous detections of the strings
                } else {
                    cost.put(currentItem, price);
                }
            }
        }

        Log.d("ASD", "resulting map: " + cost.entrySet()+", sum: "+ cost.values().stream().mapToDouble((obj) -> obj).sum());

/*
        SparseArray<TextBlock> items = detected;
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            final String detectedText = item.getValue();
            if (item != null && detectedText != null) {
                //Log.d("OcrDetectorProcessor", "Text detected! " + detectedText);
            }
            // TODO testing
            if (detectedText.contains("SUMME")) {
                //Log.d("OcrDetectorProcessor", "Found SUMME");
                // FIXME: all items to the right of the upper and lower bound of the rectangle are eligible to be the sum. Also: choose the largest number found
                final int firstY = item.getBoundingBox().centerY();
                for (int k = 0; k < items.size(); ++k) {
                    final TextBlock nextItem = items.valueAt(k);
                    if (Math.abs(nextItem.getBoundingBox().centerY() - firstY) < 20) {
                        final String nextString = nextItem.getValue();
                        Log.d("OcrDetectorProcessor", "Close item: " + nextString);
                        final int indexOfSeparator = nextString.indexOf(decimalSeparator);
                        if (indexOfSeparator != -1) {
                            final String firstSubstring = nextString.substring(0, indexOfSeparator);
                            final String secondSubstring = nextString.substring(indexOfSeparator + 1, nextString.length());
                            final double sum;
                            try {
                                final int afterSeparator = Integer.parseInt(secondSubstring);
                                final int beforeSeparator = Integer.parseInt(firstSubstring);
                                sum = Double.parseDouble(beforeSeparator + "." + afterSeparator);
                            } catch (NumberFormatException ignored) {
                                return;
                            }

                            Log.d("OcrDetectorProcessor", "Found Sum: " + sum);
                        }
                    }
                }
            }
        }*/
    }

    private Double getPriceOrNull(String currentItem) {
        if (currentItem.length() > 8) {
            return null;
        }
        final int indexOfSeparator = currentItem.indexOf(decimalSeparator);
        if (indexOfSeparator != -1) {
            final String firstSubstring = currentItem.substring(0, indexOfSeparator);
            final String secondSubstring = currentItem.substring(indexOfSeparator + 1, currentItem.length());
            final double sum;
            try {
                // FIXME: sanity check numbers, ensure  digit count makes sense (mostly XX.XX)
                final int afterSeparator = Integer.parseInt(secondSubstring.replace("B", ""));
                final int beforeSeparator = Integer.parseInt(firstSubstring);
                sum = Double.parseDouble(beforeSeparator + "." + afterSeparator);
                return sum;
            } catch (NumberFormatException ignored) {
                Log.d("TAG", "Failing due to exception: "+ignored.getMessage());
                return null;
            }
        }
        return null;
    }
}
