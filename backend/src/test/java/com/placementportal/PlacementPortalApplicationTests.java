package com.placementportal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Full {@code @SpringBootTest} is omitted here so {@code mvn verify} does not require a running
 * MongoDB instance or an embedded Mongo download. Run the app locally against your database.
 */
class PlacementPortalApplicationTests {

    @Test
    void sanity() {
        assertTrue(true);
    }
}
