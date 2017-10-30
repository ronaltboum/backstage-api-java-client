package com.taboola.backstage.services;

import com.taboola.backstage.exceptions.BackstageAPIConnectivityException;
import com.taboola.backstage.exceptions.BackstageAPIRequestException;
import com.taboola.backstage.exceptions.BackstageAPITokenExpiredException;
import com.taboola.backstage.model.auth.BackstageAuthentication;
import com.taboola.backstage.model.auth.GrantType;
import com.taboola.backstage.model.auth.Token;
import com.taboola.backstage.model.auth.TokenDetails;


import static com.taboola.backstage.model.auth.GrantType.CLIENT_CREDENTIALS;
import static com.taboola.backstage.model.auth.GrantType.PASSWORD_AUTHENTICATION;

/**
 * Created by vladi
 * Date: 10/14/2017
 * Time: 12:23 AM
 * By Taboola
 */
public class AuthenticationServiceImpl implements AuthenticationService {

    private final CommunicationHandler communicationHandler;

    public AuthenticationServiceImpl(CommunicationHandler communicationHandler) {
        this.communicationHandler = communicationHandler;
    }

    @Override
    public BackstageAuthentication clientCredentials(String clientId, String clientSecret) throws BackstageAPITokenExpiredException, BackstageAPIConnectivityException, BackstageAPIRequestException {
        return authenticate(clientId, clientSecret, null, null, CLIENT_CREDENTIALS);
    }

    @Override
    public BackstageAuthentication passwordAuthentication(String clientId, String clientSecret, String username, String password) throws BackstageAPITokenExpiredException, BackstageAPIConnectivityException, BackstageAPIRequestException {
        return authenticate(clientId, clientSecret, username, password, PASSWORD_AUTHENTICATION);
    }

    @Override
    public BackstageAuthentication reAuthenticate(BackstageAuthentication auth) throws BackstageAPITokenExpiredException, BackstageAPIConnectivityException, BackstageAPIRequestException {
        return authenticate(auth.getClientId(), auth.getClientSecret(), auth.getUsername(), auth.getPassword(), auth.getGrantType());
    }

    @Override
    public TokenDetails getTokenDetails(BackstageAuthentication auth) throws BackstageAPITokenExpiredException, BackstageAPIConnectivityException, BackstageAPIRequestException {
        return communicationHandler.request("read token details",
                 () -> communicationHandler.getAuthService().getTokenDetails(auth.getToken().getAccessTokenForHeader()));
    }

    private BackstageAuthentication authenticate(String clientId, String clientSecret, String username, String password, GrantType grantType) throws BackstageAPITokenExpiredException, BackstageAPIConnectivityException, BackstageAPIRequestException {
        Token token;
        switch (grantType) {
            case CLIENT_CREDENTIALS:
                token = communicationHandler.request("read token", () -> communicationHandler.getAuthService()
                                                .getAuthToken(clientId, clientSecret, grantType.getValue()));
                break;
            case PASSWORD_AUTHENTICATION:
                token = communicationHandler.request("read token", () -> communicationHandler.getAuthService()
                                                .getAuthToken(clientId, clientSecret, username, password, grantType.getValue()));
                break;
            default:
                    throw new IllegalStateException("Unknown grant type, seems like a bug");
        }

        return new BackstageAuthentication(clientId, clientSecret, username, password, grantType, token);
    }
}