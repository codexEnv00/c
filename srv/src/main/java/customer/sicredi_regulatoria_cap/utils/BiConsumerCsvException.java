package customer.sicredi_regulatoria_cap.utils;

import java.io.IOException;

import com.opencsv.exceptions.CsvException;

@FunctionalInterface
public interface BiConsumerCsvException<T, U> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    void accept(T t, U u) throws IOException, CsvException;
}