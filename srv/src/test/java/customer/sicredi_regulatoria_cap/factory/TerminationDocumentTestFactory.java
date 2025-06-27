package customer.sicredi_regulatoria_cap.factory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import cds.gen.balanceservice.TerminationDocument;

public class TerminationDocumentTestFactory extends BaseTestFactory {

    public static TerminationDocument getValid() {
        TerminationDocument document = TerminationDocument.create();

        document.setAmount(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)));
        document.setCadoc("4010");
        document.setCompanyExternalCode(faker.numerify("0#0#"));
        document.setDocument(faker.numerify("#########"));

        LocalDate releaseDate = randomDate();
        
        document.setDtLanc(releaseDate.with(TemporalAdjusters.lastDayOfMonth()));
        document.setExercise(String.valueOf(releaseDate.getYear()));
        document.setInterval(String.valueOf(releaseDate.getMonthValue()));
        document.setItem(faker.number().randomDigit());
        document.setRazao(faker.numerify("########"));
        document.setTpDoc(releaseDate.getMonthValue() <= 6 ? "F1" : "F2");
        
        return document;
    }

    public static List<TerminationDocument> getList(Supplier<TerminationDocument> supplier) {
        return IntStream.range(0, /*faker.number().numberBetween(10, 100)*/ 1)
            .mapToObj(e -> supplier.get())
            .toList();
    }
}
