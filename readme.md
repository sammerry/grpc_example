# Mediation gRPC Server/Client

```
brew install scala
```

Then in this project run
```
sbt compile
sbt run
```

In a new terminal run
```
sbt test
```

# Running in intelliJ

You'll want to develop in intelliJ.

Install the Scala, Markdown and Protobuf plugins. 

Then run the following to generate the required scala code
```
sbt compile
```

Now in intelliJ select `view` -> `tool windows` -> `sbt` and then click on the recycle icon to load dependencies from maven/ivy

Then open intelliJ and remove exclusion on the `target` directory. This will allow code completion to find deffenitions there.
You should be able to now use the drop down in the upper right hand corner to both run the server and run tests.

# Useful Reading
https://blog.cloudflare.com/tools-for-debugging-testing-and-using-http-2/
