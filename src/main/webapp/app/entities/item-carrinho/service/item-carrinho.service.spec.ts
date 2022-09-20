import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IItemCarrinho } from '../item-carrinho.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../item-carrinho.test-samples';

import { ItemCarrinhoService } from './item-carrinho.service';

const requireRestSample: IItemCarrinho = {
  ...sampleWithRequiredData,
};

describe('ItemCarrinho Service', () => {
  let service: ItemCarrinhoService;
  let httpMock: HttpTestingController;
  let expectedResult: IItemCarrinho | IItemCarrinho[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ItemCarrinhoService);
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

    it('should create a ItemCarrinho', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const itemCarrinho = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(itemCarrinho).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ItemCarrinho', () => {
      const itemCarrinho = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(itemCarrinho).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ItemCarrinho', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ItemCarrinho', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ItemCarrinho', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addItemCarrinhoToCollectionIfMissing', () => {
      it('should add a ItemCarrinho to an empty array', () => {
        const itemCarrinho: IItemCarrinho = sampleWithRequiredData;
        expectedResult = service.addItemCarrinhoToCollectionIfMissing([], itemCarrinho);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(itemCarrinho);
      });

      it('should not add a ItemCarrinho to an array that contains it', () => {
        const itemCarrinho: IItemCarrinho = sampleWithRequiredData;
        const itemCarrinhoCollection: IItemCarrinho[] = [
          {
            ...itemCarrinho,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addItemCarrinhoToCollectionIfMissing(itemCarrinhoCollection, itemCarrinho);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ItemCarrinho to an array that doesn't contain it", () => {
        const itemCarrinho: IItemCarrinho = sampleWithRequiredData;
        const itemCarrinhoCollection: IItemCarrinho[] = [sampleWithPartialData];
        expectedResult = service.addItemCarrinhoToCollectionIfMissing(itemCarrinhoCollection, itemCarrinho);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(itemCarrinho);
      });

      it('should add only unique ItemCarrinho to an array', () => {
        const itemCarrinhoArray: IItemCarrinho[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const itemCarrinhoCollection: IItemCarrinho[] = [sampleWithRequiredData];
        expectedResult = service.addItemCarrinhoToCollectionIfMissing(itemCarrinhoCollection, ...itemCarrinhoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const itemCarrinho: IItemCarrinho = sampleWithRequiredData;
        const itemCarrinho2: IItemCarrinho = sampleWithPartialData;
        expectedResult = service.addItemCarrinhoToCollectionIfMissing([], itemCarrinho, itemCarrinho2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(itemCarrinho);
        expect(expectedResult).toContain(itemCarrinho2);
      });

      it('should accept null and undefined values', () => {
        const itemCarrinho: IItemCarrinho = sampleWithRequiredData;
        expectedResult = service.addItemCarrinhoToCollectionIfMissing([], null, itemCarrinho, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(itemCarrinho);
      });

      it('should return initial array if no ItemCarrinho is added', () => {
        const itemCarrinhoCollection: IItemCarrinho[] = [sampleWithRequiredData];
        expectedResult = service.addItemCarrinhoToCollectionIfMissing(itemCarrinhoCollection, undefined, null);
        expect(expectedResult).toEqual(itemCarrinhoCollection);
      });
    });

    describe('compareItemCarrinho', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareItemCarrinho(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareItemCarrinho(entity1, entity2);
        const compareResult2 = service.compareItemCarrinho(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareItemCarrinho(entity1, entity2);
        const compareResult2 = service.compareItemCarrinho(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareItemCarrinho(entity1, entity2);
        const compareResult2 = service.compareItemCarrinho(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
