import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IItemCarrinho } from '../item-carrinho.model';

@Component({
  selector: 'jhi-item-carrinho-detail',
  templateUrl: './item-carrinho-detail.component.html',
})
export class ItemCarrinhoDetailComponent implements OnInit {
  itemCarrinho: IItemCarrinho | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ itemCarrinho }) => {
      this.itemCarrinho = itemCarrinho;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
