import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CarrinhoDetailComponent } from './carrinho-detail.component';

describe('Carrinho Management Detail Component', () => {
  let comp: CarrinhoDetailComponent;
  let fixture: ComponentFixture<CarrinhoDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CarrinhoDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ carrinho: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CarrinhoDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CarrinhoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load carrinho on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.carrinho).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
