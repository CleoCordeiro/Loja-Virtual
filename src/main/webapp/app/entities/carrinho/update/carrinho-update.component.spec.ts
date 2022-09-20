import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CarrinhoFormService } from './carrinho-form.service';
import { CarrinhoService } from '../service/carrinho.service';
import { ICarrinho } from '../carrinho.model';
import { IUsuario } from 'app/entities/usuario/usuario.model';
import { UsuarioService } from 'app/entities/usuario/service/usuario.service';

import { CarrinhoUpdateComponent } from './carrinho-update.component';

describe('Carrinho Management Update Component', () => {
  let comp: CarrinhoUpdateComponent;
  let fixture: ComponentFixture<CarrinhoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let carrinhoFormService: CarrinhoFormService;
  let carrinhoService: CarrinhoService;
  let usuarioService: UsuarioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CarrinhoUpdateComponent],
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
      .overrideTemplate(CarrinhoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CarrinhoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    carrinhoFormService = TestBed.inject(CarrinhoFormService);
    carrinhoService = TestBed.inject(CarrinhoService);
    usuarioService = TestBed.inject(UsuarioService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Usuario query and add missing value', () => {
      const carrinho: ICarrinho = { id: 456 };
      const usuario: IUsuario = { id: 58036 };
      carrinho.usuario = usuario;

      const usuarioCollection: IUsuario[] = [{ id: 62887 }];
      jest.spyOn(usuarioService, 'query').mockReturnValue(of(new HttpResponse({ body: usuarioCollection })));
      const additionalUsuarios = [usuario];
      const expectedCollection: IUsuario[] = [...additionalUsuarios, ...usuarioCollection];
      jest.spyOn(usuarioService, 'addUsuarioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ carrinho });
      comp.ngOnInit();

      expect(usuarioService.query).toHaveBeenCalled();
      expect(usuarioService.addUsuarioToCollectionIfMissing).toHaveBeenCalledWith(
        usuarioCollection,
        ...additionalUsuarios.map(expect.objectContaining)
      );
      expect(comp.usuariosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const carrinho: ICarrinho = { id: 456 };
      const usuario: IUsuario = { id: 49357 };
      carrinho.usuario = usuario;

      activatedRoute.data = of({ carrinho });
      comp.ngOnInit();

      expect(comp.usuariosSharedCollection).toContain(usuario);
      expect(comp.carrinho).toEqual(carrinho);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICarrinho>>();
      const carrinho = { id: 123 };
      jest.spyOn(carrinhoFormService, 'getCarrinho').mockReturnValue(carrinho);
      jest.spyOn(carrinhoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ carrinho });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: carrinho }));
      saveSubject.complete();

      // THEN
      expect(carrinhoFormService.getCarrinho).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(carrinhoService.update).toHaveBeenCalledWith(expect.objectContaining(carrinho));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICarrinho>>();
      const carrinho = { id: 123 };
      jest.spyOn(carrinhoFormService, 'getCarrinho').mockReturnValue({ id: null });
      jest.spyOn(carrinhoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ carrinho: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: carrinho }));
      saveSubject.complete();

      // THEN
      expect(carrinhoFormService.getCarrinho).toHaveBeenCalled();
      expect(carrinhoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICarrinho>>();
      const carrinho = { id: 123 };
      jest.spyOn(carrinhoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ carrinho });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(carrinhoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUsuario', () => {
      it('Should forward to usuarioService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(usuarioService, 'compareUsuario');
        comp.compareUsuario(entity, entity2);
        expect(usuarioService.compareUsuario).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
