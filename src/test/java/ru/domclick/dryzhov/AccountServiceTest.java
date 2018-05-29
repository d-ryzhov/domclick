package ru.domclick.dryzhov;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import ru.domclick.dryzhov.service.AccountService;
import ru.domclick.dryzhov.web.controller.dto.Transfer;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@Import(ThreadPoolConfiguration.class)
public class AccountServiceTest {
    private static final int N = 1000;
    private static final int ACCOUNTS_N = 3;

    private static final String CLIENTS[] = {"client1", "client2", "client3"};
    private static final String ACCOUNTS[] = {"001", "002", "003"};

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ThreadPoolTaskExecutor testExecutor;

    private CountDownLatch latch;

    @Before
    public void setUp() {
        //clients are created via import.sql now
        initClient(0, 100);
        initClient(1, 200);
        initClient(2, 300);
    }

    private void initClient(int i, long money) {
        accountService.deposit(CLIENTS[i], ACCOUNTS[i], BigDecimal.valueOf(money));
    }

    @Test
    public void shuffleMoney() throws InterruptedException {
        BigDecimal moneyBegin = getAllAccountMoney();
        log.info("Initial money on all accounts: {}", moneyBegin);
        latch = new CountDownLatch(N);

        for (int i = 0; i < N; i++) {
            testExecutor.execute(this::transferMoney);
        }
        latch.await();

        BigDecimal moneyEnd = getAllAccountMoney();
        log.info("Final money on all accounts: {}", moneyEnd);
        Assert.assertEquals(moneyBegin, moneyEnd);
    }

    private BigDecimal getAllAccountMoney() {
        BigDecimal money = BigDecimal.ZERO;
        for (int i = 0; i < ACCOUNTS_N; i++) {
            BigDecimal accountMoney = accountService.getAccountMoney(CLIENTS[i], ACCOUNTS[i]);
            money = money.add(accountMoney);
        }
        return money;
    }

    private void transferMoney() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int i = random.nextInt(0, 3), j;
        do {
            j = random.nextInt(0, ACCOUNTS_N);
        } while (j == i);
        long money = random.nextLong(1, 50);

        transferMoney(CLIENTS[i], ACCOUNTS[i], CLIENTS[j], ACCOUNTS[j], BigDecimal.valueOf(money));
        latch.countDown();
    }

    private void transferMoney(String usernameFrom, String accountFrom, String usernameTo, String accountTo, BigDecimal money) {
        log.info("Transfer {} money from account {} to {}", money, accountFrom, accountTo);
        try {
            String json = objectMapper.writeValueAsString(new Transfer(usernameFrom, accountFrom, usernameTo, accountTo, money));
            mockMvc.perform(post("/account/transfer")
                    .param("usernameFrom", usernameFrom)
                    .param("accountFrom", accountFrom)
                    .param("usernameTo", usernameTo)
                    .param("accountTo", accountTo)
                    .param("money", money.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            );

            viewAccountState();
        } catch (NestedServletException e) {
            log.info("Incorrect transfer - skip");
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    private void viewAccountState() {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < ACCOUNTS_N; i++) {
            BigDecimal accountMoney = accountService.getAccountMoney(CLIENTS[i], ACCOUNTS[i]);
            sb.append(accountMoney).append('|');
        }
        log.info(sb.toString());
    }
}