package test;

import org.junit.jupiter.api.BeforeEach;
import tracker.service.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest <InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach(){
        taskManager = new InMemoryTaskManager();
    }

}