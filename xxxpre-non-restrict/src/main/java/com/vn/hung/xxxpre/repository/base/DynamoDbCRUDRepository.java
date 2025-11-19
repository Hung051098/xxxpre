package com.vn.hung.xxxpre.repository.base;

import com.vn.hung.xxxpre.utils.CommonUtils;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.Select;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface DynamoDbCRUDRepository<Entity> {
    default DynamoDbTable<Entity> table(Class<Entity> entityClass) {
        String tableName = ((DynamoDbTableName) entityClass.getAnnotation(DynamoDbTableName.class)).value();
        return this.dynamoDbEnhancedClient().table(tableName, TableSchema.fromBean(entityClass));
    }

    default DynamoDbIndex<Entity> tableIndex(String indexName, Class<Entity> entityClass) {
        String tableName = ((DynamoDbTableName) entityClass.getAnnotation(DynamoDbTableName.class)).value();
        return this.dynamoDbEnhancedClient().table(tableName, TableSchema.fromBean(entityClass)).index(indexName);
    }

    default List<Entity> scan(Class<Entity> entityClass) {
        DynamoDbTable<Entity> table = this.table(entityClass);
        return (List) table.scan().items().stream().collect(Collectors.toList());
    }

    default <T> PageResult<T> scanPage(Class<Entity> clazz, Map<String, AttributeValue> exclusiveStartKey, int limit) {

        DynamoDbTable<Entity> table = this.table(clazz);

        ScanEnhancedRequest.Builder requestBuilder = ScanEnhancedRequest.builder()
                .limit(limit);

        if (exclusiveStartKey != null && !exclusiveStartKey.isEmpty()) {
            requestBuilder.exclusiveStartKey(exclusiveStartKey);
        }

        Iterator<Page<Entity>> iterator = table.scan(requestBuilder.build()).iterator();

        if (iterator.hasNext()) {
            Page<T> page = (Page<T>) iterator.next();
            return new PageResult<>(page.items(), page.lastEvaluatedKey());
        }

        return new PageResult<>(Collections.emptyList(), null);
    }

    /**
     * New Method: Counts all items in the table efficiently.
     */
    default int count(Class<Entity> entityClass) {
        String tableName = ((DynamoDbTableName) entityClass.getAnnotation(DynamoDbTableName.class)).value();

        // Use low-level client for Select.COUNT (cheaper than loading items)
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .select(Select.COUNT)
                .build();

        return dynamoDbClient().scan(scanRequest).count();
    }

    default void save(Entity entity, Class<Entity> entityClass) {
        DynamoDbTable<Entity> table = this.table(entityClass);
        table.putItem(entity);
    }

    default void updateItem(Entity entity, Class<Entity> entityClass) {
        DynamoDbTable<Entity> table = this.table(entityClass);
        table.updateItem(entity);
    }

    default void deleteItem(String partitionKey, String sortKey, Class<Entity> entityClass) {
        DynamoDbTable<Entity> table = this.table(entityClass);
        Key key = Key.builder().partitionValue(partitionKey).build();
        if (!CommonUtils.isNullOrEmpty(sortKey)) {
            key = Key.builder().partitionValue(partitionKey).sortValue(sortKey).build();
        }

        table.deleteItem(key);
    }

    default Entity query(String partitionKey, String sortKey, Class<Entity> entityClass) {
        DynamoDbTable<Entity> table = this.table(entityClass);
        final Key key = CommonUtils.isNullOrEmpty(sortKey)
                ? Key.builder().partitionValue(partitionKey).build()
                : Key.builder().partitionValue(partitionKey).sortValue(sortKey).build();

        return (Entity) table.getItem(r -> r.key(key));
    }

    default List<Entity> queryList(String partitionKey, String sortKey, Class<Entity> entityClass) {
        DynamoDbTable<Entity> table = this.table(entityClass);
        Key key = Key.builder().partitionValue(partitionKey).build();
        QueryEnhancedRequest request = QueryEnhancedRequest.builder().queryConditional(QueryConditional.keyEqualTo(key)).build();
        return (List) table.query(request).items().stream().collect(Collectors.toList());
    }

//    default List<Entity> queryByIndex(String partitionKey, String sortKey, String indexName, Class<Entity> entityClass) {
//        DynamoDbIndex<Entity> table = this.tableIndex(indexName, entityClass);
//        Key key = Key.builder().partitionValue(partitionKey).build();
//        if (!CommonUtils.isNullOrEmpty(sortKey)) {
//            key = Key.builder().partitionValue(partitionKey).sortValue(sortKey).build();
//        }
//
//        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder().queryConditional(QueryConditional.keyEqualTo(key)).build();
//        List<Entity> resultList = new ArrayList();
//        SdkIterable<Page<Entity>> sdkIterable = table.query(queryEnhancedRequest);
//
//        for(Page<Entity> dbmPaymentDetailPage : (List)sdkIterable.stream().collect(Collectors.toList())) {
//            List<Entity> dbmPaymentDetailList = new ArrayList(dbmPaymentDetailPage.items());
//            resultList.addAll(dbmPaymentDetailList);
//        }
//
//        return resultList;
//    }

    default DynamoDbClient dynamoDbClient() {
        return (DynamoDbClient) ((DynamoDbClientBuilder) DynamoDbClient.builder().region(Region.of("ap-southeast-1"))).build();
    }

    default DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(this.dynamoDbClient()).build();
    }
}
