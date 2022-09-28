# MachineLearningEmail Project
MachineLearningEmail project is a backend project which uses machine learning in order to determine what services you have registered for in the past (but you forgot). 

For example, if the user has gotten hacked or must migrate to a new email, it can be a headache to determine all the services the user registered for in the past. This application would compile a list of potential services the user registered for, in order to help with email migration. 

## Description
MachineLearningEmail project is a java project built on SpringBoot. It uses Google's Gmail API in order to create a session between the user and application. Once a session has been established, the application would go through the user's email subjects and try to determine which emails are associated with the user creating an account.

## Getting Started

### Dependencies
* OpenJdk 14.0.2

## Current Project Status
**Put on Hold**
This is a passion project of mine and will require more complexity than i anticipated. Currently putting on hold until i fully complete another project.

## Things to be completed
* Refactor file structure and define more robust objects
* Implementing secure practices to ensure privacy
* Integrate other mailing services (outlook, yahoo, etc)
