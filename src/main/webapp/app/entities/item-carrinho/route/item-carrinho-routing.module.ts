import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ItemCarrinhoComponent } from '../list/item-carrinho.component';
import { ItemCarrinhoDetailComponent } from '../detail/item-carrinho-detail.component';
import { ItemCarrinhoUpdateComponent } from '../update/item-carrinho-update.component';
import { ItemCarrinhoRoutingResolveService } from './item-carrinho-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const itemCarrinhoRoute: Routes = [
  {
    path: '',
    component: ItemCarrinhoComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ItemCarrinhoDetailComponent,
    resolve: {
      itemCarrinho: ItemCarrinhoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ItemCarrinhoUpdateComponent,
    resolve: {
      itemCarrinho: ItemCarrinhoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ItemCarrinhoUpdateComponent,
    resolve: {
      itemCarrinho: ItemCarrinhoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(itemCarrinhoRoute)],
  exports: [RouterModule],
})
export class ItemCarrinhoRoutingModule {}
