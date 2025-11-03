# Build Your Own Payment Integration with Adyen - Clean template


### Prerequisites

You'll need a few things to get started:
* Access to an [Adyen Test Account](https://www.adyen.com/signup).
* You can login to the [Adyen Customer Area on TEST](https://ca-test.adyen.com/) and navigate to your Merchant Account (ECOM).



### Project Structure

You can skip this section if you're familiar with this.

* The code is to be found in `src/`-folder:
  * `/src/index.js` folder contains your endpoints. The following example creates the `/hello-world` endpoint;

  ```
    // Get payment methods
    app.post("/hello-world", async (req, res) => {
      try {
        res.json("hello-world!");
      } catch (err) {
        console.error(`Error: ${err.message}, error code: ${err.errorCode}`);
        res.status(err.statusCode).json(err.message);
      }
    });
  ```

  * `/views`-folder contains view controllers that show the HTML pages in the `views/layout/` folder
    * We've added some css and images in `/src/public/css` & `/src/public/images` that you can use. Note: You don't have to use it. Text-only is fine!
  * You can manage your environment variables by creating the `.env`-file which gets picked up by the using the `.dotenv` library.

* To run the project:
  * Navigate to the folder `cd /src`
  * Install the dependencies using `npm install`
  * Run the project using `npm run dev`



### Briefing

You're working as a full-stack developer for an E-Commerce company that sells headphones and sunglasses in the Netherlands.
They sell the best headphones and sunglasses at 49.99 each, and you're incredibly excited to take on this challenge.
You've been tasked to implementing a credit card payment end-to-end.


### Start here:

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://github.com/codespaces/new?hide_repo_select=true&ref=empathy-engineering-js/clean-template&repo=761184899&skip_quickstart=true)


**Step 1.** Build the project using `cd src && npm install && npm run dev`:

The server will now start listening for incoming requests; Press `Control` + `C` to stop the webserver application.
   - Note: If you see a message with "80% executing...", you're good to go. It means that your webserver is listening for request, and it will keep listening until you force it to stop (see above).
A browser should open with the following screen: **"Workshop: Build Your Own Adyen Payment Integration"**

```
Server started -> http://localhost:8080
```


### Definition of Done:

* You've implemented a successful payment on TEST that can be seen in your [Customer Area page](https://ca-test.adyen.com/).