package com.company.automation.stepdefinitions;

import com.company.automation.domain.VegetableInventory;
import com.company.automation.utils.TestDataUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step definitions for the cucumber-mathematics feature.
 *
 * <p>These steps are intentionally <strong>thin</strong>: they parse Gherkin values,
 * delegate business operations to the {@link VegetableInventory} domain layer and
 * perform assertions. No business calculation lives here, and no browser is involved.</p>
 */
public class CucumberMathSteps extends AbstractInventorySteps {

    private static final Logger LOG = LoggerFactory.getLogger(CucumberMathSteps.class);

    // ------------------------------------------------------------------
    // Given: establish the initial inventory state
    // ------------------------------------------------------------------

    @Given("I have {int} cucumbers in stock")
    public void iHaveCucumbers(int initial) {
        // Each scenario starts with a fresh inventory (created in the Before hook),
        // so adding here establishes the initial stock.
        inventory().addCucumbers(initial);
        LOG.info("Given: {} cucumbers in stock", inventory().getCucumberCount());
    }

    @Given("I have {int} carrots in stock")
    public void iHaveCarrots(int initial) {
        inventory().addCarrots(initial);
        LOG.info("Given: {} carrots in stock", inventory().getCarrotCount());
    }

    @Given("I have the following vegetables")
    public void iHaveTheFollowingVegetables(List<Map<String, String>> rows) {
        for (Map<String, String> row : rows) {
            String vegetable = row.get("vegetable").trim().toLowerCase();
            int quantity = TestDataUtils.parseQuantity(row.get("quantity"));
            switch (vegetable) {
                case "cucumber" -> inventory().addCucumbers(quantity);
                case "carrot" -> inventory().addCarrots(quantity);
                default -> throw new IllegalArgumentException(
                        "Unsupported vegetable '" + vegetable + "' in DataTable");
            }
            LOG.info("Given: added {} {} to the inventory", quantity, vegetable);
        }
    }

    // ------------------------------------------------------------------
    // When: perform (or attempt) a consumption operation
    // ------------------------------------------------------------------

    @When("I eat {int} cucumbers")
    public void iEatCucumbers(int eaten) {
        inventory().eatCucumbers(eaten);
        LOG.info("When: ate {} cucumbers -> {} remaining", eaten, inventory().getCucumberCount());
    }

    @When("I eat {int} carrots")
    public void iEatCarrots(int eaten) {
        inventory().eatCarrots(eaten);
        LOG.info("When: ate {} carrots -> {} remaining", eaten, inventory().getCarrotCount());
    }

    @When("I try to eat {int} carrots")
    public void iTryToEatCarrots(int eaten) {
        tryToEat("carrots", eaten);
    }

    // ------------------------------------------------------------------
    // Then: verify state / outcomes
    // ------------------------------------------------------------------

    @Then("I have {int} cucumbers")
    public void iHaveCucumbersRemaining(int expected) {
        assertEquals(expected, inventory().getCucumberCount(),
                () -> "Unexpected cucumber inventory quantity. Expected "
                        + expected + " but found " + inventory().getCucumberCount());
    }

    @Then("I have {int} carrots")
    public void iHaveCarrotsRemaining(int expected) {
        assertEquals(expected, inventory().getCarrotCount(),
                () -> "Unexpected carrot inventory quantity. Expected "
                        + expected + " but found " + inventory().getCarrotCount());
    }

    @Then("I have {int} vegetables")
    public void iHaveVegetablesTotal(int expectedTotal) {
        assertEquals(expectedTotal, inventory().getTotalVegetables(),
                () -> "Unexpected total vegetable quantity. Expected "
                        + expectedTotal + " but found " + inventory().getTotalVegetables());
    }

    @Then("the operation should be rejected")
    public void theOperationShouldBeRejected() {
        assertTrue(context().isOperationRejected(),
                "Expected the consumption operation to be rejected, but it was accepted "
                        + "(or never attempted).");
    }

    @Then("I should see the message {string}")
    public void iShouldSeeTheMessage(String expectedMessage) {
        assertEquals(expectedMessage, context().getValidationMessage(),
                () -> "Unexpected validation message. Expected: '" + expectedMessage
                        + "' but was: '" + context().getValidationMessage() + "'");
    }

    @And("I should still have {int} carrots")
    public void iShouldStillHaveCarrots(int expectedRemaining) {
        assertEquals(expectedRemaining, inventory().getCarrotCount(),
                () -> "Inventory must remain unchanged after a rejected consumption. Expected "
                        + expectedRemaining + " carrots but found "
                        + inventory().getCarrotCount());
    }
}
