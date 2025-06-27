using sicredi.db.entities as db from '../db/sicredi-db';

@path: 'config'
@title: 'ConfigService OpenApi Definition'
@Core.LongDescription: 'ConfigService'
service ConfigService {
	
	@readonly
	entity TipoDoc {
		key TipoDoc: String;
		DescrTipoDoc: String;
	}

	@readonly
	entity Versao {
		key Versao: String;
		DescrVersao: String;
	}

	@Capabilities.UpdateRestrictions.DeltaUpdateSupported
	entity Companies as projection on db.Companies;
	
	action importAccount();
}