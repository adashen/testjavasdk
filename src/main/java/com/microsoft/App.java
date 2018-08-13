package com.microsoft;

import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.exceptions.ModuleClientException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;


public class App
{
    private static MessageCallbackMqtt msgCallback = new MessageCallbackMqtt();
    private static EventCallback eventCallback = new EventCallback();

    protected static class EventCallback implements IotHubEventCallback
    {
        public void execute(IotHubStatusCode status, Object context)
        {
            if (context instanceof Message) {
                System.out.println("Send message with status: " + status.name());
            } else {
                System.out.println("Invalid context passed");
            }
        }
    }

    protected static class MessageCallbackMqtt implements MessageCallback
    {
        private int counter = 0;
        public IotHubMessageResult execute(Message msg, Object context)
        {
            this.counter += 1;

            System.out.println(String.format("Received message %d: %s", this.counter, new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET)));
            if (context instanceof  ModuleClient) {
                ModuleClient client = (ModuleClient) context;
                client.sendEventAsync(msg, eventCallback, msg, "output1");
            }
            return IotHubMessageResult.COMPLETE;
        }
    }

    public static void main( String[] args ) throws IOException, ModuleClientException
    {
        IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
        System.out.println("start to create client");
        ModuleClient client = ModuleClient.createFromEnvironment(protocol);
        System.out.println("client created");
        client.setMessageCallback("input1", msgCallback, client);
        System.out.println("message callback set");
        try {
            client.open();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Opened connection to IoT Hub.");
        System.out.println("Beginning to receive messages...");
        System.out.println("Press any key to exit...");
        System.out.println("\n");

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        client.closeNow();
    }
}
