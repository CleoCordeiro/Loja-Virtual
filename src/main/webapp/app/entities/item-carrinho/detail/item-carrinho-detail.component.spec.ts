import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ItemCarrinhoDetailComponent } from './item-carrinho-detail.component';

describe('ItemCarrinho Management Detail Component', () => {
  let comp: ItemCarrinhoDetailComponent;
  let fixture: ComponentFixture<ItemCarrinhoDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ItemCarrinhoDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ itemCarrinho: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ItemCarrinhoDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ItemCarrinhoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load itemCarrinho on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.itemCarrinho).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
