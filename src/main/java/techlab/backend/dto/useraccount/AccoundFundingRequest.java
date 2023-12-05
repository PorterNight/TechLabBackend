package techlab.backend.dto.useraccount;

import java.math.BigDecimal;

public record AccoundFundingRequest(
        Long userUniqueID,
        BigDecimal account_funding_sum) {}
