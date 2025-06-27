namespace sicredi.db.entities;

using { sicredi.db.entities.Companies as Companies } from './config';

entity Balances : BalanceBase {
	key cosifAccount: String;

	level: Integer;
	parentKey: String;
	drillState: String;
	nodeID: String;
	
	order: Integer;
}

entity OutsideBalance: BalanceBase {
};

aspect BalanceBase {
	key razaoAccount: String;
	key company: Association to one Companies;
	key cadoc: String;
	key version: String;
	key interval: String;
	key exercise: String;

	cadocBalance: Decimal(20, 2);
	description: String;
}
