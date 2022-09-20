import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ItemCarrinhoService } from '../service/item-carrinho.service';

import { ItemCarrinhoComponent } from './item-carrinho.component';

describe('ItemCarrinho Management Component', () => {
  let comp: ItemCarrinhoComponent;
  let fixture: ComponentFixture<ItemCarrinhoComponent>;
  let service: ItemCarrinhoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'item-carrinho', component: ItemCarrinhoComponent }]), HttpClientTestingModule],
      declarations: [ItemCarrinhoComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(ItemCarrinhoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ItemCarrinhoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ItemCarrinhoService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.itemCarrinhos?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to itemCarrinhoService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getItemCarrinhoIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getItemCarrinhoIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
