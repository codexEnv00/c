package customer.sicredi_regulatoria_cap.factory;

import cds.gen.sicredi.db.entities.Companies;

public class CompanyTestFactory extends BaseTestFactory {

    public static Companies getValid(String externalCode) {
        Companies company = Companies.create();
        company.setExternalCode(externalCode);
        company.setName(faker.company().name());
        company.setVisibility(Boolean.TRUE);
        company.setCnpj("123456789");

        return company;
    }

    public static Companies getValid() {
        return getValid(faker.numerify("0#0#"));
    }
}
