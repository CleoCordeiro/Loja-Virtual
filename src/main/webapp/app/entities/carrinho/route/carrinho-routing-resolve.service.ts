import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICarrinho } from '../carrinho.model';
import { CarrinhoService } from '../service/carrinho.service';

@Injectable({ providedIn: 'root' })
export class CarrinhoRoutingResolveService implements Resolve<ICarrinho | null> {
  constructor(protected service: CarrinhoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICarrinho | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((carrinho: HttpResponse<ICarrinho>) => {
          if (carrinho.body) {
            return of(carrinho.body);
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
