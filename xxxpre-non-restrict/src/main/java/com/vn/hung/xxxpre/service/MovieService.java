package com.vn.hung.xxxpre.service;


import com.vn.hung.xxxpre.dto.MovieUpsertRequest;
import com.vn.hung.xxxpre.dto.PaginatedMovieResponse;
import com.vn.hung.xxxpre.entity.Movie;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MovieService {

    PaginatedMovieResponse listMovies(int page, int size, String sortDirection, String keyword) ;


    Movie createMovieWithVideoAsync(MovieUpsertRequest request, MultipartFile video);

    Movie getMovieDetail(String fileId) ;

    ResponseEntity<Resource> streamMovie(String fileId, String rangeHeader);
}