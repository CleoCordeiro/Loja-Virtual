import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IItemCarrinho, NewItemCarrinho } from '../item-carrinho.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IItemCarrinho for edit and NewItemCarrinhoFormGroupInput for create.
 */
type ItemCarrinhoFormGroupInput = IItemCarrinho | PartialWithRequiredKeyOf<NewItemCarrinho>;

type ItemCarrinhoFormDefaults = Pick<NewItemCarrinho, 'id'>;

type ItemCarrinhoFormGroupContent = {
  id: FormControl<IItemCarrinho['id'] | NewItemCarrinho['id']>;
  quantidade: FormControl<IItemCarrinho['quantidade']>;
  precoTotal: FormControl<IItemCarrinho['precoTotal']>;
  carrinho: FormControl<IItemCarrinho['carrinho']>;
  produto: FormControl<IItemCarrinho['produto']>;
};

export type ItemCarrinhoFormGroup = FormGroup<ItemCarrinhoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ItemCarrinhoFormService {
  createItemCarrinhoFormGroup(itemCarrinho: ItemCarrinhoFormGroupInput = { id: null }): ItemCarrinhoFormGroup {
    const itemCarrinhoRawValue = {
      ...this.getFormDefaults(),
      ...itemCarrinho,
    };
    return new FormGroup<ItemCarrinhoFormGroupContent>({
      id: new FormControl(
        { value: itemCarrinhoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      quantidade: new FormControl(itemCarrinhoRawValue.quantidade),
      precoTotal: new FormControl(itemCarrinhoRawValue.precoTotal),
      carrinho: new FormControl(itemCarrinhoRawValue.carrinho),
      produto: new FormControl(itemCarrinhoRawValue.produto),
    });
  }

  getItemCarrinho(form: ItemCarrinhoFormGroup): IItemCarrinho | NewItemCarrinho {
    return form.getRawValue() as IItemCarrinho | NewItemCarrinho;
  }

  resetForm(form: ItemCarrinhoFormGroup, itemCarrinho: ItemCarrinhoFormGroupInput): void {
    const itemCarrinhoRawValue = { ...this.getFormDefaults(), ...itemCarrinho };
    form.reset(
      {
        ...itemCarrinhoRawValue,
        id: { value: itemCarrinhoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ItemCarrinhoFormDefaults {
    return {
      id: null,
    };
  }
}
