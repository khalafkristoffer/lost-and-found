package com.lostfound.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tensorflow.Operation;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ImageProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(ImageProcessingService.class);
    private final SavedModelBundle model;
    private final List<String> labels;
    private static final String MODEL_PATH = "src/main/resources/SavedModel";
    private static final int IMAGE_SIZE = 224;  // MobileNetV2 input size
    private String inputTensorName;
    private String outputTensorName;

    public ImageProcessingService() {
        try {
            // Load model
            model = SavedModelBundle.load(MODEL_PATH, "serve");
            logger.info("Model loaded successfully from: " + MODEL_PATH);

            // Load labels
            labels = Files.readAllLines(Paths.get("src/main/resources/labels.txt"));
            logger.info("Loaded {} labels", labels.size());

            // Find input and output tensor names by scanning the graph
            List<String> operations = new ArrayList<>();
            for (Iterator<Operation> it = model.graph().operations(); it.hasNext(); ) {
                Operation op = it.next();
                String opName = op.name();
                String opType = op.type();
                operations.add(String.format("%s (%s)", opName, opType));
                String lowerName = opName.toLowerCase();

                // Look for potential input tensor (usually has "input" in the name)
                if (inputTensorName == null && lowerName.contains("input")) {
                    inputTensorName = opName;
                    logger.info("Found potential input tensor: {}", opName);
                }
                // Look for potential output tensor (usually has "output", "predictions", or "softmax")
                if (outputTensorName == null && (lowerName.contains("output") ||
                        lowerName.contains("predictions") ||
                        lowerName.contains("softmax"))) {
                    outputTensorName = opName;
                    logger.info("Found potential output tensor: {}", opName);
                }
                // Additional check: MobileNetV2 models exported from TF2 often use a StatefulPartitionedCall op
                if (outputTensorName == null && lowerName.contains("statefulpartitionedcall")) {
                    outputTensorName = opName;
                    logger.info("Found potential output tensor (statefulpartitionedcall): {}", opName);
                }
            }

            // Log all operations for debugging
            logger.info("All operations in model:");
            operations.stream().sorted().forEach(logger::info);

            if (inputTensorName == null || outputTensorName == null) {
                throw new RuntimeException("Could not find input/output tensor names");
            }

        } catch (Exception e) {
            logger.error("Failed to load model or labels: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize ImageProcessingService", e);
        }
    }

    public List<String> generateTags(byte[] imageData) {
        List<String> tags = new ArrayList<>();
        try {
            logger.info("Starting tag generation for image of size: {} bytes", imageData.length);

            // Use try-with-resources to ensure tensors are closed
            try (Tensor<?> imageTensor = prepareImage(imageData)) {
                if (imageTensor == null) {
                    logger.error("Failed to prepare image tensor");
                    return List.of("untagged-image-prep-failed");
                }
                try (Tensor<?> result = model.session().runner()
                        .feed(inputTensorName, imageTensor)
                        .fetch(outputTensorName)
                        .run()
                        .get(0)) {

                    // IMPORTANT: The output tensor is likely of shape [1, numLabels]
                    // so we copy it into a two-dimensional array.
                    float[][] resultArray = new float[1][labels.size()];
                    result.copyTo(resultArray);
                    float[] probabilities = resultArray[0];

                    logger.info("Processing predictions...");
                    // Loop to pick up to five predictions with confidence > 0.1
                    for (int i = 0; i < 5; i++) {
                        int maxIndex = 0;
                        float maxProb = 0;
                        for (int j = 0; j < probabilities.length; j++) {
                            if (probabilities[j] > maxProb) {
                                maxProb = probabilities[j];
                                maxIndex = j;
                            }
                        }
                        if (maxProb > 0.1f) {
                            String tag = labels.get(maxIndex);
                            logger.info("Found tag {} with confidence {}", tag, maxProb);
                            tags.add(tag);
                            // Zero out the max probability to find the next best one
                            probabilities[maxIndex] = 0;
                        }
                    }
                }
            }
            return tags.isEmpty() ? List.of("untagged-no-predictions") : tags;

        } catch (Exception e) {
            logger.error("Error generating tags", e);
            e.printStackTrace();
            return List.of("untagged-" + e.getClass().getSimpleName());
        }
    }

    private Tensor<?> prepareImage(byte[] imageData) {
        try {
            logger.info("Starting image preparation...");

            // Convert byte array to BufferedImage
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));
            if (originalImage == null) {
                logger.error("Failed to read image data");
                return null;
            }

            // Create a new BufferedImage with the target size
            BufferedImage resizedImage = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();

            // Set rendering hints for better quality
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw the original image scaled to the target size
            g.drawImage(originalImage, 0, 0, IMAGE_SIZE, IMAGE_SIZE, null);
            g.dispose();

            // Create a float array to hold the image data
            float[] imageArray = new float[IMAGE_SIZE * IMAGE_SIZE * 3];
            int index = 0;

            // Convert image to float array and normalize to [-1, 1]
            for (int y = 0; y < IMAGE_SIZE; y++) {
                for (int x = 0; x < IMAGE_SIZE; x++) {
                    int rgb = resizedImage.getRGB(x, y);
                    float r = ((rgb >> 16) & 0xFF) / 127.5f - 1.0f;
                    float gValue = ((rgb >> 8) & 0xFF) / 127.5f - 1.0f;
                    float b = (rgb & 0xFF) / 127.5f - 1.0f;

                    // MobileNetV2 expects RGB format
                    imageArray[index++] = r;
                    imageArray[index++] = gValue;
                    imageArray[index++] = b;
                }
            }

            // Create tensor with shape [1, height, width, channels]
            long[] shape = {1, IMAGE_SIZE, IMAGE_SIZE, 3};
            FloatBuffer floatBuffer = FloatBuffer.wrap(imageArray);

            logger.info("Successfully prepared image tensor with shape: [1, {}, {}, 3]", IMAGE_SIZE, IMAGE_SIZE);
            return Tensor.create(shape, floatBuffer);

        } catch (Exception e) {
            logger.error("Error preparing image: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Clean up resources when the service is destroyed
    public void close() {
        if (model != null) {
            model.close();
        }
    }
}
