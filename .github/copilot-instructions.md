# AI Coding Agent Instructions - Adyen Payment Integration Workshop

## Project Overview
This is an **educational payment integration workshop** demonstrating how to build a complete Adyen payment flow using Java Spring Boot backend with Thymeleaf templates and vanilla JavaScript frontend. The project is intentionally incomplete with stub methods that learners fill in following step-by-step documentation.

## Architecture & Data Flow

### Core Components
- **Backend**: Spring Boot 3.3.4 with Adyen Java API Library v31.3.0
- **Frontend**: Vanilla JavaScript using Adyen Web Drop-in component (renders payment methods and handles payment UI)
- **Templates**: Thymeleaf server-side template engine in `src/main/resources/templates/`
- **Configuration**: Environment variables injected via `ApplicationConfiguration` → `DependencyInjectionConfiguration` (Spring `@Bean` pattern)

### Payment Flow Architecture
1. **PaymentMethods Flow** (`/api/paymentMethods` → `AdyenCheckout` instance → Drop-in renders payment options)
2. **Payment Flow** (Frontend submits form → `/api/payments` → Adyen returns action/resultCode)
3. **3DS2 Redirect Flow** (Shopper redirected to issuer auth → returns via `/handleShopperRedirect` → `/api/payments/details`)
4. **Webhook Notifications** (`/webhooks` receives payment updates with HMAC signature validation)

### Key Integration Points
- **Adyen Client**: Configured in `DependencyInjectionConfiguration` with TEST environment
- **PaymentsApi**: Injected via Spring IoC, used for all Adyen API calls (`paymentMethods()`, `payments()`, `paymentsDetails()`)
- **ClientKey**: Passed server→client via Thymeleaf `checkout.html` to authenticate frontend Adyen.Web Drop-in
- **HMAC Validation**: `HMACValidator` bean validates webhook signatures using `ADYEN_HMAC_KEY`

## Build & Run Commands
```bash
./gradlew bootRun          # Start on http://localhost:8080 (uses embedded server, port 8080 hardcoded in application.properties)
./gradlew build            # Build JAR for Docker deployment (outputs to build/libs/)
```

For IDE debugging: Right-click `src/main/java/com/adyen/workshop/MainApplication.java` → Run/Debug.

## Configuration Pattern (Spring @Value Injection)
Environment variables are injected via `@Value` with null defaults in `ApplicationConfiguration.java`:
```java
@Value("${ADYEN_API_KEY:#{null}}")  // Tries env var, falls back to null
```

Configuration sources (in order):
1. `application.properties` (already contains dev keys for reference)
2. Environment variables (Best practice: `export ADYEN_API_KEY='...'`)
3. System properties (Spring-managed)

## Critical Patterns & Conventions

### Step-Based Learning Structure
- Every incomplete feature has a comment like `// Step N` marking where learners should implement code
- README.md contains detailed explanations with collapsible answer sections for each step
- Controllers return `ResponseEntity.ok().body(null)` as stubs awaiting implementation

### Request Handling Pattern
All external requests flow through controllers to `PaymentsApi`:
```java
// Example pattern used throughout ApiController
var request = new PaymentMethodsRequest();
request.setMerchantAccount(applicationConfiguration.getAdyenMerchantAccount());
var response = paymentsApi.paymentMethods(request);  // Passes through Adyen Client
return ResponseEntity.ok().body(response);
```

### Frontend-Backend Communication
- Frontend calls backend REST endpoints using `fetch()`
- Backend marshals Adyen Java model objects (auto-serialized to JSON by Spring)
- Frontend receives responses and handles `resultCode` values (case-sensitive: `"Authorised"`, `"Pending"`, `"Refused"`, etc.)
- Client-side routing based on payment result

### 3DS2 Authentication Handling
**Two implementations coexist in code**:
1. **Redirect 3DS2** (main flow): Shopper redirected to issuer → returns via query params → `/handleShopperRedirect` processes via `/payments/details`
2. **Native 3DS2** (optional, commented out): Challenge shown inline → `onAdditionalDetails()` calls `/payments/details`

Either flow requires `AuthenticationData` with `setAttemptAuthentication(ALWAYS)` in payment request.

### Key Request Fields (Required for 3DS2)
```java
// These must be extracted from request or client state in /api/payments:
paymentRequest.setOrigin(scheme + "://" + host);           // For Visa rules
paymentRequest.setBrowserInfo(body.getBrowserInfo());      // From frontend
paymentRequest.setShopperIP(request.getRemoteAddr());      // Server extracts
paymentRequest.setShopperInteraction(ECOMMERCE);           // Hardcoded for online
paymentRequest.setBillingAddress(billingAddress);          // Risk mitigation
```

### Idempotency (Best Practice, Optional)
Add `RequestOptions` with UUID key to prevent duplicate charges on retries:
```java
var requestOptions = new RequestOptions();
requestOptions.setIdempotencyKey(UUID.randomUUID().toString());
var response = paymentsApi.payments(paymentRequest, requestOptions);
```

## API Dependencies & Model Objects
- **`com.adyen.model.checkout.*`**: Request/Response DTOs (PaymentRequest, PaymentResponse, etc.)
- **`com.adyen.service.checkout.PaymentsApi`**: Main API client (injected as `@Bean`)
- **`com.adyen.Client`**: Configured with API key + TEST environment
- **`com.adyen.Config`**: Holds API credentials
- **`com.adyen.util.HMACValidator`**: Validates webhook signatures

## Frontend JavaScript Conventions
- Single `adyenWebImplementation.js` file with `startCheckout()` async function
- **Event Handlers** called by Adyen Drop-in:
  - `onSubmit(state, component, actions)` - User clicks Pay
  - `onAdditionalDetails(state, component, actions)` - Additional auth required
  - `onPaymentCompleted(result, component)` - Payment succeeded
  - `onPaymentFailed(result, component)` - Payment rejected/cancelled
  - `onError(error, component)` - Unexpected error
- **Actions API**: `actions.resolve({resultCode, action, order})` or `actions.reject()`
- **Result Codes**: `"Authorised"`, `"Pending"`, `"Received"`, `"Cancelled"`, `"Refused"`, `"Error"`

## Testing & Validation
- Adyen TEST environment is hardcoded (`Environment.TEST`)
- [Test card numbers](https://docs.adyen.com/development-resources/testing/test-card-numbers/) available in Adyen docs
- [Adyen Test Card Chrome Extension](https://chromewebstore.google.com/detail/adyen-test-cards/) auto-fills card fields
- 3DS2 challenge triggered with specific test cards (documented in Adyen docs)

## Deployment Notes
- Docker image: `FROM amazoncorretto:17-alpine-jdk` (Java 17)
- JAR output: `build/libs/adyen-step-by-step-integration-workshop-0.0.1-SNAPSHOT.jar`
- Server port: 8080 (configurable in `application.properties`)
- CORS/origins: Must register allowed URLs in Adyen Customer Area for Drop-in to load

## Common Pitfalls & Patterns to Avoid
1. **Merchant Account Suffix**: Must end with `-ECOM` (e.g., `YourMerchantNameECOM`)
2. **Copy-Paste Keys Carefully**: README emphasizes this three times—copy API key character-by-character
3. **Response Null Checks**: `PaymentResponse.getAction()` and `PaymentResponse.getResultCode()` must be checked before use
4. **BrowserInfo Extraction**: Must come from frontend `state.data.browserInfo` in payment request
5. **Webhook HMAC**: If validation fails, return `HTTP 422 Unprocessable Entity`
6. **Test vs Live**: Never switch to `Environment.LIVE` in test code

## Key File Map
- `ApplicationConfiguration.java` - Environment variable binding
- `DependencyInjectionConfiguration.java` - Adyen Client setup + Spring beans
- `ApiController.java` - Payment flow endpoints (where main implementation happens)
- `WebhookController.java` - Webhook signature validation
- `ViewController.java` - HTML page routing with client key injection
- `adyenWebImplementation.js` - Frontend Drop-in orchestration
- `checkout.html` - Contains `<div id="clientKey">` and payment mount point
- `build.gradle` - Adyen library version (v31.3.0)
