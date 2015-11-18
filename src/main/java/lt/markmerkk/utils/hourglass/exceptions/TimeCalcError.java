package lt.markmerkk.utils.hourglass.exceptions;

import lt.markmerkk.utils.hourglass.HourGlass;

/**
 * Represents an error thrown when trying to calculate time.
 */
public class TimeCalcError extends IllegalStateException {
  HourGlass.Error error;

  public TimeCalcError(HourGlass.Error error) {
    super(error.getMessage());
    this.error = error;
  }

  public HourGlass.Error getError() {
    return error;
  }
}
