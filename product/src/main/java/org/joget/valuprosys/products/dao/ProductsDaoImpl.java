package org.joget.valuprosys.products.dao;

import java.util.List;
import org.joget.commons.spring.model.AbstractSpringDao;
import org.joget.commons.util.LogUtil;
import org.joget.valuprosys.products.model.Products;


public class ProductsDaoImpl extends AbstractSpringDao implements ProductsDao {


    public Boolean addProducts(Products product) {
        try {
            save("Products", product);
            return true;
        } catch (Exception e) {
            LogUtil.error(ProductsDaoImpl.class.getName(), e, "Add Product Error!");
            return false;
        }
    }

    public Boolean updateProducts(Products product) {
        try {
            merge("Products", product);
            return true;
        } catch (Exception e) {
            LogUtil.error(ProductsDaoImpl.class.getName(), e, "Update Product Error!");
            return false;
        }
    }

    public Boolean deleteProducts(String id) {
        try {
            Products product = getProducts(id);
            if (product != null) {
          

                delete("Products", product);
            }
            return true;
        } catch (Exception e) {
            LogUtil.error(ProductsDaoImpl.class.getName(), e, "Delete Product Error!");
            return false;
        }
    }

    public Products getProducts(String id) {
        try {
            return (Products) find("Products", id);
        } catch (Exception e) {
            LogUtil.error(ProductsDaoImpl.class.getName(), e, "Get Product Error!");
            return null;
        }
    }

    public Products getProductsByName(String name) {
        try {
            Products product = new Products();
            product.setName(name);
            List products = findByExample("Products", product);

            if (products.size() > 0) {
                return (Products) products.get(0);
            }
        } catch (Exception e) {
            LogUtil.error(ProductsDaoImpl.class.getName(), e, "Get Products By Name Error!");
        }

        return null;
    }

  
}
