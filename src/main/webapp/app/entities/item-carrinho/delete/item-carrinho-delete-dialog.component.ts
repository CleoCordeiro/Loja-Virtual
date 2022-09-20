import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IItemCarrinho } from '../item-carrinho.model';
import { ItemCarrinhoService } from '../service/item-carrinho.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './item-carrinho-delete-dialog.component.html',
})
export class ItemCarrinhoDeleteDialogComponent {
  itemCarrinho?: IItemCarrinho;

  constructor(protected itemCarrinhoService: ItemCarrinhoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.itemCarrinhoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
