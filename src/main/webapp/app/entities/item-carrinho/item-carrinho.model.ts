import { ICarrinho } from 'app/entities/carrinho/carrinho.model';
import { IProduto } from 'app/entities/produto/produto.model';

export interface IItemCarrinho {
  id: number;
  quantidade?: number | null;
  precoTotal?: number | null;
  carrinho?: Pick<ICarrinho, 'id'> | null;
  produto?: Pick<IProduto, 'id'> | null;
}

export type NewItemCarrinho = Omit<IItemCarrinho, 'id'> & { id: null };
