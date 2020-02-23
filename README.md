Loosely based on:
* https://docs.axoniq.io/reference-guide/
* https://docs.axoniq.io/
* http://progressivecoder.com/implementing-event-sourcing-using-axon-and-spring-boot-part-1/
* http://progressivecoder.com/implementing-event-sourcing-with-axon-and-spring-boot-part-2/
* http://progressivecoder.com/implementing-event-sourcing-with-axon-and-spring-boot-part-3/
* https://www.baeldung.com/axon-cqrs-event-sourcing

Requires a standalone server instance of Axon Server: https://axoniq.io/download<br>
`java -jar ./axonserver.jar`

Then start the test application `mvn clean spring-boot:run1`

Finally, navigate to `http://localhost:8080/swagger-ui.html` in your browser.
