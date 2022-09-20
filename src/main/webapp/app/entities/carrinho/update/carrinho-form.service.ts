import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICarrinho, NewCarrinho } from '../carrinho.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICarrinho for edit and NewCarrinhoFormGroupInput for create.
 */
type CarrinhoFormGroupInput = ICarrinho | PartialWithRequiredKeyOf<NewCarrinho>;

type CarrinhoFormDefaults = Pick<NewCarrinho, 'id'>;

type CarrinhoFormGroupContent = {
  id: FormControl<ICarrinho['id'] | NewCarrinho['id']>;
  dataCriacao: FormControl<ICarrinho['dataCriacao']>;
  dataAlteracao: FormControl<ICarrinho['dataAlteracao']>;
  usuario: FormControl<ICarrinho['usuario']>;
};

export type CarrinhoFormGroup = FormGroup<CarrinhoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CarrinhoFormService {
  createCarrinhoFormGroup(carrinho: CarrinhoFormGroupInput = { id: null }): CarrinhoFormGroup {
    const carrinhoRawValue = {
      ...this.getFormDefaults(),
      ...carrinho,
    };
    return new FormGroup<CarrinhoFormGroupContent>({
      id: new FormControl(
        { value: carrinhoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      dataCriacao: new FormControl(carrinhoRawValue.dataCriacao),
      dataAlteracao: new FormControl(carrinhoRawValue.dataAlteracao),
      usuario: new FormControl(carrinhoRawValue.usuario),
    });
  }

  getCarrinho(form: CarrinhoFormGroup): ICarrinho | NewCarrinho {
    return form.getRawValue() as ICarrinho | NewCarrinho;
  }

  resetForm(form: CarrinhoFormGroup, carrinho: CarrinhoFormGroupInput): void {
    const carrinhoRawValue = { ...this.getFormDefaults(), ...carrinho };
    form.reset(
      {
        ...carrinhoRawValue,
        id: { value: carrinhoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CarrinhoFormDefaults {
    return {
      id: null,
    };
  }
}
