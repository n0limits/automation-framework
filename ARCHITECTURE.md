# Architecture Reference

**Core Automation Framework — v2.1**
**Author:** Victor Grozev

This document is the developer reference for the framework internals. It covers class hierarchy, lifecycle ordering, thread safety, extension points, and the rationale behind key design decisions. For installation, configuration, and running tests see [README.md](README.md).

---

## Table of Contents

1. [Class Hierarchy](#1-class-hierarchy)
2. [Lifecycle Execution Order](#2-lifecycle-execution-order)
3. [Thread Safety Model](#3-thread-safety-model)
4. [Extension Points](#4-extension-points)
5. [DataProvider Architecture](#5-dataprovider-architecture)
6. [Wait Strategy](#6-wait-strategy)
7. [Test Data Isolation](#7-test-data-isolation)
8. [Design Patterns Reference](#8-design-patterns-reference)
9. [Retry Mechanism](#9-retry-mechanism)
10. [Parallelism Constraints](#10-parallelism-constraints)

---

## 1. Class Hierarchy

```
BaseTest
│  @BeforeSuite  setupSuite()      — config validation, connection pool init
│  @AfterSuite   tearDownSuite()   — connection pool shutdown
│
├── BaseWebTest
│   │  @BeforeMethod  setupBrowser()    — Playwright init, browser launch, navigation
│   │  @AfterMethod   tearDownBrowser() — tracing save/discard, browser close
│   │
│   └── BaseMultibankTest
│          performAdditionalSetup()     — NavigationPage, TradingPage, FooterPage, AboutUsPage init
│          scrollToPercentage(double)   — JS scroll + waitForLoadState()
│          scrollToTop()               — JS scroll + waitForLoadState()
│          assertNavigationMenuVisible()
│
├── BaseAPITest
│   │  @BeforeMethod  setupAPI()        — APIClient.getRequestSpec(), stored in requestSpec field
│   │  @AfterMethod   tearDownAPI()     — APIClient.cleanup() (removes ThreadLocal)
│   │  logResponse(Response)
│   │  withAuth(String token)
│   │  withApiKey(String apiKey)
│   │
│   └── (direct subclasses: TradingAPITests, APIClientTest)
│
└── BaseAWSTest
       @BeforeMethod  setupAWS()        — StepFunctionsClient init, state machine logging
       @AfterMethod   tearDownAWS()     — AWSClientManager.cleanup()
       performAdditionalAWSSetup()      — template hook
       performAdditionalAWSCleanup()    — template hook
       createSimpleJsonInput(key, val)
       createJsonInput(String... keyValues)

       └── TradingWorkflowIntegrationTests
              @BeforeMethod setupIntegration() — TradingAPIClient init only
```

### Responsibility Summary

| Class | Owns |
|---|---|
| `BaseTest` | Config singleton, connection pool lifecycle |
| `BaseWebTest` | Playwright instance, browser, page, tracing, home page navigation |
| `BaseMultibankTest` | Multibank page objects, scroll helpers, navigation assertion |
| `BaseAPITest` | `RequestSpecification` ThreadLocal, API client lifecycle |
| `BaseAWSTest` | `StepFunctionsClient`, AWS client lifecycle, JSON helpers |

---

## 2. Lifecycle Execution Order

TestNG calls `@BeforeSuite` / `@AfterSuite` once per suite. For `@BeforeMethod` / `@AfterMethod`, **parent methods run before child methods on setup, and child methods run before parent methods on teardown**.

### Example: `TradingWorkflowIntegrationTests` (extends `BaseAWSTest` → `BaseTest`)

```
SUITE START
  BaseTest.setupSuite()                  @BeforeSuite — config loaded, pools opened

  [For each test method]
    BaseAWSTest.setupAWS()               @BeforeMethod (parent) — StepFunctionsClient init
    TradingWorkflowIntegrationTests
      .setupIntegration()                @BeforeMethod (child)  — TradingAPIClient init

    @Test method runs

    TradingWorkflowIntegrationTests
      .tearDownIntegration()             @AfterMethod  (child)  — (none: removed, base handles it)
    BaseAWSTest.tearDownAWS()            @AfterMethod  (parent) — AWSClientManager.cleanup()

SUITE END
  BaseTest.tearDownSuite()               @AfterSuite — connection pools closed
```

### Example: `NavigationTests` (extends `BaseMultibankTest` → `BaseWebTest` → `BaseTest`)

```
SUITE START
  BaseTest.setupSuite()

  [For each test method]
    BaseWebTest.setupBrowser()           @BeforeMethod — Playwright + browser + navigate + tracing
      └── calls performAdditionalSetup()
            └── BaseMultibankTest.performAdditionalSetup() — page object init

    @Test method runs

    BaseWebTest.tearDownBrowser()        @AfterMethod — trace save/discard + browser close

SUITE END
  BaseTest.tearDownSuite()
```

---

## 3. Thread Safety Model

### Playwright — `PlaywrightManager` (ThreadLocal)

Each thread gets its own isolated `Playwright`, `Browser`, `BrowserContext`, and `Page` instance. The `PlaywrightManager` stores all four in a `ThreadLocal<PlaywrightContext>`.

```
Thread 1: Playwright-1 → Browser-1 → BrowserContext-1 → Page-1
Thread 2: Playwright-2 → Browser-2 → BrowserContext-2 → Page-2
Thread 3: Playwright-3 → Browser-3 → BrowserContext-3 → Page-3
```

`BaseWebTest.page` is set from `PlaywrightManager.getPage()` in `@BeforeMethod` and is safe as long as the test class instance is not shared across threads (i.e. `parallel="tests"`, not `parallel="methods"` on a shared instance).

### API Client — `APIClient` (ThreadLocal)

`APIClient` stores `RequestSpecification` in a `ThreadLocal<RequestSpecification>`. `BaseAPITest.setupAPI()` initialises it before each test; `tearDownAPI()` removes it after. Each parallel thread gets its own isolated spec with no locking required.

### Database — `ConnectionPoolManager` (HikariCP)

Connection pools are global singletons per database URL, but individual `Connection` objects are obtained per-test and closed after use. `DatabaseTestUtils` is instantiated per test method in `@BeforeMethod` so there is no shared mutable state between threads.

### What is NOT thread-safe

`BaseWebTest.page` is an instance field. If TestNG is configured with `parallel="methods"` on a single test class instance, multiple threads will overwrite each other's `page` reference. All UI test suites must use `parallel="tests"` (each `<test>` block in XML gets its own class instance) or `parallel="false"`.

---

## 4. Extension Points

The framework uses the **Template Method** pattern throughout. Override the hook methods in subclasses; never override the `@BeforeMethod` / `@AfterMethod` methods themselves unless you call `super`.

### Web / UI layer

```java
// Override in BaseWebTest subclasses
protected void performAdditionalSetup() { }   // called at end of setupBrowser()
protected void performAdditionalCleanup() { } // called at start of tearDownBrowser()
```

`BaseMultibankTest` uses `performAdditionalSetup()` to initialise all four page objects. Test classes that extend `BaseMultibankTest` do not need to override it further unless they need extra page objects.

### API layer

```java
// Override in BaseAPITest subclasses
protected void performAdditionalAPISetup() { }   // called at end of setupAPI()
protected void performAdditionalAPICleanup() { } // called at start of tearDownAPI()
```

### AWS layer

```java
// Override in BaseAWSTest subclasses
protected void performAdditionalAWSSetup() { }   // called at end of setupAWS()
protected void performAdditionalAWSCleanup() { } // called at start of tearDownAWS()
```

### Adding a new test layer

1. Create `BaseXxxTest extends BaseTest`
2. Add `@BeforeMethod setupXxx()` — call your hook at the end
3. Add `@AfterMethod tearDownXxx()` — call your hook at the start, then clean up
4. Expose a `protected void performAdditionalXxxSetup()` no-op that subclasses can override

---

## 5. DataProvider Architecture

All reusable test data providers live in a single class:

```
src/test/java/com/automation/providers/TestDataProviders.java
```

This is the **single source of truth** for parameterised test data. No `@DataProvider` methods should be defined inside individual test classes.

### Registered Providers

| Provider name | Data shape | Consumers |
|---|---|---|
| `navigationItemsProvider` | `(itemName, expectedUrlPart)` | `NavigationTests.testNavigationItemIsClickable` |
| `allNavigationItemsProvider` | `(itemName, expectedUrlPart)` | `NavigationTests.testNavigationItemFunctionality` |
| `tradingSymbolsProvider` | `(symbol)` | `TradingAPITests.testTickerPerformance`, `TradingTests` |
| `tradingTabsProvider` | `(tabName)` | `TradingTests` |
| `expectedColumnsProvider` | `(columnName)` | `TradingTests` |
| `authenticatedEndpointsProvider` | `(endpoint)` | `TradingAPITests.testAuthenticatedEndpointRequiresAuth` |
| `userDataProvider` | `(username, email)` | Available for user-creation tests |

### Referencing a provider from a test class

```java
@Test(dataProvider = "tradingSymbolsProvider",
      dataProviderClass = TestDataProviders.class)
public void testTickerPerformance(String symbol) { ... }
```

The `dataProviderClass` attribute is mandatory because `TestDataProviders` is not in the same class as the test.

### Adding a new provider

1. Add a `@DataProvider(name = "myProvider")` method to `TestDataProviders`
2. Return `Object[][]` — each inner array is one test invocation's arguments
3. Reference it from the test with `dataProviderClass = TestDataProviders.class`

---

## 6. Wait Strategy

### Rule: use `LOAD`, not `NETWORKIDLE`

All `waitForLoadState()` calls in the framework use `LoadState.LOAD`.

`LOAD` fires when the DOM, CSS, images, and subframes have finished loading — equivalent to the browser's native `load` event. It is Playwright's recommended default.

`NETWORKIDLE` (≥ 500ms with no network connections) is prohibited because:
- Live price feeds and WebSocket connections prevent it from ever firing on trading UIs
- Background polling continuously resets the 500ms timer, causing CI timeouts
- It couples test stability to third-party analytics and tracking scripts

### Hierarchy of wait calls

```
page.navigate(url)
  └── BaseWebTest.waitForPageLoad()          → page.waitForLoadState(LOAD)

page.evaluate("window.scrollTo(...)")
  └── BaseMultibankTest.scrollToPercentage() → page.waitForLoadState()  [default = LOAD]
  └── BaseMultibankTest.scrollToTop()        → page.waitForLoadState()  [default = LOAD]

BasePage.navigateTo(url)
  └── BasePage.waitForPageLoad()             → page.waitForLoadState(LOAD)
```

For post-interaction waits (after button clicks triggering AJAX), prefer element-visibility checks (`waitForSelector`, `isVisible`) over load-state waits, since an SPA page's `load` event does not re-fire on DOM updates.

---

## 7. Test Data Isolation

### Problem

Database tests that use hardcoded strings for `UNIQUE`-constrained columns (e.g. `username`, `order_number`) will produce constraint violations when the same test runs concurrently in multiple threads or is retried.

### Solution: timestamp suffix

Every test method captures a single timestamp at its start and uses it as a suffix for all unique values it inserts:

```java
long ts = System.currentTimeMillis();
dataBuilder.forTable("users")
    .with("username", "test_user_" + ts)
    .with("order_number", "ORD-001-" + ts)
    .insert();
```

For tests that build a prefix used in both inserts and a `LIKE` query, thread the prefix consistently:

```java
String prefix = "GRP-" + System.currentTimeMillis();
// inserts
.with("order_number", prefix + "-" + i + "-" + j)
// query
dbUtils.executeQuery("... WHERE order_number LIKE ?", prefix + "-%");
```

### Cleanup tracking

`TestDataBuilder` tracks all inserted record IDs by default. `dataBuilder.cleanupAll()` in `@AfterMethod` issues `DELETE` statements in reverse-insertion order to respect foreign key constraints. Pass `insert(false)` to opt a record out of tracking (used when the test itself exercises deletion).

---

## 8. Design Patterns Reference

| Pattern | Implementation | Rationale |
|---|---|---|
| **Template Method** | `BaseWebTest.performAdditionalSetup()`, `BaseAPITest.performAdditionalAPISetup()`, `BaseAWSTest.performAdditionalAWSSetup()` | Lets subclasses inject behaviour into a fixed lifecycle without overriding `@BeforeMethod` / `@AfterMethod` |
| **Page Object Model** | `BasePage` → `LoginPage`, `AccountPage`, `TradingDashboardPage`, `multibank/*` | Decouples locator knowledge from test logic; single place to update selectors |
| **Factory** | `BrowserFactory`, `CloudBrowserFactory`, `PageFactory` | Centralises browser and page object creation; allows swapping providers (local vs BrowserStack) without test changes |
| **Builder** | `TestDataBuilder`, `QueryBuilder`, `PersonDataBuilder`, `OrderDataBuilder` | Fluent construction of complex objects (SQL inserts, test persons) with readable test code |
| **Singleton** | `TestConfig`, `TestMetrics`, `ConnectionPoolManager` | One config read per suite; one metrics instance; one pool per database URL |
| **ThreadLocal** | `PlaywrightManager`, `APIClient` | Gives each parallel thread its own isolated Playwright/RestAssured state with zero synchronisation overhead |
| **Fluent Interface** | `APIAssertions`, `DatabaseAssertions`, `UIAssertions` | Chainable assertions read like sentences; assertion context (`as(...)`) stays close to the check |

---

## 9. Retry Mechanism

Failed tests are automatically retried up to **2 times** before being marked as failed.

**How it works:**

1. `RetryAnalyzer` implements `IRetryAnalyzer`. It increments a per-instance counter and returns `true` (retry) while `retryCount < 2`.
2. `RetryListener` implements `IAnnotationTransformer`. It runs at suite startup and sets `retryAnalyzerClass = RetryAnalyzer.class` on every `@Test` annotation, so no test class needs to declare it explicitly.
3. `RetryListener` is registered in every TestNG XML suite file via `<listener class-name="com.automation.listeners.RetryListener"/>`.

**Implication for test design:** Tests must be idempotent — they must leave no side effects that would cause a retry to behave differently from the first attempt. Database tests use `@AfterMethod` cleanup and timestamped unique values specifically to satisfy this requirement.

---

## 10. Parallelism Constraints

| Suite XML | `parallel` mode | Safe for `BaseWebTest`? | Notes |
|---|---|---|---|
| `testng-ui.xml` | `tests` | Yes | Each `<test>` block = separate class instance |
| `testng-api-parallel.xml` | `tests` | N/A (API only) | `APIClient` ThreadLocal is safe |
| `testng/testng.xml` | `tests` (suite) + `methods` (per `<test>`) | **Use with care** | `parallel="methods"` on a shared instance is unsafe for `BaseWebTest` — only safe if each `<test>` block contains a single test class |
| `testng/testng-smoke.xml` | `methods` | **Use with care** | Same caveat as above |
| `testng/testng-api.xml` | `false` | N/A | Sequential |
| `testng/testng-integration.xml` | `false` | N/A | Sequential |
| `testng/testng-aws.xml` | `false` | N/A | Sequential |

**Safe combination:** `parallel="tests"` at the suite level with one test class per `<test>` block guarantees each thread operates on a separate class instance with its own `page` field.

**Unsafe combination:** `parallel="methods"` on a `<test>` block that contains a `BaseWebTest` subclass — multiple threads share one class instance and race on the `page` field.
