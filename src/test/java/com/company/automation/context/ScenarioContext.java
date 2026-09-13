package com.company.automation.context;

import com.company.automation.domain.VegetableInventory;

/**
 * Holds state that is scoped to a single Cucumber scenario.
 *
 * <p>Each scenario receives a fresh instance (created in the {@code Before} hook) and
 * it is wiped in the {@code After} hook. Instances are stored in a {@link ThreadLocal}
 * keyed by the worker thread executing the scenario, so:</p>
 *
 * <ul>
 *   <li>there is <strong>no shared mutable state</strong> between concurrent scenarios,</li>
 *   <li>the design is safe under Cucumber parallel execution,</li>
 *   <li>step definitions never need to pass state around manually.</li>
 * </ul>
 */
public final class ScenarioContext {

    private static final ThreadLocal<ScenarioContext> CONTEXT = new ThreadLocal<>();

    private final VegetableInventory inventory = new VegetableInventory();
    private boolean operationRejected;
    private String validationMessage;

    private ScenarioContext() {
    }

    /**
     * Creates a fresh, empty scenario context for a new scenario.
     *
     * <p>The constructor stays private so contexts can only be created through this
     * factory (and installed via {@link #set(ScenarioContext)} by the lifecycle
     * hooks), which keeps the per-thread invariant obvious.</p>
     */
    public static ScenarioContext create() {
        return new ScenarioContext();
    }

    /**
     * Returns the context for the current scenario/thread.
     *
     * @throws IllegalStateException if no scenario is active (e.g. used outside a step)
     */
    public static ScenarioContext get() {
        ScenarioContext context = CONTEXT.get();
        if (context == null) {
            throw new IllegalStateException(
                    "ScenarioContext is not initialized. Ensure the Hooks @Before runs first.");
        }
        return context;
    }

    /** Installs a fresh context for the current scenario/thread. */
    public static void set(ScenarioContext context) {
        CONTEXT.set(context);
    }

    /** Removes the context for the current scenario/thread. */
    public static void clear() {
        CONTEXT.remove();
    }

    /** The vegetable inventory belonging to the current scenario. */
    public VegetableInventory getInventory() {
        return inventory;
    }

    /** Records that a consumption operation was rejected, along with its message. */
    public void recordRejectedOperation(String message) {
        this.operationRejected = true;
        this.validationMessage = message;
    }

    /** Whether a business operation was rejected during this scenario. */
    public boolean isOperationRejected() {
        return operationRejected;
    }

    /** The validation message produced by the rejected operation (may be {@code null}). */
    public String getValidationMessage() {
        return validationMessage;
    }
}
