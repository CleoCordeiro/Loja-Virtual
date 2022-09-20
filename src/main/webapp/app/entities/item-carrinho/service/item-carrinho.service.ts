import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IItemCarrinho, NewItemCarrinho } from '../item-carrinho.model';

export type PartialUpdateItemCarrinho = Partial<IItemCarrinho> & Pick<IItemCarrinho, 'id'>;

export type EntityResponseType = HttpResponse<IItemCarrinho>;
export type EntityArrayResponseType = HttpResponse<IItemCarrinho[]>;

@Injectable({ providedIn: 'root' })
export class ItemCarrinhoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/item-carrinhos');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/item-carrinhos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(itemCarrinho: NewItemCarrinho): Observable<EntityResponseType> {
    return this.http.post<IItemCarrinho>(this.resourceUrl, itemCarrinho, { observe: 'response' });
  }

  update(itemCarrinho: IItemCarrinho): Observable<EntityResponseType> {
    return this.http.put<IItemCarrinho>(`${this.resourceUrl}/${this.getItemCarrinhoIdentifier(itemCarrinho)}`, itemCarrinho, {
      observe: 'response',
    });
  }

  partialUpdate(itemCarrinho: PartialUpdateItemCarrinho): Observable<EntityResponseType> {
    return this.http.patch<IItemCarrinho>(`${this.resourceUrl}/${this.getItemCarrinhoIdentifier(itemCarrinho)}`, itemCarrinho, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IItemCarrinho>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IItemCarrinho[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IItemCarrinho[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getItemCarrinhoIdentifier(itemCarrinho: Pick<IItemCarrinho, 'id'>): number {
    return itemCarrinho.id;
  }

  compareItemCarrinho(o1: Pick<IItemCarrinho, 'id'> | null, o2: Pick<IItemCarrinho, 'id'> | null): boolean {
    return o1 && o2 ? this.getItemCarrinhoIdentifier(o1) === this.getItemCarrinhoIdentifier(o2) : o1 === o2;
  }

  addItemCarrinhoToCollectionIfMissing<Type extends Pick<IItemCarrinho, 'id'>>(
    itemCarrinhoCollection: Type[],
    ...itemCarrinhosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const itemCarrinhos: Type[] = itemCarrinhosToCheck.filter(isPresent);
    if (itemCarrinhos.length > 0) {
      const itemCarrinhoCollectionIdentifiers = itemCarrinhoCollection.map(
        itemCarrinhoItem => this.getItemCarrinhoIdentifier(itemCarrinhoItem)!
      );
      const itemCarrinhosToAdd = itemCarrinhos.filter(itemCarrinhoItem => {
        const itemCarrinhoIdentifier = this.getItemCarrinhoIdentifier(itemCarrinhoItem);
        if (itemCarrinhoCollectionIdentifiers.includes(itemCarrinhoIdentifier)) {
          return false;
        }
        itemCarrinhoCollectionIdentifiers.push(itemCarrinhoIdentifier);
        return true;
      });
      return [...itemCarrinhosToAdd, ...itemCarrinhoCollection];
    }
    return itemCarrinhoCollection;
  }
}
