package org.example.javafileop;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AwsResources {
    private String bucketName;
    private String keyName;
    Region region = Region.US_EAST_1;

    public AwsResources(String bucketName, String keyName){
        this.keyName = keyName;
        this.bucketName = bucketName;
    }

    public void display_image_info(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        System.out.println("Image Loaded:");
        System.out.println("Width: " + width + ", Height: " + height);
    }

    public void printData() {
        System.out.printf("API Key: %s", "something here");
    }

    // Method declaration and definition
    public void list_s3_bucket() {
        // Method body
        ListBucketsResponse response;
        try (S3Client s3 = connect_client()) {
            response = s3.listBuckets();
        }

        response.buckets().forEach(bucket -> System.out.println(bucket.name()));
        System.out.println("This is the list_s3_bucket method.");
    }

    public S3Client connect_client() {
        // 1. Create S3 Client
        // Use your bucket's region
        // Uses ~/.aws/credentials
        System.out.println("This is the connect_client method.");
        return S3Client.builder()
                .region(Region.US_EAST_1) // Use your bucket's region
                .credentialsProvider(ProfileCredentialsProvider.create()) // Uses ~/.aws/credentials
                .build();
    }

    public void rekognition() throws FileNotFoundException {
        // Change bucket and photo to your S3 Bucket and image.
        String photo = "photo";
        String bucket = "bucket";

        RekognitionClient rekognitionClient = RekognitionClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create()) // optional if default creds work
                .build();
        System.out.println(rekognitionClient);


        String sourceImage = "src/main/resources/house.jpg";
        InputStream sourceStream = new FileInputStream(sourceImage);
        SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

        // Create an Image object for the source image.
        Image souImage = Image.builder()
                .bytes(sourceBytes)
                .build();

        DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                .image(souImage)
                .maxLabels(10)
                .build();

        DetectLabelsResponse labelsResponse = rekognitionClient.detectLabels(detectLabelsRequest);
        List<Label> labels = labelsResponse.labels();
        System.out.println("Detected labels for the given photo");
        for (Label label : labels) {
            System.out.println(label.name() + ": " + label.confidence().toString());
        }
    }


    public void DetectLabelsS3 (){
        String bucket = "awsjavaimageshope";
        String image = "cartoonified_under_1mb.jpg";
//        String image = "Buster4.jpg";

        Region region = Region.US_EAST_1;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .build();

        try {
            S3Object s3Object = S3Object.builder()
                    .bucket(bucket)
                    .name(image)
                    .build();

            Image myImage = Image.builder()
                    .s3Object(s3Object)
                    .build();

            DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                    .image(myImage)
                    .maxLabels(10)
                    .build();

            DetectLabelsResponse labelsResponse = rekClient.detectLabels(detectLabelsRequest);
            List<Label> labels = labelsResponse.labels();
            System.out.println("Detected labels for the given photo");
            for (Label label : labels) {
                System.out.println(label.name() + ": " + label.confidence().toString());
            }

        } catch (RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public void save_s3_file() {
        String key = "casa.jpg"; // S3 key (filename in S3)
        String filePath = "src/main/resources/house.jpg";     // Local file in current dir

        // 1. Create S3 Client
        try (S3Client s3 = connect_client()) {

            // 2. Create the upload request
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            // 3. Upload file
            s3.putObject(request, RequestBody.fromFile(Paths.get(filePath)));
        }

        System.out.println("File uploaded to S3: " + bucketName + "/" + key);

    }

    public void detectFacesinImage(){
        Region region = Region.US_EAST_1;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .build();

        Image souImage = Image.builder()
                .s3Object(S3Object.builder()
                        .bucket(this.bucketName)
                        .name(this.keyName)
                        .build())
                .build();
        DetectFacesRequest facesRequest = DetectFacesRequest.builder()
                .attributes(Attribute.ALL)
                .image(souImage)
                .build();

        DetectFacesResponse facesResponse = rekClient.detectFaces(facesRequest);
        List<FaceDetail> faceDetails = facesResponse.faceDetails();


        if (faceDetails.isEmpty()) {
            System.out.println("No face identified in list: " + faceDetails);
        } else {
            for (FaceDetail face : faceDetails) {
                System.out.println("face %s" + face.confidence());
                AgeRange ageRange = face.ageRange();
                System.out.println("The detected face is estimated to be between "
                        + ageRange.low().toString() + " and " + ageRange.high().toString()
                        + " years old.");

                System.out.println("Verify if matches any image in database.");
                compare_faces(souImage);

//                System.out.println("There is a smile : " + face.smile().value().toString());
            }
        }

    }

    public List<String> list_bucket_objects() {
        List<String> targetImages = new ArrayList<>();
        try (S3Client s3 = connect_client()) {
            String folderPrefix = "image_source/"; // Specify the folder inside the bucket

            s3.listObjectsV2(builder -> builder.bucket(bucketName).prefix(folderPrefix).build())
                    .contents()
                    .forEach(object -> {
//                        System.out.println("Key: " + object.key());
                        targetImages.add(object.key());
                    });
        } catch (Exception e) {
            System.err.println("Error while listing objects in the bucket: " + e.getMessage());
        }
        return targetImages;
    }


    public void compare_faces(Image sourceImage) {

        Float similarityThreshold = 70F;
        //        String sourceImage = args[0];
        //        String targetImage = args[1];
        Region region = Region.US_EAST_1;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .build();

        try {

            // Create an Image object for the source image.

//            Image souImage = Image.builder()
//                    .s3Object(S3Object.builder()
//                            .bucket(this.bucketName)
//                            .name(sourceImage)
//                            .build())
//                    .build();

//            list all items from bucket
            Image tarImage = null;
// FIX here this method call
            S3Client s3Client = connect_client();
            for (String targetImg : list_bucket_objects()) {
                System.out.println("Image name" + targetImg);


                try {
                    // Check if target image exists in the bucket
//                    boolean imageExists = s3Client.listObjectsV2(builder -> builder.bucket(this.bucketName).prefix(targetImg.trim()).build())
//                            .contents()
//                            .stream()
//                            .anyMatch(obj -> obj.key().equals(targetImg.trim()));

                    if (Objects.equals(targetImg, "image_source/")) {
                        continue;
                    }

                    // Proceed to build Image object if it exists
                    tarImage = Image.builder()
                            .s3Object(S3Object.builder()
                                    .bucket(this.bucketName)
                                    .name(targetImg.trim()) // Trim to avoid spaces
                                    .build())
                            .build();

                    CompareFacesRequest facesRequest = CompareFacesRequest.builder()
                            .sourceImage(sourceImage)
                            .targetImage(tarImage)
                            .similarityThreshold(similarityThreshold)
                            .build();

                    // Compare the two images.
                    CompareFacesResponse compareFacesResult = rekClient.compareFaces(facesRequest);
                    List<CompareFacesMatch> faceDetails = compareFacesResult.faceMatches();
                    for (CompareFacesMatch match : faceDetails) {
                        ComparedFace face = match.face();
                        BoundingBox position = face.boundingBox();
                        System.out.println("Face at " + position.left().toString()
                                + " " + position.top()
                                + " matches with " + face.confidence().toString()
                                + "% confidence.");

                        // Check if confidence exceeds 60% and display the target image name
                        if (face.confidence() > 60) {
                            System.out.println("Match found in target image: " + targetImg);
                        }
                    }
                } catch (Exception e) {
                    System.err.printf("Error while checking or processing target image '%s': %s%n", targetImg.trim(), e.getMessage());
                }

                CompareFacesRequest facesRequest = CompareFacesRequest.builder()
                        .sourceImage(sourceImage)
                        .targetImage(tarImage)
                        .similarityThreshold(similarityThreshold)
                        .build();

                // Compare the two images.
                CompareFacesResponse compareFacesResult = rekClient.compareFaces(facesRequest);
                List<CompareFacesMatch> faceDetails = compareFacesResult.faceMatches();
                for (CompareFacesMatch match : faceDetails) {
                    ComparedFace face = match.face();
                    BoundingBox position = face.boundingBox();
                    System.out.println("Face at " + position.left().toString()
                            + " " + position.top()
                            + " matches with " + face.confidence().toString()
                            + "% confidence.");

                    // Check if confidence exceeds 60% and display the target image name
                    if (face.confidence() > 60) {
                        System.out.println("Match found in target image: " + targetImg);
                    }
                }
            }
//            List<ComparedFace> uncompared = compareFacesResult.unmatchedFaces();
//            System.out.println("There was " + uncompared.size() + " face(s) that did not match");
//            System.out.println("Source image rotation: " + compareFacesResult.sourceImageOrientationCorrection());
//            System.out.println("target image rotation: " + compareFacesResult.targetImageOrientationCorrection());

        } catch (RekognitionException e) {
            System.out.println("exception" + e);;
        }
    }
}
