package com.dursuneryilmaz.duscrumtool.controller;

import com.dursuneryilmaz.duscrumtool.domain.*;
import com.dursuneryilmaz.duscrumtool.model.response.OperationModel;
import com.dursuneryilmaz.duscrumtool.model.response.OperationName;
import com.dursuneryilmaz.duscrumtool.model.response.OperationStatus;
import com.dursuneryilmaz.duscrumtool.service.*;
import com.dursuneryilmaz.duscrumtool.shared.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    ThemeService themeService;
    @Autowired
    ProductBacklogService productBacklogService;
    @Autowired
    SprintService sprintService;
    @Autowired
    RequestValidationService requestValidationService;
    @Autowired
    Utils utils;

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody Product product, BindingResult bindingResult) {
        ResponseEntity<?> errorMap = requestValidationService.mapValidationErrors(bindingResult);
        if (errorMap != null) return errorMap;
        // initialize product backlog before product creation
        ProductBacklog productBacklog = new ProductBacklog();
        productBacklog.setProduct(product);
        productBacklog.setProductBacklogId(utils.generatePublicId(32));
        product.setProductBacklog(productBacklog);
        return new ResponseEntity<Product>(productService.createProduct(product), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping(path = "/{productId}")
    public ResponseEntity<Product> getProductByProductId(@PathVariable String productId) {
        return new ResponseEntity<Product>(productService.getProductById(productId), HttpStatus.OK);
    }

    @PutMapping(path = "/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable String productId, @RequestBody Product product, BindingResult bindingResult) {
        ResponseEntity<?> errorMap = requestValidationService.mapValidationErrors(bindingResult);
        if (errorMap != null) return errorMap;
        return new ResponseEntity<Product>(productService.updateProductById(productId, product), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{productId}")
    public ResponseEntity<OperationModel> deleteProductByProductId(@PathVariable String productId) {
        OperationModel operationModel = new OperationModel();
        if (productService.deleteProductById(productId)) {
            operationModel.setOperationName(OperationName.DELETE.name());
            operationModel.setOperationStatus(OperationStatus.SUCCESS.name());
            return new ResponseEntity<>(operationModel, HttpStatus.OK);
        }
        operationModel.setOperationName(OperationName.DELETE.name());
        operationModel.setOperationStatus(OperationStatus.ERROR.name());
        return new ResponseEntity<>(operationModel, HttpStatus.OK);
    }

    @GetMapping(path = "/{productId}/themes")
    public ResponseEntity<List<Theme>> getProductThemes(@PathVariable String productId) {
        Product product = productService.getProductById(productId);
        return new ResponseEntity<List<Theme>>(themeService.getAllByProduct(product), HttpStatus.OK);
    }

    @GetMapping(path = "/{productId}/themes/{themeId}")
    public ResponseEntity<Theme> getProductTheme(@PathVariable String productId, @PathVariable String themeId) {
        return new ResponseEntity<Theme>(themeService.getThemeById(themeId), HttpStatus.OK);
    }

    @GetMapping(path = "/{productId}/backlog")
    public ResponseEntity<ProductBacklog> getProductBacklog(@PathVariable String productId) {
        Product product = productService.getProductById(productId);
        return new ResponseEntity<ProductBacklog>(productBacklogService.getByProduct(product), HttpStatus.OK);
    }

    @GetMapping(path = "/{productId}/sprints")
    public ResponseEntity<List<Sprint>> getProductSprints(@PathVariable String productId) {
        Product product = productService.getProductById(productId);
        return new ResponseEntity<List<Sprint>>(sprintService.getAllByProduct(product), HttpStatus.OK);
    }

    // get stake holders
    @GetMapping(path = "/{productId}/stake-holders")
    public ResponseEntity<List<User>> getProductStakeHolders(@PathVariable String productId) {
        Product product = productService.getProductById(productId);
        return new ResponseEntity<List<User>>(productService.getProductStakeHolders(product), HttpStatus.OK);
    }

    // get scrum managers
    @GetMapping(path = "/{productId}/scrum-managers")
    public ResponseEntity<List<User>> getProductScrumManagers(@PathVariable String productId) {
        Product product = productService.getProductById(productId);
        return new ResponseEntity<List<User>>(productService.getProductScrumManagers(product), HttpStatus.OK);
    }

    // get scrum devs
    @GetMapping(path = "/{productId}/developers")
    public ResponseEntity<List<User>> getProductDevelopers(@PathVariable String productId) {
        Product product = productService.getProductById(productId);
        return new ResponseEntity<List<User>>(productService.getProductDevelopers(product), HttpStatus.OK);
    }

}
