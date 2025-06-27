namespace sicredi.db.entities;

using { cuid } from '@sap/cds/common';

using { 
	sicredi.db.entities.Companies as Companies
} from './config';

using {
    sicredi.db.entities.Cadoc as Cadoc
} from './cadoc';

aspect BaseDocument : cuid {
	company: Association to one Companies;
	
    cadoc: Association to one Cadoc;
}

