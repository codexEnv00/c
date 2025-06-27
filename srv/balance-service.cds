using sicredi.db.entities as db from '../db/sicredi-db';
using ConfigService from './config-service';

@path                : 'balance'
@title               : 'BalanceService OpenApi Definition'
@Core.LongDescription: 'BalanceService'
@odata
@cds.query.limit: { default: 10000, max: 10000}
service BalanceService {

	@readonly
	@cds.persistence.skip
	entity Balance {
		key nodeID               : String;
			hierarchyLevel       : Integer;
			parentNodeID         : String;
			drillState           : String;
			company              : String;
			cnpj                 : String;
			cosif                : String;
			razao                : String;
			description          : String;
			cadocBalance         : Decimal(20, 2);
			originalCadocBalance : Decimal(20, 2);
	};

	@readonly
	entity TerminationDocument as projection on db.TerminationDocument;
	
	@readonly
	entity OutsideBalance      as projection on db.OutsideBalance;
	
	@readonly
	entity Company             as projection on ConfigService.Companies;
	

	function status(id: UUID) returns {
		status: String enum {
			ERROR = 'ERROR';
			PROCESSING = 'PROCESSING';
			COMPLETE = 'COMPLETE';
		};
		message: String;
	};

	action loadBalance(
		cadoc: String, exercise: String, version: String, interval: String, blc: String,
		companies: array of String, parentNodeID: Integer
	) returns {
		id: UUID
	};

	action transmitBacen(
		cadoc : String, exercise : String, version : String, interval : String, 
		releaseDate : Date, blc : String,
		companies: array of String, tipoDoc: array of String,
		terminationCompanies: array of String,
		updateDocumentsBacen: Boolean,
		user: String,
		remittanceType: String
	) returns {
		message: String;
		sent: Boolean;
		userConfirmation: Boolean;
	};
}
