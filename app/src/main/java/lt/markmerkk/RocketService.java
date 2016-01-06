package lt.markmerkk;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Soon to die object
 */
public class RocketService {

  @PostConstruct
  private void initialize() {
    System.out.println("Preparing for launch");
  }

  public void run() {
    System.out.println("Launching rocket!");
  }

  @PreDestroy
  private void destroy() {
    System.out.println("Destroying rocket");
  }

}
