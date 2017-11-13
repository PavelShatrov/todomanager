# todomanager
It's a simple TODO Manager. It's a WEB based application and has two parts - client and server.
The server subproject(TODOManager) based on SpringBoot framework. To build the project with Maven
  mvn clean install
to run it
  mvn spring-boot:run
after start it can accept REST requests on URL
  localhost:8090/todo
  
The client part is GWT based project(TTest). It is created in Netbeans IDE with GWT-plugin.
After compile it can be started with any WEB-server and can communicate with server using Ajax-requests.
 
