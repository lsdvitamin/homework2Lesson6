package ru.otus.bank.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sergei on 30.12.2024 11:40.
 * @project otus-unittests-2
 */
class AccountTest {

    @Test
    void setAgreementId() {
        Account account = new Account();
        account.setAgreementId(10L);
        assertEquals(10L, account.getAgreementId());
    }
}