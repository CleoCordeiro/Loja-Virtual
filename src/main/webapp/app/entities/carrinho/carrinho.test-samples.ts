import dayjs from 'dayjs/esm';

import { ICarrinho, NewCarrinho } from './carrinho.model';

export const sampleWithRequiredData: ICarrinho = {
  id: 79608,
};

export const sampleWithPartialData: ICarrinho = {
  id: 81725,
  dataAlteracao: dayjs('2022-09-19'),
};

export const sampleWithFullData: ICarrinho = {
  id: 98744,
  dataCriacao: dayjs('2022-09-19'),
  dataAlteracao: dayjs('2022-09-19'),
};

export const sampleWithNewData: NewCarrinho = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
