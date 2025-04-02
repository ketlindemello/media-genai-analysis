package org.example.javafileop;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;

public class LambdaHandler implements RequestHandler<S3Event, String> {

    @Override
    public String handleRequest(S3Event s3event, Context context) {

        for (S3EventNotification.S3EventNotificationRecord record : s3event.getRecords()) {
            String bucket = record.getS3().getBucket().getName();
            String key = record.getS3().getObject().getKey();
            long size = record.getS3().getObject().getSizeAsLong();

            context.getLogger().log("New file uploaded:\n");
            context.getLogger().log("Bucket: " + bucket + "\n");
            context.getLogger().log("Key: " + key + "\n");
            context.getLogger().log("Size: " + size + " bytes\n");
            try {
                AwsResources rekog_analysis = new AwsResources(bucket, key);
                rekog_analysis.detectFacesinImage();
            } catch (Exception e) {
                System.out.println("Exception:" + e);
            }



        }

        return "Processed S3 event successfully";
    }
}