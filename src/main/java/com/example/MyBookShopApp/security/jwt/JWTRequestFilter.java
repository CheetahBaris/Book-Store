package com.example.MyBookShopApp.security.jwt;

import com.example.MyBookShopApp.errs.InvalidJwtTokenException;
import com.example.MyBookShopApp.security.BookstoreUserDetails;
import com.example.MyBookShopApp.services.BookstoreUserDetailsService;
import liquibase.pro.packaged.S;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTUtil jwtUtil;
    @Value("${auth.secret}")
    private String secret;
    @Value("${spring.security.oauth2.client.registration.vkontakte.client-id}")
    private String clientId;
    private List<String> blackList = new ArrayList<>();


    @Autowired
    public JWTRequestFilter(BookstoreUserDetailsService bookstoreUserDetailsService, JWTUtil jwtUtil) {
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        String username = null;
        boolean isTokenValid = true;
        Cookie[] cookies = httpServletRequest.getCookies();

        if (cookies != null) {

            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();

                    if(!jwtUtil.isTokenExpired(token)) {
                           username = jwtUtil.extractUsername(token);
                    }

                }
                for (String tokens : blackList) {
                    if (tokens.equals(token)) {
                        isTokenValid = false;
                        Logger.getLogger(this.getClass().getSimpleName()).warning("used BLACKLIST token");
                        break;
                    }
                }
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            BookstoreUserDetails userDetails = (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByUsername(username);
            try {
                if (jwtUtil.validateToken(token, userDetails) && isTokenValid) {

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (InvalidJwtTokenException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);

        } catch (IllegalArgumentException ex) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    public List<String> getBlackList() {
        return blackList;
    }

    public void setBlackList(String blackList) {
        this.blackList.add(blackList);
    }
}
