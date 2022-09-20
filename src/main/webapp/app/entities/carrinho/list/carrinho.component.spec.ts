import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CarrinhoService } from '../service/carrinho.service';

import { CarrinhoComponent } from './carrinho.component';

describe('Carrinho Management Component', () => {
  let comp: CarrinhoComponent;
  let fixture: ComponentFixture<CarrinhoComponent>;
  let service: CarrinhoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'carrinho', component: CarrinhoComponent }]), HttpClientTestingModule],
      declarations: [CarrinhoComponent],
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
      .overrideTemplate(CarrinhoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CarrinhoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CarrinhoService);

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
    expect(comp.carrinhos?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to carrinhoService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getCarrinhoIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getCarrinhoIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
