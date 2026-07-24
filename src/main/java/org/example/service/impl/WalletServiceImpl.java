package org.example.service.impl;

import org.example.common.BusinessException;
import org.example.entity.TransactionLog;
import org.example.entity.Wallet;
import org.example.mapper.TransactionLogMapper;
import org.example.mapper.WalletMapper;
import org.example.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private TransactionLogMapper transactionLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        if (fromUserId.equals(toUserId)) {
            throw BusinessException.of(400, "不能向自己转账");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessException.of(400, "转账金额必须大于0");
        }

        String transactionNo = generateTransactionNo();

        TransactionLog existingLog = transactionLogMapper.findByTransactionNo(transactionNo);
        if (existingLog != null && existingLog.getStatus().equals("SUCCESS")) {
            return;
        }

        Wallet fromWallet = walletMapper.findByUserIdForUpdate(fromUserId);
        if (fromWallet == null) {
            throw BusinessException.of(404, "付款方钱包不存在");
        }

        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw BusinessException.of(400, "余额不足");
        }

        Wallet toWallet = walletMapper.findByUserIdForUpdate(toUserId);
        if (toWallet == null) {
            throw BusinessException.of(404, "收款方钱包不存在");
        }

        int fromRows = walletMapper.deduct(fromUserId, amount, fromWallet.getVersion());
        if (fromRows == 0) {
            throw BusinessException.of(500, "扣款失败，请重试");
        }

        int toRows = walletMapper.add(toUserId, amount, toWallet.getVersion());
        if (toRows == 0) {
            throw BusinessException.of(500, "入账失败，请重试");
        }

        TransactionLog log = TransactionLog.builder()
                .transactionNo(transactionNo)
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .amount(amount)
                .type("TRANSFER")
                .status("SUCCESS")
                .remark("转账")
                .createdAt(LocalDateTime.now())
                .build();

        transactionLogMapper.insert(log);
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        Wallet wallet = walletMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUserId, userId));
        return wallet != null ? wallet.getBalance() : BigDecimal.ZERO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recharge(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessException.of(400, "充值金额必须大于0");
        }

        Wallet wallet = walletMapper.findByUserIdForUpdate(userId);
        if (wallet == null) {
            wallet = Wallet.builder()
                    .userId(userId)
                    .balance(BigDecimal.ZERO)
                    .version(0)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            walletMapper.insert(wallet);
        }

        walletMapper.add(userId, amount, wallet.getVersion());

        TransactionLog log = TransactionLog.builder()
                .transactionNo(generateTransactionNo())
                .fromUserId(null)
                .toUserId(userId)
                .amount(amount)
                .type("RECHARGE")
                .status("SUCCESS")
                .remark("充值")
                .createdAt(LocalDateTime.now())
                .build();

        transactionLogMapper.insert(log);
    }

    @Override
    public Map<String, Object> getWalletInfo(Long userId) {
        Map<String, Object> info = new HashMap<>();
        BigDecimal balance = getBalance(userId);
        info.put("userId", userId);
        info.put("balance", balance);
        return info;
    }

    private String generateTransactionNo() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }
}