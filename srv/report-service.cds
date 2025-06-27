using sicredi.db.entities as db from '../db/sicredi-db';

using { ConfigService as config } from './config-service';

@path: 'report'
@title: 'ReportService OpenApi Definition'
@Core.LongDescription: 'ReportService'
service ReportService {

	entity Protocols as projection on db.Protocols;

	entity ProtocolDetails as projection on db.ProtocolDetails;

	@cds.persistence.exists
	entity ProtocolDetailsHeader {
		system: String;
		cadoc: String;
		key protocol: String;
		baseDate: Date;
		cnpj: String
	}

	@cds.persistence.exists
	entity ProtocolDetailsHistorical {
		key code: Integer;
		description: String;
		dateHour: DateTime;
		isError: Boolean;
	}

	@cds.persistence.exists
	entity ProtocolDetailsDetails {
		key code: String;
		description: String;
		complement: String;
	}

	@cds.persistence.exists
	entity ProtocolData {
		key header: Composition of one ProtocolDetailsHeader;
		historical: Composition of many ProtocolDetailsHistorical;
		accounts: Composition of many ProtocolDetails;
		details: Composition of many ProtocolDetailsDetails;
	}
	
	@readonly
	entity Companies as projection on config.Companies;
}