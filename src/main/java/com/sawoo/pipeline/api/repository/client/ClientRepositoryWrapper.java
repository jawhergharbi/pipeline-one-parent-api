package com.sawoo.pipeline.api.repository.client;

import com.sawoo.pipeline.api.model.client.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepositoryWrapper {

    Optional<Client> findByLinkedInUrl(String linkedInUrl);

    Optional<Client> findById(Long id);

    List<Client> findByUserId(String id);

    Client save(Client client);

    Iterable<Client> findAll();

    void delete(Client client);
}
