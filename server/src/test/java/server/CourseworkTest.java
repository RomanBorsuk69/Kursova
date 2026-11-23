package server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CourseworkTest {
    @Test void f1ForMinimumLevelIsZero() { assertEquals(0.0,PriorityCalculator.factor1(1)); }
    @Test void f1ForDemoDataIs75() { assertEquals(75.0,PriorityCalculator.factor1(4)); }
    @Test void f2ForDemoDataIs50() { assertEquals(50.0,PriorityCalculator.factor2(3)); }
    @Test void f3ForDemoDataIs50() { assertEquals(50.0,PriorityCalculator.factor3(36)); }
    @Test void f3IsLimitedTo100() { assertEquals(100.0,PriorityCalculator.factor3(500)); }
    @Test void priorityForDemoDataIs60() { assertEquals(60.0,PriorityCalculator.calculate(75,50,50)); }
    @Test void priorityRejectsFactorAbove100() { assertThrows(IllegalArgumentException.class,()->PriorityCalculator.calculate(101,10,10)); }
    @Test void newToDiagnosedAllowed() { assertTrue(ServiceRequestService.isAllowedTransition(RequestState.NEW,RequestState.DIAGNOSED)); }
    @Test void diagnosedToInServiceAllowed() { assertTrue(ServiceRequestService.isAllowedTransition(RequestState.DIAGNOSED,RequestState.IN_SERVICE)); }
    @Test void inServiceToCompletedAllowed() { assertTrue(ServiceRequestService.isAllowedTransition(RequestState.IN_SERVICE,RequestState.COMPLETED)); }
    @Test void newToCompletedRejected() { assertFalse(ServiceRequestService.isAllowedTransition(RequestState.NEW,RequestState.COMPLETED)); }
    @Test void specialistValidationWorks() { assertThrows(IllegalArgumentException.class,()->SpecialistService.validate(new Specialist(null,"","Оптичні прилади","+380501112233"))); }
    @Test void equipmentValidationWorks() { assertThrows(IllegalArgumentException.class,()->EquipmentService.validate(new Equipment(null,"LAB-1","Назва","Категорія","Лабораторія",9,1))); }
    @Test void serviceRecordValidationWorks() { assertThrows(IllegalArgumentException.class,()->ServiceRecordService.validate(new ServiceRecord(null,1,1,"Роботи","2026-13-40",1,100))); }
}
