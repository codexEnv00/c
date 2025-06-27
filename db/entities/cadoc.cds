namespace sicredi.db.entities;

using { cuid } from '@sap/cds/common';

using { 
	sicredi.db.entities.Companies as Companies
} from './config';

entity Cadoc : cuid {
    key company: Association to one Companies;
    key version: String;
    key interval: String;
    key exercise: String;

    key cadoc: String;
}

