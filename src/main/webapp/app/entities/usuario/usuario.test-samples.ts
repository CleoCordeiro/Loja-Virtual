import dayjs from 'dayjs/esm';

import { TipoUsuario } from 'app/entities/enumerations/tipo-usuario.model';

import { IUsuario, NewUsuario } from './usuario.model';

export const sampleWithRequiredData: IUsuario = {
  id: 19585,
};

export const sampleWithPartialData: IUsuario = {
  id: 52,
  senha: 'synthesize Avon SAS',
  cpf: 'Rua',
  tipoUsuario: TipoUsuario['CLIENTE'],
};

export const sampleWithFullData: IUsuario = {
  id: 98607,
  nome: 'França',
  email: 'Silas35@gmail.com',
  senha: 'synthesizing',
  dataNascimento: dayjs('2022-09-19'),
  cpf: 'open Implemented',
  telefone: 'East IB Dollar',
  tipoUsuario: TipoUsuario['ADMINISTRADOR'],
};

export const sampleWithNewData: NewUsuario = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
