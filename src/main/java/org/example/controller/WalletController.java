package org.example.controller;

import org.example.common.Result;
import org.example.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping("/balance/{userId}")
    public Result<BigDecimal> getBalance(@PathVariable Long userId) {
        BigDecimal balance = walletService.getBalance(userId);
        return Result.success(balance);
    }

    @GetMapping("/info/{userId}")
    public Result<Map<String, Object>> getWalletInfo(@PathVariable Long userId) {
        Map<String, Object> info = walletService.getWalletInfo(userId);
        return Result.success(info);
    }

    @PostMapping("/transfer")
    public Result<String> transfer(
            @RequestParam Long fromUserId,
            @RequestParam Long toUserId,
            @RequestParam BigDecimal amount) {
        walletService.transfer(fromUserId, toUserId, amount);
        return Result.success("转账成功");
    }

    @PostMapping("/recharge")
    public Result<String> recharge(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount) {
        walletService.recharge(userId, amount);
        return Result.success("充值成功");
    }
}