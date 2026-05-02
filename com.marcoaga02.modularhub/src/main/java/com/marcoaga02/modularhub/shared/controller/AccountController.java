package com.marcoaga02.modularhub.shared.controller;

import com.marcoaga02.modularhub.shared.dto.AccountDTO;
import com.marcoaga02.modularhub.shared.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<AccountDTO> getCurrentAccount() {
        return ResponseEntity.ok(accountService.getCurrentAccount());
    }

}
