// This is an automatically generated file. Please do not change its contents manually!
import * as _sicredi_db_entities_aspects from './../aspects';
import * as _sicredi_db_entities from './..';
import * as __ from './../../../../_';

export function _EstbanAspect<TBase extends new (...args: any[]) => object>(Base: TBase) {
  return class Estban extends _sicredi_db_entities_aspects._TreeTableAspect(Base) {
    declare company?: __.Key<__.Association.to<_sicredi_db_entities.Company>>
    declare company_externalCode?: __.Key<string>
    declare interval?: __.Key<string>
    declare exercise?: __.Key<string>
    declare agency?: __.Key<string>
    declare account?: __.Key<string>
    declare description?: string | null
    declare balance?: number | null
    static override readonly kind: 'entity' | 'type' | 'aspect' = 'entity';
    declare static readonly keys: __.KeysOf<Estban>;
    declare static readonly elements: __.ElementsOf<Estban>;
    declare static readonly actions: typeof _sicredi_db_entities_aspects.TreeTable.actions & globalThis.Record<never, never>;
  };
}
export class Estban extends _EstbanAspect(__.Entity) {}
Object.defineProperty(Estban, 'name', { value: 'sicredi.db.entities.cadoc4500.Estban' })
Object.defineProperty(Estban, 'is_singular', { value: true })
export class Estban_ extends Array<Estban> {$count?: number}
Object.defineProperty(Estban_, 'name', { value: 'sicredi.db.entities.cadoc4500.Estban' })
