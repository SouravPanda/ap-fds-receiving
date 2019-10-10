# Foundation Receiving Service

### Build Image Locally

    docker build -f docker/Dockerfile . -t apfsregistrydev.azurecr.io/ap-fds-receive:{version}  # Run this from base directory of project and replace version with desired version 

### Azure Deployment Sample Using Docker Compose:


       version: '2'
       services:
         mesh:
           image: apfsregistrydev.azurecr.io/ap-fds-servicemesh:1.0.0
           ports:
             - "4141:4141"
         api:
           image: apfsregistrydev.azurecr.io/ap-fds-receive:2.1.99-SNAPSHOT


