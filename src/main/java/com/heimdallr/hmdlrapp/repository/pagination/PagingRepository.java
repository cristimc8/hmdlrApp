package com.heimdallr.hmdlrapp.repository.pagination;

import com.heimdallr.hmdlrapp.models.BaseEntity;
import com.heimdallr.hmdlrapp.repository.RepoInterface;

public interface PagingRepository<EntityType, IdType> extends RepoInterface<EntityType, IdType> {

    Page<EntityType> findAll(Pageable pageable);   // Pageable e un fel de paginator
}
