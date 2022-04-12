package ca.bc.gov.hlth.accounttransfer.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.security.oauth2.core.oidc.IdTokenClaimNames.AUD;

@Component
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private static final Logger logger = LoggerFactory.getLogger(AudienceValidator.class);

    private static final OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, "The " + AUD + " claim is not valid",
            "https://tools.ietf.org/html/rfc6750#section-3.1");


    private static final String AUDIENCE = "LDAP-ACCOUNT-TRANSFER-API";
    private static final String CLAIM_USERNAME = "preferred_username";

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        Assert.notNull(jwt, "Token cannot be null");

        if (jwt.getAudience() != null && jwt.getAudience().contains(AUDIENCE)) {
            return OAuth2TokenValidatorResult.success();
        } else {
            // No audience means the user does not have any MSP Direct roles and should be considered Unauthorized
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            logger.warn("User {} is missing aud claim {} ", jwt.getClaim(CLAIM_USERNAME).toString(), AUDIENCE);
            return OAuth2TokenValidatorResult.failure(error);
        }
    }
}
