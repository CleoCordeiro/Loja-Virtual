import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICarrinho } from '../carrinho.model';
import { CarrinhoService } from '../service/carrinho.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './carrinho-delete-dialog.component.html',
})
export class CarrinhoDeleteDialogComponent {
  carrinho?: ICarrinho;

  constructor(protected carrinhoService: CarrinhoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.carrinhoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
