import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { UsuarioService } from '../service/usuario.service';

import { UsuarioComponent } from './usuario.component';

describe('Usuario Management Component', () => {
  let comp: UsuarioComponent;
  let fixture: ComponentFixture<UsuarioComponent>;
  let service: UsuarioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'usuario', component: UsuarioComponent }]), HttpClientTestingModule],
      declarations: [UsuarioComponent],
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
      .overrideTemplate(UsuarioComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UsuarioComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(UsuarioService);

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
    expect(comp.usuarios?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to usuarioService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getUsuarioIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getUsuarioIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
