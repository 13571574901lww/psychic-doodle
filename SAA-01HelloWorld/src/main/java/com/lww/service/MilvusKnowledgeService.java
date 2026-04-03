package com.lww.service;

import io.milvus.client.MilvusClient;
import io.milvus.v2.common.ConsistencyLevel;
import io.milvus.grpc.DataType;
import io.milvus.param.ConnectParam;
import io.milvus.param.dml.InsertParam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MilvusKnowledgeService {

    private final MilvusClient milvusClient;

    public MilvusKnowledgeService(@Qualifier("milvusClient") MilvusClient milvusClient) {
        this.milvusClient = milvusClient;
    }

    /**
     * 插入文档切片到向量库
     */
    public void insertDocument(String id, float[] vector, String content, String source) {
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("id", id);
        row.put("vector", vector);
        row.put("content", content);
        row.put("source", source);
        rows.add(row);

        InsertParam param = InsertParam.newBuilder()
                .withCollectionName("medical_kb")
                .addFieldValues(rows)
                .build();

        milvusClient.insert(param);
    }

    /**
     * 搜索相似内容
     */
    public List<SearchResult> search(float[] queryVector, int topK, double minScore) {
        io.milvus.param.query.SearchParam searchParam = io.milvus.param.query.SearchParam.builder()
                .withCollectionName("medical_kb")
                .withAnnFields(Collections.singletonList("vector"))
                .withTopK(topK)
                .withMetricType(io.milvus.common.enums.MetricType.IP.name())
                .withConsistencyLevel(ConsistencyLevel.Session)
                .withVectors(Collections.singletonList(queryVector))
                .withVectorFieldName("vector")
                .withOutputFields(Arrays.asList("id", "content", "source"))
                .build();

        var results = milvusClient.search(searchParam);

        List<SearchResult> searchResults = new ArrayList<>();
        if (!results.isEmpty()) {
            var resultArray = results.get(0).getResult();
            for (var entity : resultArray) {
                Double score = (Double) entity.get("score");
                if (score >= minScore) {
                    SearchResult sr = new SearchResult();
                    sr.setId((String) entity.get("id"));
                    sr.setContent((String) entity.get("content"));
                    sr.setSource((String) entity.get("source"));
                    sr.setScore(score);
                    searchResults.add(sr);
                }
            }
        }
        return searchResults;
    }

    public static class SearchResult {
        private String id;
        private String content;
        private String source;
        private Double score;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }

        @Override
        public String toString() {
            return "SearchResult{" +
                    "id='" + id + '\'' +
                    ", content='" + content + '\'' +
                    ", source='" + source + '\'' +
                    ", score=" + score +
                    '}';
        }
    }
}
