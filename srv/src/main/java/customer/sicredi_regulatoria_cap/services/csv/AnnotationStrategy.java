package customer.sicredi_regulatoria_cap.services.csv;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class AnnotationStrategy<T> extends HeaderColumnNameTranslateMappingStrategy<T> {
    public AnnotationStrategy(Class<T> clazz) {
        Map<String, String> map = new HashMap<>();
        // To prevent the column sorting
        List<String> originalFieldOrder = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            CsvBindByName annotation = field.getAnnotation(CsvBindByName.class);
            if (annotation != null) {
                map.put(annotation.column(), annotation.column());
                originalFieldOrder.add(annotation.column());
            }
        }
        setType(clazz);
        setColumnMapping(map);
        // Order the columns as they were created
        setColumnOrderOnWrite((a, b) -> Integer.compare(originalFieldOrder.indexOf(a), originalFieldOrder.indexOf(b)));
    }

    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        String[] result = super.generateHeader(bean);
        for (int i = 0; i < result.length; i++) {
            result[i] = getColumnName(i);
        }
        return result;
    }
}