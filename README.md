# Rohlik BDD workshop
You will find here a simple `Cart` + `Discount` models which we will use to explore and implement BDD with [cucumber](https://cucumber.io/).

We are going to iterate the solution through several steps. For each step we have created a branch called `stepX` where `X` represents the number of the step. If at any point you feel lost or want to check our proposed solution, consult the code there.

# Setup
 - ### Requirements
   - [Java JDK 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
   - [Docker](https://docs.docker.com/get-docker/) + [docker compose](https://docs.docker.com/compose/install/)
 
 - ### IDE
   - Install [intelliJ](https://www.jetbrains.com/idea/download/) community edition
     - Enable [cucumber](https://www.jetbrains.com/help/idea/enabling-cucumber-support-in-project.html) plugin
     - Enable [Annotation Processing](https://www.jetbrains.com/help/idea/annotation-processors-support.html) for `Lombok`
       - Preferences | Build, Execution, Deployment | Compiler | Annotation Processors
         - Enable annotation processing box is checked 
         - Obtain processors from project classpath option is selected

# Project
Basic MVC web project build on top of `spring` with `spring-boot 2.7`

- ### Lifecycle:
  - #### Dependencies 
    - `make dependencies`
  - #### test 
    - `make test`

<p align="right"><sup><sub>Made with ♥ by <img src="https://www.rohlik.group/sites/default/files/obsah/paticka/logo/rohlik-grouplogo.svg" width="20" height="20"></sub></sup></p>  