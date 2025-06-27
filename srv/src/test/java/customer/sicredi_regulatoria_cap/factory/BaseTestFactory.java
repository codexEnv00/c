package customer.sicredi_regulatoria_cap.factory;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

import com.github.javafaker.Faker;

public abstract class BaseTestFactory {

	protected static Faker faker = new Faker();

	protected static LocalDate randomDate() {
		return LocalDate.ofInstant(faker.date().past(1, TimeUnit.DAYS).toInstant(), ZoneId.systemDefault());
	}

	protected static LocalDate getLastMonthDay(LocalDate date) {
		return YearMonth.of(date.getYear(), date.getMonth()).atEndOfMonth();
	}
}
