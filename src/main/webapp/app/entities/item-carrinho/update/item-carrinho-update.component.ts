import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ItemCarrinhoFormService, ItemCarrinhoFormGroup } from './item-carrinho-form.service';
import { IItemCarrinho } from '../item-carrinho.model';
import { ItemCarrinhoService } from '../service/item-carrinho.service';
import { ICarrinho } from 'app/entities/carrinho/carrinho.model';
import { CarrinhoService } from 'app/entities/carrinho/service/carrinho.service';
import { IProduto } from 'app/entities/produto/produto.model';
import { ProdutoService } from 'app/entities/produto/service/produto.service';

@Component({
  selector: 'jhi-item-carrinho-update',
  templateUrl: './item-carrinho-update.component.html',
})
export class ItemCarrinhoUpdateComponent implements OnInit {
  isSaving = false;
  itemCarrinho: IItemCarrinho | null = null;

  carrinhosSharedCollection: ICarrinho[] = [];
  produtosSharedCollection: IProduto[] = [];

  editForm: ItemCarrinhoFormGroup = this.itemCarrinhoFormService.createItemCarrinhoFormGroup();

  constructor(
    protected itemCarrinhoService: ItemCarrinhoService,
    protected itemCarrinhoFormService: ItemCarrinhoFormService,
    protected carrinhoService: CarrinhoService,
    protected produtoService: ProdutoService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCarrinho = (o1: ICarrinho | null, o2: ICarrinho | null): boolean => this.carrinhoService.compareCarrinho(o1, o2);

  compareProduto = (o1: IProduto | null, o2: IProduto | null): boolean => this.produtoService.compareProduto(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ itemCarrinho }) => {
      this.itemCarrinho = itemCarrinho;
      if (itemCarrinho) {
        this.updateForm(itemCarrinho);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const itemCarrinho = this.itemCarrinhoFormService.getItemCarrinho(this.editForm);
    if (itemCarrinho.id !== null) {
      this.subscribeToSaveResponse(this.itemCarrinhoService.update(itemCarrinho));
    } else {
      this.subscribeToSaveResponse(this.itemCarrinhoService.create(itemCarrinho));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IItemCarrinho>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(itemCarrinho: IItemCarrinho): void {
    this.itemCarrinho = itemCarrinho;
    this.itemCarrinhoFormService.resetForm(this.editForm, itemCarrinho);

    this.carrinhosSharedCollection = this.carrinhoService.addCarrinhoToCollectionIfMissing<ICarrinho>(
      this.carrinhosSharedCollection,
      itemCarrinho.carrinho
    );
    this.produtosSharedCollection = this.produtoService.addProdutoToCollectionIfMissing<IProduto>(
      this.produtosSharedCollection,
      itemCarrinho.produto
    );
  }

  protected loadRelationshipsOptions(): void {
    this.carrinhoService
      .query()
      .pipe(map((res: HttpResponse<ICarrinho[]>) => res.body ?? []))
      .pipe(
        map((carrinhos: ICarrinho[]) =>
          this.carrinhoService.addCarrinhoToCollectionIfMissing<ICarrinho>(carrinhos, this.itemCarrinho?.carrinho)
        )
      )
      .subscribe((carrinhos: ICarrinho[]) => (this.carrinhosSharedCollection = carrinhos));

    this.produtoService
      .query()
      .pipe(map((res: HttpResponse<IProduto[]>) => res.body ?? []))
      .pipe(
        map((produtos: IProduto[]) => this.produtoService.addProdutoToCollectionIfMissing<IProduto>(produtos, this.itemCarrinho?.produto))
      )
      .subscribe((produtos: IProduto[]) => (this.produtosSharedCollection = produtos));
  }
}
