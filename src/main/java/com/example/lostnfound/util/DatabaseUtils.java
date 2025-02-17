package com.example.lostnfound.util;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DatabaseUtils {

    private DatabaseUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String getTableAndColumnNames(Class<?> clazz) {
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        String tableName = (tableAnnotation != null) ? tableAnnotation.name() : clazz.getSimpleName();
        String columnNames = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .map(field -> {
                    Column column = field.getAnnotation(Column.class);
                    String columnName = column.name();
                    String columnType = field.getType().getSimpleName();
                    return columnName + " (" + columnType + ")";
                })
                .collect(Collectors.joining(", "));
        return "Table: " + tableName + ", Columns: " + columnNames;
    }
}