package customer.sicredi_regulatoria_cap.factory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import cds.gen.sicredi.db.entities.Protocols;
import customer.sicredi_regulatoria_cap.services.BacenService.ECadoc;

public class ProtocolTestFactory extends BaseTestFactory {

    public static Protocols getValid() {
        final var protocol = Protocols.create();
        protocol.setBloc(faker.options().nextElement(Arrays.asList("jun", "dez")));
        protocol.setCadoc(faker.options().option(ECadoc.class).toString());
        protocol.setCompanyExternalCode(faker.numerify("0#0#"));
        
        LocalDate date = getLastMonthDay(randomDate());
        protocol.setExercise(date.getYear() + "");
        protocol.setInterval(date.getMonthValue() + "");
        protocol.setReleaseDate(date);
        protocol.setStatus(faker.lorem().word());
        protocol.setTipoDoc(date.getMonthValue() > 6 ? "F2" : "F1");
        protocol.setVersion("SI25");

        return protocol;
    }

    public static List<Protocols> getList(Supplier<Protocols> supplier) {
        return IntStream.range(0, faker.number().numberBetween(1, 3))
            .mapToObj(e -> supplier.get())
            .toList();
    }
}
