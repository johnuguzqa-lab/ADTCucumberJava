package com.company.automation.domain;

/**
 * Business object representing a vegetable inventory.
 *
 * <p>Implements the core business rule used by the Cucumber feature:
 * <strong>a user cannot consume more vegetables than are currently available</strong>.
 * Consumption that would drive a count below zero is rejected <em>before</em> the state
 * is mutated, so a rejected operation always leaves the inventory unchanged and never
 * produces a negative quantity.</p>
 *
 * <p>This class is intentionally free of any Cucumber / Selenium / framework concerns so
 * the business rules can be unit-tested and reasoned about in isolation.</p>
 */
public class VegetableInventory {

    public static final String CANNOT_EAT_MORE_CUCUMBERS_MESSAGE =
            "Cannot eat more cucumbers than are available";

    public static final String CANNOT_EAT_MORE_CARROTS_MESSAGE =
            "Cannot eat more carrots than are available";

    private int cucumberCount;
    private int carrotCount;

    /** Adds the given number of cucumbers to the inventory. */
    public void addCucumbers(int quantity) {
        requireNotNegative(quantity, "cucumbers");
        cucumberCount += quantity;
    }

    /** Adds the given number of carrots to the inventory. */
    public void addCarrots(int quantity) {
        requireNotNegative(quantity, "carrots");
        carrotCount += quantity;
    }

    /**
     * Consumes the given number of cucumbers.
     *
     * @param quantity number of cucumbers to eat
     * @throws InvalidQuantityException if the quantity is negative or exceeds the available stock
     */
    public void eatCucumbers(int quantity) {
        requireNotNegative(quantity, "cucumbers");
        if (quantity > cucumberCount) {
            throw new InvalidQuantityException(
                    CANNOT_EAT_MORE_CUCUMBERS_MESSAGE, cucumberCount, quantity);
        }
        cucumberCount -= quantity;
    }

    /**
     * Consumes the given number of carrots.
     *
     * @param quantity number of carrots to eat
     * @throws InvalidQuantityException if the quantity is negative or exceeds the available stock
     */
    public void eatCarrots(int quantity) {
        requireNotNegative(quantity, "carrots");
        if (quantity > carrotCount) {
            throw new InvalidQuantityException(
                    CANNOT_EAT_MORE_CARROTS_MESSAGE, carrotCount, quantity);
        }
        carrotCount -= quantity;
    }

    /** Returns the current cucumber stock (never negative). */
    public int getCucumberCount() {
        return cucumberCount;
    }

    /** Returns the current carrot stock (never negative). */
    public int getCarrotCount() {
        return carrotCount;
    }

    /** Returns the total number of vegetables currently in stock. */
    public int getTotalVegetables() {
        return cucumberCount + carrotCount;
    }

    private void requireNotNegative(int quantity, String vegetable) {
        if (quantity < 0) {
            throw new InvalidQuantityException(
                    "Cannot use a negative quantity of " + vegetable, 0, quantity);
        }
    }
}
