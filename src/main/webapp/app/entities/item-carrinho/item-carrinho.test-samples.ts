import { IItemCarrinho, NewItemCarrinho } from './item-carrinho.model';

export const sampleWithRequiredData: IItemCarrinho = {
  id: 83760,
};

export const sampleWithPartialData: IItemCarrinho = {
  id: 75430,
  quantidade: 59940,
};

export const sampleWithFullData: IItemCarrinho = {
  id: 97558,
  quantidade: 26269,
  precoTotal: 93085,
};

export const sampleWithNewData: NewItemCarrinho = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
