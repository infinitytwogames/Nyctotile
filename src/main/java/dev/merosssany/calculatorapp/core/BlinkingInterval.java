package dev.merosssany.calculatorapp.core; // Or wherever you want to put utility classes

public class BlinkingInterval {
    private long lastPhaseChangeTime; // Stores System.nanoTime() when the current phase started
    private final long activeDurationNanos;   // Duration for the "active" phase (e.g., light is on)
    private final long inactiveDurationNanos; // Duration for the "inactive" phase (e.g., light is off)
    private boolean isCurrentlyActive;        // Current state: true = active phase, false = inactive phase
    private final Runnable actionOnActiveStart; // Optional action to run when it becomes active
    private boolean run = true;               // Overall control: true = blinking, false = paused

    /**
     * Creates a new blinking/pulsing interval timer.
     * This timer cycles between an active phase and an inactive phase.`
     * The associated action is optionally triggered at the start of each active phase.
     *
     * @param activeMillis The duration (in milliseconds) for which the interval is "active" (e.g., a light is on, or a state is true).
     * @param inactiveMillis The duration (in milliseconds) for which the interval is "inactive" (e.g., a light is off, or a state is false).
     * @param actionOnActiveStart An optional {@code Runnable} to execute each time the active phase begins. Can be {@code null}.
     * @throws IllegalArgumentException if durations are negative.
     */
    public BlinkingInterval(long activeMillis, long inactiveMillis, Runnable actionOnActiveStart) {
        if (activeMillis < 0 || inactiveMillis < 0) {
            throw new IllegalArgumentException("Durations must be non-negative.");
        }
        this.activeDurationNanos = activeMillis * 1_000_000L;
        this.inactiveDurationNanos = inactiveMillis * 1_000_000L;
        this.actionOnActiveStart = actionOnActiveStart;

        // Start in the active phase
        this.isCurrentlyActive = true;
        this.lastPhaseChangeTime = System.nanoTime();
        // If you want the action to fire immediately upon creation when starting active, uncomment:
        // if (this.actionOnActiveStart != null) {
        //     this.actionOnActiveStart.run();
        // }
    }

    /**
     * Call this method from your main application loop to update the interval's state.
     * This method will manage the transition between active and inactive phases
     * and execute the action when the active phase starts.
     *
     * @return true if the interval is currently in its active phase, false otherwise.
     */
    public boolean update() {
        if (!run) {
            return isCurrentlyActive; // If paused, just return current state without updating
        }

        long currentTime = System.nanoTime();
        long elapsedInCurrentPhase = currentTime - lastPhaseChangeTime;

        if (isCurrentlyActive) {
            // We are in the "active" phase (e.g., light is on)
            if (elapsedInCurrentPhase >= activeDurationNanos) {
                // Active phase has ended, switch to inactive
                isCurrentlyActive = false;
                lastPhaseChangeTime = currentTime; // Reset phase timer
            }
        } else {
            // We are in the "inactive" phase (e.g., light is off)
            if (elapsedInCurrentPhase >= inactiveDurationNanos) {
                // Inactive phase has ended, switch to active
                isCurrentlyActive = true;
                lastPhaseChangeTime = currentTime; // Reset phase timer
                // Execute the action when the active phase begins
                if (actionOnActiveStart != null) {
                    actionOnActiveStart.run();
                }
            }
        }
        return isCurrentlyActive;
    }

    /**
     * Checks if the interval is currently in its active phase.
     * This can be polled by rendering or logic code to determine what to draw or do.
     *
     * @return true if the interval is currently active, false otherwise.
     */
    public boolean isCurrentlyActive() {
        return isCurrentlyActive;
    }

    /**
     * Starts (or resumes) the blinking cycle. If it was paused, it will resume from its last state.
     * The internal timer for the current phase is also reset to avoid immediate phase changes.
     */
    public void start() {
        if (!run) { // Only reset time if it was actually paused, to prevent immediate state change
            lastPhaseChangeTime = System.nanoTime();
        }
        run = true;
    }

    /**
     * Stops (pauses) the blinking cycle. The internal state (active/inactive) will freeze.
     */
    public void end() {
        run = false;
    }

    /**
     * Resets the entire blinking cycle, forcing it to start anew from the active phase.
     * The timer is reset to the current time, and the interval immediately becomes active.
     */
    public void reset() {
        this.isCurrentlyActive = true;
        this.lastPhaseChangeTime = System.nanoTime();
        this.run = true; // Ensure it's running after reset
        // If you want the action to fire on initial reset when starting active, uncomment:
        // if (this.actionOnActiveStart != null) {
        //     this.actionOnActiveStart.run();
        // }
    }

    // Optional getters for durations if needed externally
    public long getActiveDurationMillis() { return activeDurationNanos / 1_000_000L; }
    public long getInactiveDurationMillis() { return inactiveDurationNanos / 1_000_000L; }
}