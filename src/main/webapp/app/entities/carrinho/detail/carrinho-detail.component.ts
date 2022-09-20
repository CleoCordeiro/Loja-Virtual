import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICarrinho } from '../carrinho.model';

@Component({
  selector: 'jhi-carrinho-detail',
  templateUrl: './carrinho-detail.component.html',
})
export class CarrinhoDetailComponent implements OnInit {
  carrinho: ICarrinho | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ carrinho }) => {
      this.carrinho = carrinho;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
