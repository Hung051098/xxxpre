package com.vn.hung.xxxpre.repository;

import com.vn.hung.xxxpre.entity.Movie;
import com.vn.hung.xxxpre.repository.base.DynamoDbCRUDRepository;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

@Repository
public class MovieRepository implements DynamoDbCRUDRepository<Movie> {

    /**
     * Queries movies sorted by release date using the GSI.
     */
    public Page<Movie> findAllByReleaseDate(int pageSize, Map<String, AttributeValue> startKey, boolean forward) {
        // 1. Get the GSI Index
        DynamoDbIndex<Movie> index = this.tableIndex("movies-by-releaseDate-gsi", Movie.class);

        // 2. Build the query (Partition Key = "Movie")
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue("Movie")
                .build());

        // 3. Build the request with pagination and sort direction
        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .limit(pageSize);
//                .scanIndexForward(forward); // true = ASC (oldest), false = DESC (newest)

        if (startKey != null && !startKey.isEmpty()) {
            requestBuilder.exclusiveStartKey(startKey);
        }

        // 4. Execute and get the first page
        return index.query(requestBuilder.build()).iterator().next();
    }
}