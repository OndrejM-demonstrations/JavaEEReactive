package reactivejavaee;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.*;
import java.util.logging.*;
import static org.hamcrest.Matchers.*;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reactivestreams.*;

/**
 *
 * @author ondrejm
 */
public class CompletionStageRxJavaTest {

    public CompletionStageRxJavaTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    String testResult;

    @Test
    public void hello_world_RxJava2() throws InterruptedException {

        Flowable.fromCallable(() -> {
            Thread.sleep(1000); //  imitate expensive computation
            return "Done";
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(s -> testResult = s, Throwable::printStackTrace);

        Thread.sleep(2000); // <--- wait for the flow to finish}
        assertThat("Result", testResult, is(equalTo("Done")));
    }

    @Test
    public void combine_CF_with_RxJava2_blocking() throws InterruptedException {
        CompletionStage<String> stage = expensiveComputation();

        testResult = Flowable.fromPublisher((Subscriber<? super String> subscriber) -> {
            stage.whenComplete((result, error) -> {
                if (error != null) {
                    subscriber.onError(error);
                } else {
                    subscriber.onNext(result);
                    subscriber.onComplete();
                }
            });
        }).toList().blockingGet().iterator().next();

        Thread.sleep(2000); // <--- wait for the flow to finish}
        assertThat("Result", testResult, is(equalTo("Done")));
    }

    @Test
    public void combine_CF_with_RxJava2_chaining() throws InterruptedException {
        CompletionStage<String> stage = expensiveComputation();

        Flowable.fromPublisher((Subscriber<? super String> subscriber) -> {
            stage.whenComplete((result, error) -> {
                if (error != null) {
                    subscriber.onError(error);
                } else {
                    subscriber.onNext(result);
                    subscriber.onComplete();
                }
            });
        })
                .subscribe(s -> testResult = s, Throwable::printStackTrace);

        Thread.sleep(2000); // <--- wait for the flow to finish}
        assertThat("Result", testResult, is(equalTo("Done")));
    }

    @Test
    public void combine_CF_with_RxJava2_almost_fluent() throws InterruptedException {
        flowableFromStage(expensiveComputation())
                .subscribe(s -> testResult = s, Throwable::printStackTrace);

        Thread.sleep(2000); // <--- wait for the flow to finish}
        assertThat("Result", testResult, is(equalTo("Done")));
    }

    private <T> Flowable<T> flowableFromStage(CompletionStage<T> stage) {
        return Flowable.fromPublisher((subscriber) -> {
            stage.whenComplete((result, error) -> {
                if (error != null) {
                    subscriber.onError(error);
                } else {
                    subscriber.onNext(result);
                    subscriber.onComplete();
                }
            });
        });
    }

    @Test
    public void combine_CF_with_RxJava2_fluent() throws InterruptedException {
        expensiveComputation()
                .convert(this::flowableFromStage)
                .subscribe(s -> testResult = s, Throwable::printStackTrace);

        Thread.sleep(2000); // <--- wait for the flow to finish}
        assertThat("Result", testResult, is(equalTo("Done")));
    }

    private static RxCompletionStage<String> expensiveComputation() {
        return new RxCompletionStageWrapper(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000); //  imitate expensive computation
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            return "Done";
        }));
    }
    
}
