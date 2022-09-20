import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ICarrinho } from '../carrinho.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../carrinho.test-samples';

import { CarrinhoService, RestCarrinho } from './carrinho.service';

const requireRestSample: RestCarrinho = {
  ...sampleWithRequiredData,
  dataCriacao: sampleWithRequiredData.dataCriacao?.format(DATE_FORMAT),
  dataAlteracao: sampleWithRequiredData.dataAlteracao?.format(DATE_FORMAT),
};

describe('Carrinho Service', () => {
  let service: CarrinhoService;
  let httpMock: HttpTestingController;
  let expectedResult: ICarrinho | ICarrinho[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CarrinhoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Carrinho', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const carrinho = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(carrinho).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Carrinho', () => {
      const carrinho = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(carrinho).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Carrinho', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Carrinho', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Carrinho', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCarrinhoToCollectionIfMissing', () => {
      it('should add a Carrinho to an empty array', () => {
        const carrinho: ICarrinho = sampleWithRequiredData;
        expectedResult = service.addCarrinhoToCollectionIfMissing([], carrinho);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(carrinho);
      });

      it('should not add a Carrinho to an array that contains it', () => {
        const carrinho: ICarrinho = sampleWithRequiredData;
        const carrinhoCollection: ICarrinho[] = [
          {
            ...carrinho,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCarrinhoToCollectionIfMissing(carrinhoCollection, carrinho);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Carrinho to an array that doesn't contain it", () => {
        const carrinho: ICarrinho = sampleWithRequiredData;
        const carrinhoCollection: ICarrinho[] = [sampleWithPartialData];
        expectedResult = service.addCarrinhoToCollectionIfMissing(carrinhoCollection, carrinho);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(carrinho);
      });

      it('should add only unique Carrinho to an array', () => {
        const carrinhoArray: ICarrinho[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const carrinhoCollection: ICarrinho[] = [sampleWithRequiredData];
        expectedResult = service.addCarrinhoToCollectionIfMissing(carrinhoCollection, ...carrinhoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const carrinho: ICarrinho = sampleWithRequiredData;
        const carrinho2: ICarrinho = sampleWithPartialData;
        expectedResult = service.addCarrinhoToCollectionIfMissing([], carrinho, carrinho2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(carrinho);
        expect(expectedResult).toContain(carrinho2);
      });

      it('should accept null and undefined values', () => {
        const carrinho: ICarrinho = sampleWithRequiredData;
        expectedResult = service.addCarrinhoToCollectionIfMissing([], null, carrinho, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(carrinho);
      });

      it('should return initial array if no Carrinho is added', () => {
        const carrinhoCollection: ICarrinho[] = [sampleWithRequiredData];
        expectedResult = service.addCarrinhoToCollectionIfMissing(carrinhoCollection, undefined, null);
        expect(expectedResult).toEqual(carrinhoCollection);
      });
    });

    describe('compareCarrinho', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCarrinho(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCarrinho(entity1, entity2);
        const compareResult2 = service.compareCarrinho(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCarrinho(entity1, entity2);
        const compareResult2 = service.compareCarrinho(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCarrinho(entity1, entity2);
        const compareResult2 = service.compareCarrinho(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
