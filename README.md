# OpenAPI Demo

The goal of this project is to show how to use two project in Spring Boot to enable api first contract implementation 
of Java services.

The two projects are:

* [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator)
* [SpringDoc](https://springdoc.org/)

The OpenAPI generator is used as a maven plugin to create annotated code and interfaces for you to implement, whilst 
SpringDoc is a regular Maven dependency to show Swagger's UI pointing to your locally running instance.

## Walkthrough

<span style="color:red">NOTE: This implementation uses Spring Boot 3. If you are currently using Spring Boot 2 then 
make sure that you port to Spring Boot 3 before making these changes, or you will hit lots of issues based on package 
names and dependencies, specifically the move of lots of javax dependencies to jakarta.</span>

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.2</version>
</dependency>
```
Next we add the plugin to generator to autogenerate a class structure from our openapi.yaml file.
```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>
                    ${project.basedir}/openapi.yaml
                </inputSpec>
                <generatorName>spring</generatorName>
                <apiPackage>com.precisely.engage.demo.api</apiPackage>
                <modelPackage>com.precisely.engage.demo.model</modelPackage>
                <configOptions>
                    <sourceFolder>src/main/java/</sourceFolder>
                    <interfaceOnly>true</interfaceOnly>
                    <useSpringBoot3>true</useSpringBoot3>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```
`<inputSpec>` tells the plugin where to find the OpenAPI definition for your service.

`<generatorName>` tells the plugin that we are using Spring.

`<apiPackage>` and `<modelPackage>` are complimentary and tell the plugin what package to place first the 
autogenerated api classes and then the model classes.

`<sourceFolder>` tells the plugin the file structure to create under `target/generated-sources/openapi`.

`<interfaceOnly>` stops the plugin from generating an implementation of the required interface. We need to do this 
ourselves in the main source tree of the project.

`<useSpringBoot3>` allows the plugin to work with SpringBoot3, making some changes to generated classes. The major change is changing `javax` imports to `jakarta`.

More configuration options can be found in the [plugin documentation](https://openapi-generator.tech/docs/generators/spring/).

Finally we need to add a dependency on `jackson-databind-nullable` from the `org.openapitools` group.
```xml
<dependency>
    <groupId>org.openapitools</groupId>
    <artifactId>jackson-databind-nullable</artifactId>
    <version>0.2.4</version>
</dependency>
```
With all this in place, the only thing that remains is to put our `openapi.yml` file in the root of the project and 
then create the generated code.

```shell
./mvnw clean compile
```

<span style="color:red">NOTE: If you are using IDEA, right click on the project (`openapidemo`) and then choose **File, Maven, Generate Sources and
Update Folders**. This will ensure the IDE can find the sources and enable code completion etc.</span>

With this accomplished successfully we now need to create an implementation for the autogenerated interface. Note that 
this is just a dummy implementation and performs the processing directly rather than taking a hexagonal architecture 
approach so isn't good practise but is much shorter for a demo.

```Java
package com.precisely.engage.openapidemo;

import com.precisely.engage.demo.api.PlanetApi;
import com.precisely.engage.demo.model.Planet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PlanetController implements PlanetApi {
    @Override
    public ResponseEntity<Planet> onePlanet(Integer planetId) {
        Planet planet = new Planet();
        planet.setName("Mars");
        return ResponseEntity.ok(planet);
    }

    @Override
    public ResponseEntity<List<Planet>> allPlanets() {
        List<Planet> planets = new ArrayList<>();
        Planet planet = new Planet();
        planet.setName("Pluto");
        planet.setMoons(5);
        planets.add(planet);
        return ResponseEntity.ok(planets);
    }
}
```
Essentially this creates a new `RestController` object which extends the autogenerated `PlanetApi` interface. There 
are two required methods in the interface, `onePlanet` and `allPlanets`. 

The `onePlanet` implementation simply creates a `Planet` model class with the name **Mars** and returns it to the 
caller.

The `allPlanets` implementation creates and `ArrayList` containing and single Planet ("Pluto") with 5 moons and 
returns it to the caller.

With that in place you should now be able to run `OpenapidemoApplication` in your IDE as normal.

The Swagger UI user interface is available at http://localhost:8080/swagger-ui/index.html.
You can execute the endpoints from there or use the following two curl commands.

```shell
curl http://localhost:8080/planet/1 
```

```shell
curl http://localhost:8080/planet
```

# Notes

* Remember to refresh your dependencies via the IDE or by running `./mvnw clean compile` every time your openapi.yaml file changes.
* Use the maven wrapper (`mvnw`) rather than locally installed maven as this ensures everyone is on the same version.
* The application.properties file contains a `springdoc.swagger-ui.enabled = true` entry. This makes it easier to turn 
  off the Swagger User Interface if required without having to find the correct configuration flag.

Enjoy!

Colin