import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IItemCarrinho } from '../item-carrinho.model';
import { ItemCarrinhoService } from '../service/item-carrinho.service';

@Injectable({ providedIn: 'root' })
export class ItemCarrinhoRoutingResolveService implements Resolve<IItemCarrinho | null> {
  constructor(protected service: ItemCarrinhoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IItemCarrinho | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((itemCarrinho: HttpResponse<IItemCarrinho>) => {
          if (itemCarrinho.body) {
            return of(itemCarrinho.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
