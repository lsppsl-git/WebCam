package org.example.service;

import java.math.BigDecimal;
import java.util.Map;

public interface WalletService {

    void transfer(Long fromUserId, Long toUserId, BigDecimal amount);

    BigDecimal getBalance(Long userId);

    void recharge(Long userId, BigDecimal amount);

    Map<String, Object> getWalletInfo(Long userId);
}