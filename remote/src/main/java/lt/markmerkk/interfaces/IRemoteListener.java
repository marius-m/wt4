package lt.markmerkk.interfaces;

import java.util.List;
import java.util.Map;
import net.rcarz.jiraclient.WorkLog;

/**
 * Created by mariusmerkevicius on 1/23/16.
 * Represents callback listener for the remote executor
 */
public interface IRemoteListener {
  /**
   * Reported remote logs
   * @param remoteLogs
   */
  void onWorklogDownloadComplete(Map<String, List<WorkLog>> remoteLogs);

  /**
   * Reported error when fetching data
   * @param error error
   */
  void onError(String error);

  /**
   * Reported fetching cancel
   */
  void onCancel();
}
