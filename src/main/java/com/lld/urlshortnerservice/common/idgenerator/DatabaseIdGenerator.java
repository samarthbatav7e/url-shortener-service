package com.lld.urlshortnerservice.common.idgenerator;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class DatabaseIdGenerator implements IdGenerator {

    private static final Long SEQUENCE_ID= 1L;
    private final IdSequenceRepository repository;

    public DatabaseIdGenerator(IdSequenceRepository idSequenceRepository)
    {
        this.repository=idSequenceRepository;
    }

    @Override
    @Transactional
    public long generateId()
    {
        IdSequence sequence=repository.findById(SEQUENCE_ID)
                .orElseThrow(()-> new IllegalStateException("ID sequence is not initialized. "));

        long generatedId=sequence.getNextId();
        sequence.setNextId(generatedId+1);
        repository.save(sequence);

        return generatedId;
    }


}
