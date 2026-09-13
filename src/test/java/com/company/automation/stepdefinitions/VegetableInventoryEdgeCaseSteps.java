package com.company.automation.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Step definitions for the {@code vegetable-inventory-edge-cases} feature.
 *
 * <p>Thin steps that drive the boundary, negative and real-life flows through the
 * {@link com.company.automation.domain.VegetableInventory} domain layer. Shared helpers for
 * rejected operations and inventory access are inherited from {@link AbstractInventorySteps},
 * so this class contains only the Gherkin bindings that belong to its own feature file.</p>
 */
public class VegetableInventoryEdgeCaseSteps extends AbstractInventorySteps {

    private static final Logger LOG = LoggerFactory.getLogger(VegetableInventoryEdgeCaseSteps.class);

    @When("I try to eat {int} cucumbers")
    public void iTryToEatCucumbers(int eaten) {
        tryToEat("cucumbers", eaten);
    }

    @When("I try to add {int} carrots")
    public void iTryToAddCarrots(int quantity) {
        tryToAdd("carrots", quantity);
    }

    @When("I try to add {int} cucumbers")
    public void iTryToAddCucumbers(int quantity) {
        tryToAdd("cucumbers", quantity);
    }

    @When("I restock {int} cucumbers")
    public void iRestockCucumbers(int quantity) {
        inventory().addCucumbers(quantity);
        LOG.info("When: restocked {} cucumbers -> {} in stock", quantity, inventory().getCucumberCount());
    }

    @And("I should still have {int} cucumbers")
    public void iShouldStillHaveCucumbers(int expectedRemaining) {
        assertEquals(expectedRemaining, inventory().getCucumberCount(),
                () -> "Inventory must remain unchanged after a rejected consumption. Expected "
                        + expectedRemaining + " cucumbers but found "
                        + inventory().getCucumberCount());
    }
}
