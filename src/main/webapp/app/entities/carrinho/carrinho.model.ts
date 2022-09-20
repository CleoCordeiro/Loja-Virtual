import dayjs from 'dayjs/esm';
import { IUsuario } from 'app/entities/usuario/usuario.model';

export interface ICarrinho {
  id: number;
  dataCriacao?: dayjs.Dayjs | null;
  dataAlteracao?: dayjs.Dayjs | null;
  usuario?: Pick<IUsuario, 'id'> | null;
}

export type NewCarrinho = Omit<ICarrinho, 'id'> & { id: null };
