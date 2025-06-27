// This is an automatically generated file. Please do not change its contents manually!
import * as __ from './../_';

export default class {
  declare static readonly importAccount: typeof importAccount;
}

export function _TipoDocAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class TipoDoc extends Base {
    declare TipoDoc?: __.Key<string>
    declare DescrTipoDoc?: string | null
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<TipoDoc>;
    declare static readonly elements: __.ElementsOf<TipoDoc>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class TipoDoc extends _TipoDocAspect(__.Entity) {}
Object.defineProperty(TipoDoc, 'name', { value: 'ConfigService.TipoDoc' })
Object.defineProperty(TipoDoc, 'is_singular', { value: true })
export class TipoDoc_ extends Array<TipoDoc> {$count?: number}
Object.defineProperty(TipoDoc_, 'name', { value: 'ConfigService.TipoDoc' })

export function _VersaoAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class Versao extends Base {
    declare Versao?: __.Key<string>
    declare DescrVersao?: string | null
    static readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<Versao>;
    declare static readonly elements: __.ElementsOf<Versao>;
    declare static readonly actions: globalThis.Record<never, never>;
  };
}
export class Versao extends _VersaoAspect(__.Entity) {}
Object.defineProperty(Versao, 'name', { value: 'ConfigService.Versao' })
Object.defineProperty(Versao, 'is_singular', { value: true })
export class Versao_ extends Array<Versao> {$count?: number}
Object.defineProperty(Versao_, 'name', { value: 'ConfigService.Versao' })

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
Object.defineProperty(Company, 'name', { value: 'ConfigService.Companies' })
Object.defineProperty(Company, 'is_singular', { value: true })
export class Companies extends Array<Company> {$count?: number}
Object.defineProperty(Companies, 'name', { value: 'ConfigService.Companies' })


export declare const importAccount:  {
  // positional
  (): void | null
  // named
  ({}: globalThis.Record<never, never>): void | null
  // metadata (do not use)
  __parameters: globalThis.Record<never, never>, __returns: void | null
  kind: 'action'
}