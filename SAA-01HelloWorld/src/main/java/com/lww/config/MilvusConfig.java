package com.lww.config;

import io.milvus.param.MilvusParam;
import io.milvus.param.connect.ConnectParam;
import io.milvus.grpc.DataType;
import io.milvus.orm.collection.CollectionSchema;
import io.milvus.orm.index.IndexType;
import io.milvus.orm.index.IndexMetaData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MilvusConfig {

    @Value("${milvus.endpoint:127.0.0.1:19530}")
    private String milvusEndpoint;

    @Value("${milvus.database:default}")
    private String database;

    @Bean
    public io.milvus.client.MilvusClient milvusClient() {
        return new io.milvus.client.MilvusClient(
            MilvusParam.newBuilder()
                .withHost(milvusEndpoint.split(":")[0])
                .withPort(Integer.parseInt(milvusEndpoint.split(":")[1]))
                .withAuthorization("")
                .build()
        );
    }

    @Bean
    public void initMilvusCollection(io.milvus.client.MilvusClient client) {
        String collectionName = "medical_kb";

        // 检查集合是否已存在，不存在则创建
        if (!client.hasCollection(io.milvus.param.collection.HasCollectionReq.builder()
                .collectionName(collectionName).build()).getValue()) {

            // 定义 schema
            List<io.milvus.grpc.FieldSchema> fieldSchemas = new ArrayList<>();

            // ID 字段 (string)
            fieldSchemas.add(io.milvus.grpc.FieldSchema.newBuilder()
                    .setFieldName("id")
                    .setDataType(DataType.VarChar)
                    .setIsPrimaryKey(true)
                    .addMaxLength(64)
                    .build());

            // 向量字段 (float vector, dim=1536 for glm-embedding)
            fieldSchemas.add(io.milvus.grpc.FieldSchema.newBuilder()
                    .setFieldName("vector")
                    .setDataType(DataType.FloatVector)
                    .setMaxDimension(1536)
                    .build());

            // 内容字段 (text)
            fieldSchemas.add(io.milvus.grpc.FieldSchema.newBuilder()
                    .setFieldName("content")
                    .setDataType(DataType.VarChar)
                    .addMaxLength(65535)
                    .build());

            // 来源文档字段
            fieldSchemas.add(io.milvus.grpc.FieldSchema.newBuilder()
                    .setFieldName("source")
                    .setDataType(DataType.VarChar)
                    .addMaxLength(256)
                    .build());

            CollectionSchema schema = CollectionSchema.newBuilder()
                    .withCollectionName(collectionName)
                    .addFieldSchema(fieldSchemas.get(0))
                    .addFieldSchema(fieldSchemas.get(1))
                    .addFieldSchema(fieldSchemas.get(2))
                    .addFieldSchema(fieldSchemas.get(3))
                    .withDescription("Medical knowledge base")
                    .build();

            client.createCollection(io.milvus.param.collection.CreateCollReq.builder()
                    .collectionSchema(schema)
                    .numPartitions(10)
                    .collectionName(collectionName)
                    .build());

            // 创建索引
            IndexType indexType = IndexType.IP; // Inner Product
            io.milvus.param.index.IndexParams indexParams = io.milvus.param.index.IndexParams.builder()
                    .addIndex("vector", indexType.name(), "IVF_FLAT", 64)
                    .build();

            client.CreateIndex(io.milvus.param.index.CreateIndexReq.builder()
                    .collectionName(collectionName)
                    .indexParams(indexParams)
                    .build());

            System.out.println("Milvus collection 'medical_kb' created successfully");
        }
    }
}
