namespace sicredi.db.entities;

entity Companies {
	name: String;
	key externalCode: String;
	cnpj: String;
	visibility: Boolean default true;
}

entity CompanyAuthenticators {
	key company: Composition of one Companies;
	user: String;
	password: String;
}