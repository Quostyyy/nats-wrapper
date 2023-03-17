# Example Java nats-wrapper

---

#### Repository(Maven)
```xml
<repository>
  <id>quosty-repository-releases</id>
  <name>quosty-dev</name>
  <url>https://repo.quosty.dev/releases</url>
</repository>
```
#### Dependency(Maven)
```xml
<dependency>
  <groupId>dev.quosty</groupId>
  <artifactId>nats-wrapper</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

#### Example use:

```java
public class ExampleApplication {
    

    public static void main(String[] args) {
        /*
        Create a new instance of the NatsWrapper and connect to the nats-server
         */
        NatsWrapper natsWrapper = new NatsWrapper(Nats.connect("nats://localhost:4222"));
        
        /*
         Listening to packets on the channel    
         */
        natsWrapper.registerListener(new NatsExampleListener());
        
        /*
         Create ExampleObject
         */
        ExampleObject exampleObject = new ExampleObject("test");

        /*
         Sending packet     
         */
        natsWrapper.sendPacket(
                "nats.example.listener", // Channel
                exampleObject // Object
        );
        
        /*
         Sending a callback packet
         */

        natsWrapper.sendCallbackPacket(
                "nats.example.listener", // Channel
                exampleObject, // Object
                3000, // Timeout
                new NatsCallback<ExampleObject>() {
                    @Override
                    public void onReceive(ExampleObject packet) {
                        System.out.println("Received callback packet");
                    }

                    @Override
                    public void exit() {
                        System.out.println("Timeout");
                    }
                }
        );
    }
}
```

#### Example Listener

```java
public class NatsExampleListener extends NatsListener<ExampleObject> {

    public NatsExampleListener() {
        super("nats.example.listener", ExampleObject.class);
    }

    @Override
    public void onReceive(ExampleObject packet, String replyTo) {
        System.out.println("Received ExampleObject: " + packet.getUserName());
    }
}
```

#### Example Object

```java
public class ExampleObject extends NatsPacket {

    private final String userName;

    public ExampleObject(String userName) {
        this.userName = userName;
    }
}
```
