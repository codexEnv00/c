package customer.sicredi_regulatoria_cap.utils;

import java.util.Objects;

import cds.gen.sicredi.db.entities.Companies;

public class CompaniesWrapper {
	private final Companies companies;

	public CompaniesWrapper(Companies companies) {
		this.companies = companies;
	}

	public static CompaniesWrapper createCompany(String id, String name, String cnpj, String externalCode) {
		Companies company = Companies.create();
		company.setCnpj(cnpj);
		company.setName(name);
		company.setExternalCode(externalCode);

		return new CompaniesWrapper(company);
	}

	public static CompaniesWrapper build(Companies company) {
		return new CompaniesWrapper(company);
	}

	public Companies getCompany() {
		return companies;
	}

	public Companies getCloneCompany() {
		Companies these = Companies.create();
		these.setName(companies.getName());
		these.setCnpj(companies.getCnpj());
		these.setExternalCode(companies.getExternalCode());

		return these;
	}

	public String getExternalCode() {
		return this.companies.getExternalCode();
	}

	@Override
	public int hashCode() {
		return Objects.hash(companies.getName(), companies.getExternalCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		
		CompaniesWrapper wrapper = (CompaniesWrapper) obj;
		
		Companies that = wrapper.getCompany();
		return Objects.equals(companies.getCnpj(), that.getCnpj()) &&
			Objects.equals(companies.getName(), that.getName()) &&
			Objects.equals(companies.getExternalCode(), that.getExternalCode());
	}
}