// This is an automatically generated file. Please do not change its contents manually!
import * as __ from './../_';

export default class {
  declare static readonly transmitBacen: typeof transmitBacen;
}

export function _CompanyAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class Company extends Base {
    declare name?: string | null
    declare externalCode?: __.Key<string>
    declare cnpj?: string | null
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<Company>;
    declare static readonly elements: __.ElementsOf<Company>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class Company extends _CompanyAspect(__.Entity) {}
Object.defineProperty(Company, 'name', { value: 'Cadoc4500Service.Company' })
Object.defineProperty(Company, 'is_singular', { value: true })
export class Company_ extends Array<Company> {$count?: number}
Object.defineProperty(Company_, 'name', { value: 'Cadoc4500Service.Company' })

export function _EstbanAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class Estban extends Base {
    declare nodeID?: __.Key<string>
    declare hierarchyLevel?: number | null
    declare parentNodeID?: string | null
    declare drillState?: string | null
    declare company?: __.Composition.of<Company> | null
    declare company_externalCode?: string | null
    declare interval?: string | null
    declare exercise?: string | null
    declare agency?: string | null
    declare account?: string | null
    declare description?: string | null
    declare balance?: number | null
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<Estban>;
    declare static readonly elements: __.ElementsOf<Estban>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class Estban extends _EstbanAspect(__.Entity) {}
Object.defineProperty(Estban, 'name', { value: 'Cadoc4500Service.Estban' })
Object.defineProperty(Estban, 'is_singular', { value: true })
export class Estban_ extends Array<Estban> {$count?: number}
Object.defineProperty(Estban_, 'name', { value: 'Cadoc4500Service.Estban' })


export declare const transmitBacen:  {
  // positional
  (companies: Array<string>, exercise: string | null, interval: string | null, user: string | null, remittanceType: string | null): globalThis.Promise< {
  message?: string | null,
  sent?: boolean | null,
  userConfirmation?: boolean | null,
} | null> |  {
  message?: string | null,
  sent?: boolean | null,
  userConfirmation?: boolean | null,
} | null
  // named
  ({companies, exercise, interval, user, remittanceType}: {companies?: Array<string>, exercise?: string | null, interval?: string | null, user?: string | null, remittanceType?: string | null}): globalThis.Promise< {
  message?: string | null,
  sent?: boolean | null,
  userConfirmation?: boolean | null,
} | null> |  {
  message?: string | null,
  sent?: boolean | null,
  userConfirmation?: boolean | null,
} | null
  // metadata (do not use)
  __parameters: {companies?: Array<string>, exercise?: string | null, interval?: string | null, user?: string | null, remittanceType?: string | null}, __returns: globalThis.Promise< {
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