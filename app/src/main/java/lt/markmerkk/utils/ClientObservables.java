package lt.markmerkk.utils;

import com.google.common.base.Strings;
import rx.Observable;

/**
 * Created by mariusmerkevicius on 2/2/16.
 */
@Deprecated
public class ClientObservables {

  /**
   * Returns a modified serach phrase
   * @return
   */
  public static Observable<String> issueSearchInputObservable(String phrase) {
    return Observable.just(phrase)
        .filter(searchPhrase -> searchPhrase != null)
        .map(searchPhrase -> {
          String key = LogUtils.INSTANCE.validateTaskTitle(searchPhrase);
          if (!Strings.isNullOrEmpty(key))
            return String.format("summary ~ \"%s\" OR key = \"%s\"", searchPhrase, key);
          return String.format("summary ~ \"%s\"", searchPhrase);
        });
  }

}
