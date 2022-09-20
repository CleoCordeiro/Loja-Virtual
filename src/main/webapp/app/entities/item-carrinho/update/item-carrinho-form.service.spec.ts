import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../item-carrinho.test-samples';

import { ItemCarrinhoFormService } from './item-carrinho-form.service';

describe('ItemCarrinho Form Service', () => {
  let service: ItemCarrinhoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ItemCarrinhoFormService);
  });

  describe('Service methods', () => {
    describe('createItemCarrinhoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createItemCarrinhoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantidade: expect.any(Object),
            precoTotal: expect.any(Object),
            carrinho: expect.any(Object),
            produto: expect.any(Object),
          })
        );
      });

      it('passing IItemCarrinho should create a new form with FormGroup', () => {
        const formGroup = service.createItemCarrinhoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantidade: expect.any(Object),
            precoTotal: expect.any(Object),
            carrinho: expect.any(Object),
            produto: expect.any(Object),
          })
        );
      });
    });

    describe('getItemCarrinho', () => {
      it('should return NewItemCarrinho for default ItemCarrinho initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createItemCarrinhoFormGroup(sampleWithNewData);

        const itemCarrinho = service.getItemCarrinho(formGroup) as any;

        expect(itemCarrinho).toMatchObject(sampleWithNewData);
      });

      it('should return NewItemCarrinho for empty ItemCarrinho initial value', () => {
        const formGroup = service.createItemCarrinhoFormGroup();

        const itemCarrinho = service.getItemCarrinho(formGroup) as any;

        expect(itemCarrinho).toMatchObject({});
      });

      it('should return IItemCarrinho', () => {
        const formGroup = service.createItemCarrinhoFormGroup(sampleWithRequiredData);

        const itemCarrinho = service.getItemCarrinho(formGroup) as any;

        expect(itemCarrinho).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IItemCarrinho should not enable id FormControl', () => {
        const formGroup = service.createItemCarrinhoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewItemCarrinho should disable id FormControl', () => {
        const formGroup = service.createItemCarrinhoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
