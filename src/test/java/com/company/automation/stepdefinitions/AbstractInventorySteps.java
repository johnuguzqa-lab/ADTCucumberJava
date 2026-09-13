package com.company.automation.stepdefinitions;

import com.company.automation.context.ScenarioContext;
import com.company.automation.domain.InvalidQuantityException;
import com.company.automation.domain.VegetableInventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Shared, non-annotated helpers for the vegetable-inventory step definition classes.
 *
 * <p>Deliberately contains <strong>no</strong> {@code @Given}/{@code @When}/{@code @Then}
 * annotations, so Cucumber never registers this class as glue. Concrete per-feature step
 * classes ({@link CucumberMathSteps}, {@link VegetableInventoryEdgeCaseSteps}) extend it to
 * share the scenario/inventory accessors and the "rejected operation" helpers without
 * duplicating logic, while keeping each feature's steps in its own file.</p>
 */
public abstract class AbstractInventorySteps {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractInventorySteps.class);

    protected ScenarioContext context() {
        return ScenarioContext.get();
    }

    protected VegetableInventory inventory() {
        return context().getInventory();
    }

    /**
     * Attempts to consume the given quantity of a vegetable and records the rejection.
     * Always asserts that the inventory is preserved (it must never go negative).
     */
    protected void tryToEat(String vegetable, int quantity) {
        int before = countOf(vegetable);
        try {
            eat(vegetable, quantity);
            LOG.info("Consumption of {} {} was accepted (unexpected for this scenario)", quantity, vegetable);
        } catch (InvalidQuantityException e) {
            LOG.warn("Consumption rejected: '{}' (available={}, requested={})",
                    e.getMessage(), e.getAvailable(), e.getRequested());
            context().recordRejectedOperation(e.getMessage());
        }
        assertEquals(before, countOf(vegetable),
                () -> "Inventory must be preserved after a rejected consumption attempt. Expected "
                        + before + " " + vegetable + " but found " + countOf(vegetable)
                        + " (inventory became negative!)");
    }

    /**
     * Attempts to add the given quantity of a vegetable and records the rejection.
     * Always asserts that the inventory is preserved (negative quantities must not mutate it).
     */
    protected void tryToAdd(String vegetable, int quantity) {
        int before = countOf(vegetable);
        try {
            add(vegetable, quantity);
            LOG.info("Adding {} {} was accepted (unexpected for this scenario)", quantity, vegetable);
        } catch (InvalidQuantityException e) {
            LOG.warn("Add rejected: '{}' (available={}, requested={})",
                    e.getMessage(), e.getAvailable(), e.getRequested());
            context().recordRejectedOperation(e.getMessage());
        }
        assertEquals(before, countOf(vegetable),
                () -> "Inventory must be preserved after a rejected add attempt. Expected "
                        + before + " " + vegetable + " but found " + countOf(vegetable));
    }

    protected int countOf(String vegetable) {
        return switch (vegetable) {
            case "cucumbers" -> inventory().getCucumberCount();
            case "carrots" -> inventory().getCarrotCount();
            default -> throw new IllegalArgumentException("Unsupported vegetable '" + vegetable + "'");
        };
    }

    protected void eat(String vegetable, int quantity) {
        switch (vegetable) {
            case "cucumbers" -> inventory().eatCucumbers(quantity);
            case "carrots" -> inventory().eatCarrots(quantity);
            default -> throw new IllegalArgumentException("Unsupported vegetable '" + vegetable + "'");
        }
    }

    protected void add(String vegetable, int quantity) {
        switch (vegetable) {
            case "cucumbers" -> inventory().addCucumbers(quantity);
            case "carrots" -> inventory().addCarrots(quantity);
            default -> throw new IllegalArgumentException("Unsupported vegetable '" + vegetable + "'");
        }
    }
}
