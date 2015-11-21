package lt.markmerkk.storage2.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
public class DBBaseExecutorExample1Test {
  //@Test public void shouldDie() throws Exception {
  //  // Arrange
  //  DBBaseExecutor executor = new DBMockExecutor();
  //  // Act
  //  // Assert
  //  executor.execute(new IQueryJob() {
  //    @Override public void execute(Connection connection) throws SQLException {
  //      Statement statement = connection.createStatement();
  //      statement.setQueryTimeout(30);  // set timeout to 30 sec.
  //      statement.executeUpdate("drop table if exists person");
  //      statement.executeUpdate("create table person (id integer, name string)");
  //    }
  //  });
  //}
}