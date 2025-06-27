namespace sicredi.db.entities.aspects;

aspect TreeTable {
    level: Integer;
	parentKey: String;
	drillState: String;
	nodeID: String;
	
	order: Integer;
}