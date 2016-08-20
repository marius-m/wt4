package lt.markmerkk.entities.database;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import lt.markmerkk.entities.database.helpers.entities.Mock3;
import lt.markmerkk.entities.jobs.CreateJobIfNeeded;
import lt.markmerkk.entities.jobs.InsertJob;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 2/22/16.
 */
public class DBMockExecutorConcurrencyTest {
  public static final Logger logger = LoggerFactory.getLogger(DBMockExecutor.class);

  private DBMockExecutor executor;
  private Random rand;

  @Before
  public void setUp() {
    executor = new DBMockExecutor();
    executor.execute(new CreateJobIfNeeded<>(Mock3.class));
    rand = new Random();
  }

  @Test
  public void test_inputMultipleInjectionsTest_shouldWorkProperly() throws Exception {
    // Arrange
    CountDownLatch latch = new CountDownLatch(10);

    for (int i = 1; i <= 10; i++) { // Stress testing database
      TestSubscriber<String> subscriber = new TestSubscriber<String>() {
        @Override
        public void onError(Throwable e) {
          super.onError(e);
          logger.error("Error!", e);
          latch.countDown();
        }

        @Override
        public void onCompleted() {
          super.onCompleted();
          latch.countDown();
        }
      };
      insertOperation(i)
          .subscribe(subscriber);
      subscriber.assertNoErrors();
    }

    // Act
    // Assert
    latch.await();
  }


  //region Observables

  public Observable<String> insertOperation(int serviceId) {
    return Observable.<String>create(s -> {
      logger.info("Starting long insert operation from " + serviceId);
      try {
        for (int i = 0; i < 100; i++) {
          Mock3 entity = new Mock3("some_title", "some_param");
          executor.execute(new InsertJob(Mock3.class, entity));
          int minDelay = 1;
          int maxDelay = 5;
          Thread.sleep((minDelay + rand.nextInt((maxDelay - minDelay) + 1)));
          logger.info("Running injection on {} with id {}", serviceId, i);
          s.onNext("Inject from " + serviceId + " with item " + i);
        }
      } catch (InterruptedException e) {
        logger.error("Error! ", e);
      }
      logger.info("Ending long insert operation from " + serviceId);
      s.onCompleted();
    }).subscribeOn(Schedulers.computation());
  }

  //endregion
}