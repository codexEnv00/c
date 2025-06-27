namespace sicredi.db.entities;

using { 
	sicredi.db.entities.Companies as Companies
} from './config';

entity TerminationDocument {
	key company: Association to one Companies;

	key document: String;
	key exercise: String;
	key interval: String;
	
	key item: Integer;
	key cadoc: String;
	
	key tpDoc: String;
	key dtLanc: Date;

	amount: Decimal(20, 2);
	razao: String;
}

