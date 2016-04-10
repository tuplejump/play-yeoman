This application is to demo the different features supported by the plugin using injected routes generator(Play 2.5).

Start sbt and run the following -

```
$ update

$ npm install

$ bower install

$ grunt

$ run
```

This application provides different routes -

**/oldhome** serves the _default_ play landing page

**/** redirects to **/ui/**

**/ui/** serves the _angular application_

**/api/somejson?keyword=hello** serves a _json response_

**/ui/views/templ.html** serves a template defined in views for which content is passed from the controller

**/tdemo** serves a dynamic template defined in views for which HTML is passed from the controller

