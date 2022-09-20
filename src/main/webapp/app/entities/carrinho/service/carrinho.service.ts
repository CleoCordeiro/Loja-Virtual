import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ICarrinho, NewCarrinho } from '../carrinho.model';

export type PartialUpdateCarrinho = Partial<ICarrinho> & Pick<ICarrinho, 'id'>;

type RestOf<T extends ICarrinho | NewCarrinho> = Omit<T, 'dataCriacao' | 'dataAlteracao'> & {
  dataCriacao?: string | null;
  dataAlteracao?: string | null;
};

export type RestCarrinho = RestOf<ICarrinho>;

export type NewRestCarrinho = RestOf<NewCarrinho>;

export type PartialUpdateRestCarrinho = RestOf<PartialUpdateCarrinho>;

export type EntityResponseType = HttpResponse<ICarrinho>;
export type EntityArrayResponseType = HttpResponse<ICarrinho[]>;

@Injectable({ providedIn: 'root' })
export class CarrinhoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/carrinhos');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/carrinhos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(carrinho: NewCarrinho): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(carrinho);
    return this.http
      .post<RestCarrinho>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(carrinho: ICarrinho): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(carrinho);
    return this.http
      .put<RestCarrinho>(`${this.resourceUrl}/${this.getCarrinhoIdentifier(carrinho)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(carrinho: PartialUpdateCarrinho): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(carrinho);
    return this.http
      .patch<RestCarrinho>(`${this.resourceUrl}/${this.getCarrinhoIdentifier(carrinho)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCarrinho>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCarrinho[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCarrinho[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getCarrinhoIdentifier(carrinho: Pick<ICarrinho, 'id'>): number {
    return carrinho.id;
  }

  compareCarrinho(o1: Pick<ICarrinho, 'id'> | null, o2: Pick<ICarrinho, 'id'> | null): boolean {
    return o1 && o2 ? this.getCarrinhoIdentifier(o1) === this.getCarrinhoIdentifier(o2) : o1 === o2;
  }

  addCarrinhoToCollectionIfMissing<Type extends Pick<ICarrinho, 'id'>>(
    carrinhoCollection: Type[],
    ...carrinhosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const carrinhos: Type[] = carrinhosToCheck.filter(isPresent);
    if (carrinhos.length > 0) {
      const carrinhoCollectionIdentifiers = carrinhoCollection.map(carrinhoItem => this.getCarrinhoIdentifier(carrinhoItem)!);
      const carrinhosToAdd = carrinhos.filter(carrinhoItem => {
        const carrinhoIdentifier = this.getCarrinhoIdentifier(carrinhoItem);
        if (carrinhoCollectionIdentifiers.includes(carrinhoIdentifier)) {
          return false;
        }
        carrinhoCollectionIdentifiers.push(carrinhoIdentifier);
        return true;
      });
      return [...carrinhosToAdd, ...carrinhoCollection];
    }
    return carrinhoCollection;
  }

  protected convertDateFromClient<T extends ICarrinho | NewCarrinho | PartialUpdateCarrinho>(carrinho: T): RestOf<T> {
    return {
      ...carrinho,
      dataCriacao: carrinho.dataCriacao?.format(DATE_FORMAT) ?? null,
      dataAlteracao: carrinho.dataAlteracao?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restCarrinho: RestCarrinho): ICarrinho {
    return {
      ...restCarrinho,
      dataCriacao: restCarrinho.dataCriacao ? dayjs(restCarrinho.dataCriacao) : undefined,
      dataAlteracao: restCarrinho.dataAlteracao ? dayjs(restCarrinho.dataAlteracao) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCarrinho>): HttpResponse<ICarrinho> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCarrinho[]>): HttpResponse<ICarrinho[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
