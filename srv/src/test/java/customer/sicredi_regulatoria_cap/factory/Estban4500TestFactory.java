package customer.sicredi_regulatoria_cap.factory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import cds.gen.sicredi.db.entities.cadoc4500.Estban;

public class Estban4500TestFactory extends BaseTestFactory {

    public static Estban getValid() {
        Estban estban = Estban.create();

        estban.setAccount(faker.numerify("#########"));
        estban.setAgency(faker.numerify("###"));
        estban.setBalance(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)));
        estban.setCompanyExternalCode(faker.numerify("0#0#"));
        estban.setDescription(faker.lorem().word());
        estban.setDrillState("leaft");
        
        LocalDate date = randomDate();
        estban.setExercise(date.getYear() + "");
        estban.setInterval(date.getMonthValue() + "");
        estban.setLevel(0);

        return estban;
    }

    public static List<Estban> getList(Supplier<Estban> supplier) {
        return IntStream.range(0, /*faker.number().numberBetween(10, 100)*/ 1)
            .mapToObj(e -> supplier.get())
            .toList();
    }
}
