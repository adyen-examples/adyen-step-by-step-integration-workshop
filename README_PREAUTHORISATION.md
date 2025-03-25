## Module - Preauthorisation (adjusting payments)

Note: This is a continuation of the advanced-flow explained in the [README](README.md).


### Briefing




### Steps

Read: https://docs.adyen.com/online-payments/classic-integrations/modify-payments/adjust-authorisation/

You can start by implementing the asynchronous flow.
Once that's working, you can modify the solution slightly to try out to synchronous flow which would require you to keep track of a blob.

For this particular exercise, you can just manually remember `pspReference` and enter it in the subsequent API call.
Alternatively, you need to hook-up everything to a button. It's up to you how you want to do this.

0. [Configure your Merchant Account](https://docs.adyen.com/online-payments/capture/#enable-manual-capture) with a capture delay or manual capture
   - Alternatively, you can specify a [manual-capture per payment request](https://docs.adyen.com/online-payments/capture/?tab=individual_payment_1_2)
1. Implement a new endpoint `/api/preauthorisation` to  [preauthorize a payment](https://docs.adyen.com/online-payments/adjust-authorisation/adjust-with-preauth/#pre-authorize)
   - Handle the `AUTHORISATION` webhook
2. Implement a new endpoint `/api/modify-amount` to [adjust an authorisation](https://docs.adyen.com/online-payments/adjust-authorisation/adjust-with-preauth/#adjust-auth)
   - Handle the `AUTHORISATION_ADJUSTMENT` webhook
   - Note: You can implement the [asynchronous flow](https://docs.adyen.com/online-payments/adjust-authorisation/adjust-with-preauth/?tab=asynchronous_authorization_adjustment_0_1) first, before (later on) implementing and understanding the [synchronous flow](https://docs.adyen.com/online-payments/adjust-authorisation/adjust-with-preauth/?tab=synchronous_authorization_adjustment_1_2)
3. Implement a new endpoint `/api/capture` to [capture the payment](https://docs.adyen.com/online-payments/capture/)
   - Handle the `CAPTURE` webhook
   - Handle the [`CAPTURE_FAILED` webhook](https://docs.adyen.com/online-payments/capture/failure-reasons/)
4. Implement a new endpoint [`/api/cancel`](https://docs.adyen.com/online-payments/cancel/)
   - Handle the `TECHNICAL_CANCEL` webhook
   - Handle the `CANCELLATION` webhook
5. Implement a new endpoint [`/api/refund`](https://docs.adyen.com/online-payments/refund/)
   - Handle the `REFUND`, `REFUND_FAILED` `REFUNDED_REVERSED` webhooks

I've verified and tested the following flows and understand the successful/unsuccessful scenarios:
* [ ] `/api/preauthorisation` -> `/api/capture`
* [ ] `/api/preauthorisation` -> `/api/modify-amount` -> `/api/capture`
* [ ] `/api/preauthorisation` -> `/api/modify-amount` -> `/api/capture` -> `/api/cancel`
* [ ] `/api/preauthorisation` -> `/api/modify-amount` -> `/api/capture` -> `/api/refund`
* [ ] `/api/preauthorisation` -> `/api/modify-amount` -> `/api/capture` -> `/api/refund` -> `/api/cancel`

* [ ] `/api/preauthorisation` -> `/api/cancel`
* [ ] `/api/preauthorisation` -> `/api/cancel` -> `/api/capture`

* [ ] `/api/preauthorisation` -> `/api/refund`
* [ ] `/api/preauthorisation` -> `/api/refund` -> `/api/capture`

I've triggered & handled the following webhooks:
* [ ] Handle the `TECHNICAL_CANCEL` webhook
* [ ] Handle the `REFUND_FAILED` webhook
* [ ] Handle the `REFUNDED_REVERSED` webhook