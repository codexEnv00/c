namespace sicredi.db.entities.cadoc4500;
using { sicredi.db.entities.Companies as Companies } from '../config';
using { sicredi.db.entities.aspects.TreeTable as TreeTable } from '../aspects';

entity Estban : TreeTable{
    key company: Association to one Companies;
    key interval: String;
	key exercise: String;
    
    key agency: String;
    key account: String;

    description: String;

    balance: Decimal(20, 2);
}

