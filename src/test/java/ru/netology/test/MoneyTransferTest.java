package ru.netology.test;

import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.TransferPage;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;

class MoneyTransferTest {

    @BeforeEach
    void before() {
        Configuration.holdBrowserOpen = true;
        Configuration.startMaximized = true;
        open("http://localhost:9999");
    }

    @Test
    void shouldTransferMoneyFromFirstCard() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var dashboardPage = verificationPage.validVerify(DataHelper.getVerificationCode(authInfo));
        var firstCardBalance = dashboardPage.getCardBalance(getFirstCardInfo());
        var secondCardBalance = dashboardPage.getCardBalance(getSecondCardInfo());
        var amount = generateValidAmount(firstCardBalance);
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceSecondCard = secondCardBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(getSecondCardInfo());
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), getFirstCardInfo());

        assertEquals(expectedBalanceFirstCard, dashboardPage.getCardBalance(getFirstCardInfo()));
        assertEquals(expectedBalanceSecondCard, dashboardPage.getCardBalance(getSecondCardInfo()));
    }

    @Test
    void shouldTransferMoneyFromSecondCard() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var dashboardPage = verificationPage.validVerify(DataHelper.getVerificationCode(authInfo));
        var firstCardBalance = dashboardPage.getCardBalance(getFirstCardInfo());
        var secondCardBalance = dashboardPage.getCardBalance(getSecondCardInfo());
        var amount = generateValidAmount(secondCardBalance);
        var expectedBalanceFirstCard = firstCardBalance + amount;
        var expectedBalanceSecondCard = secondCardBalance - amount;
        var transferPage = dashboardPage.selectCardToTransfer(getFirstCardInfo());
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), getSecondCardInfo());

        assertEquals(expectedBalanceFirstCard, dashboardPage.getCardBalance(getFirstCardInfo()));
        assertEquals(expectedBalanceSecondCard, dashboardPage.getCardBalance(getSecondCardInfo()));
    }

    @Test
    void shouldSendingEmptyForm() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var dashboardPage = verificationPage.validVerify(DataHelper.getVerificationCode(authInfo));
        var transferPage = dashboardPage.selectCardToTransfer(getFirstCardInfo());
        transferPage.emptyForm();

        transferPage.findErrorMessage("Произошла ошибка");

    }

    @Test
    void shouldTransferMoneyCansel() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var dashboardPage = verificationPage.validVerify(DataHelper.getVerificationCode(authInfo));
        var firstCardBalance = dashboardPage.getCardBalance(getFirstCardInfo());
        var secondCardBalance = dashboardPage.getCardBalance(getSecondCardInfo());
        var amount = generateValidAmount(secondCardBalance);
        var expectedBalanceFirstCard = firstCardBalance;
        var expectedBalanceSecondCard = secondCardBalance;
        var transferPage = dashboardPage.selectCardToTransfer(getFirstCardInfo());
        transferPage.canselTransfer(String.valueOf(amount), getSecondCardInfo());

        assertEquals(expectedBalanceFirstCard, dashboardPage.getCardBalance(getFirstCardInfo()));
        assertEquals(expectedBalanceSecondCard, dashboardPage.getCardBalance(getSecondCardInfo()));
    }

    @Test
    void shouldTransferAboveBalance() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var dashboardPage = verificationPage.validVerify(DataHelper.getVerificationCode(authInfo));
        var firstCardBalance = dashboardPage.getCardBalance(getFirstCardInfo());
        var secondCardBalance = dashboardPage.getCardBalance(getSecondCardInfo());
        var amount = generateInvalidAmount(secondCardBalance);
        var transferPage = dashboardPage.selectCardToTransfer(getFirstCardInfo());
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), getSecondCardInfo());

        transferPage.findErrorMessage("Недостаточно средств на карте");

    }

    @Test
    void shouldTransferMoneyWithKopecks() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var dashboardPage = verificationPage.validVerify(DataHelper.getVerificationCode(authInfo));
        var firstCardBalance = dashboardPage.getCardBalance(getFirstCardInfo());
        var secondCardBalance = dashboardPage.getCardBalance(getSecondCardInfo());
        var amount = generateAmountWithKopecks(firstCardBalance);
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceSecondCard = secondCardBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(getSecondCardInfo());
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), getFirstCardInfo());

        assertEquals(expectedBalanceFirstCard, dashboardPage.getCardBalance(getFirstCardInfo()));
        assertEquals(expectedBalanceSecondCard, dashboardPage.getCardBalance(getSecondCardInfo()));
    }
}
