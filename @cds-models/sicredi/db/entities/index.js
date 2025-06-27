// This is an automatically generated file. Please do not change its contents manually!
const cds = require('@sap/cds')
const csn = cds.entities('sicredi.db.entities')
// Balances
module.exports.Balance = { is_singular: true, __proto__: csn.Balances }
module.exports.Balances = csn.Balances
// OutsideBalance
module.exports.OutsideBalance = { is_singular: true, __proto__: csn.OutsideBalance }
module.exports.OutsideBalance_ = csn.OutsideBalance
// Cadoc
module.exports.Cadoc = { is_singular: true, __proto__: csn.Cadoc }
module.exports.Cadoc_ = csn.Cadoc
// Companies
module.exports.Company = { is_singular: true, __proto__: csn.Companies }
module.exports.Companies = csn.Companies
// CompanyAuthenticators
module.exports.CompanyAuthenticator = { is_singular: true, __proto__: csn.CompanyAuthenticators }
module.exports.CompanyAuthenticators = csn.CompanyAuthenticators
// TerminationDocument
module.exports.TerminationDocument = { is_singular: true, __proto__: csn.TerminationDocument }
module.exports.TerminationDocument_ = csn.TerminationDocument
// Protocols
module.exports.Protocol = { is_singular: true, __proto__: csn.Protocols }
module.exports.Protocols = csn.Protocols
// ProtocolDetails
module.exports.ProtocolDetail = { is_singular: true, __proto__: csn.ProtocolDetails }
module.exports.ProtocolDetails = csn.ProtocolDetails
// ProtocolBacen
module.exports.ProtocolBacen = { is_singular: true, __proto__: csn.ProtocolBacen }
module.exports.ProtocolBacen_ = csn.ProtocolBacen
// ProtocolBacenInstitution
module.exports.ProtocolBacenInstitution = { is_singular: true, __proto__: csn.ProtocolBacenInstitution }
// ProtocolBacenError
module.exports.ProtocolBacenError = { is_singular: true, __proto__: csn.ProtocolBacenError }
module.exports.ProtocolBacenError_ = csn.ProtocolBacenError
// ProtocolBacenStatus
module.exports.ProtocolBacenStatu = { is_singular: true, __proto__: csn.ProtocolBacenStatus }
module.exports.ProtocolBacenStatus = csn.ProtocolBacenStatus
// events
// actions
// enums
module.exports.Status ??= { GENERATED: "Gerado", REGENERATED: "Regerado", PROCESSING: "Processando", FAILURE: "Falha" }
