package com.lld.urlshortnerservice.url.strategy.impl;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class Base62GeneratorTest {

    private final Base62Generator generator = new Base62Generator();

    @Test
    @DisplayName("Should map each of the 62 digit values to a distinct character")
    void shouldUse62DistinctCharacters() {

        Set<String> codes = new HashSet<>();

        for (long digit = 0; digit < 62; digit++) {
            String code = generator.generate(digit);

            assertEquals(1, code.length(), "digit " + digit);
            assertTrue(codes.add(code), "duplicate character for digit " + digit + ": " + code);
        }

        assertEquals(62, codes.size());
    }

    @Test
    @DisplayName("Should encode boundary digits in standard Base62 order")
    void shouldEncodeBoundaryDigits() {

        assertEquals("0", generator.generate(0));
        assertEquals("9", generator.generate(9));
        assertEquals("A", generator.generate(10));
        assertEquals("U", generator.generate(30));
        assertEquals("V", generator.generate(31));
        assertEquals("Z", generator.generate(35));
        assertEquals("a", generator.generate(36));
        assertEquals("z", generator.generate(61));
    }

    @Test
    @DisplayName("Should carry into a second character at 62")
    void shouldEncodeMultiCharacterCodes() {

        assertEquals("10", generator.generate(62));
        assertEquals("zz", generator.generate(62L * 61 + 61));
    }

    @Test
    @DisplayName("Should not throw for the largest long value")
    void shouldEncodeLargeValues() {

        assertDoesNotThrow(() -> generator.generate(Long.MAX_VALUE));
    }

    @Test
    @DisplayName("Should reject negative IDs")
    void shouldRejectNegativeIds() {

        assertThrows(IllegalArgumentException.class, () -> generator.generate(-1));
    }

    @Test
    @DisplayName("Should report the BASE62 strategy")
    void shouldReportStrategy() {

        assertEquals(CodeGenerationStrategy.BASE62, generator.getStrategy());
    }
}
