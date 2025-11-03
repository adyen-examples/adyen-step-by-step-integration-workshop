# Build Your Own Payment Integration with Adyen - Clean Spring Boot template



### Prerequisites

You'll need a few things to get started:
* Access to an [Adyen Test Account](https://www.adyen.com/signup).
* You can login to the [Adyen Customer Area on TEST](https://ca-test.adyen.com/) and navigate to your Merchant Account (ECOM).



### Context of the code repository.

This workshop uses a Java+Spring Boot in the backend with a static (HTML/CSS/Javascript) frontend with a `thymeleaf` template (a server-side Java template engine that can process HTML).

We start with a clean/empty Spring Boot template, which you'll extend into a fully working application that can accept payments on TEST.


### Project Structure

The project structure follows a Model-View-Controller (MVC) structure. You can skip this if you're familiar with this.

* The Java code is to be found in `src/main/java/com/adyen/workshop`
  * `/controllers` folder contains your endpoints. The following example creates the `/hello-world` endpoint; see `/controllers/ApiController.java.`

  ```
      @GetMapping("/hello-world")
      public ResponseEntity<String> helloWorld() throws Exception {
          return ResponseEntity.ok().body("This is the 'Hello World' from the workshop - You've successfully finished step 0!");
      }
  ```

  * `/views`-folder contains view controllers that show the HTML pages in the `/resources/static/` folder
    * We've added some css and images that you can use. Note: You don't have to use it. Text-only is fine!
  * You can manage your environment variables in the `application.properties`-file which gets picked up by the `ApplicationConfiguration.java` class.

* To run the project:
  * Run `gradle wrapper` in your terminal
  * Build & run the application using `./gradlew bootRun`


### Briefing

You're working as a full-stack developer for an E-Commerce company that sells headphones and sunglasses in the Netherlands.
They sell the best headphones and sunglasses at 49.99 each, and you're incredibly excited to take on this challenge.
You've been tasked to implementing a credit card payment end-to-end.

### Start here:

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://github.com/codespaces/new?hide_repo_select=true&ref=empathy-engineering/clean-template&repo=761184899&skip_quickstart=true)


**Step 1.** Build the project using `gradle wrapper && ./gradlew bootRun`:

If you see the following message in your console logs, it means that you've successfully ran the application. 

The server will now start listening for incoming requests; Press `Control` + `C` to stop the webserver application.
   - Note: If you see a message with "80% executing...", you're good to go. It means that your webserver is listening for request, and it will keep listening until you force it to stop (see above).
A browser should open with the following screen: **"Workshop: Build Your Own Adyen Payment Integration"**

```
----------------------------------------------------------
	Application is running on http://localhost:8080
----------------------------------------------------------
```


### Definition of Done:

* You've implemented a successful payment on TEST that can be seen in your [Customer Area page](https://ca-test.adyen.com/).
