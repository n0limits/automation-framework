#  CI/CD SETUP GUIDE

Complete guide for setting up GitHub Actions workflows with nightly builds.

---

##  TABLE OF CONTENTS

1. [GitHub Secrets Configuration](#github-secrets-configuration)
2. [Workflow Overview](#workflow-overview)
3. [Nightly Build Configuration](#nightly-build-configuration)
4. [Manual Workflow Triggers](#manual-workflow-triggers)
5. [Slack Integration](#slack-integration)
6. [Troubleshooting](#troubleshooting)

---

##  GITHUB SECRETS CONFIGURATION

### Step 1: Navigate to Repository Settings

```
GitHub Repository → Settings → Secrets and variables → Actions → New repository secret
```

### Step 2: Add Required Secrets

| Secret Name | Description | Example | Required For |
|-------------|-------------|---------|--------------|
| `AWS_ACCESS_KEY_ID` | AWS access key ID | `AKIAIOSFODNN7EXAMPLE` | AWS tests |
| `AWS_SECRET_ACCESS_KEY` | AWS secret access key | `wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY` | AWS tests |
| `API_BASE_URL` | API base URL (staging) | `https://api.staging.multibank.io` | API tests |
| `API_KEY` | API authentication key | `sk_test_abc123...` | API tests (if required) |
| `SLACK_WEBHOOK_URL` | Slack incoming webhook URL | `https://hooks.slack.com/services/...` | Notifications |
| `SQL_PASSWORD` | Database password | `SecurePassword123!` | DB tests (optional) |
| `POSTGRES_PASSWORD` | PostgreSQL password | `PostgresPass456!` | DB tests (optional) |

### Step 3: Verify Secrets

```bash
# Secrets should appear in:
# Settings → Secrets and variables → Actions → Repository secrets
```

---

##  WORKFLOW OVERVIEW

### Available Workflows

| Workflow | File | Trigger | Duration | Purpose |
|----------|------|---------|----------|---------|
| **Nightly Full Suite** | `nightly-tests.yml` | Schedule (2 AM UTC) + Manual | 90 min | Complete test coverage |
| **API Tests** | `api-tests.yml` | Push/PR + Manual | 10 min | Fast API validation |
| **Test Automation** | `test-automation.yml` | Push/PR | 45 min | Standard CI pipeline |
| **PR Checks** | `pr-checks.yml` | Pull requests | 15 min | Quick PR validation |

---

##  NIGHTLY BUILD CONFIGURATION

### Automatic Schedule

**Default:** Runs every night at 2 AM UTC

**Cron Expression:**
```yaml
schedule:
  - cron: '0 2 * * *'  # 2 AM UTC daily
```

**Customize Schedule:**
```yaml
# Every 6 hours
- cron: '0 */6 * * *'

# Every Monday at 3 AM
- cron: '0 3 * * 1'

# Every weekday at midnight
- cron: '0 0 * * 1-5'
```

### Execution Flow

```
Nightly Build Start (2 AM UTC)
    ↓
┌───────────────────────────────────────┐
│ Job 1: API Tests (15 min)             │
│ - Fast feedback on API health         │
│ - Runs on staging environment         │
└───────────────────────────────────────┘
    ↓ (needs: api-tests)
┌───────────────────────────────────────┐
│ Job 2: AWS Step Functions (20 min)    │
│ - Tests AWS workflows                 │
│ - Uses real AWS credentials           │
└───────────────────────────────────────┘
    ↓ (needs: api-tests, aws-tests)
┌───────────────────────────────────────┐
│ Job 3: Cross-Browser UI (90 min)      │
│ - Parallel: chromium, firefox, webkit │
│ - Matrix execution (fail-fast: false) │
└───────────────────────────────────────┘
    ↓ (needs: api-tests, aws-tests, ui-tests)
┌───────────────────────────────────────┐
│ Job 4: Integration Tests (30 min)     │
│ - API + AWS + UI combined             │
│ - End-to-end workflows                │
└───────────────────────────────────────┘
    ↓ (needs: all previous jobs)
┌───────────────────────────────────────┐
│ Job 5: Test Summary & Notifications   │
│ - Consolidate results                 │
│ - Calculate statistics                │
│ - Send Slack notifications            │
│ - Create GitHub issues on failure     │
└───────────────────────────────────────┘
```

### Environment Selection

**Default Environment:** `staging`

**Change Environment via Workflow Dispatch:**
1. Go to: Actions → Nightly Full Test Suite → Run workflow
2. Select environment: `local`, `staging`, or `prod`
3. Click "Run workflow"

---

##  MANUAL WORKFLOW TRIGGERS

### Trigger Nightly Build Manually

**Via GitHub UI:**
```
1. Navigate to: Actions tab
2. Select: "Nightly Full Test Suite"
3. Click: "Run workflow" button
4. Select environment (local/staging/prod)
5. Click: "Run workflow" (green button)
```

**Via GitHub CLI:**
```bash
# Install GitHub CLI
gh auth login

# Trigger nightly build (staging)
gh workflow run nightly-tests.yml

# Trigger with specific environment
gh workflow run nightly-tests.yml -f environment=prod
```

**Via REST API:**
```bash
curl -X POST \
  -H "Accept: application/vnd.github+json" \
  -H "Authorization: Bearer YOUR_GITHUB_TOKEN" \
  https://api.github.com/repos/OWNER/REPO/actions/workflows/nightly-tests.yml/dispatches \
  -d '{"ref":"main","inputs":{"environment":"staging"}}'
```

### Trigger API Tests Manually

**Via GitHub UI:**
```
Actions → API Tests → Run workflow
```

**Via CLI:**
```bash
gh workflow run api-tests.yml
```

---

##  SLACK INTEGRATION

### Step 1: Create Slack Incoming Webhook

1. Go to: https://api.slack.com/apps
2. Click: "Create New App" → "From scratch"
3. Name: "Test Automation Notifier"
4. Select workspace
5. Navigate to: "Incoming Webhooks"
6. Activate: "Activate Incoming Webhooks" → ON
7. Click: "Add New Webhook to Workspace"
8. Select channel (e.g., `#test-automation`)
9. Copy webhook URL: `https://hooks.slack.com/services/...`

### Step 2: Add Webhook to GitHub Secrets

```
GitHub Repo → Settings → Secrets → New repository secret
Name: SLACK_WEBHOOK_URL
Value: https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX
```

### Step 3: Test Slack Notifications

```bash
# Manually trigger nightly build to test notifications
gh workflow run nightly-tests.yml
```

### Notification Examples

**Success Notification:**
```
[PASS] Nightly Test Suite Passed
Environment: staging
Total Tests: 150
```

**Failure Notification:**
```
[FAIL] Nightly Test Suite Failed
Environment: staging
Date: 2025-12-30
Total Tests: 150
Failures: 5
[View Test Results] (link)
```

---

##  MONITORING & OBSERVABILITY

### View Test Results

**GitHub Actions UI:**
```
Repository → Actions → Select workflow run → View jobs → Download artifacts
```

**Artifacts Available:**
- `api-test-results-{run_number}` - JUnit XML
- `api-allure-report-{run_number}` - Allure HTML report
- `aws-test-results-{run_number}` - JUnit XML
- `chromium-allure-report-{run_number}` - Browser-specific Allure
- `firefox-screenshots-{run_number}` - Screenshots on failure
- `integration-test-results-{run_number}` - Integration test results

**Retention:**
- Test results: 30 days
- Allure reports: 30 days
- Screenshots: 7 days

### View Test Statistics

**Test Reporter Integration:**
```
Actions → Workflow Run → "Nightly Test Results Summary" check
```

**Features:**
- Pass/fail counts
- Execution time
- Flaky tests detection
- Historical trends

---

##  TROUBLESHOOTING

### Issue: Workflow Not Running on Schedule

**Possible Causes:**
1. Repository not active (no commits in 60 days)
2. Workflow file syntax error
3. Branch protection rules

**Solution:**
```bash
# Validate workflow syntax
gh workflow view nightly-tests.yml

# Check workflow status
gh workflow list

# Manually trigger to test
gh workflow run nightly-tests.yml
```

---

### Issue: AWS Credentials Invalid

**Symptoms:**
```
Error: The security token included in the request is invalid
```

**Solution:**
1. Verify secrets in GitHub:
   - `AWS_ACCESS_KEY_ID` is correct
   - `AWS_SECRET_ACCESS_KEY` is correct
2. Check IAM permissions:
   - User has `states:StartExecution`, `states:DescribeExecution`
3. Verify region: `us-east-1` matches your Step Function

```bash
# Test AWS credentials locally
export AWS_ACCESS_KEY_ID=your_key
export AWS_SECRET_ACCESS_KEY=your_secret
aws sts get-caller-identity
```

---

### Issue: Slack Notifications Not Working

**Symptoms:**
- Workflow completes but no Slack message

**Solution:**
1. Verify `SLACK_WEBHOOK_URL` secret exists
2. Test webhook manually:
```bash
curl -X POST -H 'Content-type: application/json' \
  --data '{"text":"Test notification"}' \
  YOUR_WEBHOOK_URL
```
3. Check Slack app permissions
4. Verify channel exists and bot has access

---

### Issue: Tests Timing Out

**Symptoms:**
```
Error: The job running on runner GitHub Actions X has exceeded the maximum execution time
```

**Solution:**
1. Increase timeout in workflow:
```yaml
jobs:
  ui-tests:
    timeout-minutes: 120  # Increase from 90
```

2. Optimize test execution:
   - Reduce test count in nightly suite
   - Increase parallelization
   - Use headless mode: `headless=true`

---

### Issue: Artifacts Not Uploaded

**Symptoms:**
- "No artifacts found" error

**Solution:**
1. Check test execution completed:
```yaml
- name: Upload test results
  if: always()  # ← Ensure this is present
```

2. Verify path exists:
```bash
# Locally test
mvn clean test
ls -la target/surefire-reports/
```

3. Check retention days < 90

---

##  BEST PRACTICES

### 1. Secrets Management

[PASS] **DO:**
- Use GitHub Secrets for all credentials
- Rotate AWS keys regularly
- Use separate AWS accounts for staging/prod
- Limit IAM permissions (principle of least privilege)

[FAIL] **DON'T:**
- Commit secrets to code
- Share AWS credentials across teams
- Use production credentials in CI/CD
- Log secret values

### 2. Workflow Optimization

[PASS] **DO:**
- Run fast tests first (API before UI)
- Use matrix for parallel execution
- Cache Maven dependencies
- Set appropriate timeouts

[FAIL] **DON'T:**
- Run all tests serially
- Install browsers for API-only tests
- Set timeout too short (causes flaky failures)
- Use `fail-fast: true` for exploratory runs

### 3. Notifications

[PASS] **DO:**
- Send notifications to dedicated channel
- Include actionable information
- Create GitHub issues for failures
- Send success notifications too (team morale)

[FAIL] **DON'T:**
- Spam personal channels
- Send notifications without context
- Ignore repeated failures
- Disable notifications (defeats purpose)

---

##  MAINTENANCE

### Weekly Tasks

- [ ] Review failed test trends
- [ ] Check artifact storage usage
- [ ] Verify Slack notifications working
- [ ] Update test statistics dashboard

### Monthly Tasks

- [ ] Rotate AWS credentials
- [ ] Review and clean up old artifacts
- [ ] Update workflow timeout values if needed
- [ ] Audit GitHub secrets (remove unused)

### Quarterly Tasks

- [ ] Review test execution time trends
- [ ] Optimize slow tests
- [ ] Update dependencies (AWS SDK, actions versions)
- [ ] Review and update notification rules

---

##  SUPPORT

**Common Commands:**

```bash
# List all workflows
gh workflow list

# View workflow details
gh workflow view nightly-tests.yml

# View recent runs
gh run list --workflow=nightly-tests.yml

# View specific run
gh run view RUN_ID

# Re-run failed jobs
gh run rerun RUN_ID --failed

# Download artifacts
gh run download RUN_ID
```

**Additional Resources:**
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [AWS SDK v2 Documentation](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/)
- [Slack Incoming Webhooks](https://api.slack.com/messaging/webhooks)

---

**Document Version:** 1.0
**Last Updated:** 2025-12-30
**Author:** Victor Grozev
