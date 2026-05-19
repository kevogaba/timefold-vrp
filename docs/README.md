# Timefold VRP Documentation

Welcome to the comprehensive documentation for the Timefold Vehicle Routing Problem (VRP) System.

## Documentation Structure

### 📐 [Architecture](architecture/)
Detailed system architecture, component diagrams, and design patterns.
- [System Overview](architecture/system-overview.md)
- [Hexagonal Architecture](architecture/hexagonal-architecture.md)
- [Data Flow](architecture/data-flow.md)
- [Technology Stack](architecture/tech-stack.md)

### 📝 [Architecture Decision Records (ADRs)](adr/)
Historical record of important architectural decisions and their rationale.
- [ADR-001: Timefold Solver for VRP Optimization](adr/001-timefold-solver-choice.md)
- [ADR-002: Temporal for Workflow Orchestration](adr/002-temporal-orchestration.md)
- [ADR-003: Hexagonal Architecture Pattern](adr/003-hexagonal-architecture.md)
- [ADR-004: Multi-tenancy via JWT](adr/004-jwt-multitenancy.md)
- [ADR-005: Spring Modulith for Module Boundaries](adr/005-spring-modulith.md)

### 🔌 [API Documentation](api/)
REST API specifications, examples, and contract tests.
- [API Overview](api/overview.md)
- [Authentication](api/authentication.md)
- [Job Management](api/jobs.md)
- [Trip Retrieval](api/trips.md)
- [Error Handling](api/errors.md)

### 🚀 [Deployment](deployment/)
Deployment guides for various environments.
- [Docker Deployment](deployment/docker.md)
- [Kubernetes Deployment](deployment/kubernetes.md)
- [GraalVM Native Image](deployment/native-image.md)
- [Production Checklist](deployment/production-checklist.md)

### 💻 [Development](development/)
Developer guides, setup instructions, and best practices.
- [Getting Started](development/getting-started.md)
- [Development Workflow](development/workflow.md)
- [Testing Strategy](development/testing.md)
- [Code Style Guide](development/code-style.md)
- [Contributing Guidelines](development/contributing.md)

## Quick Links

- [README](../README.md) - Project overview and quick start
- [CLAUDE.md](../claude.md) - AI assistant context and development history
- [LICENSE](../LICENSE) - Apache License 2.0

## Support

For questions or issues:
- **Issues**: https://github.com/kevogaba/timefold-vrp/issues
- **Discussions**: https://github.com/kevogaba/timefold-vrp/discussions
