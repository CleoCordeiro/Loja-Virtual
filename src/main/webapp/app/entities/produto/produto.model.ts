export interface IProduto {
  id: number;
  nome?: string | null;
  descricao?: string | null;
  preco?: number | null;
  quantidade?: number | null;
}

export type NewProduto = Omit<IProduto, 'id'> & { id: null };
