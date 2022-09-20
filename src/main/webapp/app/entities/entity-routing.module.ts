import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'usuario',
        data: { pageTitle: 'lojaApp.usuario.home.title' },
        loadChildren: () => import('./usuario/usuario.module').then(m => m.UsuarioModule),
      },
      {
        path: 'endereco',
        data: { pageTitle: 'lojaApp.endereco.home.title' },
        loadChildren: () => import('./endereco/endereco.module').then(m => m.EnderecoModule),
      },
      {
        path: 'produto',
        data: { pageTitle: 'lojaApp.produto.home.title' },
        loadChildren: () => import('./produto/produto.module').then(m => m.ProdutoModule),
      },
      {
        path: 'carrinho',
        data: { pageTitle: 'lojaApp.carrinho.home.title' },
        loadChildren: () => import('./carrinho/carrinho.module').then(m => m.CarrinhoModule),
      },
      {
        path: 'item-carrinho',
        data: { pageTitle: 'lojaApp.itemCarrinho.home.title' },
        loadChildren: () => import('./item-carrinho/item-carrinho.module').then(m => m.ItemCarrinhoModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
