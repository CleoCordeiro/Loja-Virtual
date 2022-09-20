import dayjs from 'dayjs/esm';
import { IEndereco } from 'app/entities/endereco/endereco.model';
import { TipoUsuario } from 'app/entities/enumerations/tipo-usuario.model';

export interface IUsuario {
  id: number;
  nome?: string | null;
  email?: string | null;
  senha?: string | null;
  dataNascimento?: dayjs.Dayjs | null;
  cpf?: string | null;
  telefone?: string | null;
  tipoUsuario?: TipoUsuario | null;
  endereco?: Pick<IEndereco, 'id'> | null;
}

export type NewUsuario = Omit<IUsuario, 'id'> & { id: null };
