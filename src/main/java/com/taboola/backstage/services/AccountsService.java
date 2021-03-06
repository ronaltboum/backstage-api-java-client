package com.taboola.backstage.services;

import com.taboola.backstage.exceptions.BackstageAPIConnectivityException;
import com.taboola.backstage.exceptions.BackstageAPIRequestException;
import com.taboola.backstage.exceptions.BackstageAPIUnauthorizedException;
import com.taboola.backstage.model.Account;
import com.taboola.backstage.model.Results;
import com.taboola.backstage.model.auth.BackstageAuthentication;

/**
 * Accounts service allow fetching data regarding different account types (Partners, Publishers)
 *
 * @author vladi
 * @version 1.0
 */
public interface AccountsService {

    /**
     * In order to be able to block specific Publishers, one must know the Publisher's account_id. Need in order to fetch a list of all Publisher Accounts in the Taboola network.
     * @param auth Authentication object {@link BackstageAuthentication}
     * @return Collection of {@link Account}
     * @throws BackstageAPIUnauthorizedException {@link com.taboola.backstage.model.auth.Token Token} is expired or bad credentials
     * @throws BackstageAPIConnectivityException Connectivity issues (HTTP status 5xx)
     * @throws BackstageAPIRequestException Bad request (HTTP status 4xx)
     */
    Results<Account> readPublishersUnderTaboolaNetwork(BackstageAuthentication auth) throws BackstageAPIUnauthorizedException, BackstageAPIConnectivityException, BackstageAPIRequestException;
}
