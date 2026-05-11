package com.fred.labplanner.model._enum;

/**
 * Represents the analysis priority level used by the laboratory planning system.
 * <p>
 * Priorities are processed using the following strict order:
 * </p>
 *
 * <pre>
 * STAT > URGENT > ROUTINE
 * </pre>
 *
 * <p>
 * This priority order must always be respected during schedule generation,
 * regardless of sample arrival time.
 * </p>
 */
public enum EPriority {

  /**
   * Highest priority level.
   * <p>
   * Represents a critical medical emergency requiring immediate processing.
   * Expected turnaround time is less than 1 hour.
   * </p>
   */
  STAT(0),

  /**
   * Medium priority level.
   * <p>
   * Represents an important analysis required for patient care,
   * but not considered life-threatening.
   * Expected turnaround time is within the same day.
   * </p>
   */
  URGENT(1),

  /**
   * Lowest priority level.
   * <p>
   * Represents a standard non-critical analysis that can be delayed
   * if higher priority samples must be processed first.
   * </p>
   */
  ROUTINE(2);

  /**
   * Numeric priority level used for sorting.
   * <p>
   * Lower values represent higher priorities.
   * </p>
   */
  private final int level;

  /**
   * Creates a priority level.
   *
   * @param level numeric level used for scheduling priority comparison
   */
  EPriority(int level) {
    this.level = level;
  }

  /**
   * Returns the numeric priority level.
   *
   * @return priority level used for sorting and comparison
   */
  public int getLevel() {
    return level;
  }
}
