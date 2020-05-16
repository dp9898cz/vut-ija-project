package ija_project;

import java.time.LocalTime;

/**
 * Interface for elements to update by timer
 * Calling by timer
 * @author Daniel Pátek (xpatek08)
 * @author Daniel Čechák (xcecha06)
 * @version 1.0
 */
public interface TimerMapUpdate {
    /**
     * Update current element
     * @param l Current time
     */
    void update(LocalTime l);
}
