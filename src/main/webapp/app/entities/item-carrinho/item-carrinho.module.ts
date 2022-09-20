import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ItemCarrinhoComponent } from './list/item-carrinho.component';
import { ItemCarrinhoDetailComponent } from './detail/item-carrinho-detail.component';
import { ItemCarrinhoUpdateComponent } from './update/item-carrinho-update.component';
import { ItemCarrinhoDeleteDialogComponent } from './delete/item-carrinho-delete-dialog.component';
import { ItemCarrinhoRoutingModule } from './route/item-carrinho-routing.module';

@NgModule({
  imports: [SharedModule, ItemCarrinhoRoutingModule],
  declarations: [ItemCarrinhoComponent, ItemCarrinhoDetailComponent, ItemCarrinhoUpdateComponent, ItemCarrinhoDeleteDialogComponent],
})
export class ItemCarrinhoModule {}
