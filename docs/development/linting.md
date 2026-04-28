# Code Linting and Style Enforcement

This project uses automated code linting and style enforcement to maintain consistent code quality across the codebase.

## Tools

### ktlint (Active)

[ktlint](https://pinterest.github.io/ktlint/) is a Kotlin linter and formatter that enforces the official Kotlin coding conventions.

**Configuration:**
- Version: 1.5.0
- Config file: `.editorconfig`
- Integration: Gradle plugin (`org.jlleitschuh.gradle.ktlint`)

**Running ktlint:**

```bash
# Check for style violations
./gradlew ktlintCheck

# Auto-fix style violations
./gradlew ktlintFormat
```

**Enforced Rules:**
- Standard Kotlin coding conventions
- 120 character line length
- Consistent indentation (4 spaces)
- No wildcard imports
- Trailing comma rules (disabled)
- Consistent comment formatting

### Detekt (Temporarily Disabled)

[Detekt](https://detekt.dev/) is a static code analysis tool for Kotlin that detects code smells, complexity issues, and potential bugs.

**Status:** Currently disabled due to Kotlin version incompatibility
- Detekt 1.23.8 requires Kotlin 2.0.x
- Project uses Kotlin 2.3.21
- Will be re-enabled when Detekt releases a compatible version

**Configuration files (ready for future use):**
- `detekt.yml` - Detekt rule configuration
- Integration: Gradle plugin (currently commented out)

**When re-enabled, you'll be able to run:**
```bash
# Run detekt analysis
./gradlew detekt

# Generate baseline (ignore existing issues)
./gradlew detektBaseline
```

## Integration with Build

Linting is integrated into the Gradle build process:

```bash
# Runs tests, linting, and coverage checks
./gradlew check
```

The `check` task depends on:
1. `ktlintCheck` - Style enforcement
2. `test` - Unit and integration tests
3. `koverVerify` - Code coverage verification (76% minimum)

**Build will fail if:**
- ktlint finds any style violations
- Code coverage drops below 76%
- Any tests fail

## Continuous Integration

Linting runs automatically in the CI pipeline:

1. **Linting step** - Runs before build
   ```yaml
   - name: Run linting
     run: ./gradlew ktlintCheck --no-daemon
   ```

2. **Check step** - Runs after build (includes linting, tests, coverage)
   ```yaml
   - name: Run tests with coverage
     run: ./gradlew check --no-daemon
   ```

Pull requests will fail if linting issues are detected.

## IDE Integration

### IntelliJ IDEA / Android Studio

**ktlint:**
1. The `.editorconfig` file is automatically recognized
2. IDEA will apply the formatting rules when you use `Ctrl+Alt+L` (Reformat Code)
3. For explicit ktlint integration:
   - Install the "ktlint" plugin
   - Go to Settings → Tools → ktlint
   - Enable "Enable ktlint"

**Detekt (when re-enabled):**
1. Install the "Detekt" plugin
2. The plugin will automatically discover `detekt.yml`
3. View issues in the "Detekt" tool window

### VS Code

**ktlint:**
1. Install the "Kotlin Language" extension
2. Install the "EditorConfig" extension
3. The `.editorconfig` rules will be applied automatically

## Configuration Files

### `.editorconfig`

Defines code style rules for ktlint and IDEs:

```ini
[*.kt]
max_line_length = 120
indent_size = 4
indent_style = space
insert_final_newline = true
trim_trailing_whitespace = true
```

### `detekt.yml` (for future use)

Configures detekt analysis rules:
- Complexity thresholds
- Code smell detection
- Naming conventions
- Exception handling rules
- And more...

## Common Issues and Solutions

### ktlint Issues

**Issue: "Expected newline before '.'"**
```kotlin
// Bad
val foo = bar.baz().qux()

// Good
val foo = bar
    .baz()
    .qux()
```

**Issue: "Trailing comma required/forbidden"**
- This rule is currently disabled in our configuration
- If you see this, check the `.editorconfig` file

**Auto-fixing:**
Most ktlint issues can be auto-fixed:
```bash
./gradlew ktlintFormat
```

### Coverage Issues

**Issue: "Coverage is below 76%"**

1. Check which files lack coverage:
   ```bash
   ./gradlew koverHtmlReport
   open build/reports/kover/html/index.html
   ```

2. Add tests for uncovered code
3. Re-run: `./gradlew check`

## Exclusions

The following directories are excluded from linting:
- `**/generated/**` - Generated code
- `**/generated-test-sources/**` - Contract test generated sources
- `**/build/**` - Build outputs

## Best Practices

1. **Run linting before committing:**
   ```bash
   ./gradlew ktlintCheck
   ```

2. **Auto-fix style issues:**
   ```bash
   ./gradlew ktlintFormat
   ```

3. **Run full checks before pushing:**
   ```bash
   ./gradlew check
   ```

4. **Keep coverage above threshold:**
   - Current threshold: 76%
   - Write tests for new code
   - Maintain existing test coverage

5. **Follow Kotlin conventions:**
   - Use official Kotlin coding style
   - Leverage IDE auto-formatting
   - Review ktlint errors carefully

## Future Improvements

When Detekt becomes compatible with Kotlin 2.3.21:

1. Uncomment Detekt plugin in `build.gradle.kts`
2. Uncomment Detekt configuration block
3. Run `./gradlew detekt` to check for issues
4. Generate baseline: `./gradlew detektBaseline`
5. Add detekt to CI pipeline
6. Update this documentation

## References

- [ktlint Documentation](https://pinterest.github.io/ktlint/)
- [Detekt Documentation](https://detekt.dev/)
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [EditorConfig Specification](https://editorconfig.org/)
