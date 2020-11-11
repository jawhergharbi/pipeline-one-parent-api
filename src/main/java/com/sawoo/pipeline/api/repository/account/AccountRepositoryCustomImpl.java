package com.sawoo.pipeline.api.repository.account;

import com.sawoo.pipeline.api.model.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AccountRepositoryCustomImpl implements AccountRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Account> searchByFullName(String fullName) {
        TextCriteria queryTextFullName = TextCriteria
                .forDefaultLanguage()
                .caseSensitive(false)
                .matching(fullName);
        Query byFullName = TextQuery.queryText(queryTextFullName).sortByScore();

        return mongoTemplate.find(byFullName, Account.class);
    }
}
