package com.lld.urlshortnerservice.common.idgenerator;


import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdSequenceRepository extends JpaRepository<IdSequence,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<IdSequence>findById(Long Id);
}
