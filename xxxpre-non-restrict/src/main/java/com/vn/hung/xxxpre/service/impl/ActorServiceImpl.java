package com.vn.hung.xxxpre.service.impl;

import com.vn.hung.xxxpre.entity.Actor;
import com.vn.hung.xxxpre.repository.ActorRepository;
import com.vn.hung.xxxpre.service.ActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActorServiceImpl implements ActorService {

    @Autowired
    private ActorRepository actorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Actor> listAllActors() {
        // JpaRepository.findAll() tự động trả về tất cả các bản ghi
        return actorRepository.findAll();
    }
}