package ru.domclick.dryzhov.web.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {
    @NotNull
    @Size(min = 1)
    private String usernameFrom;

    @NotNull
    @Size(min = 1)
    private String accountFrom;

    @NotNull
    @Size(min = 1)
    private String usernameTo;

    @NotNull
    @Size(min = 1)
    private String accountTo;

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal money;
}
