namespace sicredi.db.entities;

using { cuid, managed } from '@sap/cds/common';

using {
    sicredi.db.entities.Balances as Balances
} from './balance';

using {
	sicredi.db.entities.Companies as Companies
} from './config';

type Status : String enum {
	GENERATED = 'Gerado';
	REGENERATED = 'Regerado';
	PROCESSING = 'Processando';
	FAILURE = 'Falha';
};

entity Protocols : cuid, managed {
	company: Association to one Companies;

	exercise: String;
	version: String;
	interval: String;
	tipoDoc: String;
	releaseDate: Date;
	bloc: String;

	protocol: String;
	cadoc: String;
	status: String;
	details: Composition of many ProtocolDetails
		on details.protocol = $self;

	bacen: Composition of one ProtocolBacen
		on bacen.protocol = $self;
}

entity ProtocolDetails: Balances, cuid {
	key protocol: Association to one Protocols;
	key ID : UUID;
}

entity ProtocolBacen: ProtocolBacenCurrentStatus, ProtocolBacenDetails {
	key protocol: Association to one Protocols;
}

aspect ProtocolBacenDetails {
	system: String;
	baseDate: Date;
	cnpj: String;
	situation: {
		code: String;
		description: String;
	};
	
	error: Composition of many ProtocolBacenError
		on error.protocol = $self;
}

aspect ProtocolBacenCurrentStatus {
	fileType: String;
	cadoc: String;

	currentStatus: Composition of one ProtocolBacenStatus	
		on currentStatus.protocol = $self;

	issuer: {
		institution: ProtocolBacenInstitution;
		dependency: String;
		operator: String;
	};

	recipient: ProtocolBacenInstitution;
	
	fileSize: String;
	fileNameOrigin: String;
	hash: String;
	transmitionDateTime: DateTime;

	historical: Composition of many ProtocolBacenStatus
		on historical.protocol = $self;
}

type ProtocolBacenInstitution {
	unity: String;
	name: String;
}

entity ProtocolBacenError : cuid {
	code: String;
	description: String;
	complement: String;

	protocol: Association to one ProtocolBacen;
}

entity ProtocolBacenStatus : cuid {

	dateHour: DateTime;
	code: Integer;
	description: String;
	responsableUnity: String;
	responsableOperator: String;
	
	protocol: Association to one ProtocolBacen
}