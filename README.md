# Build Your Payment Integration with Adyen - A Step-by-Step Guide

### Prerequisites

_Note: We ask you to start with a clean Adyen Merchant Account for this workshop._

You will need a few things to get started:

* an IDE (like IntelliJ or VsCode) and Java SDK v17+, alternatively: you can spin up this workspace in a browser-IDE such as codespaces or [Gitpod](https://gitpod.io/#https://github.com/adyen-examples/adyen-step-by-step-integration-workshop).
* Access to an [Adyen Test Account](https://www.adyen.com/signup) (ECOM).




## Introduction

This repository is a step-by-step guide to building your payment integration with Adyen.
We'll guide you through the steps to build an Adyen integration and make your first payment on the Adyen TEST environment. We'll cover three main steps:
* The initial setup: Adyen Merchant Account, Adyen API Credentials, and Adyen Client Key.
* The API requests needed: `/paymentMethods`, `/payments`, `/payments/details`, and 3D Secure 2).
* The webhooks: Setup, configuration, and response handling.


### Context of the code repository.

This workshop uses a Java+Spring Boot in the backend with a static (HTML/CSS/Javascript) frontend with a `thymeleaf` template (a server-side Java template engine that can process HTML).

_Note: If the static frontend environment is not to your liking, feel free to implement your own frontend solution using the framework of your choice (f.e. using Node.js)._

In this workshop, we're not asking you to build a complete integration from scratch but rather to fill in the voids based on resources you can find in the [Adyen Documentation](https://docs.adyen.com)
or other online resources ([GitHub](https://github.com/adyen), [GitHub Examples](https://github.com/adyen-examples), [Adyen Help](https://help.adyen.com) etc.).
We use an empty Spring Boot template at the start, which you'll extend into a fully working application that can accept payments.




### Project Structure
The project structure follows a Model-View-Controller (MVC) structure.

* The Java code is to be found in `src/main/java/com/adyen/workshop`
  * `/controllers` folder contains your endpoints. The following example creates the `/hello-world` endpoint; see `/controllers/ApiController.java.`

  ```
      @GetMapping("/hello-world")
      public ResponseEntity<String> helloWorld() throws Exception {
          return ResponseEntity.ok()
                  .body("This is the 'Hello World' from the workshop - You've successfully finished step 0!");
      }
  ```

  * `/views`-folder contains view controllers that show the HTML pages in the `/resources/static/` folder
  * The code you need to update is in the `/controllers` folder. `ApiController.java`.
  * You can add your environment variables (`ADYEN_API_KEY`, `ADYEN_MERCHANT_ACCOUNT`, `ADYEN_CLIENT_KEY`, `ADYEN_HMAC_KEY`) in the `ApplicationConfiguration.java` class.
* The frontend templates are to be found in `src/main/resources/templates` and the static resources in `src/main/resources/static`
  * The code you need to update is in the `src/main/resources/static/adyenWebImplementation.js` and `src/main/resources/templates/layout.html`
* Some additional information:
  * The `clientKey` in `adyenWebImplementation.js` is automatically passed from the backend to the client side and can be accessed using: `clientKey`.
  * To experiment with multiple payment methods, a `type` value is passed from the client to the server. This value contains the name of an Adyen payment method that the Adyen web components will recognize. The value is currently hardcoded as `dropin`, see: `/preview?type=dropin` in `/resources/templates/index.html`.
```
<li class="integration-list-item">
    <a href="/preview?type=dropin" class="integration-list-item-link">
        <div class="title-container">
            <p class="integration-list-item-title">Drop-in</p>
        </div>
    </a>
</li>
```

* To run the project, you have two options:
  * `./gradlew bootRun` will start the server on port 8080.
  * To run the project from your IDE, go to `src/main/java/com/adyen/workshop/MainApplication.java` -> Right-click and click `Debug` or `Run`.


## Workshop: Accepting Online payments using the Advanced flow
Learn how to integrate with Adyen using the `/paymentMethods`, `/payments` and `/payments/details` endpoints.




### Briefing

You're working as a full-stack developer for an ecommerce company that sells headphones and sunglasses in the Netherlands.
They sell the best headphones and sunglasses at 49.99 each, and you're incredibly excited to take on this challenge.
You're not afraid of Java and can code JavaScript; you're ready for this! You've been tasked with implementing credit card, iDeal, and Klarna payments using Adyen.




### Learning goals

In this workshop, you'll learn how to:
1. Set up your Adyen Merchant Account, API Credentials, and Adyen Client Key.
2. Retrieve a list of available payment methods using `/paymentMethods`
3. Make a payment using `/payments` followed by `/payments/details`
4. Handle Strong Customer Authentication using 3DSecure 2.
5. Present the payment results to the user
6. Receive updates on the payments through webhooks





### Start - Step-by-Step Guide:

**Step 0.** Build the project using `./gradlew bootRun` and see if it works. If you can visit `http://localhost:8080/hello-world`, `https://8080-adyenexampl-adyenstepby-xxxxxx21.ws-eu114.gitpod.io/hello-world` (Gitpod) or `https://xxxx.github.dev/hello-world` (codespaces), this means it works!


**Step 1.** [Get your Adyen Merchant Account](https://docs.adyen.com/account/manage-account-structure/#request-merchant-account) or use an existing Merchant Account associated with your account (ending with -`ECOM`).


**Step 2.** [Get your Adyen API Key](https://docs.adyen.com/development-resources/api-credentials/#generate-api-key). Ensure you've created the API Key on the Merchant Account level (e.g., you've selected your MerchantAccount and created credentials in the API Credentials page in the Customer Area).
   - Pro-tip #1: Make sure you copy your key correctly.
   - Pro-tip #2: Make 101% sure you copy your key correctly! :)


**Step 3.** [Get your Adyen Client Key](https://docs.adyen.com/development-resources/client-side-authentication/#get-your-client-key).
   - Do **not** forget to add the correct URL to my allowed origins (e.g. `http://localhost:8080`, `https://*.gitpod.io`, or `https://*.github.dev`). This allows Adyen.Web Dropin/Components to load on your page. The `*`-symbol indicates to accept any subdomain.


**Step 4.** Add your keys to `ApplicationConfiguration.java` in `/main/java/com/adyen/workshop/configurations`:
   - Best practice: export the vars as follows so that the Spring Boot framework can automatically inject your variables on startup.
   - If you're using gitpod/codespaces, you can export your variables as follows in your terminal:
   - If you've used gitpod before, the program will inject previously used environment variables as configured in [https://gitpod.io/variables](https://gitpod.io/variables).
```
export ADYEN_API_KEY='Aq42....xx'
export ADYEN_CLIENT_KEY='test_yourclientkey'
export ADYEN_MERCHANT_ACCOUNT='YourMerchantAccountName'
```


You can now access your keys in your application anywhere:
- `applicationConfiguration.getAdyenApiKey()`
- `applicationConfiguration.getAdyenClientKey()`
- `applicationConfiguration.getAdyenMerchantAccount()`.

**Additional context:**

In `/com/adyen/workshop/configurations/`, you'll find the `DependencyInjectionConfiguration.java` class. This is where we create our Adyen instances and **re-use** them using Spring's Constructor Dependency Injection (CDI) - A `@Bean` is an object that is instantiated, assembled, and managed by the Spring IoC container.
You should be able to inject these classes similar to how we inject `ApplicationConfiguration.java` in any constructor.

**Exercise:** Create your Adyen-`Client` by creating a `new Config()` object in `configurations/DependencyInjectionConfiguration.java`, passing your `ADYEN_API_KEY`, and specifying `Environment.TEST`, which we'll use later.

<details>
<summary>Click here to show me the answer</summary>

```java

@Configuration
public class DependencyInjectionConfiguration {
    private final ApplicationConfiguration applicationConfiguration;

    public DependencyInjectionConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    @Bean
    Client client() {
        var config = new Config();
        // Step 4.
        config.setApiKey(applicationConfiguration.getAdyenApiKey());
        config.setEnvironment(Environment.TEST);
        return new Client(config);
    }

    @Bean
    PaymentsApi paymentsApi(){
        return new PaymentsApi(client());
    }

    @Bean
    HMACValidator hmacValidator() { return new HMACValidator(); }
}

```

</details>


**Step 5.** **You can skip this step**: Install the [Java library](https://github.com/Adyen/adyen-java-api-library) by adding the following line to the `build.gradle` file.
For your convenience, we've **already included this in this project**. You can visit the `build.gradle` file and verify whether the following line is included:

```
	implementation 'com.adyen:adyen-java-api-library:25.1.0'
```


**Step 6.** Install the latest [Adyen.Web Dropin/Components](https://docs.adyen.com/online-payments/release-notes/) by adding embed script(`.js`) and stylesheet(`.css`) to `/resources/templates/layout.html`.
  - Including this allows you to access the AdyenCheckout instance in JavaScript. In this example, we use `Web Components/Drop-in v5.63.0`.
  - Note: If the `Embed script and stylesheet` tab shows `'Coming soon!'`, don't worry, you can grab an earlier version of `Web Components/Drop-in`



**Step 7.** Let's prepare our backend (`com/adyen/workshop/controllers`) to [retrieve a list of available payment methods](https://docs.adyen.com/online-payments/build-your-integration/advanced-flow/?platform=Web&integration=Drop-in&version=5.63.0&programming_language=java#web-advanced-flow-post-payment-methods-request). Go to `ApiController.java` and use the `paymentsApi` to make `/paymentMethods`-request to Adyen.


<details>
<summary>Click here to show me the answer</summary>

```java
    @PostMapping("/api/paymentMethods")
    public ResponseEntity<PaymentMethodsResponse> paymentMethods() throws IOException, ApiException {
        // Step 7
        var paymentMethodsRequest = new PaymentMethodsRequest();
        paymentMethodsRequest.setMerchantAccount(applicationConfiguration.getAdyenMerchantAccount());

        log.info("Retrieving available Payment Methods from Adyen {}", paymentMethodsRequest);
        var response = paymentsApi.paymentMethods(paymentMethodsRequest);
        log.info("Payment Methods response from Adyen {}", response);
        return ResponseEntity.ok()
                .body(response);
    }
```

</details>

**Note:** You can send a `curl-request` to test this endpoint. However, let's move on to step 8 to see how the Drop-in (frontend) interacts with this `/api/paymentMethods` endpoint.

**Step 8.** On your frontend (`adyenWebImplementation.js`), let's make a request to this `/api/paymentMethods` endpoint and display the payment methods to the shopper.

We automatically pass on your public `ADYEN_CLIENT_KEY` to your frontend, you can access this variable using `clientKey`.


Create the configuration for the `AdyenCheckout`-instance, call the `/api/paymentMethods/`-endpoint, create the `AdyenCheckOut()`-instance, and mount it to `"payment"-div` container (see `/resources/templates/checkout.html`).

We've added a `sendPostRequest(...)` helper function to communicate with your backend. You can copy and paste the following code below:


```js
const clientKey = document.getElementById("clientKey").innerHTML;
const type = document.getElementById("type").innerHTML;

// Starts the (Adyen.Web) AdyenCheckout with your specified configuration by calling the `/paymentMethods` endpoint.
async function startCheckout() {
    try {
        // Step 8 - Retrieve payment methods and instantiate it
        let paymentMethodsResponse = await sendPostRequest("/api/paymentMethods");

        const configuration = {
            paymentMethodsResponse: paymentMethodsResponse,
            clientKey,
            locale: "en_US",
            environment: "test",
            showPayButton: true,
            paymentMethodsConfiguration: {
                card: {
                    hasHolderName: true,
                    holderNameRequired: true,
                    name: "Credit or debit card",
                    amount: {
                        value: 9998,
                        currency: "EUR",
                    },
                }
            },
        };

        // Start the AdyenCheckout and mount the element onto the `payment`-div.
        let adyenCheckout = await new AdyenCheckout(configuration);
        adyenCheckout.create(type).mount(document.getElementById("payment"));
    } catch (error) {
        console.error(error);
        alert("Error occurred. Look at console for details.");
    }
}

// Step 10 - Handles responses, do a simple redirect based on the result.
function handleResponse(response, component) {

}

// This function sends a POST request to your specified URL,
// the `data`-parameters will be serialized as JSON in the body parameters.
async function sendPostRequest(url, data) {
    const res = await fetch(url, {
        method: "POST",
        body: data ? JSON.stringify(data) : "",
        headers: {
            "Content-Type": "application/json",
        },
    });

    return await res.json();
}

startCheckout();


```


Run your application to see whether the `Adyen Drop-in` is showing a list of payment methods. You'll notice that the Drop-in won't let you click `"Pay"` as we haven't implemented the `/payments` call yet.
Here are some helpful notes if you do not see any payment methods show up on your website (`http://.../checkout?type=dropin`):
* **Empty response:** Have you configured any payment methods in the Customer Area?
* **Invalid origin:** Have you added the correct origin URLs that allow your `Adyen Drop-in` to be loaded by the page?
* **Unauthorized errors:** Have you specified your credentials correctly?

**Step 9.** Let's create the `/payments` request ([see docs](https://docs.adyen.com/online-payments/build-your-integration/advanced-flow/?platform=Web&integration=Drop-in&version=5.63.0&programming_language=java#post-payments-request-web)) on the backend.
We start by defining a new endpoint `/api/payments` to which our frontend will send a request.

<details>
<summary>Click to show me the answer</summary>

```java
    // Step 9 - Implement the /payments call to Adyen.
    @PostMapping("/api/payments")
    public ResponseEntity<PaymentResponse> payments(@RequestHeader String host, @RequestBody PaymentRequest body, HttpServletRequest request) throws IOException, ApiException {
        var paymentRequest = new PaymentRequest();

        var amount = new Amount()
                .currency("EUR")
                .value(9998L);
        paymentRequest.setAmount(amount);
        paymentRequest.setMerchantAccount(applicationConfiguration.getAdyenMerchantAccount());
        paymentRequest.setChannel(PaymentRequest.ChannelEnum.WEB);

        paymentRequest.setPaymentMethod(body.getPaymentMethod());

        var orderRef = UUID.randomUUID().toString();
        paymentRequest.setReference(orderRef);
        // The returnUrl field basically means: Once done with the payment, where should the application redirect you?
        paymentRequest.setReturnUrl(request.getScheme() + "://" + host + "/api/handleShopperRedirect?orderRef=" + orderRef); // Example: Turns into http://localhost:8080/api/handleShopperRedirect?orderRef=354fa90e-0858-4d2f-92b9-717cb8e18173


        log.info("PaymentsRequest {}", paymentRequest);
        var response = paymentsApi.payments(paymentRequest);
        log.info("PaymentsResponse {}", response);
        return ResponseEntity.ok().body(response);
    }
```

</details>



**Step 10.** Let's send a request to our backend from our frontend, and modify the `adyenWebImplementation.js` to override the `onSubmit(...)` function to send a request to the `/api/payments` endpoint.
We've added the `onSubmit(...)` event handler here and the `handleResponse(response, component)` function to handle the response (which is performing a simple redirect to the right page).

<details>
<summary>Click to show me the answer</summary>

```js
// ...
async function startCheckout() {
    try {
        let paymentMethodsResponse = await sendPostRequest("/api/paymentMethods");

        const configuration = {
            paymentMethodsResponse: paymentMethodsResponse,
            clientKey,
            locale: "en_US",
            environment: "test",
            showPayButton: true,
            paymentMethodsConfiguration: {
                card: {
                    hasHolderName: true,
                    holderNameRequired: true,
                    name: "Credit or debit card",
                    amount: {
                        value: 9998,
                        currency: "EUR",
                    },
                }
            },
            // Step 10 onSubmit(...), this function is executed when you hit the 'Pay' button
            onSubmit: async (state, component) => {
                if (state.isValid) {
                    const response = await sendPostRequest("/api/payments", state.data);
                    handleResponse(response, component);
                }
            }
        };

        // Start the AdyenCheckout and mount the element onto the `payment`-div.
        let adyenCheckout = await new AdyenCheckout(configuration);
        adyenCheckout.create(type).mount(document.getElementById("payment"));
    } catch (error) {
        console.error(error);
        alert("Error occurred. Look at console for details.");
    }
}

// Step 10 - Handles responses, do a simple redirect based on the result.
function handleResponse(response, component) {
    switch (response.resultCode) {
        case "Authorised":
            window.location.href = "/result/success";
            break;
        case "Pending":
        case "Received":
            window.location.href = "/result/pending";
            break;
        case "Refused":
            window.location.href = "/result/failed";
            break;
        default:
            window.location.href = "/result/error";
            break;
    }
}

// ...
```

</details>


**Step 11.** **Best practices:** The Adyen API supports idempotent requests, allowing you to retry a request multiple times while only performing the action once. This helps avoid unwanted duplication in case of failures and retries (e.g., you don't want to charge a shopper twice because they've hit the pay button two times, right?).
Add the idempotency key to your payment request, see [documentation](https://docs.adyen.com/development-resources/api-idempotency/).


<details>
<summary>Click to show me the answer</summary>

```java
    var requestOptions = new RequestOptions();
    requestOptions.setIdempotencyKey(UUID.randomUUID().toString());

    log.info("PaymentsRequest {}", paymentRequest);
    var response = paymentsApi.payments(paymentRequest, requestOptions);
    log.info("PaymentsResponse {}", response);
    return ResponseEntity.ok().body(response);
```

</details>

You should now be able to make a payment! **However**, we're not there yet! This flow will fail when a challenge is presented to the shopper (Strong Customer Authentication). Let's handle this by adding 3D Secure 2 Authentication support.

3D Secure 2 is an authentication protocol (3DS2) that provides an additional layer of verification for card-not-present (CNP) transactions. To trigger 3DS2, we'll need to add several parameters to the `PaymentRequest` in the `/api/payments` endpoint.
Pick one of these two options.
   * [Native](https://docs.adyen.com/online-payments/3d-secure/native-3ds2/web/): The card issuer performs the authentication within your website or mobile app using passive, biometric, and two-factor authentication approaches.
   * [Redirect](https://docs.adyen.com/online-payments/3d-secure/redirect-3ds2/web/): Shoppers are redirected to the card issuer's site to provide additional authentication data, for example, a password or an SMS verification code. The redirection might lead to lower conversion rates due to technical errors during the redirection or shoppers dropping out of the authentication process.


**Step 12.** Let's add 3DS2 to our `/payments`-request. Note: New to 3DS2? You can read our [docs](https://docs.adyen.com/online-payments/3d-secure/) or go to this [technical blog post](https://www.adyen.com/knowledge-hub/a-guide-to-integrating-with-adyen-web-for-3d-secure-2-payments) that will guide you through the why & whats.
Go back to the `/controller/ApiController`, let's add the following parameters to the Payment Request for the redirect flow:
   * Origin
   * ShopperIP
   * ShopperInteraction
   * BrowserInfo
   * BillingAddress (due to risk rules, we recommend including the `BillingAddress`, even though it's optional).

**Note:** In this example, we implement the Redirect 3DS2 flow. You can also opt-in to implement the [Native 3DS2 flow](https://docs.adyen.com/online-payments/3d-secure/native-3ds2/web-drop-in/#make-a-payment), which we've also included (commented-out*) in the answer below.

<details>
<summary>Click to show me the answer</summary>

```java
    // Step 9 - Implement the /payments call to Adyen.
    @PostMapping("/api/payments")
    public ResponseEntity<PaymentResponse> payments(@RequestHeader String host, @RequestBody PaymentRequest body, HttpServletRequest request) throws IOException, ApiException {
        var paymentRequest = new PaymentRequest();
        // ...

        // Step 12 3DS2 Redirect - Add the following additional parameters to your existing payment request for 3DS2 Redirect:
        // Note: Visa requires additional properties to be sent in the request, see documentation for Redirect 3DS2: https://docs.adyen.com/online-payments/3d-secure/redirect-3ds2/web-drop-in/#make-a-payment
        var authenticationData = new AuthenticationData();
        authenticationData.setAttemptAuthentication(AuthenticationData.AttemptAuthenticationEnum.ALWAYS);
        paymentRequest.setAuthenticationData(authenticationData);

        // Add these lines, if you like to enable the Native 3DS2 flow:
        // Note: Visa requires additional properties to be sent in the request, see documentation for Native 3DS2: https://docs.adyen.com/online-payments/3d-secure/native-3ds2/web-drop-in/#make-a-payment
        //authenticationData.setThreeDSRequestData(new ThreeDSRequestData().nativeThreeDS(ThreeDSRequestData.NativeThreeDSEnum.PREFERRED));
        //paymentRequest.setAuthenticationData(authenticationData);

        paymentRequest.setOrigin(request.getScheme() + "://" + host);
        paymentRequest.setBrowserInfo(body.getBrowserInfo());
        paymentRequest.setShopperIP(request.getRemoteAddr());
        paymentRequest.setShopperInteraction(PaymentRequest.ShopperInteractionEnum.ECOMMERCE);


        var billingAddress = new BillingAddress();
        billingAddress.setCity("Amsterdam");
        billingAddress.setCountry("NL");
        billingAddress.setPostalCode("1012KK");
        billingAddress.setStreet("Rokin");
        billingAddress.setHouseNumberOrName("49");
        paymentRequest.setBillingAddress(billingAddress);

        // ...
    }
```

</details>


**Step 13.** Implement the `/payments/details` call in `/controllers/ApiController`.

<details>
<summary>Click to show me the answer</summary>

```java
    // Step 13 - Handle details call (triggered after Native 3DS2 flow)
    @PostMapping("/api/payments/details")
    public ResponseEntity<PaymentDetailsResponse> paymentsDetails(@RequestBody PaymentDetailsRequest detailsRequest) throws IOException, ApiException {
        // Step 13.
        log.info("PaymentDetailsRequest {}", detailsRequest);
        var response = paymentsApi.paymentsDetails(detailsRequest);
        log.info("PaymentDetailsResponse {}", response);
        return ResponseEntity.ok()
                .body(response);
    }
```

</details>

Next up, in our frontend, let's override the `onAdditionalDetails(...)` function in `adyenWebImplementation.js` to call `/api/payments/details`.


<details>
<summary>Click to show me the answer</summary>

We've added the `onAdditionalDetails(...)` function in the `configuration` object and modified the `handleResponse(response, component)` function to allow the component to handle the challenge, see `component.handleAction(response.action)`.

```js
// ...

async function startCheckout() {
    try {
        let paymentMethodsResponse = await sendPostRequest("/api/paymentMethods");

        const configuration = {
            paymentMethodsResponse: paymentMethodsResponse,
            clientKey,
            locale: "en_US",
            environment: "test",
            showPayButton: true,
            paymentMethodsConfiguration: {
                card: {
                    hasHolderName: true,
                    holderNameRequired: true,
                    name: "Credit or debit card",
                    amount: {
                        value: 9998,
                        currency: "EUR",
                    },
                }
            },
            // Step 10 onSubmit(...), this function is executed when you hit the 'Pay' button
            onSubmit: async (state, component) => {
                if (state.isValid) {
                    const response = await sendPostRequest("/api/payments", state.data);
                    handleResponse(response, component);
                }
            },
            // Step 13 onAdditionalDetails(...), this function is executed when there's f.e. a Native 3DS2 flow
            onAdditionalDetails: async (state, component) => {
                const response = await sendPostRequest("/api/payments/details", state.data);
                handleResponse(response, component);
            }
        };

        // Start the AdyenCheckout and mount the element onto the `payment`-div.
        let adyenCheckout = await new AdyenCheckout(configuration);
        adyenCheckout.create(type).mount(document.getElementById("payment"));
    } catch (error) {
        console.error(error);
        alert("Error occurred. Look at console for details.");
    }
}

// Step 10 - Handles responses, do a simple redirect based on the result.
function handleResponse(response, component) {
    // Step 13 - If there's an action, handle it, otherwise redirect the user to the correct page based on the resultCode.
    if (response.action) {
        component.handleAction(response.action);
    } else {
        switch (response.resultCode) {
            case "Authorised":
                window.location.href = "/result/success";
                break;
            case "Pending":
            case "Received":
                window.location.href = "/result/pending";
                break;
            case "Refused":
                window.location.href = "/result/failed";
                break;
            default:
                window.location.href = "/result/error";
                break;
        }
    }
}

// ...
```

</details>


**Step 14.** Let's handle 3DS2 in our `/payments/details`-request by passing the `redirectResult` or `payload` in the `/payments/details`-call.


```java
// Step 14 - Handle Redirect 3DS2 during payment.
@GetMapping("/api/handleShopperRedirect")
public RedirectView redirect(@RequestParam(required = false) String payload, @RequestParam(required = false) String redirectResult) throws IOException, ApiException {
    var paymentDetailsRequest = new PaymentDetailsRequest();

    PaymentCompletionDetails paymentCompletionDetails = new PaymentCompletionDetails();

    // Handle redirect result or payload
    if (redirectResult != null && !redirectResult.isEmpty()) {
        // For redirect, you are redirected to an Adyen domain to complete the 3DS2 challenge
        // After completing the 3DS2 challenge, you get the redirect result from Adyen in the returnUrl
        // We then pass on the redirectResult
        paymentCompletionDetails.redirectResult(redirectResult);
    } else if (payload != null && !payload.isEmpty()) {
        paymentCompletionDetails.payload(payload);
    }

    paymentDetailsRequest.setDetails(paymentCompletionDetails);

    var paymentsDetailsResponse = paymentsApi.paymentsDetails(paymentDetailsRequest);
    log.info("PaymentsDetailsResponse {}", paymentsDetailsResponse);

    // Handle response and redirect user accordingly
    var redirectURL = "/result/";
    switch (paymentsDetailsResponse.getResultCode()) {
        case AUTHORISED:
            redirectURL += "success";
            break;
        case PENDING:
        case RECEIVED:
            redirectURL += "pending";
            break;
        case REFUSED:
            redirectURL += "failed";
            break;
        default:
            redirectURL += "error";
            break;
    }
    return new RedirectView(redirectURL + "?reason=" + paymentsDetailsResponse.getResultCode());
}

```



**Step 15.** Let's test this flow by making a payment using a special card number that always trigger 3DS2. You can find more test card numbers on [Adyen docs](https://docs.adyen.com/development-resources/testing/test-card-numbers/). We've included a 3DS2 test card below.

**Note:** For Cards, use the following Visa Test Card number, to trigger a 3DS2 flow. You can also download the official [Adyen Test Card Extension](https://chromewebstore.google.com/detail/adyen-test-cards/icllkfleeahmemjgoibajcmeoehkeoag) to prefill your card numbers.

```
4871 0499 9999 9910
03/30
737
```


**Step 16.** In order to receive payment updates. You need to configure webhooks in the Customer Area. The steps are quite straight forward.

You can receive webhooks by enabling webhooks in the Customer Area, followed by creating your `/webhooks`-endpoint in `Controllers/WebhookController.java`.
   - [Read the documentation first: Enable and verify HMAC signatures](https://docs.adyen.com/development-resources/webhooks/verify-hmac-signatures/)
   - Create a standard webhook in your Customer Area. Example URL -> `https://xxxx-xx.gitpod.io/webhooks` or `https://xxxx.github.dev/webhooks`
   - Don't forget to inject your `ADYEN_HMAC_KEY` in your `ApplicationConfiguration.java`, which you can then use to verify the HMAC signature.
   - Create a new `WebhookController.java` in `/java/com/adyen/workshop/controllers/WebhookController.java`


<details>
  <summary>Click to show me the answer</summary>

```java
    @PostMapping("/webhooks")
    public ResponseEntity<String> webhooks(@RequestBody String json) throws Exception {
        var notificationRequest = NotificationRequest.fromJson(json);
        var notificationRequestItem = notificationRequest.getNotificationItems().stream().findFirst();

        try {
            NotificationRequestItem item = notificationRequestItem.get();

            // Step 16 - Validate the HMAC signature using the ADYEN_HMAC_KEY
            if (!hmacValidator.validateHMAC(item, this.applicationConfiguration.getAdyenHmacKey())) {
                log.warn("Could not validate HMAC signature for incoming webhook message: {}", item);
                return ResponseEntity.unprocessableEntity().build();
            }

            // Success, log it for now
            log.info("""
                            Received webhook with event {} :\s
                            Merchant Reference: {}
                            Alias : {}
                            PSP reference : {}""",
                    item.getEventCode(),
                    item.getMerchantReference(),
                    item.getAdditionalData().get("alias"),
                    item.getPspReference());

            return ResponseEntity.accepted().build();
        } catch (SignatureException e) {
            // Handle invalid signature
            return ResponseEntity.unprocessableEntity().build();
        } catch (Exception e) {
            // Handle all other errors
            return ResponseEntity.status(500).build();
        }
    }
```
</details>


Congratulations, you've successfully built an integration with Adyen! You can now add support for different [payment methods](https://docs.adyen.com/payment-methods/).

You can now compare your solution to the solution in the [workshop/solution branch](https://github.com/adyen-examples/adyen-step-by-step-integration-workshop/tree/workshop/solution/src).


In future versions of the workshop, we'll use this module (`Module 1`) as a base line. We'll then add different payment method in different modules, for now you can try enabling the following payment methods:

**Step 18.** Enable [iDeal](https://docs.adyen.com/payment-methods/ideal/web-drop-in/).
   - Do not forget to enable the payment method in your [Customer Area](https://ca-test.adyen.com/)
   - On the frontend, modify your configuration so that it pre-selects an iDeal issuer + shows the images of the bank logos

**Step 19.** [Enable Klarna](https://docs.adyen.com/payment-methods/klarna/web-drop-in/?tab=_code_payments_code__2), we'll need to add an additional parameter in the payment request.
   - Do not forget to enable the payment method in your [Customer Area](https://ca-test.adyen.com/)
   - Do not forget to add lineItems to your payment-request


## Contacting us

If you have any questions, feel free to contact us at devrel@adyen.com.
