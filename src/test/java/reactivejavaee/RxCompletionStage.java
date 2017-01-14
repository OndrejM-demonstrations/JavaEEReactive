package reactivejavaee;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public interface RxCompletionStage<T> extends CompletionStage<T> {
    <NEW> NEW convert(Function<CompletionStage<T>, NEW> converter);
}
