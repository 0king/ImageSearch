package com.example.imagesearch.data.local;

import androidx.room.TypeConverter;

import com.example.imagesearch.data.model.Photo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class DataTypeConverter {
    private static Gson gson = new Gson();

    @TypeConverter
    public static List<Photo> stringToList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Photo>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String ListToString(List<Photo> someObjects) {
        return gson.toJson(someObjects);
    }
}
