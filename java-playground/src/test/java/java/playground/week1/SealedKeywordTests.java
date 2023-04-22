package java.playground.week1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SealedKeywordTest {

    @Test
    public void testCheckIntegrityOfBankAccountObject() {
        CheckingAccount checkingAccount = new CheckingAccount();
        checkingAccount.setAccountNumber("123456789");
        checkingAccount.setBalance(1000.00);
        assertEquals("123456789", checkingAccount.getAccountNumber());
    }



}