import { IProduto, NewProduto } from './produto.model';

export const sampleWithRequiredData: IProduto = {
  id: 79836,
};

export const sampleWithPartialData: IProduto = {
  id: 34427,
  nome: 'Amazonas',
};

export const sampleWithFullData: IProduto = {
  id: 47024,
  nome: 'THX',
  descricao: 'Polarised revolutionary hybrid',
  preco: 60170,
  quantidade: 26810,
};

export const sampleWithNewData: NewProduto = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
