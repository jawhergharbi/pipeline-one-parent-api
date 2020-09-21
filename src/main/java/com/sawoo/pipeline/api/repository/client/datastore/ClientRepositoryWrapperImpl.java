package com.sawoo.pipeline.api.repository.client.datastore;

import com.google.cloud.datastore.Key;
import com.sawoo.pipeline.api.model.DataStoreConstants;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.repository.DataStoreKeyFactory;
import com.sawoo.pipeline.api.repository.client.ClientRepositoryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientRepositoryWrapperImpl implements ClientRepositoryWrapper {

    private final ClientRepository repository;
    private final DataStoreKeyFactory datastoreKeyFactory;

    @Override
    public Optional<Client> findByLinkedInUrl(String linkedInUrl) {
        return repository.findByLinkedInUrl(linkedInUrl);
    }

    @Override
    public Optional<Client> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Client> findByUserId(String id) {
        Comparator<Client> comparator = Comparator.comparing(Client::getId);
        // CSMs
        Key csmKey = datastoreKeyFactory.getKeyFactory(DataStoreConstants.USER_ENTITY_ENTITY).newKey(id);
        List<Client> csmClients = repository.findByCSMIs(csmKey);

        // SAa
        Key saKey = datastoreKeyFactory.getKeyFactory(DataStoreConstants.USER_ENTITY_ENTITY).newKey(id);
        List<Client> saClients = repository.findBySAIs(saKey);
        return Stream
                .concat(csmClients.stream(), saClients.stream())
                .filter(new TreeSet<>(comparator)::add)
                .collect(Collectors.toList());
    }

    @Override
    public Client save(Client client) {
        return repository.save(client);
    }

    @Override
    public Iterable<Client> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Client client) {
        repository.delete(client);
    }
}
