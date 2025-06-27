using ConfigService from './config-service';

@path                : 'cadoc/4500'
@title               : 'Cadoc4500Service OpenApi Definition'
@Core.LongDescription: 'Cadoc4500Service'
@odata
@cds.query.limit     : {
	default: 10000,
	max    : 10000
}
service Cadoc4500Service {
	@cds.persistence.skip
	@readonly
	entity Company as
		projection on ConfigService.Companies {
			name,
			externalCode,
			cnpj
		};
	@cds.persistence.skip
	@readonly
	entity Estban {
		key nodeID         : String;
			hierarchyLevel : Integer;
			parentNodeID   : String;
			drillState     : String;
			company        : Composition of one Company;
			interval       : String;
			exercise       : String;
			agency         : String;
			account        : String;
			description    : String;
			balance        : Decimal(20, 2);
	}

	action transmitBacen(
		companies: array of String,
		exercise: String,
		interval: String,
		user: String,
		remittanceType: String
	) returns {
		message: String;
		sent: Boolean;
		userConfirmation: Boolean;
	};
}
