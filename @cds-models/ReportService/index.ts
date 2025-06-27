// This is an automatically generated file. Please do not change its contents manually!
import * as _ from './..';
import * as __ from './../_';

export default class {
}

export function _ProtocolAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class Protocol extends Base {
    declare ID?: __.Key<string>
    declare createdAt?: __.CdsTimestamp | null
    /** Canonical user ID */
    declare createdBy?: _.User | null
    declare modifiedAt?: __.CdsTimestamp | null
    /** Canonical user ID */
    declare modifiedBy?: _.User | null
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
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<Protocol>;
    declare static readonly elements: __.ElementsOf<Protocol>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
/**
* Aspect to capture changes by user and name
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-managed
*/
export class Protocol extends _ProtocolAspect(__.Entity) {}
Object.defineProperty(Protocol, 'name', { value: 'ReportService.Protocols' })
Object.defineProperty(Protocol, 'is_singular', { value: true })
/**
* Aspect to capture changes by user and name
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-managed
*/
export class Protocols extends Array<Protocol> {$count?: number}
Object.defineProperty(Protocols, 'name', { value: 'ReportService.Protocols' })

export function _ProtocolDetailAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class ProtocolDetail extends Base {
    declare razaoAccount?: __.Key<string>
    declare company?: __.Key<__.Association.to<Company>>
    declare company_externalCode?: __.Key<string>
    declare cadoc?: __.Key<string>
    declare version?: __.Key<string>
    declare interval?: __.Key<string>
    declare exercise?: __.Key<string>
    declare cadocBalance?: number | null
    declare description?: string | null
    declare cosifAccount?: __.Key<string>
    declare level?: number | null
    declare parentKey?: string | null
    declare drillState?: string | null
    declare nodeID?: string | null
    declare order?: number | null
    declare ID?: __.Key<string>
    declare protocol?: __.Key<__.Association.to<Protocol>>
    declare protocol_ID?: __.Key<string>
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<ProtocolDetail>;
    declare static readonly elements: __.ElementsOf<ProtocolDetail>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
/**
* Aspect for entities with canonical universal IDs
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-cuid
*/
export class ProtocolDetail extends _ProtocolDetailAspect(__.Entity) {}
Object.defineProperty(ProtocolDetail, 'name', { value: 'ReportService.ProtocolDetails' })
Object.defineProperty(ProtocolDetail, 'is_singular', { value: true })
/**
* Aspect for entities with canonical universal IDs
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-cuid
*/
export class ProtocolDetails extends Array<ProtocolDetail> {$count?: number}
Object.defineProperty(ProtocolDetails, 'name', { value: 'ReportService.ProtocolDetails' })

export function _ProtocolDetailsHeaderAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class ProtocolDetailsHeader extends Base {
    declare system?: string | null
    declare cadoc?: string | null
    declare protocol?: __.Key<string>
    declare baseDate?: __.CdsDate | null
    declare cnpj?: string | null
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<ProtocolDetailsHeader>;
    declare static readonly elements: __.ElementsOf<ProtocolDetailsHeader>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class ProtocolDetailsHeader extends _ProtocolDetailsHeaderAspect(__.Entity) {}
Object.defineProperty(ProtocolDetailsHeader, 'name', { value: 'ReportService.ProtocolDetailsHeader' })
Object.defineProperty(ProtocolDetailsHeader, 'is_singular', { value: true })
export class ProtocolDetailsHeader_ extends Array<ProtocolDetailsHeader> {$count?: number}
Object.defineProperty(ProtocolDetailsHeader_, 'name', { value: 'ReportService.ProtocolDetailsHeader' })

export function _ProtocolDetailsHistoricalAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class ProtocolDetailsHistorical extends Base {
    declare code?: __.Key<number>
    declare description?: string | null
    declare dateHour?: __.CdsDateTime | null
    declare isError?: boolean | null
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<ProtocolDetailsHistorical>;
    declare static readonly elements: __.ElementsOf<ProtocolDetailsHistorical>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class ProtocolDetailsHistorical extends _ProtocolDetailsHistoricalAspect(__.Entity) {}
Object.defineProperty(ProtocolDetailsHistorical, 'name', { value: 'ReportService.ProtocolDetailsHistorical' })
Object.defineProperty(ProtocolDetailsHistorical, 'is_singular', { value: true })
export class ProtocolDetailsHistorical_ extends Array<ProtocolDetailsHistorical> {$count?: number}
Object.defineProperty(ProtocolDetailsHistorical_, 'name', { value: 'ReportService.ProtocolDetailsHistorical' })

export function _ProtocolDetailsDetailAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class ProtocolDetailsDetail extends Base {
    declare code?: __.Key<string>
    declare description?: string | null
    declare complement?: string | null
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<ProtocolDetailsDetail>;
    declare static readonly elements: __.ElementsOf<ProtocolDetailsDetail>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class ProtocolDetailsDetail extends _ProtocolDetailsDetailAspect(__.Entity) {}
Object.defineProperty(ProtocolDetailsDetail, 'name', { value: 'ReportService.ProtocolDetailsDetails' })
Object.defineProperty(ProtocolDetailsDetail, 'is_singular', { value: true })
export class ProtocolDetailsDetails extends Array<ProtocolDetailsDetail> {$count?: number}
Object.defineProperty(ProtocolDetailsDetails, 'name', { value: 'ReportService.ProtocolDetailsDetails' })

export function _ProtocolDataAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class ProtocolData extends Base {
    declare header?: __.Key<__.Composition.of<ProtocolDetailsHeader>>
    declare header_protocol?: __.Key<string>
    declare historical?: __.Composition.of.many<ProtocolDetailsHistorical_>
    declare accounts?: __.Composition.of.many<ProtocolDetails>
    declare details?: __.Composition.of.many<ProtocolDetailsDetails>
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<ProtocolData>;
    declare static readonly elements: __.ElementsOf<ProtocolData>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class ProtocolData extends _ProtocolDataAspect(__.Entity) {}
Object.defineProperty(ProtocolData, 'name', { value: 'ReportService.ProtocolData' })
Object.defineProperty(ProtocolData, 'is_singular', { value: true })
export class ProtocolData_ extends Array<ProtocolData> {$count?: number}
Object.defineProperty(ProtocolData_, 'name', { value: 'ReportService.ProtocolData' })

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
Object.defineProperty(Company, 'name', { value: 'ReportService.Companies' })
Object.defineProperty(Company, 'is_singular', { value: true })
export class Companies extends Array<Company> {$count?: number}
Object.defineProperty(Companies, 'name', { value: 'ReportService.Companies' })

export function _ProtocolBacenAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class ProtocolBacen extends Base {
    declare fileType?: string | null
    declare cadoc?: string | null
    declare currentStatus?: __.Composition.of<ProtocolBacenStatu> | null
    declare issuer_institution_unity?: string | null
    declare issuer_institution_name?: string | null
    declare issuer_dependency?: string | null
    declare issuer_operator?: string | null
    declare recipient_unity?: string | null
    declare recipient_name?: string | null
    declare fileSize?: string | null
    declare fileNameOrigin?: string | null
    declare hash?: string | null
    declare transmitionDateTime?: __.CdsDateTime | null
    declare historical?: __.Composition.of.many<ProtocolBacenStatus>
    declare system?: string | null
    declare baseDate?: __.CdsDate | null
    declare cnpj?: string | null
    declare situation_code?: string | null
    declare situation_description?: string | null
    declare error?: __.Composition.of.many<ProtocolBacenError_>
    declare protocol?: __.Key<__.Association.to<Protocol>>
    declare protocol_ID?: __.Key<string>
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<ProtocolBacen>;
    declare static readonly elements: __.ElementsOf<ProtocolBacen>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class ProtocolBacen extends _ProtocolBacenAspect(__.Entity) {}
Object.defineProperty(ProtocolBacen, 'name', { value: 'ReportService.ProtocolBacen' })
Object.defineProperty(ProtocolBacen, 'is_singular', { value: true })
export class ProtocolBacen_ extends Array<ProtocolBacen> {$count?: number}
Object.defineProperty(ProtocolBacen_, 'name', { value: 'ReportService.ProtocolBacen' })

export function _ProtocolBacenStatuAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class ProtocolBacenStatu extends Base {
    declare ID?: __.Key<string>
    declare dateHour?: __.CdsDateTime | null
    declare code?: number | null
    declare description?: string | null
    declare responsableUnity?: string | null
    declare responsableOperator?: string | null
    declare protocol?: __.Association.to<ProtocolBacen> | null
    declare protocol_protocol_ID?: string | null
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<ProtocolBacenStatu>;
    declare static readonly elements: __.ElementsOf<ProtocolBacenStatu>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
/**
* Aspect for entities with canonical universal IDs
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-cuid
*/
export class ProtocolBacenStatu extends _ProtocolBacenStatuAspect(__.Entity) {}
Object.defineProperty(ProtocolBacenStatu, 'name', { value: 'ReportService.ProtocolBacenStatus' })
Object.defineProperty(ProtocolBacenStatu, 'is_singular', { value: true })
/**
* Aspect for entities with canonical universal IDs
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-cuid
*/
export class ProtocolBacenStatus extends Array<ProtocolBacenStatu> {$count?: number}
Object.defineProperty(ProtocolBacenStatus, 'name', { value: 'ReportService.ProtocolBacenStatus' })

export function _ProtocolBacenErrorAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class ProtocolBacenError extends Base {
    declare ID?: __.Key<string>
    declare code?: string | null
    declare description?: string | null
    declare complement?: string | null
    declare protocol?: __.Association.to<ProtocolBacen> | null
    declare protocol_protocol_ID?: string | null
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<ProtocolBacenError>;
    declare static readonly elements: __.ElementsOf<ProtocolBacenError>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
/**
* Aspect for entities with canonical universal IDs
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-cuid
*/
export class ProtocolBacenError extends _ProtocolBacenErrorAspect(__.Entity) {}
Object.defineProperty(ProtocolBacenError, 'name', { value: 'ReportService.ProtocolBacenError' })
Object.defineProperty(ProtocolBacenError, 'is_singular', { value: true })
/**
* Aspect for entities with canonical universal IDs
* 
* See https://cap.cloud.sap/docs/cds/common#aspect-cuid
*/
export class ProtocolBacenError_ extends Array<ProtocolBacenError> {$count?: number}
Object.defineProperty(ProtocolBacenError_, 'name', { value: 'ReportService.ProtocolBacenError' })
