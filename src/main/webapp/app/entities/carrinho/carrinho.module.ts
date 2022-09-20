import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CarrinhoComponent } from './list/carrinho.component';
import { CarrinhoDetailComponent } from './detail/carrinho-detail.component';
import { CarrinhoUpdateComponent } from './update/carrinho-update.component';
import { CarrinhoDeleteDialogComponent } from './delete/carrinho-delete-dialog.component';
import { CarrinhoRoutingModule } from './route/carrinho-routing.module';

@NgModule({
  imports: [SharedModule, CarrinhoRoutingModule],
  declarations: [CarrinhoComponent, CarrinhoDetailComponent, CarrinhoUpdateComponent, CarrinhoDeleteDialogComponent],
})
export class CarrinhoModule {}
