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

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class MovieRepository implements DynamoDbCRUDRepository<Movie> {

    /**
     * Queries movies sorted by release date using the GSI.
     */
    public Page<Movie> findAllByReleaseDate(int pageSize, Map<String, AttributeValue> startKey, boolean forward) {
        DynamoDbIndex<Movie> index = this.tableIndex("movies-by-releaseDate-gsi", Movie.class);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue("Movie")
                .build());

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .limit(pageSize)
                .scanIndexForward(forward);

        Optional.ofNullable(startKey).ifPresent(requestBuilder::exclusiveStartKey);

        return StreamSupport.stream(index.query(requestBuilder.build()).spliterator(), false)
                .findFirst()
                .orElse(null);
    }
}