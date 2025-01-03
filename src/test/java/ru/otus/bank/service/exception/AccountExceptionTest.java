package ru.otus.bank.service.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sergei on 30.12.2024 13:06.
 * @project otus-unittests-2
 */


class AccountExceptionTest {

    @Test
    public void testException() {
        assertEquals("Fail", new AccountException("Fail").getMessage());
    }

}