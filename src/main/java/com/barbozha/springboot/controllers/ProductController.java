package com.barbozha.springboot.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barbozha.springboot.dto.ProductRecordDto;
import com.barbozha.springboot.models.ProductModel;
import com.barbozha.springboot.repositories.ProductRepository;

import jakarta.validation.Valid;


//estes link foram colocados na mão porque a IDE nao importou.
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping
public class ProductController {
	
	@Autowired
	ProductRepository productRepository;
	
	@PostMapping("/products")
	public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto){
		var productModel = new ProductModel();
		BeanUtils.copyProperties(productRecordDto, productModel);
		return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
	}
	
	@GetMapping("/products")
	public ResponseEntity<List<ProductModel>> getAllProducts(){
		List<ProductModel> ProductList = productRepository.findAll();
		if(!ProductList.isEmpty()) {
			for(ProductModel product : ProductList) {
				UUID id = product.getIdProduct();
				product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(ProductList);
	}
	
	@GetMapping("/products/{id}")
	public ResponseEntity<Object> getOneProduct(@PathVariable(value="id") UUID id){
		Optional<ProductModel> obj = productRepository.findById(id);
		if(obj.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registro NÃO encontrado.");
		}
		obj.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());
		return ResponseEntity.status(HttpStatus.OK).body(obj.get());
	}
	
	@PutMapping("products/{id}")
	public ResponseEntity<Object> updateProduct(@PathVariable(value="id") UUID id,
							@RequestBody @Valid ProductRecordDto productRecordDto){
		Optional<ProductModel> obj = productRepository.findById(id);
		if(obj.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registro Não encontrado.");
		}
		ProductModel productModel = obj.get();
		BeanUtils.copyProperties(productRecordDto, productModel);
		return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
	}
	
	@DeleteMapping("/products/{id}")
	public ResponseEntity<Object> deletProduct(@PathVariable(value="id") UUID id){
		Optional<ProductModel> obj = productRepository.findById(id);
		if(obj.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record not found");
		}
		productRepository.delete(obj.get());
		return ResponseEntity.status(HttpStatus.OK).body("Product deleted Successfully");
	}
	
	
}
