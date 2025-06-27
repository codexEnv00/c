package customer.sicredi_regulatoria_cap.services.sheet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.function.TriConsumer;
import org.dhatim.fastexcel.Worksheet;

import com.opencsv.exceptions.CsvException;

import customer.sicredi_regulatoria_cap.services.SheetService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class ExportSheetBaseService<O, T> {

    private SheetService sheetService;

    public void exportReportCSV(T filter, OutputStream outputStream) throws IOException, CsvException {
        sheetService.exportCSV(outputStream, generateData(filter), getExportClass());
    }

    public void exportReportXLSX(T filter, OutputStream outputStream) throws IOException {
        sheetService.exportXLSX(outputStream, generateData(filter), getExportClass(), getRowConsumer());
    }

    protected abstract Stream<O> generateData(T filter);

    protected abstract Class<O> getExportClass();

    protected abstract TriConsumer<Worksheet, O, Integer> getRowConsumer();
}
