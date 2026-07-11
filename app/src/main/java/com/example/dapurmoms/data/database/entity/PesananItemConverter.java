package com.example.dapurmoms.data.database.entity;

import androidx.room.TypeConverter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PesananItemConverter {

    @TypeConverter
    public static String fromList(List<PesananItem> list) {
        if (list == null) {
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        for (PesananItem item : list) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("namaMenu", item.getNamaMenu());
                jsonObject.put("jumlah", item.getJumlah());
                jsonObject.put("hargaSatuan", item.getHargaSatuan());
                jsonObject.put("total", item.getTotal());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }

    @TypeConverter
    public static List<PesananItem> toList(String value) {
        if (value == null) {
            return new ArrayList<>();
        }
        List<PesananItem> list = new ArrayList<>();
        try {
            String trimmed = value.trim();
            if (trimmed.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(trimmed);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    PesananItem item = new PesananItem();
                    item.setNamaMenu(jsonObject.optString("namaMenu", ""));
                    item.setJumlah(jsonObject.optInt("jumlah", 0));
                    item.setHargaSatuan(jsonObject.optLong("hargaSatuan", 0));
                    item.setTotal(jsonObject.optLong("total", 0));
                    list.add(item);
                }
            } else {
                // Backward compatibility: handle legacy plain text menu names
                if (!trimmed.isEmpty()) {
                    list.add(new PesananItem(trimmed, 0, 0));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Fallback: handle as legacy plain text menu name
            if (!value.trim().isEmpty()) {
                list.add(new PesananItem(value.trim(), 0, 0));
            }
        }
        return list;
    }
}
