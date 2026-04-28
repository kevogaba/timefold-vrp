# ADR-004: Multi-tenancy via JWT Claims

## Status
Accepted

## Date
2024-01-18

## Context
The VRP system will serve multiple organizations (tenants) from a single deployment. We need:
- Data isolation between organizations
- Minimal performance overhead
- Simple authentication/authorization
- Support for future SaaS deployment
- Integration with existing OAuth2/OIDC providers

### Options Considered

1. **JWT Claim-Based Multi-tenancy**
   - Pros: Stateless, scales horizontally, standard OAuth2, simple implementation
   - Cons: Requires database-level filtering, potential for developer error

2. **Separate Databases Per Tenant**
   - Pros: Complete data isolation, easy to backup/restore per tenant
   - Cons: Complex connection management, doesn't scale to many tenants

3. **Separate Schemas Per Tenant**
   - Pros: Better isolation than shared tables, single database
   - Cons: Complex schema management, still limited scalability

4. **Discriminator Column + Spring Security**
   - Pros: Simple, uses Spring Security contexts
   - Cons: Easy to forget filtering, requires careful query auditing

## Decision
We will use **JWT claims** with an `org_id` claim for multi-tenancy, enforced at the repository layer.

## Rationale

1. **Scalability**: Stateless approach scales horizontally without shared session state
2. **Standard**: Uses standard JWT claims, compatible with any OAuth2/OIDC provider
3. **Simplicity**: Single database with organization_id column, straightforward querying
4. **Security**: Organization ID extracted from verified JWT, cannot be spoofed by client
5. **Performance**: No additional joins or complex query rewriting

## Implementation

### JWT Structure
```json
{
  "sub": "user-123",
  "org_id": "550e8400-e29b-41d4-a716-446655440000",
  "exp": 1234567890,
  "iat": 1234567800,
  "roles": ["USER"]
}
```

### Security Configuration
```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { }
            }
        return http.build()
    }

    @Bean
    fun jwtOrgIdExtractor(): JwtOrgIdExtractor {
        return JwtOrgIdExtractor()
    }
}
```

### Organization ID Extraction
```kotlin
@Component
class JwtOrgIdExtractor {
    fun extractOrganizationId(authentication: Authentication): UUID {
        val jwt = authentication.principal as Jwt
        val orgId = jwt.claims["org_id"] as? String
            ?: throw IllegalStateException("Missing org_id in JWT")
        return UUID.fromString(orgId)
    }
}
```

### Repository Layer Enforcement
```kotlin
interface OrderRepository {
    fun findById(id: UUID, organizationId: UUID): Order?
    fun findAllByIds(ids: List<UUID>, organizationId: UUID): List<Order>
}

@Repository
class OrderRepositoryAdapter(...) : OrderRepository {
    override fun findById(id: UUID, organizationId: UUID): Order? {
        return jpaRepository.findByIdAndOrganizationId(id, organizationId)
            ?.let { mapper.toDomain(it) }
    }
}
```

### Database Schema
All tenant-specific tables include `organization_id`:
```sql
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    -- other columns
    CONSTRAINT fk_organization FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
);

CREATE INDEX idx_orders_org_id ON orders(organization_id);
```

## Consequences

### Positive
- **Stateless**: No server-side session management
- **Scalable**: Works seamlessly with horizontal scaling
- **Standard**: Compatible with Auth0, Keycloak, AWS Cognito, etc.
- **Simple**: Single codebase, single database, straightforward queries
- **Performant**: Index on organization_id ensures fast filtering

### Negative
- **Developer Discipline**: Must remember to include organizationId in all queries
- **No Physical Isolation**: All tenant data in same database (mitigated by row-level filtering)
- **Manual Filtering**: Not automatic like Hibernate Filters (trade-off for clarity)

### Neutral
- **Testing**: Need to test cross-tenant data leakage scenarios
- **Monitoring**: Need alerts for queries missing organization_id filter

## Security Considerations

1. **JWT Validation**: Spring Security OAuth2 Resource Server validates JWT signature and expiry
2. **No Client Trust**: Organization ID comes from JWT (server-verified), not request parameters
3. **Repository Layer**: All repositories REQUIRE organizationId parameter (compile-time safety)
4. **Database Constraints**: Foreign key to organizations table ensures valid org_id
5. **Audit Logging**: All data changes logged with organization_id

## Testing Strategy

```kotlin
@Test
fun `should not find order from different organization`() {
    val org1 = UUID.randomUUID()
    val org2 = UUID.randomUUID()
    val order = createOrder(organizationId = org1)
    orderRepository.save(order)

    val result = orderRepository.findById(order.id, org2)

    assertThat(result).isNull()
}
```

## Migration Path

If we later need stronger isolation:
1. **Short-term**: Add database views per tenant
2. **Medium-term**: Implement schema-per-tenant with connection pooling
3. **Long-term**: Separate databases with data sharding

Current approach provides good balance of simplicity and isolation for expected scale (<1000 tenants).

## References
- [Spring Security OAuth 2.0 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [Multi-tenancy Strategies](https://docs.microsoft.com/en-us/azure/architecture/guide/multitenant/considerations/tenancy-models)
- [JWT RFC 7519](https://datatracker.ietf.org/doc/html/rfc7519)
