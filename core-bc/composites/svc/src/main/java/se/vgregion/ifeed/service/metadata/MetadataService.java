package se.vgregion.ifeed.service.metadata;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

public interface MetadataService {

    @Transactional
    void importMetadata();

    @Transactional
    void importMetadata(String rootMetadataName);


    Collection<String> getVocabulary(String metadataNodeName);
}