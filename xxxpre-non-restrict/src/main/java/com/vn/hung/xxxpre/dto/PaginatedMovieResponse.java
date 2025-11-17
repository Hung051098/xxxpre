package com.vn.hung.xxxpre.dto;

import java.util.List;

/**
 * This DTO will be returned to the client, containing the list
 * of movies for the current page, a token to get the next page,
 * and the total number of pages available.
 */
public record PaginatedMovieResponse(
        List<MovieDto> movies,
        String nextPageToken
) {}