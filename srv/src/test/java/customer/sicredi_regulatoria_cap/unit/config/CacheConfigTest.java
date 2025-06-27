package customer.sicredi_regulatoria_cap.unit.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.MessageFormat;
import java.time.ZoneId;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;

import customer.sicredi_regulatoria_cap.configs.CacheConfig;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;
import customer.sicredi_regulatoria_cap.services.BacenService.BlocoMeses;
import customer.sicredi_regulatoria_cap.services.BacenService.ECadoc;
import customer.sicredi_regulatoria_cap.services.bacen.ReadBacenService;

@ExtendWith(MockitoExtension.class)
public class CacheConfigTest {

    @Mock
    ReadBacenService readBacenService;

    @Test
    @DisplayName("It should generate key string correct")
    void shouldGenerateKeyStringCorrect() throws NoSuchMethodException, SecurityException {

        Faker faker = new Faker();

        CacheConfig cacheConfig = new CacheConfig();

        final String className = readBacenService.getClass().getName();
        final String methodName = "loadBacen";

        BalanceFilterDTO filterDTO = BalanceFilterDTO.builder()
                .bloc(faker.options().option(BlocoMeses.class))
                .cadoc(faker.options().option(ECadoc.class))
                .companies(//
                        IntStream.range(0, faker.random().nextInt(1, 10))//
                                .mapToObj(i -> faker.numerify("####"))
                                .toList()//
                )
                .exercise(faker.numerify("####"))
                .interval(String.valueOf(faker.number().numberBetween(1, 16)))
                .releaseDate(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .terminationCompanies(
                        IntStream.range(0, faker.random().nextInt(1, 10))//
                                .mapToObj(i -> faker.numerify("####"))
                                .toList()//
                )
                .tipoDoc(
                        IntStream.range(0, faker.random().nextInt(1, 10))//
                                .mapToObj(i -> faker.numerify("F1"))
                                .toList()//
                )
                .build();

        Object result = cacheConfig.keyGenerator().generate(//
                readBacenService,
                ReadBacenService.class.getMethod("loadBacen", BalanceFilterDTO.class),
                filterDTO//
        );

        filterDTO.setTerminationCompanies(null);

        final String expected = MessageFormat.format("{0}_{1}_{2}", className, methodName, String.valueOf(filterDTO.hashCodeKeyCache()));
        assertEquals(expected, result);
    }

}
