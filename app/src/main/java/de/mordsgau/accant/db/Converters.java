package de.mordsgau.accant.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import androidx.room.TypeConverter;
import de.mordsgau.accant.model.metadata.BudgetFrequency;

/**
 * TypeConverters for Room database
 */
public class Converters {
    final ObjectMapper objectMapper = new ObjectMapper();

    @TypeConverter
    public Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public Long dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }

    /*@TypeConverter
    public List<ReceiptItem> receiptItemsFromJSON(final String in) throws IOException {
        return objectMapper.readValue(in, new TypeReference<List<ReceiptItem>>() {
        });
    }

    @TypeConverter
    public String jsonFromReceiptItems(final List<ReceiptItem> receiptItems) {
        try {
            return objectMapper.writeValueAsString(receiptItems);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    @TypeConverter
    public Map<String, String> metadataFromJSON(final String in) throws IOException {
        return objectMapper.readValue(in, new TypeReference<Map<String, String>>() {
        });
    }

    @TypeConverter
    public String jsonFromMap(final Map<String, String> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public int intFromFrequency(BudgetFrequency frequency) {
        switch(frequency) {
            case WEEKLY:
                return 0;
            case MONTHLY:
                return 1;
            case YEARLY:
                return 2;
            default:
                return -1;
        }
    }

    @TypeConverter
    public BudgetFrequency frequencyFromInt(int id) {
        switch(id) {
            case 0:
                return BudgetFrequency.WEEKLY;
            case 1:
                return BudgetFrequency.MONTHLY;
            case 2:
                return BudgetFrequency.YEARLY;
            default:
                return null;
        }
    }


}
