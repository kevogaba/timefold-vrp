package com.vrp.api.security

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class JwtOrgIdExtractor {

    fun extractOrganizationId(authentication: Authentication): UUID {
        val jwt = authentication.principal as? Jwt
            ?: throw IllegalArgumentException("Principal is not a JWT")

        val orgIdClaim = jwt.getClaimAsString("org_id")
            ?: throw IllegalArgumentException("Missing org_id claim in JWT")

        return UUID.fromString(orgIdClaim)
    }
}
