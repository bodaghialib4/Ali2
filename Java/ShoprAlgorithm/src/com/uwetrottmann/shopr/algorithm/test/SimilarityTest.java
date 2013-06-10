
package com.uwetrottmann.shopr.algorithm.test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;

import com.uwetrottmann.shopr.algorithm.Similarity;
import com.uwetrottmann.shopr.algorithm.model.Attributes;
import com.uwetrottmann.shopr.algorithm.model.ClothingType;
import com.uwetrottmann.shopr.algorithm.model.Color;

import org.junit.Test;

public class SimilarityTest {

    @Test
    public void testSimilarity() {
        Attributes a1 = new Attributes();
        a1.color(new Color(Color.Value.RED));
        a1.type(new ClothingType(ClothingType.Value.DRESS));

        Attributes a2 = new Attributes();
        a2.color(new Color(Color.Value.BLACK));
        a2.type(null);

        // different color
        assertThat(Similarity.similarity(a1, a2)).isEqualTo(0.0);

        // same color
        a1.color(new Color(Color.Value.BLACK));
        assertThat(Similarity.similarity(a1, a2)).isEqualTo(1.0);

        // throw on no comparable attributes
        a1.color(null);
        try {
            Similarity.similarity(a1, a2);

            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
        }

        // different color, same type
        a1.color(new Color(Color.Value.RED));
        a2.type(new ClothingType(ClothingType.Value.DRESS));
        assertThat(Similarity.similarity(a1, a2)).isEqualTo(0.5);
    }

}