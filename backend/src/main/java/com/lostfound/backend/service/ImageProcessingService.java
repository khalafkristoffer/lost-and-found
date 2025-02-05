package com.lostfound.backend.service;

import org.springframework.stereotype.Service;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ImageProcessingService {

    private static final String MODEL_PATH = "src/main/resources/mobilenet_v2.pb";

    public List<String> generateTags(byte[] imageData) {
        // Use try-with-resources so that all resources are closed automatically.
        try (SavedModelBundle model = SavedModelBundle.load(MODEL_PATH, "serve");
             Session session = model.session();
             Tensor<Float> inputTensor = preprocessImage(imageData)) {

            // Run the session, feeding in the image tensor and fetching the model output.
            Tensor<?> outputTensor = session.runner()
                    .feed("serving_default_input", inputTensor)
                    .fetch("StatefulPartitionedCall")
                    .run()
                    .get(0);

            return processOutput(outputTensor);
        } catch (Exception e) {
            throw new RuntimeException("Error processing image", e);
        }
    }

    private Tensor<Float> preprocessImage(byte[] imageData) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));
        int width = 224, height = 224;
        BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // Draw the original image into the resized image.
        resizedImg.getGraphics().drawImage(img, 0, 0, width, height, null);

        // Prepare the pixel data array (normalized to [0,1]).
        float[] pixelData = new float[width * height * 3];
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = resizedImg.getRGB(x, y);
                // Extract red, green, and blue components and normalize them.
                pixelData[index++] = ((rgb >> 16) & 0xFF) / 255.0f;
                pixelData[index++] = ((rgb >> 8) & 0xFF) / 255.0f;
                pixelData[index++] = (rgb & 0xFF) / 255.0f;
            }
        }

        // Create a tensor of shape [1, height, width, 3] using a FloatBuffer.
        long[] shape = new long[]{1, height, width, 3};
        return Tensor.create(shape, FloatBuffer.wrap(pixelData));
    }

    private List<String> processOutput(Tensor<?> tensor) {
        // Copy the output tensor data into a 2D float array.
        float[][] probabilities = new float[1][1000];
        tensor.copyTo(probabilities);

        // Find the indices of the top 5 probabilities.
        List<Integer> topLabels = IntStream.range(0, 1000)
                .boxed()
                .sorted((i, j) -> Float.compare(probabilities[0][j], probabilities[0][i]))
                .limit(5)
                .toList();

        // Map the indices to human-readable labels.
        return topLabels.stream().map(this::imagenetLabel).collect(Collectors.toList());
    }

    private String imagenetLabel(int index) {
        // Dummy labels; replace later or change method to probabilistic? idk yet
        String[] labels = {
                "wallet", "keys", "phone", "laptop", "watch",
                "glasses", "bag", "bottle", "umbrella", "headphones"
        };
        return labels[index % labels.length];
    }
}
