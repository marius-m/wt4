package lt.markmerkk.storage2.jobs;

import java.sql.Connection;
import java.sql.SQLException;
import lt.markmerkk.storage2.database.DBQueryCreateHelper;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;
import lt.markmerkk.storage2.entities.SimpleLog;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * A job responsible for creating a new table for the {@link SimpleLog}
 */
public class CreateLogJob implements IQueryJob {

  public CreateLogJob() {
    //DBQueryCreateHelper creationHelper = new
  }

  @Override public void execute(Connection connection) throws SQLException {

  }
}
