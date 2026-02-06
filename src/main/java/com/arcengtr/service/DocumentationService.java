package com.arcengtr.service;

import dev.langchain4j.model.embedding.EmbeddingModel;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Points;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.ScoredPoint;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;
import static io.qdrant.client.WithPayloadSelectorFactory.enable;

@Slf4j
public class DocumentationService {
    private final QdrantClient client;
    private final String collectionName = "docs";

    private final EmbeddingModel embeddingModel;

    public DocumentationService(QdrantClient client, EmbeddingModel embeddingModel) {
        this.client = client;
        this.embeddingModel = embeddingModel;
        ensureCollectionExists();
    }

    public void loadFromPaths(List<String> paths) {
        if (paths == null || paths.isEmpty()) {
            log.warn("No documentation paths provided to load.");
            return;
        }

        for (String pathString : paths) {
            try {
                String content = Files.readString(Paths.get(pathString));
                String fileName = Paths.get(pathString).getFileName().toString().replace(".md", "");

                List<String> chunks = splitText(content, 1000);

                for (int i = 0; i < chunks.size(); i++) {
                    String chunk = chunks.get(i);
                    UUID chunkId = UUID.nameUUIDFromBytes((fileName + "_chunk_" + chunk).getBytes());

                    List<Float> vector = embeddingModel.embed(chunk).content().vectorAsList();

                    PointStruct point = PointStruct.newBuilder()
                            .setId(id(chunkId))
                            .setVectors(vectors(vector))
                            .putAllPayload(Map.of(
                                    "filename", value(fileName),
                                    "content", value(chunk),
                                    "chunk_index", value(i)
                            ))
                            .build();

                    client.upsertAsync(collectionName, List.of(point)).get();
                }

                log.info("Successfully loaded documentation: {}", fileName);
            } catch (IOException | InterruptedException | ExecutionException e) {
                log.error("Failed to read documentation file at {}: {}", pathString, e.getMessage());
            }
        }
    }

    public String searchDocumentation(String query) {

        try {
            List<Float> queryVector = embeddingModel.embed(query).content().vectorAsList();

            List<ScoredPoint> results = client.searchAsync(
                    Points.SearchPoints.newBuilder()
                            .setCollectionName(collectionName)
                            .addAllVector(queryVector)
                            .setWithPayload(enable(true))
                            .setLimit(3)
                            .setScoreThreshold(0.5f)
                            .build()).get();

            if (results.isEmpty()) return "No specific documentation found.";

            StringBuilder sb = new StringBuilder();
            for (ScoredPoint point : results) {
                String fileName = point.getPayloadMap().get("filename").getStringValue();
                String content = point.getPayloadMap().get("content").getStringValue();

                sb.append("### Source: ").append(fileName)
                        .append(" (Similarity: ").append(String.format("%.2f", point.getScore())).append(")\n");

                sb.append(content.length() > 500 ? content.substring(0, 500) + "..." : content).append("\n\n");
            }
            return sb.toString();

        } catch (Exception e) {
            log.error("Search failed", e);
            return "Error during vector search.";
        }
    }

    private List<String> splitText(String text, int size) {
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < text.length(); i += size) {
            chunks.add(text.substring(i, Math.min(text.length(), i + size)));
        }
        return chunks;
    }

    private void ensureCollectionExists() {
        try {
            if (!client.listCollectionsAsync().get().contains(collectionName)) {
                client.createCollectionAsync(collectionName, Collections.VectorParams.newBuilder()
                        .setSize(384)
                        .setDistance(Collections.Distance.Cosine)
                        .build()).get();
            }
        } catch (Exception e) {
            log.error("Failed to initialize collection", e);
        }
    }
}