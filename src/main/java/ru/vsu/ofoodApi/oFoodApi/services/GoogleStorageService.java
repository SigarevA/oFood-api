package ru.vsu.ofoodApi.oFoodApi.services;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class GoogleStorageService {

    private Mono<Storage> storageMono;

    @Autowired
    public GoogleStorageService(Mono<Storage> storageMono) {
        this.storageMono = storageMono;
    }

    public Mono<String> createBlob( byte[] content, String typeContent) {
        String name = "Dish_" + new Date().getTime();
        BlobId blobId = BlobId.of("images-dishes", name);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(typeContent)
            .build();
        return storageMono.map(storage -> storage.create(blobInfo, content)).map(
            blob -> "https://storage.googleapis.com/images-dishes/" + name
        );
    }
}