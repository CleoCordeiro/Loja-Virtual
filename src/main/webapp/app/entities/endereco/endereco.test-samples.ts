import { IEndereco, NewEndereco } from './endereco.model';

export const sampleWithRequiredData: IEndereco = {
  id: 91029,
};

export const sampleWithPartialData: IEndereco = {
  id: 1151,
  logradouro: 'edge connect Mesa',
  numero: 'CFP Money',
  complemento: 'transmitting parsing',
  cidade: 'Fantástico Rondônia',
  estado: 'Innovative Macedônia',
  cep: 'dynamic Shilling',
};

export const sampleWithFullData: IEndereco = {
  id: 86724,
  logradouro: 'Solutions',
  numero: 'Congelado Checking',
  complemento: 'bluetooth Home 1080p',
  bairro: 'scale Automated',
  cidade: 'Direct e-markets',
  estado: 'JBOD XML SMTP',
  cep: 'Buckinghamshire multi-byte',
};

export const sampleWithNewData: NewEndereco = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
