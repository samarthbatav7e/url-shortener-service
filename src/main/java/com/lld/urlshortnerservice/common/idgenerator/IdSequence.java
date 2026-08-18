package com.lld.urlshortnerservice.common.idgenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="id_sequence")
public class IdSequence {

    @Id
    private Long Id;

    private Long nextId;

    public IdSequence()
    {

    }
    public IdSequence(Long Id, Long nextId)
    {
        this.Id=Id;
        this.nextId=nextId;
    }
    public Long getId()
    {
        return Id;
    }
    public void setId(Long Id)
    {
        this.Id=Id;
    }
    public Long getNextId()
    {
        return nextId;
    }
    public void setNextId(Long nextId)
    {
        this.nextId=nextId;
    }

}
