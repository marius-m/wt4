package lt.markmerkk.interfaces;

import java.util.List;
import net.rcarz.jiraclient.WorkLog;

/**
 * Created by mariusmerkevicius on 1/23/16.
 * Represents callback listener for the remote executor
 */
public interface IRemoteLoadListener {
  /**
   * Reports loading changes
   * @param loading is in execution
   */
  void onLoadChange(boolean loading);

  /**
   * Reports an error.
   * @param error
   */
  void onError(String error);

}
