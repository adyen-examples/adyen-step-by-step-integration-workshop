## Module - Tokenization (Subscriptions)

Note: This is a continuation of the advanced-flow explained in the [README](README.md).


In the following section, we'll add an endpoint to support the tokenization flow.

### Briefing

Oh noes! The ecommerce company that sells headphones and sunglasses in the Netherlands has changed their sales strategy.
Sales have massively plummeted, and they are trying out a new approach: **subscription**!

Instead of paying 49.99 euros in one go, you can now **rent headphones for 5 euros a month!**
Doing some quick mathematics, you might think it would be better to buy-it one ago. But hey, who would question their decisions right?

All you know, is that without your excellent problem-solving skills, they are in some hot water (e.g. *BIG trouble*).
You're ready to put your coding skills to good use.



### Steps
Follow the guide here: https://docs.adyen.com/online-payments/tokenization/advanced-flow/.

In order to enable subscriptions, we need to do three things:
1. Perform [a zero-auth payment](https://docs.adyen.com/online-payments/tokenization/advanced-flow/#create-a-token) (a payment of 0) to **tokenize** a card payment from a shopper, you'll get the `recurringDetailReference` in the webhook.
   - Implement a new endpoint `/api/subscription-create`.
   - Understand the different ways you can flag transactions (`Subscriptions`,`CardOnFile`, and `UnscheduledCardOnFile`).
   - Update the frontend (Drop-in/Components) `adyenWebImplementation.js` so that the frontend will send a request to this endpoint.
2. Handle the `RECURRING_CONTRACT` webhook, this webhook should contain the `recurringDetailReference` - We need store this value (we refer to this as the "token").
   - Handle the `AUTHORISATION` webhook.
   - Note: instead of storing the value on application-level, you can also copy paste the recurringDetailReference manually.
3. Use this token to make a payment.
   - Create a new endpoint `/api/subscription-payment` that charges the user once when this is called.
   - (Optionally) Write a function that calls this endpoint once at the start of the month.
4. Implement a new endpoint `/api/subscriptions-cancel` that [cancels the subscriptions](https://docs.adyen.com/online-payments/tokenization/managing-tokens/#delete-stored-details) (e.g. deletes the stored token).


I've verified and tested the following flows:
* [ ] `/api/subscription-create` -> `/api/subscription-payment`
* [ ] `/api/subscription-create` -> `/api/subscription-cancel`
* [ ] `/api/subscription-create` -> `/api/subscription-payment` -> `/api/subscription-cancel`
* [ ] `/api/subscription-create` -> `/api/subscription-payment` -> `/api/subscription-cancel` -> `/api/subscription-payment`


I've triggered & handled the following webhooks:
* [ ] Handle the `AUTHORISATION` webhook
* [ ] Handle the `RECURRING_CONTRACT` webhook