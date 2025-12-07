# GitHub Actions CI/CD Setup Guide

This guide explains how to set up and use the GitHub Actions workflows for this project.

## Workflows Overview

### 1. `ci-cd.yml` - Reusable Workflow

The main workflow that handles:
- Build and test
- Security scanning
- Docker build and push
- Deployment

**This workflow is reusable** - it can be called from other workflows.

### 2. `build.yml` - Main Build

Triggers on:
- Push to `main` or `develop`
- Pull requests to `main` or `develop`

Uses the reusable `ci-cd.yml` workflow.

Passes through the triggering GitHub event name so reusable-job conditions (like deployments) behave the same way as native runs.

### 3. `release.yml` - Release Pipeline

Triggers on:
- Tags matching `v*.*.*` (e.g., v1.0.0)

Creates production releases with full security scanning.

## Initial Setup

### 1. Enable GitHub Actions

1. Go to your repository Settings
2. Navigate to Actions → General
3. Ensure "Allow all actions and reusable workflows" is selected
4. Save changes

### 2. Configure Docker Registry (Docker Hub by default)

The workflows build and push Docker images to a Docker registry. By default, they target Docker Hub (`docker.io`).

**Set up access for Docker Hub:**

1. Create a Docker Hub account and namespace.
2. Add repository-scoped credentials in Docker Hub (recommended: access tokens).
3. In GitHub, go to Settings → Secrets and variables → Actions and add:
   - `DOCKER_USERNAME`: Your Docker Hub username or namespace.
   - `DOCKER_PASSWORD`: Your Docker Hub password or access token.

**Use a different registry:**

Pass the registry host through the `docker-registry` input in your calling workflow:

```yaml
with:
  docker-registry: 'registry.example.com'
secrets:
  DOCKER_USERNAME: ${{ secrets.DOCKER_USERNAME }}
  DOCKER_PASSWORD: ${{ secrets.DOCKER_PASSWORD }}
```

### 3. Optional: Configure Secrets

Add these secrets in Settings → Secrets and variables → Actions:

#### Required for SonarCloud (Optional)

- `SONAR_TOKEN`: Your SonarCloud token
  - Get from: https://sonarcloud.io/account/security
  - Update `pom.xml` with your organization and project key

### 4. Configure Branch Protection

For `main` branch:

1. Settings → Branches → Add rule
2. Branch name pattern: `main`
3. Enable:
   - Require pull request reviews
   - Require status checks to pass
   - Require branches to be up to date
4. Select required status checks:
   - `Build and Test`
   - `Security Scan`
   - `Build and Push Docker Image`

## Workflow Usage

### Trigger Build on Push

```bash
# Make changes
git add .
git commit -m "feat: add new feature"

# Push to trigger workflow
git push origin main
```

The workflow will:
1. Build the application
2. Run all tests
3. Generate coverage reports
4. Scan for vulnerabilities
5. Build and push Docker image
6. Deploy to staging (if on `develop` branch)

### Create a Release

```bash
# Create and push tag
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

The release workflow will:
1. Run full test suite
2. Perform security scans
3. Build multi-platform Docker images
4. Generate SBOM
5. Create GitHub Release
6. Deploy to production (manual approval)

### Manual Workflow Trigger

```bash
# Using GitHub CLI
gh workflow run build.yml

# Or use the GitHub web interface:
# Actions → Select workflow → Run workflow
```

## Understanding Workflow Outputs

### Build Artifacts

After each workflow run, artifacts are available:

1. **application-jar**: Built JAR file
2. **sbom**: Software Bill of Materials
3. **dependency-check-report**: Security scan results

**Download artifacts:**

```bash
# Using GitHub CLI
gh run list
gh run download <run-id>

# Or from GitHub UI:
# Actions → Select run → Artifacts section
```

### Docker Images

Images are tagged automatically:

| Event | Tags Created |
|-------|-------------|
| Push to `main` | `latest`, `main-<sha>` |
| Push to `develop` | `develop`, `develop-<sha>` |
| Tag `v1.2.3` | `v1.2.3`, `1.2`, `1`, `latest` |
| Pull Request | `pr-<number>` |

**Pull an image from Docker Hub (default):**

```bash
# Latest from main
docker pull docker.io/your-org/testcontainers-demo:latest

# Specific version
docker pull docker.io/your-org/testcontainers-demo:v1.0.0

# Specific commit
docker pull docker.io/your-org/testcontainers-demo:main-abc1234
```

## Customizing Workflows

### Modify Build Parameters

Edit `.github/workflows/build.yml`:

```yaml
jobs:
  build:
    uses: ./.github/workflows/ci-cd.yml
    with:
      java-version: '21'                    # Change Java version
      docker-platforms: 'linux/amd64'      # Single platform for speed
      enable-security-scan: false          # Disable scanning
      run-tests: true                      # Always run tests
```

### Add Custom Steps

Add steps to `.github/workflows/build.yml`:

```yaml
jobs:
  build:
    uses: ./.github/workflows/ci-cd.yml
    # ... existing config ...

  custom-job:
    runs-on: ubuntu-latest
    needs: build
    steps:
      - name: Custom step
        run: echo "Custom logic here"
```

### Change Docker Registry

Use a different registry:

```yaml
with:
  docker-registry: 'docker.io'
secrets:
  DOCKER_USERNAME: ${{ secrets.DOCKER_USERNAME }}
  DOCKER_PASSWORD: ${{ secrets.DOCKER_PASSWORD }}
```

## Workflow Stages Explained

### Stage 1: Build and Test

```yaml
- Checkout code
- Set up JDK 17
- Cache Maven dependencies
- Build with Maven
- Run tests
- Generate coverage report
- Upload to Codecov
- SonarCloud scan (optional)
- Upload build artifact
```

**Duration:** ~3-5 minutes

### Stage 2: Security Scan

```yaml
- OWASP Dependency Check
  - Scans all dependencies
  - Fails on CVSS ≥ 7
  - Generates HTML report

- CodeQL Analysis
  - Static code analysis
  - Finds security vulnerabilities
  - Reports to Security tab
```

**Duration:** ~5-10 minutes

### Stage 3: Docker Build

```yaml
- Set up QEMU (multi-platform)
- Set up Docker Buildx
- Login to registry
- Extract metadata (tags)
- Build and push image
- Run Trivy scan
- Generate SBOM
- Upload artifacts
```

**Duration:** ~5-15 minutes (depending on platforms)

### Stage 4: Deployment

```yaml
# Staging (automatic)
- Triggered on: push to develop
- Environment: staging
- Approval: not required

# Production (manual)
- Triggered on: version tags
- Environment: production
- Approval: required
```

**Duration:** Varies by deployment method

## Monitoring Workflows

### View Workflow Status

```bash
# List recent runs
gh run list

# Watch a running workflow
gh run watch

# View logs
gh run view <run-id> --log
```

### Check Build Status Badge

Add to your README:

```markdown
![CI/CD](https://github.com/your-org/testcontainers-demo/actions/workflows/build.yml/badge.svg)
```

### Notifications

Configure in: Settings → Notifications

Options:
- Email on workflow failure
- Slack/Discord webhooks
- GitHub mobile app alerts

## Troubleshooting

### Workflow Not Triggering

**Check:**
1. Workflow file is in `.github/workflows/`
2. YAML syntax is valid: `yamllint .github/workflows/`
3. Branch matches trigger pattern
4. Actions are enabled in repository settings

### Build Failures

**Common issues:**

1. **Test failures**
   ```bash
   # Run tests locally
   mvn test
   ```

2. **Dependency issues**
   ```bash
   # Clear cache
   mvn clean install -U
   ```

3. **Docker build fails**
   - Check Dockerfile syntax
   - Verify base image availability
   - Review build logs

### Security Scan Failures

**OWASP Dependency Check:**

If a vulnerability is a false positive:

1. Review the CVE report
2. Add to `dependency-check-suppressions.xml`:
   ```xml
   <suppress>
     <cve>CVE-2021-12345</cve>
     <notes>
       False positive: We don't use this feature
     </notes>
   </suppress>
   ```
3. Commit and push

**Trivy:**

View detailed report:
```bash
# Download artifact
gh run download <run-id>

# Review trivy-results.sarif
```

### Docker Push Failures

**Check:**

1. Token has write permissions
2. Package visibility settings
3. Network connectivity

**Re-authenticate:**

```bash
echo $DOCKER_PASSWORD | docker login docker.io -u $DOCKER_USERNAME --password-stdin
```

## Performance Optimization

### Speed Up Builds

1. **Use build cache:**
   - Already enabled with `cache-from: type=gha`

2. **Single platform for testing:**
   ```yaml
   docker-platforms: 'linux/amd64'
   ```

3. **Parallel jobs:**
   - Security and Docker jobs run in parallel

4. **Skip tests in Docker build:**
   - Tests run in CI, skipped in Dockerfile

### Reduce Costs

GitHub Actions free tier:
- Public repos: Unlimited minutes
- Private repos: 2,000 minutes/month

**Optimize:**
1. Cache dependencies
2. Use self-hosted runners for intensive builds
3. Only build multiple platforms for releases

## Best Practices

### ✅ DO

- Use semantic versioning for releases
- Run security scans on every build
- Keep workflows DRY with reusable workflows
- Cache dependencies
- Use specific action versions (not `@latest`)
- Review security scan results
- Test locally before pushing
- Use branch protection
- Add status badges to README

### ❌ DON'T

- Commit secrets to repository
- Skip tests in CI
- Ignore security warnings
- Use `@latest` for actions
- Deploy without approval
- Run untrusted code in workflows
- Store credentials in workflow files

## Advanced Usage

### Matrix Builds

Test multiple Java versions:

```yaml
strategy:
  matrix:
    java: [17, 21]
steps:
  - uses: actions/setup-java@v4
    with:
      java-version: ${{ matrix.java }}
```

### Conditional Steps

```yaml
- name: Deploy to staging
  if: github.ref == 'refs/heads/develop'
  run: ./deploy.sh staging
```

### Workflow Dispatch

Trigger manually with inputs:

```yaml
on:
  workflow_dispatch:
    inputs:
      environment:
        description: 'Deployment environment'
        required: true
        type: choice
        options:
          - staging
          - production
```

### Composite Actions

Create reusable actions:

```yaml
# .github/actions/setup/action.yml
name: 'Setup'
runs:
  using: 'composite'
  steps:
    - uses: actions/setup-java@v4
    - uses: actions/cache@v4
```

## Support

### Resources

- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Workflow Syntax](https://docs.github.com/en/actions/reference/workflow-syntax-for-github-actions)
- [Security Hardening](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions)

### Get Help

- GitHub Issues: https://github.com/your-org/testcontainers-demo/issues
- GitHub Discussions: Enable in repository settings
- Community: https://github.community/

---

**Questions?** Open an issue or check the [main documentation](../DEPLOYMENT.md).
