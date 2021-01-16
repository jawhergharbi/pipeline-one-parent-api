package com.sawoo.pipeline.api.repository.migration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.sawoo.pipeline.api.dummy.DummyEntity;
import com.sawoo.pipeline.api.dummy.DummyRepository;

import java.util.ArrayList;
import java.util.List;

@ChangeLog
public class DBChangeLog {

    @ChangeSet(order = "001", id = "seedDatabase", author = "miguel")
    public void seedDatabase(DummyRepository dummyRepository) {
        List<DummyEntity> dummieList = new ArrayList<>();
        dummieList.add(createNewDummie("Movie Tickets", 12, 1));
        dummieList.add(createNewDummie("Dinner", 11, 1));
        dummieList.add(createNewDummie("Netflix", 23, 3));
        dummieList.add(createNewDummie("Gym", 23, 2));
        dummieList.add(createNewDummie("Internet", 45, 1));

        dummyRepository.insert(dummieList);
    }

    private DummyEntity createNewDummie(String name, int age, int version) {
        DummyEntity dummie = new DummyEntity();
        dummie.setName(name);
        dummie.setNumber(age);
        dummie.setVersion(version);
        return dummie;
    }
}
