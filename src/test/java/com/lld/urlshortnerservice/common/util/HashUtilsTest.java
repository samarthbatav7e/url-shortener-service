package com.lld.urlshortnerservice.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HashUtilsTest {
    @Test
    void shouldMatchKnownSha256Vector()
    {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                HashUtils.sha256Hex("abc"));
    }
    @Test
    void shouldReturn64LowercaseHexCharacters() {
        assertTrue(HashUtils.sha256Hex("https://github.com").matches("[0-9a-f]{64}"));
    }

    @Test
    void shouldBeDeterministic() {
        assertEquals(HashUtils.sha256Hex("x"), HashUtils.sha256Hex("x"));
    }

}
