import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../carrinho.test-samples';

import { CarrinhoFormService } from './carrinho-form.service';

describe('Carrinho Form Service', () => {
  let service: CarrinhoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CarrinhoFormService);
  });

  describe('Service methods', () => {
    describe('createCarrinhoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCarrinhoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dataCriacao: expect.any(Object),
            dataAlteracao: expect.any(Object),
            usuario: expect.any(Object),
          })
        );
      });

      it('passing ICarrinho should create a new form with FormGroup', () => {
        const formGroup = service.createCarrinhoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dataCriacao: expect.any(Object),
            dataAlteracao: expect.any(Object),
            usuario: expect.any(Object),
          })
        );
      });
    });

    describe('getCarrinho', () => {
      it('should return NewCarrinho for default Carrinho initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createCarrinhoFormGroup(sampleWithNewData);

        const carrinho = service.getCarrinho(formGroup) as any;

        expect(carrinho).toMatchObject(sampleWithNewData);
      });

      it('should return NewCarrinho for empty Carrinho initial value', () => {
        const formGroup = service.createCarrinhoFormGroup();

        const carrinho = service.getCarrinho(formGroup) as any;

        expect(carrinho).toMatchObject({});
      });

      it('should return ICarrinho', () => {
        const formGroup = service.createCarrinhoFormGroup(sampleWithRequiredData);

        const carrinho = service.getCarrinho(formGroup) as any;

        expect(carrinho).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICarrinho should not enable id FormControl', () => {
        const formGroup = service.createCarrinhoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCarrinho should disable id FormControl', () => {
        const formGroup = service.createCarrinhoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
