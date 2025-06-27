// This is an automatically generated file. Please do not change its contents manually!
const cds = require('@sap/cds')
const csn = cds.entities('BalanceService')
// service
const BalanceService = { name: 'BalanceService' }
module.exports = BalanceService
module.exports.BalanceService = BalanceService
// Balance
module.exports.Balance = { is_singular: true, __proto__: csn.Balance }
module.exports.Balance_ = csn.Balance
// TerminationDocument
module.exports.TerminationDocument = { is_singular: true, __proto__: csn.TerminationDocument }
module.exports.TerminationDocument_ = csn.TerminationDocument
// OutsideBalance
module.exports.OutsideBalance = { is_singular: true, __proto__: csn.OutsideBalance }
module.exports.OutsideBalance_ = csn.OutsideBalance
// Company
module.exports.Company = { is_singular: true, __proto__: csn.Company }
module.exports.Company_ = csn.Company
// events
// actions
module.exports.status = 'status'
module.exports.loadBalance = 'loadBalance'
module.exports.transmitBacen = 'transmitBacen'
// enums
