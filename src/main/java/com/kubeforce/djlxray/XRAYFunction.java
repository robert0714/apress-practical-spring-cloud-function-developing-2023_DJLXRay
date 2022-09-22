package com.kubeforce.djlxray;

import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.repository.zoo.Criteria;

import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.Translator;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class XRAYFunction implements Function<Map<String,String>, String> {

    private static final Logger logger = LoggerFactory.getLogger(DjlxRayApplication.class);
    private static final List<String> CLASSES = Arrays.asList("Normal", "Pneumonia");
    String imagePath;
    String savedModelPath;


    @SneakyThrows
    @Override
    public String apply(Map<String, String> imageinput) {
            imagePath= imageinput.get("url");
            savedModelPath = imageinput.get("savedmodelpath");
            Image image;
            try {
                image = ImageFactory.getInstance().fromUrl(imagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Translator<Image, Classifications> translator =
                    ImageClassificationTranslator.builder()
                            .addTransform(a -> NDImageUtils.resize(a, 224).div(255.0f))
                            .optSynset(CLASSES)
                            .build();
            Criteria<Image, Classifications> criteria =
                    Criteria.builder()
                            .setTypes(Image.class, Classifications.class)
//                           .optModelUrls("https://djl-ai.s3.amazonaws.com/resources/demo/pneumonia-detection-model/saved_model.zip")
                            .optModelUrls(savedModelPath)
                            .optTranslator(translator)
                            .build();


            try (ZooModel<Image, Classifications> model = criteria.loadModel();
                 Predictor<Image, Classifications> predictor = model.newPredictor()) {
                Classifications result = predictor.predict(image);
                logger.info("Diagnose: {}", result);
                return result.toJson();
            }

        }
    }



