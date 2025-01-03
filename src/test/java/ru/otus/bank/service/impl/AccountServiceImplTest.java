package ru.otus.bank.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.bank.dao.AccountDao;
import ru.otus.bank.entity.Account;
import ru.otus.bank.entity.Agreement;
import ru.otus.bank.service.exception.AccountException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {
    @Mock
    AccountDao accountDao;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @Test
    public void testTransfer() {
        Account sourceAccount = new Account();
        sourceAccount.setAmount(new BigDecimal(100));

        Account destinationAccount = new Account();
        destinationAccount.setAmount(new BigDecimal(10));

        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(2L))).thenReturn(Optional.of(destinationAccount));

        accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));

        assertEquals(new BigDecimal(90), sourceAccount.getAmount());
        assertEquals(new BigDecimal(20), destinationAccount.getAmount());
    }

    @Test
    public void testSourceNotFound() {
        when(accountDao.findById(any())).thenReturn(Optional.empty());

        AccountException result = assertThrows(AccountException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));
            }
        });
        assertEquals("No source account", result.getLocalizedMessage());
    }


    @Test
    public void testTransferWithVerify() {
        Account sourceAccount = new Account();
        sourceAccount.setAmount(new BigDecimal(100));
        sourceAccount.setId(1L);

        Account destinationAccount = new Account();
        destinationAccount.setAmount(new BigDecimal(10));
        destinationAccount.setId(2L);

        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(2L))).thenReturn(Optional.of(destinationAccount));

        ArgumentMatcher<Account> sourceMatcher =
                argument -> argument.getId().equals(1L) && argument.getAmount().equals(new BigDecimal(90));

        ArgumentMatcher<Account> destinationMatcher =
                argument -> argument.getId().equals(2L) && argument.getAmount().equals(new BigDecimal(20));

        accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));

        verify(accountDao).save(argThat(sourceMatcher));
        verify(accountDao).save(argThat(destinationMatcher));
    }



    @Test
    void chargeCallsTest() {
        Account sourceAccount = new Account();
        sourceAccount.setAmount(new BigDecimal(100));
        sourceAccount.setId(1L);

        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(sourceAccount));

        ArgumentMatcher<Account> sourceMatcher =
                argument -> argument.getId().equals(1L) && argument.getAmount().equals(new BigDecimal(50));

        accountServiceImpl.charge(1L, new BigDecimal(50));
        verify(accountDao).save(argThat(sourceMatcher));

    }


    @Test
    public void addAccountTest(){
        Account account = new Account();
        account.setId(0L);
        account.setAmount(new BigDecimal(10));
        account.setType(1);
        account.setNumber("1111111");
        account.setAgreementId(1L);
        when(accountDao.save(any())).thenReturn(account);
        Agreement agreement = new Agreement();
        agreement.setId(1L);
        agreement.setName("contract1");
        Account result = accountServiceImpl.addAccount(agreement, "4", 1, new BigDecimal(10));
        verify(accountDao).save(any());
        assertEquals(result.toString(), account.toString());
    }

    @Test
    public void chargeTest(){
        Account account = new Account();
        account.setAmount(new BigDecimal(10));
        account.setId(1L);
        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(account));
        when(accountDao.save(any())).thenReturn(account);
        boolean result = accountServiceImpl.charge(1L, new BigDecimal(10));
        assertTrue(result);
    }

    @Test
    public void chargeExceptionTest(){
        AccountException exception = new AccountException("No source account");
        when(accountDao.findById(any())).thenThrow(exception);
        assertThrows(AccountException.class, () -> accountServiceImpl.charge(1L, new BigDecimal(100)));
    }

    @Test
    public void makeTransferExceptionTest(){
        AccountException exception = new AccountException("No destination account");
        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(new Account()));
        when(accountDao.findById(eq(2L))).thenThrow(exception);
        AccountException result = assertThrows(AccountException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));
            }
        });
        assertEquals("No destination account", result.getLocalizedMessage());
    }

    @Test
    public void getAccountsTest(){
        List<Account> list = new ArrayList<>();
        list.add(new Account());
        when(accountDao.findAll()).thenReturn(list);
        assertFalse(accountServiceImpl.getAccounts().isEmpty());
    }

}
