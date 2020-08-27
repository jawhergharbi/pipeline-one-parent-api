## Running in the Local environment
How to run App Engine and Datastore locally to execute the tests. 
1. Start datastore emulator: 
   ```console
   gcloud beta emulators datastore start
   ```

2. Setting environment variables
   1. If the application and the emulator are running in the same machine: run **env-init** using command substitution:
   ```console
   $(gcloud beta emulators datastore env-init)
   ```
      
   2. If the application and the emulator are running in different machines, please check [this](https://cloud.google.com/datastore/docs/tools/datastore-emulator#manually_setting_the_variables) out.
    
3. Once you're done, remove the environment variables
   1. If the application and the emulator are running in the same machine: run **env-unset** using command substitution: 
   ```console
   $(gcloud beta emulators datastore env-unset)
   ```
      
   2. If the application and the emulator are running in different machines, please check [this](https://cloud.google.com/datastore/docs/tools/datastore-emulator#manually_removing_the_variables) out.

### Testing
- [Hamcrest CheatSheet](https://www.marcphilipp.de/downloads/posts/2013-01-02-hamcrest-quick-reference/Hamcrest-1.3.pdf)
   
### TO BE DONE
- Integration tests
- How to automate the emulator startup for the testing phase?
- How to execute the application inside a docker container (locally)?

### Utilities
- How to create fancy title for scripting bash files:
  - [How can I print existing ascii-art from a Bash script?](https://askubuntu.com/questions/690926/how-can-i-print-existing-ascii-art-from-a-bash-script)
  - [Ascii art generator sites](https://askubuntu.com/a/993510)

### Training
- TDD:
  - [Pluralsight- Effective Automated Testing with Spring](https://app.pluralsight.com/course-player?clipId=91b03d84-5432-459d-b696-9ffe35ebece8)
  - [Pluralsight - TDD with Spring and JUnit 5](https://app.pluralsight.com/course-player?clipId=13679ca7-ba35-42a6-81d1-8f77ba36687f)
  
- GCP and Spring Boot
  - [Pluralsight - Building Scalable Java Microservices with Spring Boot and Spring Cloud on Google Cloud](https://app.pluralsight.com/course-player?clipId=85242777-7a3a-4d98-baca-ecf970ff04c2)
  
- DevOps
  - [Using Gitlab CI/CD to deploy a Spring Boot application in Google Cloud](https://medium.com/@lars.willemsens/using-gitlab-ci-cd-to-deploy-a-spring-boot-application-in-google-cloud-3b22474e3ffc)
  - [Spring Boot Tutorial: Building Microservices Deployed to Google Cloud](https://www.infoq.com/articles/spring-boot-tutorial/)
  - [Circle CI - Automated releases in a cross-language monorepo](https://systemseed.com/blog/automated-releases-cross-language-monorepo)
  - [Deploy a Java application to Kubernetes on Google Kubernetes Engine](https://codelabs.developers.google.com/codelabs/cloud-springboot-kubernetes/index.html?index=..%2F..index#4)
  - [GCP: Spring To Production With App Engine, Cloud Build And GitHub](https://medium.com/google-cloud/gcp-deploying-to-app-engine-from-github-using-cloud-build-df2582f968c7)
