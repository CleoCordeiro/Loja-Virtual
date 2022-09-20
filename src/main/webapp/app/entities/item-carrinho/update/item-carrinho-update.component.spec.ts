import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ItemCarrinhoFormService } from './item-carrinho-form.service';
import { ItemCarrinhoService } from '../service/item-carrinho.service';
import { IItemCarrinho } from '../item-carrinho.model';
import { ICarrinho } from 'app/entities/carrinho/carrinho.model';
import { CarrinhoService } from 'app/entities/carrinho/service/carrinho.service';
import { IProduto } from 'app/entities/produto/produto.model';
import { ProdutoService } from 'app/entities/produto/service/produto.service';

import { ItemCarrinhoUpdateComponent } from './item-carrinho-update.component';

describe('ItemCarrinho Management Update Component', () => {
  let comp: ItemCarrinhoUpdateComponent;
  let fixture: ComponentFixture<ItemCarrinhoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let itemCarrinhoFormService: ItemCarrinhoFormService;
  let itemCarrinhoService: ItemCarrinhoService;
  let carrinhoService: CarrinhoService;
  let produtoService: ProdutoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ItemCarrinhoUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ItemCarrinhoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ItemCarrinhoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    itemCarrinhoFormService = TestBed.inject(ItemCarrinhoFormService);
    itemCarrinhoService = TestBed.inject(ItemCarrinhoService);
    carrinhoService = TestBed.inject(CarrinhoService);
    produtoService = TestBed.inject(ProdutoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Carrinho query and add missing value', () => {
      const itemCarrinho: IItemCarrinho = { id: 456 };
      const carrinho: ICarrinho = { id: 77877 };
      itemCarrinho.carrinho = carrinho;

      const carrinhoCollection: ICarrinho[] = [{ id: 41348 }];
      jest.spyOn(carrinhoService, 'query').mockReturnValue(of(new HttpResponse({ body: carrinhoCollection })));
      const additionalCarrinhos = [carrinho];
      const expectedCollection: ICarrinho[] = [...additionalCarrinhos, ...carrinhoCollection];
      jest.spyOn(carrinhoService, 'addCarrinhoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ itemCarrinho });
      comp.ngOnInit();

      expect(carrinhoService.query).toHaveBeenCalled();
      expect(carrinhoService.addCarrinhoToCollectionIfMissing).toHaveBeenCalledWith(
        carrinhoCollection,
        ...additionalCarrinhos.map(expect.objectContaining)
      );
      expect(comp.carrinhosSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Produto query and add missing value', () => {
      const itemCarrinho: IItemCarrinho = { id: 456 };
      const produto: IProduto = { id: 18983 };
      itemCarrinho.produto = produto;

      const produtoCollection: IProduto[] = [{ id: 85288 }];
      jest.spyOn(produtoService, 'query').mockReturnValue(of(new HttpResponse({ body: produtoCollection })));
      const additionalProdutos = [produto];
      const expectedCollection: IProduto[] = [...additionalProdutos, ...produtoCollection];
      jest.spyOn(produtoService, 'addProdutoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ itemCarrinho });
      comp.ngOnInit();

      expect(produtoService.query).toHaveBeenCalled();
      expect(produtoService.addProdutoToCollectionIfMissing).toHaveBeenCalledWith(
        produtoCollection,
        ...additionalProdutos.map(expect.objectContaining)
      );
      expect(comp.produtosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const itemCarrinho: IItemCarrinho = { id: 456 };
      const carrinho: ICarrinho = { id: 64319 };
      itemCarrinho.carrinho = carrinho;
      const produto: IProduto = { id: 8220 };
      itemCarrinho.produto = produto;

      activatedRoute.data = of({ itemCarrinho });
      comp.ngOnInit();

      expect(comp.carrinhosSharedCollection).toContain(carrinho);
      expect(comp.produtosSharedCollection).toContain(produto);
      expect(comp.itemCarrinho).toEqual(itemCarrinho);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IItemCarrinho>>();
      const itemCarrinho = { id: 123 };
      jest.spyOn(itemCarrinhoFormService, 'getItemCarrinho').mockReturnValue(itemCarrinho);
      jest.spyOn(itemCarrinhoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ itemCarrinho });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: itemCarrinho }));
      saveSubject.complete();

      // THEN
      expect(itemCarrinhoFormService.getItemCarrinho).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(itemCarrinhoService.update).toHaveBeenCalledWith(expect.objectContaining(itemCarrinho));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IItemCarrinho>>();
      const itemCarrinho = { id: 123 };
      jest.spyOn(itemCarrinhoFormService, 'getItemCarrinho').mockReturnValue({ id: null });
      jest.spyOn(itemCarrinhoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ itemCarrinho: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: itemCarrinho }));
      saveSubject.complete();

      // THEN
      expect(itemCarrinhoFormService.getItemCarrinho).toHaveBeenCalled();
      expect(itemCarrinhoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IItemCarrinho>>();
      const itemCarrinho = { id: 123 };
      jest.spyOn(itemCarrinhoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ itemCarrinho });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(itemCarrinhoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCarrinho', () => {
      it('Should forward to carrinhoService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(carrinhoService, 'compareCarrinho');
        comp.compareCarrinho(entity, entity2);
        expect(carrinhoService.compareCarrinho).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProduto', () => {
      it('Should forward to produtoService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(produtoService, 'compareProduto');
        comp.compareProduto(entity, entity2);
        expect(produtoService.compareProduto).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
