package org.joget.valuprosys.products.dao;

import java.util.Collection;
import org.joget.valuprosys.products.model.Products;

public interface ProductsDao {

    Boolean addProducts(Products products);

    Boolean updateProducts(Products products);

    Boolean deleteProducts(String id);

    Products getProducts(String id);

    Products getProductsByName(String name);
}
