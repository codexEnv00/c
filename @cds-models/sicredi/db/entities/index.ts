// This is an automatically generated file. Please do not change its contents manually!
import * as __ from './../../../_';
import * as _ from './../../..';

// enum
export const Status = {
  GENERATED: "Gerado",
  REGENERATED: "Regerado",
  PROCESSING: "Processando",
  FAILURE: "Falha",
} as const;
export type Status = "Gerado" | "Regerado" | "Processando" | "Falha"

// the following represents the CDS aspect 'BalanceBase'
export function _BalanceBaseAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class BalanceBase extends Base {
    declare razaoAccount?: __.Key<string>
    declare company?: __.Key<__.Association.to<Company>>
    declare company_externalCode?: __.Key<string>
    declare cadoc?: __.Key<string>
    declare version?: __.Key<string>
    declare interval?: __.Key<string>
    declare exercise?: __.Key<string>
    declare cadocBalance?: number | null
    declare description?: string | null
    static readonly kind: 'entity' | 'type' | 'aspect' = 'aspect';
    declare static readonly keys: __.KeysOf<BalanceBase>;
    declare static readonly elements: __.ElementsOf<BalanceBase>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class BalanceBase extends _BalanceBaseAspect(__.Entity) {}
export class BalanceBase_ extends Array<BalanceBase> {$count?: number}
Object.defineProperty(BalanceBase_, 'name', { value: 'sicredi.db.entities.BalanceBase' })
// the following represents the CDS aspect 'ProtocolBacenDetails'
export function _ProtocolBacenDetailAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class ProtocolBacenDetail extends Base {
    declare system?: string | null
    declare baseDate?: __.CdsDate | null
    declare cnpj?: string | null
    declare situation_code?: string | null
    declare situation_description?: string | null
    declare error?: __.Composition.of.many<ProtocolBacenError_>
    static readonly kind: 'entity' | 'type' | 'aspect' = 'aspect';
    declare static readonly keys: __.KeysOf<ProtocolBacenDetail>;
    declare static readonly elements: __.ElementsOf<ProtocolBacenDetail>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class ProtocolBacenDetail extends _ProtocolBacenDetailAspect(__.Entity) {}
export class ProtocolBacenDetails extends Array<ProtocolBacenDetail> {$count?: number}
Object.defineProperty(ProtocolBacenDetails, 'name', { value: 'sicredi.db.entities.ProtocolBacenDetails' })
// the following represents the CDS aspect 'ProtocolBacenCurrentStatus'
export function _ProtocolBacenCurrentStatuAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class ProtocolBacenCurrentStatu extends Base {
    declare fileType?: string | null
    declare cadoc?: string | null
    declare currentStatus?: __.Composition.of<ProtocolBacenStatu> | null
    declare issuer_institution?: ProtocolBacenInstitution | null
    declare issuer_dependency?: string | null
    declare issuer_operator?: string | null
    declare recipient?: ProtocolBacenInstitution | null
    declare fileSize?: string | null
    declare fileNameOrigin?: string | null
    declare hash?: string | null
    declare transmitionDateTime?: __.CdsDateTime | null
    declare historical?: __.Composition.of.many<ProtocolBacenStatus>
    static readonly kind: 'entity' | 'type' | 'aspect' = 'aspect';
    declare static readonly keys: __.KeysOf<ProtocolBacenCurrentStatu>;
    declare static readonly elements: __.ElementsOf<ProtocolBacenCurrentStatu>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class ProtocolBacenCurrentStatu extends _ProtocolBacenCurrentStatuAspect(__.Entity) {}
export class ProtocolBacenCurrentStatus extends Array<ProtocolBacenCurrentStatu> {$count?: number}
Object.defineProperty(ProtocolBacenCurrentStatus, 'name', { value: 'sicredi.db.entities.ProtocolBacenCurrentStatus' })
export function _BalanceAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class Balance extends _BalanceBaseAspect(Base) {
    declare cosifAccount?: __.Key<string>
    declare level?: number | null
    declare parentKey?: string | null
    declare drillState?: string | null
    declare nodeID?: string | null
    declare order?: number | null
    static override readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<Balance> & typeof BalanceBase.keys;
    declare static readonly elements: __.ElementsOf<Balance>;
    declare static readonly actions: typeof BalanceBase.actions & globalThis.Record<never, never>;
  };
}
export class Balance extends _BalanceAspect(__.Entity) {}
Object.defineProperty(Balance, 'name', { value: 'sicredi.db.entities.Balances' })
Object.defineProperty(Balance, 'is_singular', { value: true })
export class Balances extends Array<Balance> {$count?: number}
Object.defineProperty(Balances, 'name', { value: 'sicredi.db.entities.Balances' })

export function _OutsideBalanceAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class OutsideBalance extends _BalanceBaseAspect(Base) {
    static override readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<OutsideBalance> & typeof BalanceBase.keys;
    declare static readonly elements: __.ElementsOf<OutsideBalance>;
    declare static readonly actions: typeof BalanceBase.actions & globalThis.Record<never, never>;
  };
}
export class OutsideBalance extends _OutsideBalanceAspect(__.Entity) {}
Object.defineProperty(OutsideBalance, 'name', { value: 'sicredi.db.entities.OutsideBalance' })
Object.defineProperty(OutsideBalance, 'is_singular', { value: true })
export class OutsideBalance_ extends Array<OutsideBalance> {$count?: number}
Object.defineProperty(OutsideBalance_, 'name', { value: 'sicredi.db.entities.OutsideBalance' })

export function _CadocAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class Cadoc extends _._cuidAspect(Base) {
    declare company?: __.Key<__.Association.to<Company>>
    declare company_externalCode?: __.Key<string>
    declare version?: __.Key<string>
    declare interval?: __.Key<string>
    declare exercise?: __.Key<string>
    declare cadoc?: __.Key<string>
    static override readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<Cadoc> & typeof _.cuid.keys;
    declare static readonly elements: __.ElementsOf<Cadoc>;
    declare static readonly actions: typeof _.cuid.actions & globalThis.Record<never, never>;
  };
}
/**
* Aspect for entities with canonical universal IDs
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-cuid
*/
export class Cadoc extends _CadocAspect(__.Entity) {}
Object.defineProperty(Cadoc, 'name', { value: 'sicredi.db.entities.Cadoc' })
Object.defineProperty(Cadoc, 'is_singular', { value: true })
/**
* Aspect for entities with canonical universal IDs
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-cuid
*/
export class Cadoc_ extends Array<Cadoc> {$count?: number}
Object.defineProperty(Cadoc_, 'name', { value: 'sicredi.db.entities.Cadoc' })

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
Object.defineProperty(Company, 'name', { value: 'sicredi.db.entities.Companies' })
Object.defineProperty(Company, 'is_singular', { value: true })
export class Companies extends Array<Company> {$count?: number}
Object.defineProperty(Companies, 'name', { value: 'sicredi.db.entities.Companies' })

export function _CompanyAuthenticatorAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class CompanyAuthenticator extends Base {
    declare company?: __.Key<__.Composition.of<Company>>
    declare company_externalCode?: __.Key<string>
    declare user?: string | null
    declare password?: string | null
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<CompanyAuthenticator>;
    declare static readonly elements: __.ElementsOf<CompanyAuthenticator>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class CompanyAuthenticator extends _CompanyAuthenticatorAspect(__.Entity) {}
Object.defineProperty(CompanyAuthenticator, 'name', { value: 'sicredi.db.entities.CompanyAuthenticators' })
Object.defineProperty(CompanyAuthenticator, 'is_singular', { value: true })
export class CompanyAuthenticators extends Array<CompanyAuthenticator> {$count?: number}
Object.defineProperty(CompanyAuthenticators, 'name', { value: 'sicredi.db.entities.CompanyAuthenticators' })

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
Object.defineProperty(TerminationDocument, 'name', { value: 'sicredi.db.entities.TerminationDocument' })
Object.defineProperty(TerminationDocument, 'is_singular', { value: true })
export class TerminationDocument_ extends Array<TerminationDocument> {$count?: number}
Object.defineProperty(TerminationDocument_, 'name', { value: 'sicredi.db.entities.TerminationDocument' })

export function _ProtocolAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class Protocol extends _._cuidAspect(_._managedAspect(Base)) {
    declare company?: __.Association.to<Company> | null
    declare company_externalCode?: string | null
    declare exercise?: string | null
    declare version?: string | null
    declare interval?: string | null
    declare tipoDoc?: string | null
    declare releaseDate?: __.CdsDate | null
    declare bloc?: string | null
    declare protocol?: string | null
    declare cadoc?: string | null
    declare status?: string | null
    declare details?: __.Composition.of.many<ProtocolDetails>
    declare bacen?: __.Composition.of<ProtocolBacen> | null
    static override readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<Protocol> & typeof _.cuid.keys;
    declare static readonly elements: __.ElementsOf<Protocol>;
    declare static readonly actions: typeof _.managed.actions & typeof _.cuid.actions & globalThis.Record<never, never>;
  };
}
/**
* Aspect to capture changes by user and name
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-managed
*/
export class Protocol extends _ProtocolAspect(__.Entity) {}
Object.defineProperty(Protocol, 'name', { value: 'sicredi.db.entities.Protocols' })
Object.defineProperty(Protocol, 'is_singular', { value: true })
/**
* Aspect to capture changes by user and name
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-managed
*/
export class Protocols extends Array<Protocol> {$count?: number}
Object.defineProperty(Protocols, 'name', { value: 'sicredi.db.entities.Protocols' })

export function _ProtocolDetailAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class ProtocolDetail extends _BalanceAspect(_._cuidAspect(Base)) {
    declare protocol?: __.Key<__.Association.to<Protocol>>
    declare protocol_ID?: __.Key<string>
    static override readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<ProtocolDetail> & typeof _.cuid.keys & typeof Balance.keys;
    declare static readonly elements: __.ElementsOf<ProtocolDetail>;
    declare static readonly actions: typeof _.cuid.actions & typeof Balance.actions & globalThis.Record<never, never>;
  };
}
/**
* Aspect for entities with canonical universal IDs
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-cuid
*/
export class ProtocolDetail extends _ProtocolDetailAspect(__.Entity) {}
Object.defineProperty(ProtocolDetail, 'name', { value: 'sicredi.db.entities.ProtocolDetails' })
Object.defineProperty(ProtocolDetail, 'is_singular', { value: true })
/**
* Aspect for entities with canonical universal IDs
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-cuid
*/
export class ProtocolDetails extends Array<ProtocolDetail> {$count?: number}
Object.defineProperty(ProtocolDetails, 'name', { value: 'sicredi.db.entities.ProtocolDetails' })

export function _ProtocolBacenAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class ProtocolBacen extends _ProtocolBacenCurrentStatuAspect(_ProtocolBacenDetailAspect(Base)) {
    declare protocol?: __.Key<__.Association.to<Protocol>>
    declare protocol_ID?: __.Key<string>
    static override readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<ProtocolBacen>;
    declare static readonly elements: __.ElementsOf<ProtocolBacen>;
    declare static readonly actions: typeof ProtocolBacenDetail.actions & typeof ProtocolBacenCurrentStatu.actions & globalThis.Record<never, never>;
  };
}
export class ProtocolBacen extends _ProtocolBacenAspect(__.Entity) {}
Object.defineProperty(ProtocolBacen, 'name', { value: 'sicredi.db.entities.ProtocolBacen' })
Object.defineProperty(ProtocolBacen, 'is_singular', { value: true })
export class ProtocolBacen_ extends Array<ProtocolBacen> {$count?: number}
Object.defineProperty(ProtocolBacen_, 'name', { value: 'sicredi.db.entities.ProtocolBacen' })

export function _ProtocolBacenInstitutionAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class ProtocolBacenInstitution extends Base {
    unity?: string | null
    name?: string | null
    static readonly kind: 'entity' | 'type' | 'aspect' = 'type';
    declare static readonly keys: __.KeysOf<ProtocolBacenInstitution>;
    declare static readonly elements: __.ElementsOf<ProtocolBacenInstitution>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class ProtocolBacenInstitution extends _ProtocolBacenInstitutionAspect(__.Entity) {}
Object.defineProperty(ProtocolBacenInstitution, 'name', { value: 'sicredi.db.entities.ProtocolBacenInstitution' })
Object.defineProperty(ProtocolBacenInstitution, 'is_singular', { value: true })

export function _ProtocolBacenErrorAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class ProtocolBacenError extends _._cuidAspect(Base) {
    declare code?: string | null
    declare description?: string | null
    declare complement?: string | null
    declare protocol?: __.Association.to<ProtocolBacen> | null
    declare protocol_protocol_ID?: string | null
    static override readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<ProtocolBacenError> & typeof _.cuid.keys;
    declare static readonly elements: __.ElementsOf<ProtocolBacenError>;
    declare static readonly actions: typeof _.cuid.actions & globalThis.Record<never, never>;
  };
}
/**
* Aspect for entities with canonical universal IDs
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-cuid
*/
export class ProtocolBacenError extends _ProtocolBacenErrorAspect(__.Entity) {}
Object.defineProperty(ProtocolBacenError, 'name', { value: 'sicredi.db.entities.ProtocolBacenError' })
Object.defineProperty(ProtocolBacenError, 'is_singular', { value: true })
/**
* Aspect for entities with canonical universal IDs
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-cuid
*/
export class ProtocolBacenError_ extends Array<ProtocolBacenError> {$count?: number}
Object.defineProperty(ProtocolBacenError_, 'name', { value: 'sicredi.db.entities.ProtocolBacenError' })

export function _ProtocolBacenStatuAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class ProtocolBacenStatu extends _._cuidAspect(Base) {
    declare dateHour?: __.CdsDateTime | null
    declare code?: number | null
    declare description?: string | null
    declare responsableUnity?: string | null
    declare responsableOperator?: string | null
    declare protocol?: __.Association.to<ProtocolBacen> | null
    declare protocol_protocol_ID?: string | null
    static override readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<ProtocolBacenStatu> & typeof _.cuid.keys;
    declare static readonly elements: __.ElementsOf<ProtocolBacenStatu>;
    declare static readonly actions: typeof _.cuid.actions & globalThis.Record<never, never>;
  };
}
/**
* Aspect for entities with canonical universal IDs
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-cuid
*/
export class ProtocolBacenStatu extends _ProtocolBacenStatuAspect(__.Entity) {}
Object.defineProperty(ProtocolBacenStatu, 'name', { value: 'sicredi.db.entities.ProtocolBacenStatus' })
Object.defineProperty(ProtocolBacenStatu, 'is_singular', { value: true })
/**
* Aspect for entities with canonical universal IDs
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-cuid
*/
export class ProtocolBacenStatus extends Array<ProtocolBacenStatu> {$count?: number}
Object.defineProperty(ProtocolBacenStatus, 'name', { value: 'sicredi.db.entities.ProtocolBacenStatus' })
