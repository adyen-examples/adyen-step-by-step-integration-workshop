const express = require("express");
const path = require("path");
const hbs = require("express-handlebars");
const dotenv = require("dotenv");
const morgan = require("morgan");
const { uuid } = require("uuidv4");

// init app
const app = express();
// setup request logging
app.use(morgan("dev"));
// Parse JSON bodies
app.use(express.json());
// Parse URL-encoded bodies
app.use(express.urlencoded({ extended: true }));
// Serve client from build folder
app.use(express.static(path.join(__dirname, "/public")));

// enables environment variables by
// parsing the .env file and assigning it to process.env
dotenv.config({
  path: "./.env",
});


app.engine(
  "handlebars",
  hbs.engine({
    defaultLayout: "main",
    layoutsDir: __dirname + "/views/layouts",
    //helpers: require("./util/helpers"),
  })
);

app.set("view engine", "handlebars");

/* ################# YOUR CONTROLLER / API ENDPOINTS ###################### */


// Hello-world
app.get("/hello-world", async (req, res) => {
  try {
    res.json("hello-world");
  } catch (err) {
    console.error(`Error: ${err.message}, error code: ${err.errorCode}`);
    res.status(err.statusCode).json(err.message);
  }
});



/* ################# YOUR VIEW CONTROLLERS ###################### */

// Index (select a demo)
app.get("/", (req, res) => res.render("index"));

// Result page example on how to pass parameters
app.get("/result/:type", (req, res) =>
  res.render("result", {
    type: req.params.type,
  })
);

// Start server
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => console.log(`Server started -> http://localhost:${PORT}`));
