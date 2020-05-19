package de.mordsgau.accant.detection;

import android.graphics.Rect;
import android.os.Build;
import android.util.Log;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import androidx.annotation.RequiresApi;
import de.mordsgau.accant.model.Receipt;
import de.mordsgau.accant.model.atomic.ReceiptItem;

import static java.util.Collections.EMPTY_MAP;

public class ReceiptReader {
    public static final int ALIGNMENT_CUTOFF = 20;
    private static final int COLUMN_ALIGNMENT_CUTOFF = 30;
    public static final double SIMILARITY_CUTOFF = 4;
    private final char decimalSeparator;

    private final Map<String, Double> cacheMap;
    private Double cachedSum;

    private AtomicLong lastTime;

    public ReceiptReader() {
        this.decimalSeparator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
        this.cacheMap = new HashMap<>();
        this.cachedSum = 0D;
        this.lastTime = new AtomicLong(System.currentTimeMillis());
    }

    public interface OnReceiptDetectedCallback {
        void receiptDetected(Receipt receipt);
    }

    public interface FinishedDetectionCallback {
        void finished();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void detectReceipt(FirebaseVisionText firebaseVisionText, List<OnReceiptDetectedCallback> callbacks, FinishedDetectionCallback finishCallback) {
        // Line based merging strategy for now
        final List<FirebaseVisionText.Line> lines = firebaseVisionText.getTextBlocks().stream()
                .flatMap(block -> block.getLines().stream())
                .collect(Collectors.toList());
        // Sort by rectangle top first then left first, to get a rudimentary ordering of the text blocks on the screen to categorize
        // TODO: still necessary?
        final Comparator<FirebaseVisionText.Line> comparing = getComparing();
        lines.sort(comparing);

        /*final List<Integer> sortedLeft = lines.stream().map(line -> line.getBoundingBox())
                // sort by left alignment instead top-first
                .sorted((o1, o2) -> Integer.compare(o1.left, o2.left))
                .map(rect -> rect.left)
                .collect(Collectors.toList());*/
        final List<Cluster<LineClustered>> columns = findColumnLocations(lines);

        // do not continue if we can't find at least 2 columns (key, value at least is necessary)
        if (columns.size() < 2) {
            Log.d("asd", "Not enough columns found");
            finishCallback.finished();
            return;
        }

        // build key column -> value column from clusters
        List<List<Cluster<LineClustered>>> tables = new ArrayList<>();
        for (int i = 0; i < columns.size(); ++i) {
            List<Cluster<LineClustered>> columnsAligned = new ArrayList<>();
            final Cluster<LineClustered> clusteredColumn = columns.get(i);
            // Average the center of all rectangles to find an approximate position of the column in Y plane
            final Optional<Integer> average = getAverageColumnYPlane(clusteredColumn);
            if (!average.isPresent()) {
                continue;
            }
            final int averageLocation = average.get();
            // Now compare with other columns
            columnsAligned.add(clusteredColumn);
            for (int k = 0; k != i && k < columns.size(); ++k) {
                final Cluster<LineClustered> clusteredColumnOther = columns.get(k);
                final Optional<Integer> averageLocationOther = getAverageColumnYPlane(clusteredColumnOther);
                // euclidean distance with a certain cutoff, the other column might not have same amount of elements
                if (averageLocationOther.isPresent() && Math.abs(averageLocation - averageLocationOther.get()) < ALIGNMENT_CUTOFF) {
                    // Columns align, save
                    columnsAligned.add(clusteredColumnOther);
                }
            }
            tables.add(columnsAligned);
        }
        List<Map<FirebaseVisionText.Line, String>> tablesAligned = new ArrayList<>();
        final List<LineClustered> missingLines = new ArrayList<>();
        for (List<Cluster<LineClustered>> table : tables) {
            Map<FirebaseVisionText.Line, String> currentTable = new HashMap<>();
            for (int i = 0; i < table.size(); ++i) {
                final Cluster<LineClustered> clusterLeft = table.get(i);

                // Find a proper cutoff for the rects by getting the average height of the rects
                int heightCutoff = 0;
                for (LineClustered clusteredLine : clusterLeft.getPoints()) {
                    heightCutoff += Math.abs(clusteredLine.getLine().getBoundingBox().height());
                }
                if (lines.size() > 1) {
                    heightCutoff = (heightCutoff / lines.size()) / 2;
                } else {
                    heightCutoff = Integer.MAX_VALUE;
                }
                // Get the next column in the cluster, to align entries with
                // FIXME: multivalue for larger tables, just a quick fix for now
                for (int k = i + 1; k < table.size(); ++k) {
                    // one of the right columns
                    final Cluster<LineClustered> clusterRight = table.get(k);
                    final List<LineClustered> rightLinesRemaining = new ArrayList<>(clusterRight.getPoints());
                    for (LineClustered lineLeft : clusterLeft.getPoints()) {
                        boolean wasAppended = false;
                        for (LineClustered lineRight : clusterRight.getPoints()) {
                            if (Math.abs(lineLeft.getLine().getBoundingBox().centerY() - lineRight.getLine().getBoundingBox().centerY()) < heightCutoff) {
                                // TODO remove this strnig buuilder
                                currentTable.put(lineLeft.getLine(), lineRight.getLine().getText());
                                rightLinesRemaining.remove(lineRight);
                                wasAppended = true;
                                // only a single R-value for each L-value (cannot have multiple prices per item only multiple keys)
                                break;
                            }
                        }
                        if (!wasAppended) {
                            missingLines.add(lineLeft);
                        }
                    }

                    for (LineClustered missingLine : missingLines) {
                        LineClustered minDistLine = null;
                        int previousScore = heightCutoff * 2;
                        for (LineClustered lineRight : rightLinesRemaining) {
                            Log.d("asd", "missing line: " + missingLine.getLine().getText() + ", trying to match against: " + lineRight.getLine().getText());
                            int currentScore = Math.abs(missingLine.getLine().getBoundingBox().centerY() - lineRight.getLine().getBoundingBox().centerY());
                            if (currentScore < previousScore && isPrice(lineRight.getLine().getText())) {
                                minDistLine = lineRight;
                                previousScore = currentScore;
                            }
                        }

                        if (minDistLine != null) {
                            // Lets just hope this alignment is good for now TODO
                            Log.d("asd", "putting missing line: " + missingLine.getLine().getText() + ", " + minDistLine.getLine().getText());
                            currentTable.put(missingLine.getLine(), minDistLine.getLine().getText());
                            rightLinesRemaining.remove(minDistLine);
                        } else {
                            // TODO search for other lines close to this one and remove / replace them into the map with the key appended
                            Map.Entry<FirebaseVisionText.Line, String> replaceEntry = null;
                            for (Map.Entry<FirebaseVisionText.Line, String> entry : currentTable.entrySet()) {
                                if (Math.abs(entry.getKey().getBoundingBox().centerY() - missingLine.getLine().getBoundingBox().centerY()) < heightCutoff * 2) {
                                    replaceEntry = entry;
                                    break;
                                }
                            }
                            if (replaceEntry != null) {
                                currentTable.remove(replaceEntry.getKey());
                                final Rect rect1 = replaceEntry.getKey().getBoundingBox();
                                final Rect rect2 = missingLine.getLine().getBoundingBox();
                                final Rect rectMerged = new Rect(
                                        Math.min(rect1.left, rect2.left),
                                        Math.min(rect1.top, rect2.top),
                                        Math.max(rect1.right, rect2.right),
                                        Math.max(rect1.bottom, rect2.bottom));
                                final FirebaseVisionText.Line line =
                                        new FirebaseVisionText.Line(replaceEntry.getKey().getText() + missingLine.getLine().getText(),
                                                rectMerged, new ArrayList<>(), new ArrayList<>(), 0F);
                                currentTable.put(line, replaceEntry.getValue());
                            }
                        }
                    }
                    for (LineClustered lineClustered : rightLinesRemaining) {
                        Log.d("asd", "R-line remaining: " + lineClustered.getLine().getText());
                    }
                    for (LineClustered missingLine : missingLines) {
                        Log.d("asd", "L-line remaining: " + missingLine.getLine().getText());
                    }
                }
            }
            if (currentTable.size() > 0) {
                tablesAligned.add(currentTable);
            }
        }

        Map<String, Double> priceMap = new HashMap<>();
        for (Map<FirebaseVisionText.Line, String> table : tablesAligned) {
            for (Map.Entry<FirebaseVisionText.Line, String> entry : table.entrySet()) {
                if (!isPrice(entry.getKey().getText()) && isPrice(entry.getValue())) {
                    final Double price = getPriceOrNull(entry.getValue());
                    if (price != null) {
                        priceMap.put(entry.getKey().getText(), price);
                    }
                }
            }
        }
        Double totalSum = 0D;
        for (Map.Entry<String, Double> entry : priceMap.entrySet()) {
            Log.d("asd", "price entry: " + entry.getKey() + ", " + entry.getValue());
            totalSum += entry.getValue();
        }
        Log.d("asd", "overall sum of seen items: " + totalSum);
        List<ReceiptItem> receiptItems = new ArrayList<>();
        for (Map.Entry<String, Double> receiptItem : priceMap.entrySet()) {
            receiptItems.add(new ReceiptItem(receiptItem.getKey(), receiptItem.getValue()));
        }
        // TODO read date from receipt
        if (totalSum > 0D && priceMap.entrySet().size() > 0) {
            for (OnReceiptDetectedCallback callback : callbacks) {
                finishCallback.finished();
                callback.receiptDetected(new Receipt(totalSum, receiptItems, new Date(), EMPTY_MAP));
            }
        }
        if (totalSum < 1D) {
            finishCallback.finished();
            return;
        }
        finishCallback.finished();
        return;
    }

    private Comparator<FirebaseVisionText.Line> getComparing() {
        return Comparator.comparing(line -> line.getBoundingBox(), (o1, o2) -> {
            if (o1.top > o2.top) {
                return 1;
            } else if (o1.top < o2.top) {
                return -1;
            } else {
                // further left, same top value => further left is first
                return Integer.compare(o1.left, o2.left);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Optional<Integer> getAverageColumnYPlane(Cluster<LineClustered> clusteredColumn) {
        return clusteredColumn.getPoints().stream()
                .map(point -> point.line.getBoundingBox().centerY())
                .reduce((integer, integer2) -> (integer + integer2) / 2);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Cluster<LineClustered>> findColumnLocations(List<FirebaseVisionText.Line> lines) {
        /*final int[] laplace = {-1, 2, -1};

        final int[] output = new int[sortedLeft.size()];

        // Apply transformation
        for (int i = 0; i < sortedLeft.size(); ++i) {
            output[i] = 0;
            for (int j = 0; j < 3; ++j) {
                int mult;
                if ((j - 1) + i < 0) {
                    mult = -100;
                } else if ((j - 1) + i > sortedLeft.size() - 1) {
                    mult = -100;
                } else {
                    mult = sortedLeft.get(j - 1 + i);
                }
                output[i] += laplace[j] * mult;
            }
            output[i] = Math.abs(output[i]);
        }
        //Log.d("asd", "result array: "+ Arrays.toString(output));

        // Find maxima*/
        // Find maxima using DBCLUSTER algorithm
        final DBSCANClusterer<LineClustered> clusterer = new DBSCANClusterer<>(100D, 1,
                /* Move alignment "tighter" due to preview size FIXME determine this properly */
                (DistanceMeasure) (a, b) -> 0.8 * Math.abs(a[0] - b[0]) + (0.45 * (Math.abs(a[1] - b[1]))));
        return clusterer.cluster(lines
                .stream().map(line -> new LineClustered(line.getBoundingBox().centerX(), line.getBoundingBox().centerY(), line))
                .collect(Collectors.toList()));
        /*final List<Cluster<LineClustered>> columns = new ArrayList<>();
        for (Cluster<LineClustered> myPointCluster : cluster) {
            columns.add(myPointCluster);
        }
        return columns;*/
    }

    /*private Map<String, Double> findReceiptItems(final List<Map.Entry<String, Double>> sortedEntries) {
        sortedEntries.sort();
        List<Map.Entry<String, Double>> finishedEntries = new ArrayList<>();
        Double countingSum = 0D;
        for (Map.Entry<String, Double> entry : sortedEntries) {
            if (!entry.getValue().equals(sum) && sortedEntries.size() > 1) {
                finishedEntries.add(entry);
                boolean alreadyCached = false;
                for (Map.Entry<String, Double> cachedEntry : cacheMap.entrySet()) {
                    final int stringEditDistance = HeuristicUtil.stringEditDistance(entry.getKey(), cachedEntry.getKey());
                    if (stringEditDistance < SIMILARITY_CUTOFF) {
                        alreadyCached = true;
                    }
                }
                if (!alreadyCached) {
                    cacheMap.put(entry.getKey(), entry.getValue());
                }
                countingSum += entry.getValue();
            }
            if (countingSum.equals(cachedSum)) {
                // We have a final and valid receipt map
                Log.d("asd", "*** FINISHED: " + finishedEntries);
                break;
            } else if (countingSum > cachedSum) {
                // Failed, checksum invalid, we counted too much, probably OCR error
                return null;
            }
        }

        // Merged cached entries into final map
        List<Map.Entry<String, Double>> newEntries = new ArrayList<>();
        for (Map.Entry<String, Double> cachedEntry : cacheMap.entrySet()) {
            boolean alreadyKnown = false;
            for (Map.Entry<String, Double> finishedEntry : finishedEntries) {
                if (HeuristicUtil.stringEditDistance(cachedEntry.getKey(), finishedEntry.getKey()) < SIMILARITY_CUTOFF) {
                    alreadyKnown = true;
                }
            }
            // None of the already present entries are similar
            if (!alreadyKnown && finishedEntries.size() > 0) {
                newEntries.add(cachedEntry);
                countingSum += cachedEntry.getValue();
            }
        }
        finishedEntries.addAll(newEntries);

        Log.d("asd", "Final map: " + finishedEntries);
    }*/

    class LineClustered implements Clusterable {

        private final double[] myData;
        private final FirebaseVisionText.Line line;

        public LineClustered(final int left, final int top, final FirebaseVisionText.Line line) {
            this.myData = new double[2];
            this.myData[0] = left;
            this.myData[1] = top;
            this.line = line;
        }

        @Override
        public double[] getPoint() {
            return myData;
        }

        @Override
        public String toString() {
            return String.valueOf(myData[0]) + String.valueOf(myData[1]) + ", line: " + line.getText();
        }

        public FirebaseVisionText.Line getLine() {
            return line;
        }
    }

    class Column {
        private final int columnLocation;

        private final int maxDeviation;


        public Column(int columnLocation, int maxDeviation) {
            this.columnLocation = columnLocation;
            this.maxDeviation = maxDeviation;
        }

        @Override
        public String toString() {
            return "Column{" +
                    "columnLocation=" + columnLocation +
                    ", maxDeviation=" + maxDeviation +
                    '}';
        }
    }

    private boolean isPrice(String text) {
        // Price can contain a minus in some cases
        // Price can use different separators, we match against the one used in the get price method for now
        // Characters may appear after a price
        // FIXME characters may also appear in front of a price (EUR, currency signs, etc.), needs a more precise regex, so it doesn't mistrigger
        return text.matches("[-]?[0-9]+[" + decimalSeparator + "][0-9]+.*");
    }

    private Double getPriceOrNull(String currentItem) {
        if (currentItem.length() > 10) {
            // no price on a receipt is this high wtf are you buying
            return null;
        }
        final int indexOfSeparator = currentItem.indexOf(decimalSeparator);
        if (indexOfSeparator != -1) {
            final String firstSubstring;
            boolean isNegative = false;
            if ('-' == currentItem.charAt(0)) {
                firstSubstring = currentItem.substring(1, indexOfSeparator);
                isNegative = true;
            } else {
                firstSubstring = currentItem.substring(0, indexOfSeparator);
            }

            final String secondSubstring = currentItem.substring(indexOfSeparator + 1, currentItem.length());
            int digitCountAfterSeparator = getDigitCount(secondSubstring);

            final String digitsAfterSeparator = secondSubstring.substring(0, digitCountAfterSeparator);

            final double sum;
            try {
                // FIXME: sanity check numbers, ensure  digit count makes sense (mostly XX.XX)
                final int afterSeparator = Integer.parseInt(digitsAfterSeparator);
                final int beforeSeparator = Integer.parseInt(firstSubstring);
                sum = isNegative ? -1 * Double.parseDouble(beforeSeparator + "." + digitsAfterSeparator)
                        : Double.parseDouble(beforeSeparator + "." + digitsAfterSeparator);
                return sum;
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private int getDigitCount(String secondSubstring) {
        // Find last number in the string and cut if further (usually 2 digits after separator)
        int digits = 0;
        for (int i = 0; i < secondSubstring.length(); ++i) {
            if (Character.isDigit(secondSubstring.charAt(i))) {
                ++digits;
            } else {
                // abort if not a digit, further characters are irrelevant and break the parsing process
                break;
            }
        }
        return digits;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void mergeElements(FirebaseVisionText firebaseVisionText, StringBuilder mergedText) {
        final List<FirebaseVisionText.Element> elements = firebaseVisionText
                .getTextBlocks().stream()
                .flatMap(block -> block.getLines().stream()
                        .flatMap(line ->
                        {
                            final List<FirebaseVisionText.Element> lineElements = line.getElements();
                            lineElements.add(new FirebaseVisionText.Element("\n", lineElements.get(lineElements.size() - 1).getBoundingBox(), new ArrayList<>(), null));
                            return lineElements.stream();
                        }))
                .collect(Collectors.toList());

        final Comparator<FirebaseVisionText.Element> comparing = Comparator.comparing(element -> element.getBoundingBox(), (o1, o2) -> {
            if (o1.top > o2.top) {
                return 1;
            } else if (o1.top < o2.top) {
                return -1;
            } else {
                // further left, same top value => further left is first
                return Integer.compare(o1.left, o2.left);
            }
        });
        elements.sort(comparing);
        for (FirebaseVisionText.Element element : elements) {
            mergedText.append(element.getText());
        }
    }

    private void mergeText(List<FirebaseVisionText.TextBlock> textBlocksActual, List<TextHolder> textBlocks, int topCutOff, StringBuilder mergedText) {
        for (int i = 0; i < textBlocksActual.size(); ++i) {
            final TextHolder currentBlock = textBlocks.get(i);
            // Merge the current block with all following blocks within the height of the rectangle
            // Ignore previously processed blocks
            final Rect currentBox = currentBlock.getBoundingBox();
            if (!(currentBox.top <= topCutOff)) {
                final int boxHeight = currentBox.bottom - currentBox.top;
                if (i + 1 < textBlocksActual.size()) {
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
            }
        }
    }
}
