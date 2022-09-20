import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CarrinhoFormService, CarrinhoFormGroup } from './carrinho-form.service';
import { ICarrinho } from '../carrinho.model';
import { CarrinhoService } from '../service/carrinho.service';
import { IUsuario } from 'app/entities/usuario/usuario.model';
import { UsuarioService } from 'app/entities/usuario/service/usuario.service';

@Component({
  selector: 'jhi-carrinho-update',
  templateUrl: './carrinho-update.component.html',
})
export class CarrinhoUpdateComponent implements OnInit {
  isSaving = false;
  carrinho: ICarrinho | null = null;

  usuariosSharedCollection: IUsuario[] = [];

  editForm: CarrinhoFormGroup = this.carrinhoFormService.createCarrinhoFormGroup();

  constructor(
    protected carrinhoService: CarrinhoService,
    protected carrinhoFormService: CarrinhoFormService,
    protected usuarioService: UsuarioService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUsuario = (o1: IUsuario | null, o2: IUsuario | null): boolean => this.usuarioService.compareUsuario(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ carrinho }) => {
      this.carrinho = carrinho;
      if (carrinho) {
        this.updateForm(carrinho);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const carrinho = this.carrinhoFormService.getCarrinho(this.editForm);
    if (carrinho.id !== null) {
      this.subscribeToSaveResponse(this.carrinhoService.update(carrinho));
    } else {
      this.subscribeToSaveResponse(this.carrinhoService.create(carrinho));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICarrinho>>): void {
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

  protected updateForm(carrinho: ICarrinho): void {
    this.carrinho = carrinho;
    this.carrinhoFormService.resetForm(this.editForm, carrinho);

    this.usuariosSharedCollection = this.usuarioService.addUsuarioToCollectionIfMissing<IUsuario>(
      this.usuariosSharedCollection,
      carrinho.usuario
    );
  }

  protected loadRelationshipsOptions(): void {
    this.usuarioService
      .query()
      .pipe(map((res: HttpResponse<IUsuario[]>) => res.body ?? []))
      .pipe(map((usuarios: IUsuario[]) => this.usuarioService.addUsuarioToCollectionIfMissing<IUsuario>(usuarios, this.carrinho?.usuario)))
      .subscribe((usuarios: IUsuario[]) => (this.usuariosSharedCollection = usuarios));
  }
}
