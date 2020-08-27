package com.sawoo.pipeline.api.dummy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DummyServiceTest {

    @Autowired
    private DummyService service;

    @MockBean
    private DummyRepository repository;

    @Test
    @DisplayName("Dummy Service: findById - Success")
    void findByIdWhenDummyExitsReturnsSuccess() {
        // Set up mock dummy
        String DUMMY_ID = "my_dummy_id";
        DummyEntity mockDummy = new DummyEntity(DUMMY_ID, "hello my friend", 15, 1);

        // Set up the mocked repository
        doReturn(Optional.of(mockDummy)).when(repository).findById(DUMMY_ID);

        // Execute the service call
        Optional<DummyEntity> returnedDummyEntity = service.findById(DUMMY_ID);

        // Assert the response
        Assertions.assertTrue(returnedDummyEntity.isPresent(), "Dummy entity with id " + DUMMY_ID + " was not found");
        Assertions.assertEquals(returnedDummyEntity.get(), mockDummy, "Dummy entity should be the same");
    }

    @Test
    @DisplayName("Dummy Service: findById - Failed")
    void findByIdWhenDummyNotFoundReturnsFailure() {
        // Set up mock dummy
        String DUMMY_ID = "my_dummy_id_not_found";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(DUMMY_ID);

        // Execute the service call
        Optional<DummyEntity> returnedDummyEntity = service.findById(DUMMY_ID);

        // Assert the response
        Assertions.assertFalse(returnedDummyEntity.isPresent(), "Dummy entity with id " + DUMMY_ID + " was found. It should not be");
    }

    @Test
    @DisplayName("Dummy Service: findAll - Success")
    void findAllWhenTwoDummyEntitiesFoundReturnsSuccess() {
        // Set up mock dummy entities
        DummyEntity mockDummy1 = new DummyEntity("dummy_id_1", "name1", 15, 1);
        DummyEntity mockDummy2 = new DummyEntity("dummy_id_2", "name2", 25, 2);

        // Set up the mocked repository
        doReturn(Arrays.asList(mockDummy1, mockDummy2)).when(repository).findAll();

        // Execute the service call
        List<DummyEntity> returnedDummyList = service.findAll();

        // Assert the response
        Assertions.assertEquals(2, returnedDummyList.size(), "findAll should return 2 dummy entities");
    }

    @Test
    @DisplayName("Dummy Service: save - Success")
    void saveWhenDummyEntityCorrectReturnsSuccess() {
        // Set up mock dummy entities
        DummyEntity serviceDummy = new DummyEntity();
        serviceDummy.setNumber(15);
        serviceDummy.setName("Miguelito");

        // Set up mocked repository
        doReturn(serviceDummy).when(repository).save(any());

        // Execute the service call
        DummyEntity dummy = service.save(serviceDummy);

        // Assert the response
        Assertions.assertNotNull(dummy, "The saved Dummy entity can not be null");
        Assertions.assertEquals(1, dummy.getVersion(), "Dummy entity version must be 1");
        Assertions.assertNotNull(dummy.getId(), "Dummy entity id can not be null");
        Assertions.assertFalse(dummy.getId().isBlank(), "Dummy entity id can not be blank");
    }

    @Test
    @DisplayName("Dummy Service: update - Success")
    void updateWhenDummyEntityReturnsSuccess() {
        // Set up mock dummy entity
        String DUMMY_ID = "my_dummy_id";
        DummyEntity updatedDummy = new DummyEntity();
        updatedDummy.setNumber(34);
        updatedDummy.setName("Miguelín");
        updatedDummy.setVersion(2);
        updatedDummy.setId(DUMMY_ID);

        DummyEntity serviceDummy = new DummyEntity();
        serviceDummy.setName("Miguelito");
        serviceDummy.setNumber(15);
        serviceDummy.setVersion(1);
        serviceDummy.setId(DUMMY_ID);

        // Set up mocked repository
        doReturn(Optional.of(serviceDummy)).when(repository).findById(DUMMY_ID);
        doReturn(updatedDummy).when(repository).save(any());

        // Execute the service call
        Optional<DummyEntity> dummy = service.update(DUMMY_ID, serviceDummy);

        // Assert the response
        Assertions.assertTrue(dummy.isPresent(), "Dummy entity with id " + DUMMY_ID + " was not found");
        Assertions.assertEquals(dummy.get().getVersion(), 2, "Dummy entity version has to be 2");
        Assertions.assertEquals(dummy.get().getName(), "Miguelito", "Dummy entity name has to be 'Miguelito'");
    }
}
