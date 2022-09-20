import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CarrinhoComponent } from '../list/carrinho.component';
import { CarrinhoDetailComponent } from '../detail/carrinho-detail.component';
import { CarrinhoUpdateComponent } from '../update/carrinho-update.component';
import { CarrinhoRoutingResolveService } from './carrinho-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const carrinhoRoute: Routes = [
  {
    path: '',
    component: CarrinhoComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CarrinhoDetailComponent,
    resolve: {
      carrinho: CarrinhoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CarrinhoUpdateComponent,
    resolve: {
      carrinho: CarrinhoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CarrinhoUpdateComponent,
    resolve: {
      carrinho: CarrinhoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(carrinhoRoute)],
  exports: [RouterModule],
})
export class CarrinhoRoutingModule {}
