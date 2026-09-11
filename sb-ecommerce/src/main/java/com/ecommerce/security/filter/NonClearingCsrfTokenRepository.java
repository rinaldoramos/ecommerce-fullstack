package com.ecommerce.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;

// CsrfAuthenticationStrategy clears the CSRF token (saveToken(null, ...)) on every
// request where a new Authentication is established - which, with a stateless JWT
// filter re-authenticating from the cookie on every request, is EVERY authenticated
// request. That wipes the token before the client can reuse it. Since there's no
// session to fixate in a stateless design anyway, this repository just ignores
// "clear" requests and keeps delegating everything else.
public class NonClearingCsrfTokenRepository implements CsrfTokenRepository {

    private final CsrfTokenRepository delegate;

    public NonClearingCsrfTokenRepository(CsrfTokenRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return delegate.generateToken(request);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        if (token == null) {
            return;
        }
        delegate.saveToken(token, request, response);
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        return delegate.loadToken(request);
    }
}
