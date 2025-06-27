package customer.sicredi_regulatoria_cap.services;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.function.TriConsumer;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;

@Service
public class SheetService {

	public <T> void exportCSV(OutputStream outputStream, Stream<T> data, Class<T> clazz) throws IOException, CsvException {
		HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
		strategy.setType(clazz);

		String headerLine = Arrays.stream(clazz.getDeclaredFields())
				.map(field -> field.getAnnotation(CsvBindByName.class))
				.filter(Objects::nonNull)
				.map(CsvBindByName::column)
				.collect(Collectors.joining(","));

		try (StringReader stringReader = new StringReader(headerLine);
				CSVReader reader = new CSVReader(stringReader)) {
			CsvToBean<T> csv = new CsvToBeanBuilder<T>(reader)
					.withType(clazz)
					.withMappingStrategy(strategy)
					.build();
			for (@SuppressWarnings("unused") T csvRow : csv) {}
		}

		ICSVWriter writer = new CSVWriterBuilder(new OutputStreamWriter(outputStream))
			.build();

		StatefulBeanToCsv<T> sbc = new StatefulBeanToCsvBuilder<T>(writer)
			.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
			.withMappingStrategy(strategy)
			.withQuotechar('\'')
			.build();

		sbc.write(data);
		writer.close();
		outputStream.flush();


		// CsvMapper mapper = CsvMapper.builder()//
		// 		.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
		// 		.findAndAddModules()
		// 		.build();

		// CsvSchema schema = mapper.schemaFor(clazz).withHeader();

		// var writer = mapper.writer(schema);

		// var generator = writer.createGenerator(outputStream);
		// generator.writeStartArray();
		// data.forEach(d -> {
		// 	try {
		// 		writer.writeValue(generator, d);
		// 	} catch (IOException e) {
		// 		e.printStackTrace();
		// 		throw new RuntimeException(e);
		// 	}
		// });
		// generator.writeEndArray();

		// generator.close();

	}

	public <T> void exportXLSX(OutputStream outputStream, Stream<T> report, Class<T> clazz,
			TriConsumer<Worksheet, T, Integer> consumerRow) throws IOException {

		List<String> headers = Arrays.stream(clazz.getDeclaredFields())
				.map(field -> field.getAnnotation(CsvBindByName.class))
				.filter(Objects::nonNull)
				.map(CsvBindByName::column)
				.toList();

		try (Workbook wb = new Workbook(outputStream, "RelatorioSicredi", "1.0");) {
			Worksheet ws = wb.newWorksheet("Relatório");

			final int[] columnNum = { 0 };
			headers.forEach(p -> {
				ws.value(0, columnNum[0], p);
				columnNum[0]++;
			});

			AtomicInteger rowNumber = new AtomicInteger(1);
			
			report.forEach(e -> {
				consumerRow.accept(ws, e, rowNumber.addAndGet(1) - 1);
			});
		}

	}

}