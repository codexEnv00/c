// This is an automatically generated file. Please do not change its contents manually!
import * as __ from './../_';

export default class {
  declare static readonly status: typeof status;
  declare static readonly loadBalance: typeof loadBalance;
  declare static readonly transmitBacen: typeof transmitBacen;
}

export function _BalanceAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class Balance extends Base {
    declare nodeID?: __.Key<string>
    declare hierarchyLevel?: number | null
    declare parentNodeID?: string | null
    declare drillState?: string | null
    declare company?: string | null
    declare cnpj?: string | null
    declare cosif?: string | null
    declare razao?: string | null
    declare description?: string | null
    declare cadocBalance?: number | null
    declare originalCadocBalance?: number | null
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<Balance>;
    declare static readonly elements: __.ElementsOf<Balance>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class Balance extends _BalanceAspect(__.Entity) {}
Object.defineProperty(Balance, 'name', { value: 'BalanceService.Balance' })
Object.defineProperty(Balance, 'is_singular', { value: true })
export class Balance_ extends Array<Balance> {$count?: number}
Object.defineProperty(Balance_, 'name', { value: 'BalanceService.Balance' })

export function _TerminationDocumentAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class TerminationDocument extends Base {
    declare company?: __.Key<__.Association.to<Company>>
    declare company_externalCode?: __.Key<string>
    declare document?: __.Key<string>
    declare exercise?: __.Key<string>
    declare interval?: __.Key<string>
    declare item?: __.Key<number>
    declare cadoc?: __.Key<string>
    declare tpDoc?: __.Key<string>
    declare dtLanc?: __.Key<__.CdsDate>
    declare amount?: number | null
    declare razao?: string | null
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<TerminationDocument>;
    declare static readonly elements: __.ElementsOf<TerminationDocument>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class TerminationDocument extends _TerminationDocumentAspect(__.Entity) {}
Object.defineProperty(TerminationDocument, 'name', { value: 'BalanceService.TerminationDocument' })
Object.defineProperty(TerminationDocument, 'is_singular', { value: true })
export class TerminationDocument_ extends Array<TerminationDocument> {$count?: number}
Object.defineProperty(TerminationDocument_, 'name', { value: 'BalanceService.TerminationDocument' })

export function _OutsideBalanceAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class OutsideBalance extends Base {
    declare razaoAccount?: __.Key<string>
    declare company?: __.Key<__.Association.to<Company>>
    declare company_externalCode?: __.Key<string>
    declare cadoc?: __.Key<string>
    declare version?: __.Key<string>
    declare interval?: __.Key<string>
    declare exercise?: __.Key<string>
    declare cadocBalance?: number | null
    declare description?: string | null
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<OutsideBalance>;
    declare static readonly elements: __.ElementsOf<OutsideBalance>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class OutsideBalance extends _OutsideBalanceAspect(__.Entity) {}
Object.defineProperty(OutsideBalance, 'name', { value: 'BalanceService.OutsideBalance' })
Object.defineProperty(OutsideBalance, 'is_singular', { value: true })
export class OutsideBalance_ extends Array<OutsideBalance> {$count?: number}
Object.defineProperty(OutsideBalance_, 'name', { value: 'BalanceService.OutsideBalance' })

export function _CompanyAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class Company extends Base {
    declare name?: string | null
    declare externalCode?: __.Key<string>
    declare cnpj?: string | null
    declare visibility?: boolean | null
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<Company>;
    declare static readonly elements: __.ElementsOf<Company>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class Company extends _CompanyAspect(__.Entity) {}
Object.defineProperty(Company, 'name', { value: 'BalanceService.Company' })
Object.defineProperty(Company, 'is_singular', { value: true })
export class Company_ extends Array<Company> {$count?: number}
Object.defineProperty(Company_, 'name', { value: 'BalanceService.Company' })


export declare const status:  {
  // positional
  (id: string | null): globalThis.Promise< {
  status?: string | null,
  message?: string | null,
} | null> |  {
  status?: string | null,
  message?: string | null,
} | null
  // named
  ({id}: {id?: string | null}): globalThis.Promise< {
  status?: string | null,
  message?: string | null,
} | null> |  {
  status?: string | null,
  message?: string | null,
} | null
  // metadata (do not use)
  __parameters: {id?: string | null}, __returns: globalThis.Promise< {
  status?: string | null,
  message?: string | null,
} | null> |  {
  status?: string | null,
  message?: string | null,
} | null
  kind: 'function'
}

export declare const loadBalance:  {
  // positional
  (cadoc: string | null, exercise: string | null, version: string | null, interval: string | null, blc: string | null, companies: Array<string>, parentNodeID: number | null): globalThis.Promise< {
  id?: string | null,
} | null> |  {
  id?: string | null,
} | null
  // named
  ({cadoc, exercise, version, interval, blc, companies, parentNodeID}: {cadoc?: string | null, exercise?: string | null, version?: string | null, interval?: string | null, blc?: string | null, companies?: Array<string>, parentNodeID?: number | null}): globalThis.Promise< {
  id?: string | null,
} | null> |  {
  id?: string | null,
} | null
  // metadata (do not use)
  __parameters: {cadoc?: string | null, exercise?: string | null, version?: string | null, interval?: string | null, blc?: string | null, companies?: Array<string>, parentNodeID?: number | null}, __returns: globalThis.Promise< {
  id?: string | null,
} | null> |  {
  id?: string | null,
} | null
  kind: 'action'
}

export declare const transmitBacen:  {
  // positional
  (cadoc: string | null, exercise: string | null, version: string | null, interval: string | null, releaseDate: __.CdsDate | null, blc: string | null, companies: Array<string>, tipoDoc: Array<string>, terminationCompanies: Array<string>, updateDocumentsBacen: boolean | null, user: string | null, remittanceType: string | null): globalThis.Promise< {
  message?: string | null,
  sent?: boolean | null,
  userConfirmation?: boolean | null,
} | null> |  {
  message?: string | null,
  sent?: boolean | null,
  userConfirmation?: boolean | null,
} | null
  // named
  ({cadoc, exercise, version, interval, releaseDate, blc, companies, tipoDoc, terminationCompanies, updateDocumentsBacen, user, remittanceType}: {cadoc?: string | null, exercise?: string | null, version?: string | null, interval?: string | null, releaseDate?: __.CdsDate | null, blc?: string | null, companies?: Array<string>, tipoDoc?: Array<string>, terminationCompanies?: Array<string>, updateDocumentsBacen?: boolean | null, user?: string | null, remittanceType?: string | null}): globalThis.Promise< {
  message?: string | null,
  sent?: boolean | null,
  userConfirmation?: boolean | null,
} | null> |  {
  message?: string | null,
  sent?: boolean | null,
  userConfirmation?: boolean | null,
} | null
  // metadata (do not use)
  __parameters: {cadoc?: string | null, exercise?: string | null, version?: string | null, interval?: string | null, releaseDate?: __.CdsDate | null, blc?: string | null, companies?: Array<string>, tipoDoc?: Array<string>, terminationCompanies?: Array<string>, updateDocumentsBacen?: boolean | null, user?: string | null, remittanceType?: string | null}, __returns: globalThis.Promise< {
  message?: string | null,
  sent?: boolean | null,
  userConfirmation?: boolean | null,
} | null> |  {
  message?: string | null,
  sent?: boolean | null,
  userConfirmation?: boolean | null,
} | null
  kind: 'action'
}