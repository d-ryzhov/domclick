package dryzhov.domclick;

import dryzhov.domclick.service.AccountService;
import dryzhov.domclick.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.NestedServletException;

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
    private static final int N = 2;
    private static final int ACCOUNTS_N = 3;

    private static final String CLIENTS[] = {"client1", "client2", "client3"};
    private static final String ACCOUNTS[] = {"001", "002", "003"};

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ThreadPoolTaskExecutor testExecutor;

    private CountDownLatch latch;

    @Before
    public void setUp() {
        createClient(0, 100);
        createClient(1, 200);
        createClient(2, 300);
    }

    private void createClient(int i, long money) {
        clientService.createClient(CLIENTS[i]);
        accountService.createAccount(CLIENTS[i], ACCOUNTS[i]);
        accountService.deposit(CLIENTS[i], ACCOUNTS[i], BigDecimal.valueOf(money));
    }

    @Test
    public void shuffleMoney() throws InterruptedException {
        log.info("Initial money on all accounts: {}", getAllAccountMoney());
        latch = new CountDownLatch(N);

        for (int i = 0; i < N; i++) {
            testExecutor.execute(this::transferMoney);
        }
        latch.await();

        log.info("Final money on all accounts: {}", getAllAccountMoney());
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
            ResultActions result = mockMvc.perform(post("/account/transfer")
                    .param("usernameFrom", usernameFrom)
                    .param("accountFrom", accountFrom)
                    .param("usernameTo", usernameTo)
                    .param("accountTo", accountTo)
                    .param("money", money.toString())
            );

            viewAccountState();
        } catch (NestedServletException e) {
            log.info("Incorrect transfer - skip");
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    private void viewAccountState() {
        StringBuilder sb = new StringBuilder('|');
        for (int i = 0; i < ACCOUNTS_N; i++) {
            BigDecimal accountMoney = accountService.getAccountMoney(CLIENTS[i], ACCOUNTS[i]);
            sb.append(accountMoney).append('|');
        }
        log.info(sb.toString());
    }
}