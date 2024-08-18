package com.hit.joonggonara.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Getter
@Entity
public class Photo {

    @GeneratedValue
    @Id
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    public Photo(String fileName, String filePath, Product product) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.product = product;
    }

    public void addProduct(Product product){
        if(this.product != null){
            this.product.getPhotos().remove(this);
        }

        this.product = product;
        this.product.getPhotos().add(this);
    }
}
